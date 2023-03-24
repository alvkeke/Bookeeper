package com.alvkeke.bookeeper.data;

import java.util.ArrayList;
import java.util.Locale;

public class BookItem {

    private int money;
    private String moneyString;
    private long time;
    private String category;
    private ArrayList<String> tags;
    private String account;

    public BookItem(int money, long time, String category, String account) {
        this.money = money;
        this.time = time;
        this.category = category;
        this.account = account;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getMoney() {
        return money;
    }

    public String getMoneyString() {
        if (moneyString != null) return moneyString;

        char c_sign;
        if (money < 0)
            c_sign = '-';
        else
            c_sign = '+';

        money = Math.abs(money);
        moneyString = String.format(Locale.getDefault(),
                "%c%d.%02d", c_sign, money/100, money%100);
        return moneyString;
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

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAccount() {
        return account;
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
