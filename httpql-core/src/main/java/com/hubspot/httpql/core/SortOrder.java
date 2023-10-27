package com.hubspot.httpql.core;

public enum SortOrder {

    /**
     * Ascending sort order.
     */
    ASC("asc"),

    /**
     * Descending sort order.
     */
    DESC("desc"),

    /**
     * Default sort order.
     */
    DEFAULT("");

    private final String sql;

    SortOrder(String sql) {
        this.sql = sql;
    }

    public final String toSQL() {
        return sql;
    }
}
