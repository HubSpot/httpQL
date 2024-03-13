package com.hubspot.httpql;

import com.hubspot.httpql.core.filter.Filter;
import com.hubspot.httpql.impl.filter.FilterImpl;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;

public class Filters {

  private static final Map<String, com.hubspot.httpql.Filter> FILTERS_BY_NAME =
    new HashMap<>();
  private static final Map<String, FilterImpl> FILTER_IMPLS_BY_NAME = new HashMap<>();

  private static final Map<FilterImpl, String[]> FILTER_NAMES_BY_IMPL = new HashMap<>();
  private static final ServiceLoader<com.hubspot.httpql.Filter> FILTER_LOADER =
    ServiceLoader.load(com.hubspot.httpql.Filter.class);
  private static final ServiceLoader<FilterImpl> FILTER_IMPL_LOADER = ServiceLoader.load(
    FilterImpl.class
  );
  private static final Map<Class<? extends Filter>, FilterImpl> FILTER_IMPLS =
    new HashMap<>();

  static {
    for (com.hubspot.httpql.Filter filter : FILTER_LOADER) {
      for (String name : filter.names()) {
        FILTERS_BY_NAME.put(name, filter);
      }
    }

    for (FilterImpl filterImpl : FILTER_IMPL_LOADER) {
      for (Class<? extends Filter> annotationClass : filterImpl.getAnnotationClasses()) {
        try {
          Filter filterIF = annotationClass.getDeclaredConstructor().newInstance();
          for (String name : filterIF.names()) {
            FILTER_IMPLS_BY_NAME.put(name, filterImpl);
          }
          FILTER_NAMES_BY_IMPL.put(filterImpl, filterIF.names());
        } catch (
          InstantiationException
          | IllegalAccessException
          | InvocationTargetException
          | NoSuchMethodException e
        ) {
          throw new RuntimeException(e);
        }
        filterImpl.getAnnotationClasses().forEach(f -> FILTER_IMPLS.put(f, filterImpl));
      }
    }
  }

  public static Optional<FilterImpl> getFilterImplByName(String name) {
    return Optional.ofNullable(FILTER_IMPLS_BY_NAME.get(name));
  }

  public static String[] getFilterNames(FilterImpl filter) {
    return FILTER_NAMES_BY_IMPL.get(filter);
  }

  public static Optional<FilterImpl> getFilterImpl(Class<? extends Filter> filter) {
    return Optional.ofNullable(FILTER_IMPLS.get(filter));
  }

  @Deprecated
  public static Optional<com.hubspot.httpql.Filter> getFilterByName(String name) {
    return Optional.ofNullable(FILTERS_BY_NAME.get(name));
  }
}
