package com.hubspot.httpql.internal;

import com.google.common.base.Objects;
import com.hubspot.httpql.DefaultMetaUtils;
import com.hubspot.httpql.impl.filter.FilterImpl;

public class FilterEntry {

  private final String queryName;
  private final String fieldName;
  private final FilterImpl filter;

  public FilterEntry(FilterImpl filter, String queryName, Class<?> queryType) { // Only valid for comparison
    this(filter, queryName, queryName, queryType);
  }

  public FilterEntry(
    FilterImpl filter,
    String fieldName,
    String queryName,
    Class<?> queryType
  ) {
    this.filter = filter;
    this.fieldName = fieldName;
    this.queryName = normalizeQueryName(queryName, queryType);
  }

  private String normalizeQueryName(String queryName, Class<?> queryType) {
    return DefaultMetaUtils.convertToSnakeCaseIfSupported(queryName, queryType);
  }

  public String getQueryName() {
    return queryName;
  }

  public String getFieldName() {
    return fieldName;
  }

  public FilterImpl getFilter() {
    return filter;
  }

  @Override
  public boolean equals(Object other) {
    FilterEntry fe = (FilterEntry) other;

    return (
      Objects.equal(getQueryName(), fe.getQueryName()) &&
      Objects.equal(getFilter(), fe.getFilter())
    );
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getQueryName(), getFilter());
  }
}
