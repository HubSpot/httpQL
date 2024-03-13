package com.hubspot.httpql.impl;

import com.google.common.collect.ImmutableList;
import com.hubspot.httpql.Filter;
import java.util.List;
import java.util.stream.Collectors;

public class FieldFilter {

  private Filter filter;
  private String field;
  private List<String> values;
  private String filterName;

  public FieldFilter(
    Filter filter,
    String filterName,
    String field,
    List<String> values
  ) {
    this.filter = filter;
    this.filterName = filterName;
    this.field = field;
    this.values = values;
  }

  public FieldFilter(Filter filter, String filterName, String field, String value) {
    this.filter = filter;
    this.filterName = filterName;
    this.field = field;
    this.values = ImmutableList.of(value);
  }

  public Filter getFilter() {
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
    return (
      "FieldFilter{" +
      "filter=" +
      filter +
      ", field='" +
      field +
      '\'' +
      ", values=" +
      values +
      ", filterName='" +
      filterName +
      '\'' +
      '}'
    );
  }

  public String toQueryParam() {
    return (
      this.field +
      "__" +
      filterName +
      "=" +
      values.stream().collect(Collectors.joining(","))
    );
  }
}
