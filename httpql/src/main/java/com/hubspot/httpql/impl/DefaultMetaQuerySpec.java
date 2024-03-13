package com.hubspot.httpql.impl;

import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.hubspot.httpql.DefaultMetaUtils;
import com.hubspot.httpql.FieldFactory;
import com.hubspot.httpql.Filters;
import com.hubspot.httpql.MetaQuerySpec;
import com.hubspot.httpql.QuerySpec;
import com.hubspot.httpql.ann.desc.JoinDescriptor;
import com.hubspot.httpql.core.filter.Filter;
import com.hubspot.httpql.error.FilterViolation;
import com.hubspot.httpql.error.UnknownFieldException;
import com.hubspot.httpql.impl.filter.FilterImpl;
import com.hubspot.httpql.internal.BoundFilterEntry;
import com.hubspot.httpql.internal.FilterEntry;
import com.hubspot.httpql.internal.JoinFilter;
import com.hubspot.httpql.jackson.BeanPropertyIntrospector;
import java.util.HashMap;
import java.util.Map;
import org.jooq.Field;
import org.jooq.impl.DSL;

public class DefaultMetaQuerySpec<T extends QuerySpec> implements MetaQuerySpec<T> {

  private final Map<String, BeanPropertyDefinition> fieldMap;
  private final Table<BoundFilterEntry<T>, String, BeanPropertyDefinition> filterTable;
  private final T instance;
  private final Class<T> specType;

  public DefaultMetaQuerySpec(Class<T> specType) {
    this.fieldMap = new HashMap<>();
    this.filterTable = HashBasedTable.create();
    this.specType = specType;
    try {
      this.instance = specType.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
    buildMetaData();
  }

  @Override
  public Class<?> getFieldType(String name) {
    name = DefaultMetaUtils.convertToSnakeCaseIfSupported(name, specType);
    Class<?> clz = fieldMap.get(name).getField().getAnnotated().getType();
    if (clz.isEnum()) {
      // TODO support @JsonValue. This assumes default jackson serialization.
      return String.class;
    }
    return clz;
  }

  @Override
  public Table<BoundFilterEntry<T>, String, BeanPropertyDefinition> getFilterTable() {
    return filterTable;
  }

  @Override
  public Map<String, BeanPropertyDefinition> getFieldMap() {
    return fieldMap;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <E> Field<E> createField(String name, FieldFactory fieldFactory) {
    String tableName = instance.tableName();
    org.jooq.Table<?> table = DSL.table(
      fieldFactory.tableAlias(tableName).orElse(tableName)
    );
    return fieldFactory.createField(name, (Class<E>) getFieldType(name), table);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Class<? extends Filter>[] getFiltersForField(String name) {
    BeanPropertyDefinition prop = fieldMap.get(name);
    if (prop != null) {
      return DefaultMetaUtils.getFilterByClasses(prop);
    }
    return new Class[] {};
  }

  private void buildMetaData() {
    final Map<String, BeanPropertyDefinition> fields = BeanPropertyIntrospector.getFields(
      specType
    );

    for (Map.Entry<String, BeanPropertyDefinition> entry : fields.entrySet()) {
      fieldMap.put(entry.getKey(), entry.getValue());
      filterTable.putAll(tableFor(entry.getValue(), getFiltersForField(entry.getKey())));
    }
  }

  @Override
  @SafeVarargs
  public final Table<BoundFilterEntry<T>, String, BeanPropertyDefinition> tableFor(
    BeanPropertyDefinition field,
    Class<? extends Filter>... filters
  ) {
    final Table<BoundFilterEntry<T>, String, BeanPropertyDefinition> table =
      HashBasedTable.create();

    JoinCondition join = null;

    FilterJoinInfo filterJoin = DefaultMetaUtils.findFilterJoin(field);
    if (filterJoin != null) {
      join =
        new JoinCondition(
          DSL.table(DSL.name(filterJoin.table())),
          DSL
            .field(DSL.name(instance.tableName(), filterJoin.on()))
            .eq(DSL.field(DSL.name(filterJoin.table(), filterJoin.eq())))
        );
    } else {
      JoinDescriptor joinDescriptor = DefaultMetaUtils.findJoinDescriptor(field);
      if (joinDescriptor != null) {
        join = joinDescriptor.getJoinCondition(instance);
      }
    }

    for (Class<? extends Filter> filterType : filters) {
      FilterImpl filter = DefaultMetaUtils.getFilterInstance(filterType);
      String[] filterNames = Filters.getFilterNames(filter);
      if (join != null) {
        filter = new JoinFilter(filter, join);
      }

      BoundFilterEntry<T> fe = new BoundFilterEntry<>(filter, field, this);

      for (String filterName : filterNames) {
        table.put(fe, filterName, field);
      }
    }
    return table;
  }

  @Override
  public BoundFilterEntry<T> getNewBoundFilterEntry(
    String fieldName,
    Class<? extends Filter> filterType
  ) {
    // Filter can be null; we only want FilterEntry for name normalization
    FilterImpl filter = DefaultMetaUtils.getFilterInstance(filterType);
    FilterEntry filterEntry = new FilterEntry(filter, fieldName, getQueryType());
    BeanPropertyDefinition filterProperty = getFilterProperty(fieldName, filterType);
    if (filterProperty == null) {
      throw new UnknownFieldException(
        String.format(
          "No filter %s on field named '%s' exists.",
          Filters.getFilterNames(filter)[0],
          fieldName
        )
      );
    }
    BoundFilterEntry<T> boundColumn = filterTable
      .rowKeySet()
      .stream()
      .filter(bfe -> bfe.equals(filterEntry))
      .findFirst()
      .orElseThrow(() ->
        new FilterViolation("Filter column " + filterEntry + " not found")
      );
    String as = DefaultMetaUtils.getFilterByAs(filterProperty.getPrimaryMember());
    if (as != null) {
      boundColumn.setActualField(getFieldMap().get(as));
    }
    return boundColumn;
  }

  @Override
  public BeanPropertyDefinition getFilterProperty(
    String fieldName,
    Class<? extends Filter> filterType
  ) {
    // Filter can be null; we only want FilterEntry for name normalization
    FilterImpl filter = DefaultMetaUtils.getFilterInstance(filterType);
    FilterEntry filterEntry = new FilterEntry(filter, fieldName, getQueryType());
    final Table<BoundFilterEntry<T>, String, BeanPropertyDefinition> filterTable =
      getFilterTable();
    BeanPropertyDefinition filterProperty = filterTable.get(
      filterEntry,
      Filters.getFilterNames(filter)[0]
    );
    if (filterProperty == null) {
      throw new UnknownFieldException(
        String.format(
          "No filter %s on field named '%s' exists.",
          Filters.getFilterNames(filter)[0],
          fieldName
        )
      );
    }
    return filterProperty;
  }

  @Override
  public Class<T> getQueryType() {
    return specType;
  }
}
