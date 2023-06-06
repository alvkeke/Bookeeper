package com.alvkeke.bookeeper.data;

import androidx.annotation.NonNull;

public class Account {

    private long id = -1;
    private String name;
    private long balance;

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

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    @NonNull
    @Override
    public String toString() {
        return this.name;
    }
}
