package com.alvkeke.bookeeper.data;

import androidx.annotation.NonNull;

public class Category {
    private long id = -1;
    private String name;
    private CategoryType type;

    public enum CategoryType {
        INCOME,
        OUTLAY,
    }

    public Category(long id, String name, CategoryType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public Category(String name, CategoryType type) {
        this.name = name;
        this.type = type;
    }

    public CategoryType getType() {
        return type;
    }
    public void setType(CategoryType type) {
        this.type = type;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return this.name;
    }
}
