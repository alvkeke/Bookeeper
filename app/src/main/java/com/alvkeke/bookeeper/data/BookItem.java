package com.alvkeke.bookeeper.data;

import java.util.ArrayList;

public class BookItem {

    private int money;
    private long time;
    private String category;
    private ArrayList<String> tags;

    public BookItem(int money, long time, String category) {
        this.money = money;
        this.time = time;
        this.category = category;
        tags = new ArrayList<>();
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getMoney() {
        return money;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void addTag(String tag) {
         tags.add(tag);
    }

    public ArrayList<String> getTags() {
        return tags;
    }
}
