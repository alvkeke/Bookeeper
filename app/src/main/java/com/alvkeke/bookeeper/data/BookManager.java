package com.alvkeke.bookeeper.data;

import java.util.ArrayList;

public class BookManager {

    private static final BookManager manager = new BookManager();

    private final ArrayList<BookItem> bookItems;
    private final ArrayList<Category> categories;
    private final ArrayList<Account> accounts;
    private final ArrayList<String> tags;
    private ArrayList<String> stringAccounts;
    private ArrayList<String> outcomeCategories;
    private ArrayList<String> incomeCategories;


    private BookManager() {
        bookItems = new ArrayList<>();
        accounts = new ArrayList<>();
        categories = new ArrayList<>();
        tags = new ArrayList<>();
    }

    public static BookManager getInstance() {
        return manager;
    }

    public ArrayList<Account> getAccounts() {
        return accounts;
    }
    public ArrayList<String> getStringAccounts() {
        stringAccounts.clear();
        for (Account e : accounts) {
            stringAccounts.add(e.getName());
        }
        return stringAccounts;
    }
    public ArrayList<Category> getCategories() {
        return categories;
    }
    public ArrayList<String> getOutlayCategories() {
        return outcomeCategories;
    }

    public ArrayList<String> getIncomeCategories() {
        return incomeCategories;
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
        return null;
    }

    public Category getCategoryById(long id) {
        for (Category e : categories) {
            if (e.getId() == id)
                return e;
        }
        return null;
    }
}
