package com.hubspot.httpql.lib.impl;

import com.google.common.collect.ImmutableList;
import com.hubspot.httpql.lib.filter.FilterImpl;
import java.util.List;

public class FieldFilter {

  private final FilterImpl filter;
  private final String field;
  private final List<String> values;
  private final String filterName;

  public FieldFilter(FilterImpl filter, String filterName, String field, List<String> values) {
    this.filter = filter;
    this.filterName = filterName;
    this.field = field;
    this.values = values;
  }

  public FieldFilter(FilterImpl filter, String filterName, String field, String value) {
    this.filter = filter;
    this.filterName = filterName;
    this.field = field;
    this.values = ImmutableList.of(value);
  }

  public FilterImpl getFilter() {
    return filter;
  }

  public String getField() {
    return field;
  }

  public List<String> getValues() {
    return values;
  }

  public String getValue() {
    if (values.isEmpty()) {
      return null;
    }
    return values.get(0);
  }

  public String getFilterName() {
    return filterName;
  }

  @Override
  public String toString() {
    return "FieldFilter{" +
        "filter=" + filter +
        ", field='" + field + '\'' +
        ", values=" + values +
        ", filterName='" + filterName + '\'' +
        '}';
  }

  public String toQueryParam() {
    return this.field + "__" + filterName + "=" + String.join(",", values);
  }
}
