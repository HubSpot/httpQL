package com.hubspot.httpql.core.filter;

/**
 * Marker class for filters. Actual implementation should be added in an *Impl class in another module and reference this class with the getAnnotationClass() method
 */
public interface Filter {

    String[] names();

    default boolean takesMultiParameters() {
        return false;
    }

}
