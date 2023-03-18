package com.alvkeke.bookeeper.data;

import java.util.ArrayList;

public class BookManager {

    private static final BookManager manager = new BookManager();

    private final ArrayList<BookItem> bookItems;
    private final ArrayList<String> outcomeCategories;
    private final ArrayList<String> incomeCategories;
    private final ArrayList<String> accounts;
    private final ArrayList<String> tags;


    private BookManager() {
        bookItems = new ArrayList<>();
        outcomeCategories = new ArrayList<>();
        incomeCategories = new ArrayList<>();
        accounts = new ArrayList<>();
        tags = new ArrayList<>();
    }

    public static BookManager getInstance() {
        return manager;
    }

    public ArrayList<String> getAccounts() {
        return accounts;
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

    public void addBookItem(BookItem e) {
        if (e != null) {
            this.bookItems.add(e);
        }
    }

}
