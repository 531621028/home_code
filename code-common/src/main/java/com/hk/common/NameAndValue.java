package com.hk.common;

import java.util.List;

/**
 * Created by kang on 2019/1/8.
 */
public class NameAndValue {
    private String name;
    private Object value;

    public NameAndValue() {
    }

    public NameAndValue(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String toParam() {
        return String.format("%s&%s", name, value);
    }
}
