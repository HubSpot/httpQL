package com.hubspot.httpql.internal;

import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.hubspot.httpql.Filter;
import com.hubspot.httpql.MetaQuerySpec;
import com.hubspot.httpql.QuerySpec;

public class JoinedBoundFilterEntry<T extends QuerySpec> extends BoundFilterEntry<T> {

  public JoinedBoundFilterEntry(Filter filter, BeanPropertyDefinition prop, MetaQuerySpec<T> meta) {
    super(filter, prop, meta);
    // TODO Auto-generated constructor stub
  }

}
