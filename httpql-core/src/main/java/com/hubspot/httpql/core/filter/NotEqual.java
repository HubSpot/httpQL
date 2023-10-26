package com.hubspot.httpql.core.filter;

public class NotEqual implements Filter {

    @Override
    public String[] names() {
        return new String[] {
                "ne", "neq", "not"
        };
    }

}
