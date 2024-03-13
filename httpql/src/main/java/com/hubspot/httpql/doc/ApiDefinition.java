package com.hubspot.httpql.doc;

import com.hubspot.httpql.Filters;
import com.hubspot.httpql.impl.QueryParser;
import com.hubspot.httpql.internal.BoundFilterEntry;
import java.util.Collection;
import java.util.Objects;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class ApiDefinition {

  private final SortedMap<String, FilterableField> fields;

  public ApiDefinition(QueryParser<?> queryParser) {
    this.fields = new TreeMap<>();

    for (BoundFilterEntry<?> filterEntry : queryParser
      .getMeta()
      .getFilterTable()
      .rowKeySet()) {
      FilterableField f = fields.get(filterEntry.getFieldName());

      if (f == null) {
        f =
          new FilterableField(
            filterEntry.getFieldName(),
            filterEntry.getFieldType(),
            queryParser.getOrderableFields().contains(filterEntry.getFieldName())
          );
        fields.put(filterEntry.getFieldName(), f);
      }

      for (String filterOp : Filters.getFilterNames(filterEntry.getFilter())) {
        f.addFilter(filterOp);
      }
    }
  }

  public Collection<FilterableField> getFields() {
    return fields.values();
  }

  static class FilterableField implements Comparable<FilterableField> {

    private final String field;
    private final Class<?> fieldType;
    private final boolean sortable;

    private final SortedSet<String> filters;

    public FilterableField(String field, Class<?> fieldType, boolean sortable) {
      this.field = field;
      this.fieldType = fieldType;
      this.sortable = sortable;

      this.filters = new TreeSet<>();
    }

    public String getField() {
      return field;
    }

    public Class<?> getFieldType() {
      return fieldType;
    }

    public void addFilter(String filter) {
      filters.add(filter);
    }

    public SortedSet<String> getFilters() {
      return filters;
    }

    public boolean isSortable() {
      return sortable;
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof FilterableField)) {
        return false;
      }

      FilterableField f = (FilterableField) obj;
      return Objects.equals(field, f.field);
    }

    @Override
    public int hashCode() {
      return Objects.hash(field);
    }

    @Override
    public int compareTo(FilterableField o) {
      return field.compareTo(o.field);
    }
  }
}
