package com.hubspot.httpql.lib.internal;

import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.hubspot.httpql.core.FilterEntry;
import com.hubspot.httpql.core.ann.FilterBy;
import com.hubspot.httpql.core.ann.FilterJoin;
import com.hubspot.httpql.core.filter.Filter;
import com.hubspot.httpql.lib.ConditionProvider;
import com.hubspot.httpql.lib.DefaultMetaUtils;
import com.hubspot.httpql.lib.FieldFactory;
import com.hubspot.httpql.lib.MetaQuerySpec;
import com.hubspot.httpql.lib.MultiParamConditionProvider;
import com.hubspot.httpql.lib.QuerySpec;
import com.hubspot.httpql.lib.filter.FilterImpl;
import com.hubspot.httpql.lib.impl.DefaultFieldFactory;
import com.hubspot.httpql.lib.impl.JoinDescriptorImpl;
import java.util.Collection;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

public class BoundFilterEntry<T extends QuerySpec> extends FilterEntry implements FilterEntryConditionCreator<T> {

  private final BeanPropertyDefinition prop;
  private final MetaQuerySpec<T> meta;
  private BeanPropertyDefinition actualField;

  private final FilterImpl filterImpl;

  public BoundFilterEntry(Filter filter, FilterImpl filterImpl, String fieldName, String queryName, BeanPropertyDefinition prop, MetaQuerySpec<T> meta) {
    super(filter, fieldName, queryName, meta.getQueryType());
    this.prop = prop;
    this.meta = meta;
    this.filterImpl = filterImpl;
  }

  public BoundFilterEntry(Filter filter, FilterImpl filterImpl, BeanPropertyDefinition prop, MetaQuerySpec<T> meta) {
    super(filter, prop.getName(), getBestQueryName(prop), meta.getQueryType());
    this.prop = prop;
    this.meta = meta;
    this.filterImpl = filterImpl;
  }

  public BoundFilterEntry(FilterEntry entry, FilterImpl filterImpl, BeanPropertyDefinition prop, MetaQuerySpec<T> meta) {
    this(entry.getFilter(), filterImpl, prop, meta);
  }

  public boolean isMultiValue() {
    return getConditionProvider(new DefaultFieldFactory()) instanceof MultiParamConditionProvider;
  }

  private static String getBestQueryName(BeanPropertyDefinition prop) {
    FilterBy ann = DefaultMetaUtils.findFilterBy(prop);
    return Strings.emptyToNull(ann == null ? "" : ann.as()) != null ? ann.as() : prop.getName();
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
    JoinDescriptorImpl joinDescriptor = DefaultMetaUtils.findJoinDescriptor(prop);
    if (join != null) {
      field = DSL.field(DSL.name(join.table(), getQueryName()));
    } else if (joinDescriptor != null) {
      field = joinDescriptor.getField(this);
    } else {
      field = meta.createField(getQueryName(), fieldFactory);
    }

    return filterImpl.getConditionProvider(field);
  }

  public FilterImpl getFilterImpl() {
    return filterImpl;
  }

  @Override
  public Condition getCondition(QuerySpec value, FieldFactory fieldFactory) {
    return getConditionProvider(fieldFactory).getCondition(getProperty().getGetter().getValue(value), getProperty().getName());
  }

  public void setActualField(BeanPropertyDefinition actualField) {
    this.actualField = actualField;
  }

  @Override
  public Collection<BoundFilterEntry<T>> getFlattenedBoundFilterEntries() {
    return ImmutableSet.of(this);
  }
}
