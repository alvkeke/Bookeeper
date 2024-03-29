package com.alvkeke.bookeeper.data;

import java.util.ArrayList;
import java.util.Locale;

public class BookItem {

    private long id = -1;
    private int money;
    private String moneyString;
    private long time;
    private long category_id;
    private ArrayList<String> tags = new ArrayList<>();
    private long account_id;

    public static BookItem getNullInstance() {
        return new BookItem();
    }
    private BookItem() {
    }
    public BookItem(int money, long time, long category, long account) {
        this.money = money;
        this.time = time;
        this.category_id = category;
        this.account_id = account;
    }
    public BookItem(long id, int money, long time, long category, long account) {
        this.id = id;
        this.money = money;
        this.time = time;
        this.category_id = category;
        this.account_id = account;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public void setMoney(int money) {
        this.money = money;
        this.moneyString = null;    // reset to re-gen the money string
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

        int x = Math.abs(money);
        moneyString = String.format(Locale.getDefault(),
                "%c%d.%02d", c_sign, x/100, x%100);
        return moneyString;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setCategoryId(long category_id) {
        this.category_id = category_id;
    }

    public long getCategoryId() {
        return category_id;
    }

    public void setAccountId(long account_id) {
        this.account_id = account_id;
    }

    public long getAccountId() {
        return account_id;
    }

    public void setTagList(ArrayList<String> tags) {
         this.tags = tags;
    }
    public void addTag(String tag) {
        tags.add(tag);
    }

    public ArrayList<String> getTags() {
        return tags;
    }
}
