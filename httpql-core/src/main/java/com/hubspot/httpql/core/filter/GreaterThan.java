package com.hubspot.httpql.core.filter;

public class GreaterThan  implements Filter {

    @Override
    public String[] names() {
        return new String[] {
                "gt"
        };
    }

}
