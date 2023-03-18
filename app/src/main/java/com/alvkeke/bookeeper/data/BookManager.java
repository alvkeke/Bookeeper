package com.alvkeke.bookeeper.data;

import java.util.ArrayList;

public class BookManager {

    private static final BookManager manager = new BookManager();

    private final ArrayList<BookItem> bookItems;
    private final ArrayList<String> categories;
    private final ArrayList<String> tags;


    private BookManager() {
        bookItems = new ArrayList<>();
        categories = new ArrayList<>();
        tags = new ArrayList<>();
    }

    public static BookManager getInstance() {
        return manager;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public ArrayList<BookItem> getBookItems() {
        return bookItems;
    }

}
