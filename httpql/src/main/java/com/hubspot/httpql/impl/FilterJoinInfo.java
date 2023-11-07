package com.hubspot.httpql.impl;

import com.hubspot.httpql.ann.FilterJoin;

import javax.annotation.Nullable;

public class FilterJoinInfo {

    private final String on;
    private final String table;
    private final String eq;

    @Nullable
    public static FilterJoinInfo of(@Nullable  FilterJoin filterJoin) {
        if (filterJoin == null) {
            return null;
        }
        return new FilterJoinInfo(filterJoin);
    }

    @Nullable
    public static FilterJoinInfo of(@Nullable com.hubspot.httpql.core.ann.FilterJoin filterJoin) {
        if (filterJoin == null) {
            return null;
        }
        return new FilterJoinInfo(filterJoin);
    }

    private FilterJoinInfo(FilterJoin filterJoin) {
        this.on = filterJoin.on();
        this.table = filterJoin.table();
        this.eq = filterJoin.eq();
    }

    private FilterJoinInfo(com.hubspot.httpql.core.ann.FilterJoin filterJoin) {
        this.on = filterJoin.on();
        this.table = filterJoin.table();
        this.eq = filterJoin.eq();
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
