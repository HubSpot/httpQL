package com.hubspot.httpql.impl;

import javax.annotation.Nullable;

public class FilterJoinInfo {

    private final String on;
    private final String table;
    private final String eq;

    @Nullable
    public static FilterJoinInfo of(@Nullable com.hubspot.httpql.ann.FilterJoin filterJoin) {
        if (filterJoin == null) {
            return null;
        }
        return new FilterJoinInfo(filterJoin.on(), filterJoin.table(), filterJoin.eq());
    }

    @Nullable
    public static FilterJoinInfo of(@Nullable com.hubspot.httpql.core.ann.FilterJoin filterJoin) {
        if (filterJoin == null) {
            return null;
        }
        return new FilterJoinInfo(filterJoin.on(), filterJoin.table(), filterJoin.eq());
    }

    private FilterJoinInfo(String on, String table, String eq) {
        this.on = on;
        this.table = table;
        this.eq = eq;
    }
    public String on() {
        return on;
    }

    public String table() {
        return table;
    }

    public String eq() {
        return eq;
    }
}
