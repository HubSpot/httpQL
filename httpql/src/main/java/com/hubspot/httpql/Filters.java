package com.hubspot.httpql;

import com.hubspot.httpql.core.filter.FilterIF;
import com.hubspot.httpql.impl.filter.FilterImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;

public class Filters {

    private static final Map<String, Filter> FILTERS_BY_NAME = new HashMap<>();
    private static final Map<String, FilterImpl> FILTER_IMPLS_BY_NAME = new HashMap<>();
    private static final ServiceLoader<Filter> FILTER_LOADER = ServiceLoader.load(Filter.class);
    private static final ServiceLoader<FilterImpl> FILTER_IMPL_LOADER = ServiceLoader.load(FilterImpl.class);
    private static final Map<Class<? extends FilterIF>, FilterImpl> FILTER_IMPLS = new HashMap<>();

    static {
        for (Filter filter : FILTER_LOADER) {
            for (String name : filter.names()) {
                FILTERS_BY_NAME.put(name, filter);
            }
        }

        for (FilterImpl filterImpl : FILTER_IMPL_LOADER) {
            for (String name : filterImpl.names()) {
                FILTER_IMPLS_BY_NAME.put(name, filterImpl);
            }
            filterImpl.getAnnotationClasses().forEach(f -> FILTER_IMPLS.put(f, filterImpl));
        }
    }

    public static Optional<FilterImpl> getFilterImplByName(String name) {
        return Optional.ofNullable(FILTER_IMPLS_BY_NAME.get(name));
    }

    public static Optional<FilterImpl> getFilterImpl(Class<? extends FilterIF> filter) {
        return Optional.ofNullable(FILTER_IMPLS.get(filter));
    }

    @Deprecated
    public static Optional<Filter> getFilterByName(String name) {
        return Optional.ofNullable(FILTERS_BY_NAME.get(name));
    }
}
