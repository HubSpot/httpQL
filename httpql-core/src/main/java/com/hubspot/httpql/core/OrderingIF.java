package com.hubspot.httpql.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public interface OrderingIF {

    String getQueryName();

    String getFieldName();

    int getSortOrdinal();

    /**
     * @return either "asc" or "desc"
     */
    String getOrderString();

    @JsonCreator
    static Ordering fromString(String ordering) {
        SortOrder order;
        if (ordering.startsWith("-")) {
            ordering = ordering.substring(1);
            order = SortOrder.DESC;
        } else {
            order = SortOrder.ASC;
        }
        return new Ordering(ordering, order);
    }

    @JsonValue
    String jsonValue();

}
