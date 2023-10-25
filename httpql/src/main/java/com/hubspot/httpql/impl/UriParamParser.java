package com.hubspot.httpql.impl;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.hubspot.httpql.ConditionProvider;
import com.hubspot.httpql.Filter;
import com.hubspot.httpql.Filters;
import com.hubspot.httpql.MultiParamConditionProvider;
import com.hubspot.httpql.error.FilterViolation;
import com.hubspot.httpql.filter.Equal;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.jooq.impl.DSL;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class UriParamParser {


  private static final String FILTER_PARAM_DELIMITER = "__";
  private static final Splitter FILTER_PARAM_SPLITTER = Splitter.on(FILTER_PARAM_DELIMITER).trimResults();
  private static final Joiner FILTER_PARAM_JOINER = Joiner.on(FILTER_PARAM_DELIMITER);
  private static final Splitter MULTIVALUE_PARAM_SPLITTER = Splitter.on(",").omitEmptyStrings().trimResults();

  private final Set<String> ignoredParams;

  protected UriParamParser(Set<String> ignoredParams) {
    this.ignoredParams = ignoredParams;
  }

  public Set<String> getIgnoredParams() {
    return ignoredParams;
  }

  public Map<String, List<String>> multimapToMultivaluedMap(Multimap<String, String> map) {
    Map<String, List<String>> result = new HashMap<>();
    map.asMap().forEach((key, value) -> result.put(key, new ArrayList<>(value)));
    return result;
  }

  public ParsedUriParams parseUriParams(Multimap<String, String> uriParams) {
    return parseUriParams(multimapToMultivaluedMap(uriParams));
  }

  public ParsedUriParams parseUriParams(Map<String, List<String>> uriParams) {
    return parseUriParams(uriParams, false);
  }

  @SuppressWarnings("rawtypes")
  public ParsedUriParams parseUriParams(Map<String, List<String>> uriParams, boolean allowDoubleUnderscoreInFieldName) {

    final ParsedUriParams result = new ParsedUriParams();

    // make a copy so we can modify it
    Map<String, List<String>> params = new HashMap<>();
    for (Map.Entry<String, List<String>> entry : uriParams.entrySet()) {
      if (!ignoredParams.contains(entry.getKey().toLowerCase())) {
        params.put(entry.getKey(), entry.getValue());
      }
    }

    result.setIncludeDeleted(BooleanUtils.toBoolean(getFirst(params, "includeDeleted")));
    params.remove("includeDeleted");

    final int limit = NumberUtils.toInt(getFirst(params, "limit"), 0);
    if (limit != 0) {
      result.setLimit(limit);
    }
    params.remove("limit");

    final int offset = NumberUtils.toInt(getFirst(params, "offset"), 0);
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
      if (parts.size() > 2 && !allowDoubleUnderscoreInFieldName) {
        continue;
      }

      final String fieldName = fieldNameFromParts(parts, allowDoubleUnderscoreInFieldName);
      final String filterName = filterNameFromParts(parts);
      final Optional<Filter> filter = Filters.getFilterByName(filterName);

      if (filter.isEmpty()) {
        throw new FilterViolation(String.format("Unknown filter type `%s`", filterName));
      }

      List<String> values = entry.getValue();
      ConditionProvider conditionProvider = filter.get().getConditionProvider(DSL.field(fieldName));

      if (conditionProvider instanceof MultiParamConditionProvider && values.size() == 1 && values.get(0).contains(",")) {
        values = MULTIVALUE_PARAM_SPLITTER.splitToList(values.get(0));
      }

      result.addFieldFilter(new FieldFilter(filter.get(), filterName, fieldName, values));
    }

    return result;
  }

  private static String filterNameFromParts(List<String> parts) {
    if (parts.size() == 1) {
      return (new Equal()).names()[0];
    } else {
      return parts.get(parts.size() - 1);
    }
  }

  private static String fieldNameFromParts(List<String> parts, boolean allowDoubleUnderscoreInFieldName) {
    if (!allowDoubleUnderscoreInFieldName) {
      return parts.get(0);
    }

    if (Filters.getFilterImplByName(parts.get(parts.size() - 1)).isPresent()) {
      List<String> partsCopy = new ArrayList<>(parts);
      partsCopy.remove(parts.size() - 1);
      return FILTER_PARAM_JOINER.join(partsCopy);
    }

    return FILTER_PARAM_JOINER.join(parts);
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    protected Set<String> ignoredParams;

    /**
     * In Strict Mode, the parser will throw an Exception when an unknown query parameter is found, not only when a known field is not allowed to have the specified filter applied.
     * <p>
     * Defaults to OFF.
     */
    public Builder withIgnoredParams(Set<String> ignoredParams) {
      this.ignoredParams = ignoredParams;
      return this;
    }

    public UriParamParser build() {

      if (ignoredParams == null) {
        ignoredParams = ImmutableSet.of();
      }

      return new UriParamParser(ignoredParams);
    }
  }

  @Nullable
  private static String getFirst(Map<String, List<String>> map, String key) {
    return Optional.ofNullable(map.get(key))
        .filter(list -> !list.isEmpty())
        .map(list -> list.get(0))
        .orElse(null);
  }
}
