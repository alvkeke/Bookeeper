package com.alvkeke.bookeeper.data;

import java.util.ArrayList;

public class BookManager {

    private static final BookManager manager = new BookManager();

    private final ArrayList<BookItem> bookItems;
    private BookManager() {
        bookItems = new ArrayList<>();
    }

    public static BookManager getInstance() {
        return manager;
    }

    public ArrayList<BookItem> getBookItems() {
        return bookItems;
    }

}
