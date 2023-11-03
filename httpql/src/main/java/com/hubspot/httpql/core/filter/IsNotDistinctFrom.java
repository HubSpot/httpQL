package com.hubspot.httpql.core.filter;

public class IsNotDistinctFrom implements Filter {

    @Override
    public String[] names() {
        return new String[] {
                "ndistinct"
        };
    }

}
