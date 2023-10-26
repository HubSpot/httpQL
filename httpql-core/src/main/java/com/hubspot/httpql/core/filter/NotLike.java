package com.hubspot.httpql.core.filter;

public class NotLike implements Filter {


    @Override
    public String[] names() {
        return new String[] {
                "nlike", "not_like"
        };
    }

    @Override
    public boolean takesMultiParameters() {
        return true;
    }

}
