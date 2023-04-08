package com.alvkeke.bookeeper.data;

import androidx.annotation.NonNull;

public class Account {

    private long id = -1;
    private String name;

    public Account(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Account(String name) {
        this.name = name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @NonNull
    @Override
    public String toString() {
        return this.name;
    }
}
