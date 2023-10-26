package com.hubspot.httpql.core.filter;

public class Contains implements Filter {

    @Override
    public String[] names() {
        return new String[] {
                "contains", "like"
        };
    }
}
