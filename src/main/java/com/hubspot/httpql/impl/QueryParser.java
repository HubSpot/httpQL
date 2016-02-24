package com.hubspot.httpql.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.CaseFormat;
import com.google.common.base.Strings;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import com.google.common.base.Splitter;
import com.hubspot.httpql.DefaultMetaUtils;
import com.hubspot.httpql.Filter;
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
  private static final ServiceLoader<Filter> loader = ServiceLoader.load(Filter.class);
  private static final Map<String, Filter> byName = new HashMap<>();
  private static final Set<String> reservedWords = ImmutableSet.of("offset", "limit", "order", "includeDeleted");

  static {
    for (Filter filter : loader) {
      for (String name : filter.names()) {
        byName.put(name, filter);
      }
    }
  }

  private static final Splitter FILTER_PARAM_SPLITTER = Splitter.on("__").trimResults();
  private static final Splitter MULTIVALUE_PARAM_SPLITTER = Splitter.on(",").omitEmptyStrings().trimResults();

  private static final Function<String, String> SNAKE_CASE_TRANSFORMER = input -> CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, input);

  private final Class<T> queryType;
  private final Collection<String> orderableFields;
  private final ObjectMapper mapper;
  private final DefaultMetaQuerySpec<T> meta;
  private final boolean strictMode;

  private int maxLimit;
  private int maxOffset;
  private int defaultLimit = 100;

  private QueryParser(final Class<T> spec, boolean strictMode) {
    this.orderableFields = new ArrayList<>();
    this.queryType = spec;
    this.mapper = Rosetta.getMapper();
    this.meta = new DefaultMetaQuerySpec<T>(spec);
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
    return new Builder<T>(spec);
  }

  public ParsedQuery<T> parse(UriInfo uriInfo) {
    return parse(uriInfo.getQueryParameters());
  }

  public ParsedQuery<T> parse(Multimap<String, String> uriParams) {
    MultivaluedMap<String, String> mmap = new MultivaluedMapImpl();
    for (Map.Entry<String, String> entry : uriParams.entries()) {
      mmap.add(entry.getKey(), entry.getValue());
    }
    return parse(mmap);
  }

  public ParsedQuery<T> createEmptyQuery() {
    return parse(new MultivaluedMapImpl());
  }

  public ParsedQuery<T> parse(MultivaluedMap<String, String> uriParams) {
    final Map<String, Object> fieldValues = new HashMap<>();
    final List<BoundFilterEntry<T>> boundFilterEntries = new ArrayList<>();

    final boolean includeDeleted = BooleanUtils.toBoolean(uriParams.getFirst("includeDeleted"));
    final Optional<Integer> limit = getLimit(uriParams.getFirst("limit"));
    final Optional<Integer> offset = getOffset(uriParams.getFirst("offset"));

    final Collection<Ordering> orderings = getOrderings(uriParams.get("order"), meta);
    orderings.addAll(getOrderings(uriParams.get("orderBy"), meta));

    final Table<BoundFilterEntry<T>, String, BeanPropertyDefinition> filterTable = meta.getFilterTable();
    final Map<String, BeanPropertyDefinition> fieldMap = meta.getFieldMap();

    for (Map.Entry<String, List<String>> entry : uriParams.entrySet()) {
      List<String> parts = FILTER_PARAM_SPLITTER.splitToList(entry.getKey().trim());
      if (parts.size() > 2) {
        continue;
      }

      String filterName = filterNameFromParts(parts);

      if (!byName.containsKey(filterName)) {
        throw new FilterViolation(String.format("Unknown filter type `%s`", filterName));
      }

      // If not in "strict mode", gracefully accept completely unknown query parameters
      String fieldName = parts.get(0);
      if (!fieldMap.keySet().contains(fieldName) && !strictMode) {
        String fieldNameSnakeCase = SNAKE_CASE_TRANSFORMER.apply(fieldName);
        if (!fieldMap.keySet().contains(fieldNameSnakeCase)) {
          continue;
        }
        fieldName = fieldNameSnakeCase;
      }

      FilterEntry filterEntry = new FilterEntry(byName.get(filterName), fieldName, this.getQueryType());

      // Use reserved words instead of simple look-up to throw exception on disallowed fields
      if (!reservedWords.contains(filterEntry.getQueryName()) && !filterTable.contains(filterEntry, filterName)) {
        throw new FilterViolation(String.format("Filtering by \"%s %s\" is not allowed",
            filterEntry.getQueryName(), filterName));
      } else if (reservedWords.contains(filterEntry.getQueryName())) {
        continue;
      }

      // Add value using key associated with the Type's field name, *not* the query parameter name.
      BeanPropertyDefinition prop = filterTable.get(filterEntry, filterName);
      BoundFilterEntry<T> boundColumn = filterTable.rowKeySet().stream()
          .filter(bfe -> bfe.equals(filterEntry)).findFirst()
          .orElseThrow(() -> new FilterViolation("Filter column " + filterEntry + " not found"));

      FilterBy ann = prop.getPrimaryMember().getAnnotation(FilterBy.class);
      if (Strings.emptyToNull(ann.as()) != null) {
        boundColumn.setActualField(fieldMap.get(ann.as()));
      }

      if (boundColumn.isMultiValue()) {
        List<String> paramVals = entry.getValue();
        // support for comma-delimited param values (e.g. name__in=bob,joe,sarah)
        if (paramVals.size() == 1 && paramVals.get(0).contains(",")) {
          paramVals = MULTIVALUE_PARAM_SPLITTER.splitToList(paramVals.get(0));
        }

        List<?> boundVals = paramVals.stream()
            .map(v -> Rosetta.getMapper().convertValue(v, prop.getField().getAnnotated().getType()))
            .collect(Collectors.toList());

        boundColumn = new MultiValuedBoundFilterEntry<T>(boundColumn, boundVals);

      } else {
        fieldValues.put(prop.getName(), entry.getValue().get(0));
      }

      boundFilterEntries.add(boundColumn);
    }

    try {
      T boundQuerySpec = mapper.convertValue(fieldValues, queryType);
      return new ParsedQuery<T>(boundQuerySpec, queryType, boundFilterEntries, meta, limit, offset, orderings, includeDeleted);

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
  Optional<Integer> getLimit(String limit) {
    if (limit != null) {
      try {
        int parsedLimit = Integer.parseInt(limit);
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
  Optional<Integer> getOffset(String offset) {
    if (offset != null) {
      try {
        int parsedOffset = Integer.parseInt(offset);
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

  private Collection<Ordering> getOrderings(List<String> orderStrings, DefaultMetaQuerySpec<T> meta) {
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

  private String filterNameFromParts(List<String> parts) {
    if (parts.size() == 1) {
      return (new Equal()).names()[0];
    } else {
      return parts.get(1);
    }
  }

  public Class<T> getQueryType() {
    return queryType;
  }

  public DefaultMetaQuerySpec<T> getMeta() {
    return meta;
  }

  public Collection<String> getOrderableFields() {
    return orderableFields;
  }

  public static class Builder<T extends QuerySpec> {
    private static final Table<Class<?>, Boolean, QueryParser<?>> CACHED_PARSERS = HashBasedTable.create();

    private Class<T> spec;
    private boolean strictMode;

    public Builder(Class<T> spec) {
      this.spec = spec;
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

      Map<String, BeanPropertyDefinition> fields = BeanPropertyIntrospector.getFields(spec);

      qp = new QueryParser<T>(spec, strictMode);
      qp.setConstraints(spec);
      qp.buildOrderableFields(fields);

      CACHED_PARSERS.put(spec, strictMode, qp);

      return qp;
    }
  }

  public static Filter named(String name) {
    return byName.get(name);
  }

}
