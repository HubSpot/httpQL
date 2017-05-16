package com.hubspot.httpql.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.jooq.impl.DSL;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.hubspot.httpql.ConditionProvider;
import com.hubspot.httpql.Filter;
import com.hubspot.httpql.MultiParamConditionProvider;
import com.hubspot.httpql.error.FilterViolation;
import com.hubspot.httpql.filter.Equal;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class UriParamParser {

  public static final Map<String, Filter> BY_NAME = new HashMap<>();
  private static final ServiceLoader<Filter> LOADER = ServiceLoader.load(Filter.class);

  private static final Splitter FILTER_PARAM_SPLITTER = Splitter.on("__").trimResults();
  private static final Splitter MULTIVALUE_PARAM_SPLITTER = Splitter.on(",").omitEmptyStrings().trimResults();

  static {
    for (Filter filter : LOADER) {
      for (String name : filter.names()) {
        BY_NAME.put(name, filter);
      }
    }
  }

  private final Set<String> ignoredParams;

  protected UriParamParser(Set<String> ignoredParams) {
    this.ignoredParams = ignoredParams;
  }

  public Set<String> getIgnoredParams() {
    return ignoredParams;
  }

  public MultivaluedMap<String, String> multimapToMultivaluedMap(Multimap<String, String> map) {

    MultivaluedMap<String, String> result = new MultivaluedMapImpl();
    for (Map.Entry<String, String> entry : map.entries()) {
      result.add(entry.getKey(), entry.getValue());
    }

    return result;
  }

  public ParsedUriParams parseUriParams(Multimap<String, String> uriParams) {
    return parseUriParams(multimapToMultivaluedMap(uriParams));
  }

  @SuppressWarnings("rawtypes")
  public ParsedUriParams parseUriParams(MultivaluedMap<String, String> uriParams) {

    final ParsedUriParams result = new ParsedUriParams();

    // make a copy so we can modify it
    MultivaluedMap<String, String> params = new MultivaluedMapImpl();
    for (Map.Entry<String, List<String>> entry : uriParams.entrySet()) {
      if (!ignoredParams.contains(entry.getKey().toLowerCase())) {
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

  private static String filterNameFromParts(List<String> parts) {
    if (parts.size() == 1) {
      return (new Equal()).names()[0];
    } else {
      return parts.get(1);
    }
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

}
