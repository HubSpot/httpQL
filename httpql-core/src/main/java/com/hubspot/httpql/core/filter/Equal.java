package com.hubspot.httpql.core.filter;

public class Equal implements Filter {
    @Override
    public String[] names() {
        return new String[] {
                "eq", "exact", "is"
        };
    }

}
