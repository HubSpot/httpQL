package com.hubspot.httpql.internal;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.google.common.base.Strings;
import com.hubspot.httpql.ConditionProvider;
import com.hubspot.httpql.DefaultMetaUtils;
import com.hubspot.httpql.FieldFactory;
import com.hubspot.httpql.Filter;
import com.hubspot.httpql.MetaQuerySpec;
import com.hubspot.httpql.MultiParamConditionProvider;
import com.hubspot.httpql.QuerySpec;
import com.hubspot.httpql.ann.FilterBy;
import com.hubspot.httpql.ann.FilterJoin;
import com.hubspot.httpql.ann.desc.JoinDescriptor;
import com.hubspot.httpql.impl.DefaultFieldFactory;

public class BoundFilterEntry<T extends QuerySpec>extends FilterEntry {

  private final BeanPropertyDefinition prop;
  private final MetaQuerySpec<T> meta;
  private BeanPropertyDefinition actualField;

  public BoundFilterEntry(Filter filter, String fieldName, String queryName, BeanPropertyDefinition prop, MetaQuerySpec<T> meta) {
    super(filter, fieldName, queryName, meta.getQueryType());
    this.prop = prop;
    this.meta = meta;
  }

  public BoundFilterEntry(Filter filter, BeanPropertyDefinition prop, MetaQuerySpec<T> meta) {
    super(filter, prop.getName(), getBestQueryName(prop), meta.getQueryType());
    this.prop = prop;
    this.meta = meta;
  }

  public BoundFilterEntry(FilterEntry entry, BeanPropertyDefinition prop, MetaQuerySpec<T> meta) {
    this(entry.getFilter(), prop, meta);
  }

  public boolean isMultiValue() {
    return getConditionProvider(new DefaultFieldFactory()) instanceof MultiParamConditionProvider;
  }

  private static String getBestQueryName(BeanPropertyDefinition prop) {
    FilterBy ann = DefaultMetaUtils.findFilterBy(prop);
    return Strings.emptyToNull(ann.as()) != null ? ann.as() : prop.getName();
  }

  public BeanPropertyDefinition getProperty() {
    return prop;
  }

  public MetaQuerySpec<T> getMeta() {
    return meta;
  }

  public Class<?> getFieldType() {
    return (actualField != null ? actualField : prop).getField().getAnnotated().getType();
  }

  public ConditionProvider<?> getConditionProvider(FieldFactory fieldFactory) {
    Field<?> field;

    FilterJoin join = DefaultMetaUtils.findFilterJoin(prop);
    JoinDescriptor joinDescriptor = DefaultMetaUtils.findJoinDescriptor(prop);
    if (join != null) {
      field = DSL.field(DSL.name(join.table(), getQueryName()));
    } else if (joinDescriptor != null) {
      field = joinDescriptor.getField(this);
    } else {
      field = meta.createField(getQueryName(), fieldFactory);
    }

    return getFilter().getConditionProvider(field);
  }

  public Condition getCondition(QuerySpec value, FieldFactory fieldFactory) {
    return getConditionProvider(fieldFactory).getCondition(getProperty().getGetter().getValue(value), getProperty().getName());
  }

  public void setActualField(BeanPropertyDefinition actualField) {
    this.actualField = actualField;
  }

}
