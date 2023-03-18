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

    public void setTagList(ArrayList<String> tags) {
         this.tags = tags;
    }
    public void addTag(String tag) {
        if (tags == null) {
            tags = new ArrayList<>();
        }
        tags.add(tag);
    }

    public ArrayList<String> getTags() {
        return tags;
    }
}
