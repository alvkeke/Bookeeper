package com.alvkeke.bookeeper.data;

import android.util.Log;

import java.util.ArrayList;
import java.util.Locale;

public class BookManager {

    private static final BookManager manager = new BookManager();

    private final ArrayList<BookItem> bookItems;
    private final ArrayList<Category> categoriesIncome;
    private final ArrayList<Category> categoriesOutlay;
    private final ArrayList<Account> accounts;
    private final ArrayList<String> tags;


    private BookManager() {
        bookItems = new ArrayList<>();
        accounts = new ArrayList<>();
        categoriesIncome = new ArrayList<>();
        categoriesOutlay = new ArrayList<>();
        tags = new ArrayList<>();
    }

    public static BookManager getInstance() {
        return manager;
    }

    public ArrayList<Account> getAccounts() {
        return accounts;
    }

    public void sortCategories(ArrayList<Category> categories) {
        for (Category c : categories) {
            if (c.getType() == Category.CategoryType.INCOME) {
                categoriesIncome.add(c);
            } else if (c.getType() == Category.CategoryType.OUTLAY) {
                categoriesOutlay.add(c);
            } else {
                Log.e(this.toString(), String.format(Locale.getDefault(),
                        "Got Wrong category Type for %s[%d] : %s",
                        c.getName(), c.getId(), c.getType()));
            }
        }
    }

    public ArrayList<Category> getCategoriesIncome() {
        return categoriesIncome;
    }

    public ArrayList<Category> getCategoriesOutlay() {
        return categoriesOutlay;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public ArrayList<BookItem> getBookItems() {
        return bookItems;
    }

    public ArrayList<BookItem> getBookItemsWithIndex(int[] idx) {
        ArrayList<BookItem> items = new ArrayList<>();
        if (idx == null) return items;
        for (int i : idx) {
            items.add(bookItems.get(i));
        }
        return items;
    }

    public void addBookItem(BookItem e) {
        if (e != null) {
            this.bookItems.add(e);
        }
    }

    public Account getAccountById(long accountId) {
        for (Account e : accounts) {
            if (e.getId() == accountId)
                return e;
        }
        Log.e(this.toString(), "Account["+accountId+"] not found.");
        return null;
    }

    public Category getCategoryById(long categoryId) {
        for (Category e : categoriesIncome) {
            if (e.getId() == categoryId)
                return e;
        }
        for (Category e : categoriesOutlay) {
            if (e.getId() == categoryId)
                return e;
        }
        return null;
    }
}
