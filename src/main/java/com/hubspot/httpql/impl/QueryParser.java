package com.hubspot.httpql.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.CaseFormat;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import com.hubspot.httpql.ConditionProvider;
import com.hubspot.httpql.DefaultMetaUtils;
import com.hubspot.httpql.Filter;
import com.hubspot.httpql.MetaQuerySpec;
import com.hubspot.httpql.MultiParamConditionProvider;
import com.hubspot.httpql.ParsedQuery;
import com.hubspot.httpql.QueryConstraints;
import com.hubspot.httpql.QuerySpec;
import com.hubspot.httpql.ann.FilterBy;
import com.hubspot.httpql.ann.OrderBy;
import com.hubspot.httpql.error.ConstraintType;
import com.hubspot.httpql.error.ConstraintViolation;
import com.hubspot.httpql.error.FilterViolation;
import com.hubspot.httpql.error.LimitViolationType;
import com.hubspot.httpql.filter.Equal;
import com.hubspot.httpql.internal.BoundFilterEntry;
import com.hubspot.httpql.internal.FilterEntry;
import com.hubspot.httpql.internal.MultiValuedBoundFilterEntry;
import com.hubspot.httpql.jackson.BeanPropertyIntrospector;
import com.hubspot.rosetta.Rosetta;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * Primary entry point into httpQL.
 * <p>
 * The parser's job is to take a set of query arguments (string key/value pairs) and turn it into a high-level query representation, assuming it is valid according to the defined filtering rules (provided via {@link FilterBy} and
 * {@link OrderBy} annotations).
 *
 * @author tdavis
 */
public class QueryParser<T extends QuerySpec> {
  private static final Logger LOG = LoggerFactory.getLogger(QueryParser.class);
  private static final ServiceLoader<Filter> LOADER = ServiceLoader.load(Filter.class);
  private static final Map<String, Filter> BY_NAME = new HashMap<>();
  private static final Set<String> RESERVED_WORDS = ImmutableSet.of("offset", "limit", "order", "includeDeleted");
  private static final Set<String> IGNORED_PARAMS = ImmutableSet.of("hapikey", "access_token", "clienttimeout");

  static {
    for (Filter filter : LOADER) {
      for (String name : filter.names()) {
        BY_NAME.put(name, filter);
      }
    }
  }

  private static final Splitter FILTER_PARAM_SPLITTER = Splitter.on("__").trimResults();
  private static final Splitter MULTIVALUE_PARAM_SPLITTER = Splitter.on(",").omitEmptyStrings().trimResults();

  private static final Function<String, String> SNAKE_CASE_TRANSFORMER = input -> CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, input);

  private final Class<T> queryType;
  protected final Collection<String> orderableFields;
  private final ObjectMapper mapper;
  private final MetaQuerySpec<T> meta;
  private final boolean strictMode;

  private int maxLimit;
  private int maxOffset;
  private int defaultLimit = 100;

  protected QueryParser(final Class<T> spec, boolean strictMode, MetaQuerySpec<T> meta) {
    this.orderableFields = new ArrayList<>();
    this.queryType = spec;
    this.mapper = Rosetta.getMapper();
    this.meta = meta;
    this.strictMode = strictMode;
  }

  protected void setConstraints(final Class<T> spec) {
    QueryConstraints ann = spec.getAnnotation(QueryConstraints.class);
    if (ann != null) {
      this.defaultLimit = ann.defaultLimit();
      this.maxLimit = ann.maxLimit();
      this.maxOffset = ann.maxOffset();
    } else {
      LOG.warn(String.format("Type %s has no @QueryConstraints annotation; using built-in defaults.", spec));
    }
  }

  protected void buildOrderableFields(final Map<String, BeanPropertyDefinition> fields) {
    OrderBy ann;
    for (Map.Entry<String, BeanPropertyDefinition> entry : fields.entrySet()) {
      ann = DefaultMetaUtils.findOrderBy(entry.getValue());
      if (ann != null) {
        orderableFields.add(entry.getKey());
      }
    }
  }

  public static <T extends QuerySpec> Builder<T> newBuilder(Class<T> spec) {
    return new Builder<>(spec);
  }

  public ParsedQuery<T> parse(UriInfo uriInfo) {
    return parse(uriInfo.getQueryParameters());
  }

  public static MultivaluedMap<String, String> multimapToMultivaluedMap(Multimap<String, String> map) {

    MultivaluedMap<String, String> result = new MultivaluedMapImpl();
    for (Map.Entry<String, String> entry : map.entries()) {
      result.add(entry.getKey(), entry.getValue());
    }

    return result;
  }

  public ParsedQuery<T> parse(Multimap<String, String> uriParams) {
    return parse(multimapToMultivaluedMap(uriParams));
  }

  public ParsedQuery<T> createEmptyQuery() {
    return parse(new MultivaluedMapImpl());
  }

  public static ParsedUriParams parseUriParams(Multimap<String, String> uriParams) {
    return parseUriParams(multimapToMultivaluedMap(uriParams));
  }

  public static ParsedUriParams parseUriParams(MultivaluedMap<String, String> uriParams) {

    final ParsedUriParams result = new ParsedUriParams();

    // make a copy so we can modify it
    MultivaluedMap<String, String> params = new MultivaluedMapImpl();
    for (Map.Entry<String, List<String>> entry : uriParams.entrySet()) {
      if (!IGNORED_PARAMS.contains(entry.getKey().toLowerCase())) {
        params.put(entry.getKey(), entry.getValue());
      }
    }

    result.setIncludeDeleted(BooleanUtils.toBoolean(params.getFirst("includeDeleted")));
    params.remove("includeDeleted");

    final int limit = NumberUtils.toInt(params.getFirst("limit"), 0);
    if (limit != 0) {
      result.setLimit(limit);
    }
    params.remove("limit");

    final int offset = NumberUtils.toInt(params.getFirst("offset"), 0);
    if (offset != 0) {
      result.setOffset(offset);
    }
    params.remove("offset");

    result.addOrderBys(params.get("order"));
    params.remove("order");
    result.addOrderBys(params.get("orderBy"));
    params.remove("orderBy");

    for (Map.Entry<String, List<String>> entry : params.entrySet()) {
      List<String> parts = FILTER_PARAM_SPLITTER.splitToList(entry.getKey().trim());
      if (parts.size() > 2) {
        continue;
      }

      final String fieldName = parts.get(0);
      final String filterName = filterNameFromParts(parts);
      final Filter filter = BY_NAME.get(filterName);

      if (filter == null) {
        throw new FilterViolation(String.format("Unknown filter type `%s`", filterName));
      }

      List<String> values = entry.getValue();
      ConditionProvider conditionProvider = filter.getConditionProvider(DSL.field(fieldName));

      if (conditionProvider instanceof MultiParamConditionProvider && values.size() == 1 && values.get(0).contains(",")) {
        values = MULTIVALUE_PARAM_SPLITTER.splitToList(values.get(0));
      }

      result.addFieldFilter(new FieldFilter(filter, filterName, fieldName, values));
    }

    return result;
  }

  public ParsedQuery<T> parse(MultivaluedMap<String, String> uriParams) {
    final Map<String, Object> fieldValues = new HashMap<>();
    final List<BoundFilterEntry<T>> boundFilterEntries = new ArrayList<>();

    final ParsedUriParams parsedUriParams = parseUriParams(uriParams);

    final boolean includeDeleted = parsedUriParams.isIncludeDeleted();
    final Optional<Integer> limit = getLimit(parsedUriParams.getLimit());
    final Optional<Integer> offset = getOffset(parsedUriParams.getOffset());

    final Collection<Ordering> orderings = getOrderings(parsedUriParams.getOrderBys(), meta);

    final Table<BoundFilterEntry<T>, String, BeanPropertyDefinition> filterTable = meta.getFilterTable();
    final Map<String, BeanPropertyDefinition> fieldMap = meta.getFieldMap();

    for (FieldFilter fieldFilter : parsedUriParams.getFieldFilters()) {

      final String filterName = fieldFilter.getFilterName();

      // If not in "strict mode", gracefully accept completely unknown query parameters
      String fieldName = fieldFilter.getField();
      if (!fieldMap.keySet().contains(fieldName) && !strictMode) {
        String fieldNameSnakeCase = SNAKE_CASE_TRANSFORMER.apply(fieldName);
        if (!fieldMap.keySet().contains(fieldNameSnakeCase)) {
          continue;
        }
        fieldName = fieldNameSnakeCase;
      }

      String finalFieldName = fieldName;
      Optional<BoundFilterEntry<T>> filterEntryOptional = filterTable.rowKeySet().stream().filter(f -> Objects.equals(f.getFieldName(), finalFieldName) && Objects.equals(f.getFilter(), BY_NAME
          .get(filterName))).findFirst();

      // Use reserved words instead of simple look-up to throw exception on disallowed fields
      if (!filterEntryOptional.isPresent() || (!RESERVED_WORDS.contains(filterEntryOptional.get().getQueryName()) && !filterTable.contains(filterEntryOptional.get(), filterName))) {
        throw new FilterViolation(String.format("Filtering by \"%s %s\" is not allowed",
            finalFieldName, filterName));
      } else if (RESERVED_WORDS.contains(filterEntryOptional.get().getQueryName())) {
        continue;
      }

      FilterEntry filterEntry = filterEntryOptional.get();

      // Add value using key associated with the Type's field name, *not* the query parameter name.
      BeanPropertyDefinition prop = filterTable.get(filterEntry, filterName);
      BoundFilterEntry<T> boundColumn = filterTable.rowKeySet().stream()
          .filter(bfe -> bfe.equals(filterEntry)).findFirst()
          .orElseThrow(() -> new FilterViolation("Filter column " + filterEntry + " not found"));

      FilterBy ann = null;

      if (prop.getPrimaryMember() != null) {
        ann = prop.getPrimaryMember().getAnnotation(FilterBy.class);
        if (Strings.emptyToNull(ann.as()) != null) {
          boundColumn.setActualField(fieldMap.get(ann.as()));
        }
      }

      if (boundColumn.isMultiValue()) {
        List<String> paramVals = fieldFilter.getValues();

        List<?> boundVals = paramVals.stream()
            .map(v -> Rosetta.getMapper().convertValue(v, prop.getField().getAnnotated().getType()))
            .collect(Collectors.toList());

        boundColumn = new MultiValuedBoundFilterEntry<>(boundColumn, boundVals);

      } else {
        fieldValues.put(prop.getName(), fieldFilter.getValue());
      }

      boundFilterEntries.add(boundColumn);
    }

    try {
      T boundQuerySpec = mapper.convertValue(fieldValues, queryType);
      return new ParsedQuery<>(boundQuerySpec, queryType, boundFilterEntries, meta, limit, offset, orderings, includeDeleted);

    } catch (IllegalArgumentException e) {
      if (e.getCause() instanceof InvalidFormatException) {
        InvalidFormatException cause = (InvalidFormatException) e.getCause();
        String filterLabel = "filter";
        if (cause.getPath().size() > 0 && cause.getPath().get(0).getFieldName() != null) {
          filterLabel = cause.getPath().get(0).getFieldName() + " filter";
        }
        throw new FilterViolation(String.format("Invalid %s value '%s'; expected a %s", filterLabel, cause.getValue(), cause.getTargetType()));
      }
      throw e;
    }
  }

  public SelectBuilder<T> newSelectBuilder(UriInfo query) {
    return SelectBuilder.forParsedQuery(parse(query), meta);
  }

  public SelectBuilder<T> newSelectBuilder(Multimap<String, String> query) {
    return SelectBuilder.forParsedQuery(parse(query), meta);
  }

  public SelectBuilder<T> newSelectBuilder(MultivaluedMap<String, String> query) {
    return SelectBuilder.forParsedQuery(parse(query), meta);
  }

  @VisibleForTesting
  Optional<Integer> getLimit(Optional<Integer> limit) {
    if (limit.isPresent()) {
      try {
        int parsedLimit = limit.get();
        if (parsedLimit < 0) {
          throw new ConstraintViolation(ConstraintType.OFFSET, LimitViolationType.MINIMUM, parsedLimit, 0);
        }
        if (maxLimit > 0 && parsedLimit > maxLimit) {
          return Optional.of(maxLimit);
        }
        return Optional.of(parsedLimit);
      } catch (NumberFormatException ignored) {}
    }
    return Optional.of(defaultLimit);
  }

  @VisibleForTesting
  Optional<Integer> getOffset(Optional<Integer> offset) {
    if (offset.isPresent()) {
      try {
        int parsedOffset = offset.get();
        if (parsedOffset < 0) {
          throw new ConstraintViolation(ConstraintType.OFFSET, LimitViolationType.MINIMUM, parsedOffset, 0);
        }
        if (maxOffset > 0 && parsedOffset > maxOffset) {
          return Optional.of(maxOffset);
        }
        return Optional.of(parsedOffset);
      } catch (NumberFormatException ignored) {}
    }
    return Optional.empty();
  }

  protected Collection<Ordering> getOrderings(List<String> orderStrings, MetaQuerySpec<T> meta) {
    Collection<Ordering> orderings = new ArrayList<>();
    if (orderStrings != null) {
      for (String order : orderStrings) {
        Ordering ordering = Ordering.fromString(order);
        FilterEntry entry = new FilterEntry(null, ordering.getFieldName(), getQueryType());
        BeanPropertyDefinition prop = meta.getFieldMap().get(entry.getQueryName());
        if (prop == null) {
          prop = meta.getFieldMap().get(entry.getFieldName());
        }
        if (prop != null && orderableFields.contains(prop.getName())) {
          orderings.add(new Ordering(entry.getFieldName(), entry.getQueryName(), ordering.getOrder()));
        }
      }
    }
    return orderings;
  }

  private static String filterNameFromParts(List<String> parts) {
    if (parts.size() == 1) {
      return (new Equal()).names()[0];
    } else {
      return parts.get(1);
    }
  }

  public Class<T> getQueryType() {
    return queryType;
  }

  public MetaQuerySpec<T> getMeta() {
    return meta;
  }

  public Collection<String> getOrderableFields() {
    return orderableFields;
  }

  public static class Builder<T extends QuerySpec> {
    private static final Table<Class<?>, Boolean, QueryParser<?>> CACHED_PARSERS = HashBasedTable.create();

    protected Class<T> spec;
    protected boolean strictMode;
    protected MetaQuerySpec<T> meta;

    public Builder(Class<T> spec) {
      this.spec = spec;
    }

    public Builder<T> withMetaQuerySpec(MetaQuerySpec<T> meta) {
      this.meta = meta;
      return this;
    }

    /**
     * In Strict Mode, the parser will throw an Exception when an unknown query parameter is found, not only when a known field is not allowed to have the specified filter applied.
     * <p>
     * Defaults to OFF.
     */
    public Builder<T> withStrictMode() {
      this.strictMode = true;
      return this;
    }

    @SuppressWarnings("unchecked")
    public QueryParser<T> build() {
      QueryParser<T> qp = (QueryParser<T>) CACHED_PARSERS.get(spec, strictMode);
      if (qp != null) {
        return qp;
      }

      if (meta == null) {
        meta = new DefaultMetaQuerySpec<>(spec);
      }

      Map<String, BeanPropertyDefinition> fields = BeanPropertyIntrospector.getFields(spec);

      qp = new QueryParser<>(spec, strictMode, meta);
      qp.setConstraints(spec);
      qp.buildOrderableFields(fields);

      CACHED_PARSERS.put(spec, strictMode, qp);

      return qp;
    }
  }

  public static Filter named(String name) {
    return BY_NAME.get(name);
  }

}
