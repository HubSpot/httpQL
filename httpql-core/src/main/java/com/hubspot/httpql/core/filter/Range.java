package com.hubspot.httpql.core.filter;

public class Range implements Filter {

    @Override
    public String[] names() {
        return new String[] {
                "range"
        };
    }

    @Override
    public boolean takesMultiParameters() {
        return true;
    }
}
