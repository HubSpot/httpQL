package com.hubspot.httpql.jersey;

import com.hubspot.httpql.ParsedQuery;
import com.hubspot.httpql.QuerySpec;
import com.hubspot.httpql.impl.QueryParser;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;

public class BindQueryInjectableProvider implements InjectableProvider<BindQuery, Parameter> {

  @Override
  public ComponentScope getScope() {
    return ComponentScope.PerRequest;
  }

  @Override
  public Injectable<ParsedQuery<? extends QuerySpec>> getInjectable(ComponentContext ic, final BindQuery a, Parameter c) {
    return new AbstractHttpContextInjectable<ParsedQuery<? extends QuerySpec>>() {
      @Override
      public ParsedQuery<? extends QuerySpec> getValue(HttpContext context) {
        return QueryParser.newBuilder(a.value()).build().parse(context.getUriInfo().getQueryParameters());
      }
    };
  }

}
