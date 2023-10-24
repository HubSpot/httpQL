package com.hubspot.httpql.lib.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ParsedUriParams {

  private List<String> orderBys = new ArrayList<>();
  private Optional<Integer> offset = Optional.empty();
  private Optional<Integer> limit = Optional.empty();
  private boolean includeDeleted;

  private final List<FieldFilter> fieldFilters = new ArrayList<>();

  public List<String> getOrderBys() {
    return orderBys;
  }

  public void setOrderBys(List<String> orderBys) {
    this.orderBys = orderBys;
  }

  public Optional<Integer> getOffset() {
    return offset;
  }

  public void setOffset(int offset) {
    this.offset = Optional.of(offset);
  }

  public Optional<Integer> getLimit() {
    return limit;
  }

  public void setLimit(int limit) {
    this.limit = Optional.of(limit);
  }

  public boolean isIncludeDeleted() {
    return includeDeleted;
  }

  public void setIncludeDeleted(boolean includeDeleted) {
    this.includeDeleted = includeDeleted;
  }

  public List<FieldFilter> getFieldFilters() {
    return fieldFilters;
  }

  public void addOrderBys(List<String> orders) {
    if (orders == null) {
      return;
    }

    orderBys.addAll(orders);
  }

  public void addFieldFilter(FieldFilter fieldFilter) {
    fieldFilters.add(fieldFilter);
  }

  @Override
  public String toString() {
    return "ParsedUriParams{" +
        "orderBys=" + orderBys +
        ", offset=" + offset +
        ", limit=" + limit +
        ", includeDeleted=" + includeDeleted +
        ", fieldFilters=" + fieldFilters +
        '}';
  }
}
