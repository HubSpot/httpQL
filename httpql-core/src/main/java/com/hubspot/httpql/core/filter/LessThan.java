package com.hubspot.httpql.core.filter;

public class LessThan implements Filter {

    @Override
    public String[] names() {
        return new String[] {
                "lt"
        };
    }

}
