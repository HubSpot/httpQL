package com.hubspot.httpql.core;

public interface OrderingIF {

    String getQueryName();

    String getFieldName();

    int getSortOrdinal();

    String getOrderString();
}
