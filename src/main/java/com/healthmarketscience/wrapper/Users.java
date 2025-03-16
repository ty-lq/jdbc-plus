package com.healthmarketscience.wrapper;

import com.healthmarketscience.core.Table;

@Table(value = "users")
public class Users {
    private Integer id;
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
