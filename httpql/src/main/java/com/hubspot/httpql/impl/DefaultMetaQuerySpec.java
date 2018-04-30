package com.hubspot.httpql.impl;

import java.util.HashMap;
import java.util.Map;

import org.jooq.Field;
import org.jooq.impl.DSL;

import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.google.common.base.Throwables;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.hubspot.httpql.DefaultMetaUtils;
import com.hubspot.httpql.FieldFactory;
import com.hubspot.httpql.Filter;
import com.hubspot.httpql.MetaQuerySpec;
import com.hubspot.httpql.QuerySpec;
import com.hubspot.httpql.ann.FilterBy;
import com.hubspot.httpql.ann.FilterJoin;
import com.hubspot.httpql.ann.desc.JoinDescriptor;
import com.hubspot.httpql.internal.BoundFilterEntry;
import com.hubspot.httpql.internal.JoinFilter;
import com.hubspot.httpql.jackson.BeanPropertyIntrospector;

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
      throw Throwables.propagate(e);
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
    org.jooq.Table<?> table = DSL.table(fieldFactory.tableAlias(tableName).orElse(tableName));
    return fieldFactory.createField(name, (Class<E>) getFieldType(name), table);
  }

  @Override
  @SuppressWarnings("unchecked")
  // Unchecked is an empty array.
  public Class<? extends Filter>[] getFiltersForField(String name) {
    BeanPropertyDefinition prop = fieldMap.get(name);
    if (prop != null) {
      FilterBy ann = DefaultMetaUtils.findFilterBy(prop);
      if (ann != null) {
        return ann.value();
      }
    }
    return new Class[] {};
  }

  private void buildMetaData() {
    final Map<String, BeanPropertyDefinition> fields = BeanPropertyIntrospector.getFields(specType);

    for (Map.Entry<String, BeanPropertyDefinition> entry : fields.entrySet()) {
      fieldMap.put(entry.getKey(), entry.getValue());
      filterTable.putAll(tableFor(entry.getValue(), getFiltersForField(entry.getKey())));
    }
  }

  @Override
  @SafeVarargs
  public final Table<BoundFilterEntry<T>, String, BeanPropertyDefinition> tableFor(BeanPropertyDefinition field,
                                                                                   Class<? extends Filter>... filters) {

    final Table<BoundFilterEntry<T>, String, BeanPropertyDefinition> table = HashBasedTable.create();

    JoinCondition join = null;

    FilterJoin filterJoin = DefaultMetaUtils.findFilterJoin(field);
    if (filterJoin != null) {
      join = new JoinCondition(DSL.table(DSL.name(filterJoin.table())),
          DSL.field(DSL.name(instance.tableName(), filterJoin.on()))
              .eq(DSL.field(DSL.name(filterJoin.table(), filterJoin.eq()))));
    } else {
      JoinDescriptor joinDescriptor = DefaultMetaUtils.findJoinDescriptor(field);
      if (joinDescriptor != null) {
        join = joinDescriptor.getJoinCondition(instance);
      }
    }

    for (Class<? extends Filter> filterType : filters) {
      assert Filter.class.isAssignableFrom(filterType);

      Filter filter = DefaultMetaUtils.getFilterInstance(filterType);
      if (join != null) {
        filter = new JoinFilter(filter, join);
      }

      BoundFilterEntry<T> fe = new BoundFilterEntry<>(filter, field, this);

      for (String filterName : filter.names()) {
        table.put(fe, filterName, field);
      }
    }
    return table;
  }

  @Override
  public Class<T> getQueryType() {
    return specType;
  }

}
