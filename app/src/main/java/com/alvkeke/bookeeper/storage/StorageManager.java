package com.alvkeke.bookeeper.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.alvkeke.bookeeper.data.Account;
import com.alvkeke.bookeeper.data.BookItem;
import com.alvkeke.bookeeper.data.Category;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class StorageManager {

    public final static String FILE_DATABASE = "data.sqlite3";
    public final static String TABLE_BOOK_ITEMS = "book_items";
    public final static String KEY_BOOK_ID = "item_id";
    public final static String KEY_BOOK_MONEY = "money";
    public final static String KEY_BOOK_TIME = "time";
    public final static String KEY_BOOK_ACCOUNT = "account_id";
    public final static String KEY_BOOK_CATEGORY = "category_id";
    public final static String KEY_BOOK_TAGS = "tags";
    public final static String TABLE_CATEGORIES = "categories";
    public final static String KEY_CATEGORY_ID = "category_id";
    public final static String KEY_CATEGORY_NAME = "category_name";
    public final static String KEY_CATEGORY_TYPE = "category_type";
    public final static String TABLE_ACCOUNTS = "accounts";
    public final static String KEY_ACCOUNT_ID = "account_id";
    public final static String KEY_ACCOUNT_NAME = "account_name";
    public final static String TABLE_TAGS = "tags";
    public final static String KEY_TAG_ID = "tag_id";
    public final static String KEY_TAG_NAME = "tag_name";
    public final static String TABLE_MANAGE = "table_manage";
    public final static String KEY_VERSION = "version";

    private final int TYPE_CATEGORY_INCOME = 0;
    private final int TYPE_CATEGORY_OUTLAY = 1;

    private static Context mContext;
    private static StorageManager manager = null;
    private SQLiteDatabase database;


    private void initializeDatabase() {
        File fdb = new File(mContext.getExternalFilesDir(""), FILE_DATABASE);
        database = SQLiteDatabase.openOrCreateDatabase(fdb, null);
    }
    private void setupDataTables() {
        assert database != null;
        database.execSQL(String.format(
                "CREATE TABLE IF NOT EXISTS %s (%s %s, %s %s, %s %s, %s %s, %s %s, %s %s);",
                TABLE_BOOK_ITEMS,
                KEY_BOOK_ID, "INTEGER PRIMARY KEY AUTOINCREMENT",
                KEY_BOOK_MONEY, "INTEGER NOT NULL",
                KEY_BOOK_TIME, "INTEGER NOT NULL",
                KEY_BOOK_ACCOUNT, "INTEGER NOT NULL",
                KEY_BOOK_CATEGORY, "INTEGER NOT NULL",
                KEY_BOOK_TAGS, "TEXT"
        ));
        database.execSQL(String.format(
                "CREATE TABLE IF NOT EXISTS %s (%s %s, %s %s, %s %s);",
                TABLE_CATEGORIES,
                KEY_CATEGORY_ID, "INTEGER PRIMARY KEY AUTOINCREMENT",
                KEY_CATEGORY_NAME, "TEXT NOT NULL",
                KEY_CATEGORY_TYPE, "INTEGER NOT NULL"
        ));
        database.execSQL(String.format(
                "CREATE TABLE IF NOT EXISTS %s (%s %s, %s %s);",
                TABLE_ACCOUNTS,
                KEY_ACCOUNT_ID, "INTEGER PRIMARY KEY AUTOINCREMENT",
                KEY_ACCOUNT_NAME, "TEXT NOT NULL"
        ));
        database.execSQL(String.format(
                "CREATE TABLE IF NOT EXISTS %s (%s %s, %s %s);",
                TABLE_TAGS,
                KEY_TAG_ID, "INTEGER PRIMARY KEY AUTOINCREMENT",
                KEY_TAG_NAME, "TEXT NOT NULL"
        ));
    }
    private void setupManageTables() {
        database.execSQL(String.format(
                "CREATE TABLE IF NOT EXISTS %s (%s %s);",
                TABLE_MANAGE,
                KEY_VERSION, "INTEGER PRIMARY KEY"
        ));
        long count = DatabaseUtils.queryNumEntries(database, TABLE_MANAGE);
        if (count == 0) {
            database.execSQL(String.format(Locale.getDefault(),
                    "INSERT INTO %s VALUES (%d);",
                    TABLE_MANAGE, 0
            ));
        }
    }
    private StorageManager() {
        initializeDatabase();
        setupManageTables();
        setupDataTables();
    }

    /**
     * get an instance of StorageManager, singleton,
     * this function should NOT be called out of an activity
     * @param context context of the caller, cannot be null. if the instance is already generated,
     *                this param will not be used.
     * @return An instance of StorageManager
     */
    public static StorageManager getInstance(Context context) {
        if (manager != null) return manager;
        assert context != null;
        mContext = context;
        manager = new StorageManager();
        return manager;
    }
    public void storageDestroy() {
        assert database != null;
        database.close();
        new File(database.getPath()).delete();
    }

    public long addBookItem(BookItem item) {
        assert database != null;
        ContentValues values = new ContentValues();
        values.put(KEY_BOOK_MONEY, item.getMoney());
        values.put(KEY_BOOK_TIME, item.getTime());
        values.put(KEY_BOOK_CATEGORY, item.getCategoryId());
        values.put(KEY_BOOK_ACCOUNT, item.getAccountId());
        StringBuilder sb = new StringBuilder();
        for (String s : item.getTags()) {
            sb.append(s);
            sb.append(",");
        }
        values.put(KEY_BOOK_TAGS, sb.toString());
        return database.insert(TABLE_BOOK_ITEMS, null, values);
    }
    public int modifyBookItem(BookItem item) {
        assert database != null;
        if (item == null) return 0;
        long id = item.getId();
        ContentValues values = new ContentValues();
        values.put(KEY_BOOK_MONEY, item.getMoney());
        values.put(KEY_BOOK_TIME, item.getTime());
        values.put(KEY_BOOK_CATEGORY, item.getCategoryId());
        values.put(KEY_BOOK_ACCOUNT, item.getAccountId());
        StringBuilder sb = new StringBuilder();
        for (String s : item.getTags()) {
            sb.append(s);
            sb.append(",");
        }
        values.put(KEY_BOOK_TAGS, sb.toString());
        return database.update(TABLE_BOOK_ITEMS, values,
                KEY_BOOK_ID + "=?", new String[]{"" + id});
    }
    public void delBookItem(long id) {
        assert database != null;
        database.execSQL(String.format(Locale.getDefault(),
                "DELETE FROM %s WHERE %s=%d",
                TABLE_BOOK_ITEMS, KEY_BOOK_ID, id
        ));
    }
    public long addAccount(String account) {
        assert database != null;
        ContentValues values = new ContentValues();
        values.put(KEY_ACCOUNT_NAME, account);
        return database.insert(TABLE_ACCOUNTS, null, values);
    }
    public void delAccount(long id) {
        assert database != null;
        database.execSQL(String.format(Locale.getDefault(),
                "DELETE FROM %s WHERE %s=%d",
                TABLE_ACCOUNTS, KEY_ACCOUNT_ID, id
        ));
    }
    public long addCategory(Category category) {
        assert database != null;
        ContentValues values = new ContentValues();
        values.put(KEY_CATEGORY_NAME, category.getName());
        values.put(KEY_CATEGORY_TYPE, (category.getType() == Category.CategoryType.INCOME) ?
                TYPE_CATEGORY_INCOME : TYPE_CATEGORY_OUTLAY);
        return database.insert(TABLE_CATEGORIES, null, values);
    }
    public void delCategory(long id) {
        assert database != null;
        database.execSQL(String.format(Locale.getDefault(),
                "DELETE FROM %s WHERE %s=%d",
                TABLE_CATEGORIES, KEY_CATEGORY_ID, id
        ));
    }

    private final static String [] books_columns = new String[]
            { KEY_BOOK_ID, KEY_BOOK_MONEY, KEY_BOOK_ACCOUNT,
                    KEY_BOOK_CATEGORY, KEY_BOOK_TIME, KEY_BOOK_TAGS, };
    public int loadBookItems(ArrayList<BookItem> items) {
        assert database != null;
        int count = 0;

        Cursor c = database.query(TABLE_BOOK_ITEMS, books_columns,
                null, null, null, null, null);
        if (c == null) return -1;
        while(c.moveToNext()) {
            long id = c.getLong(0);
            int money = c.getInt(1);
            long category_id = c.getLong(2);
//            String category = c.getString(2);
            long account_id = c.getLong(3);
//            String account = c.getString(3);
            long time = c.getLong(4);
            String tags = c.getString(5);
            String[] tag_arr = tags.split(",");
            ArrayList<String> tag_list = new ArrayList<>(Arrays.asList(tag_arr));
            BookItem item = new BookItem(id, money, time, category_id, account_id);
            item.setTagList(tag_list);
            items.add(item);
            count++;
        }
        c.close();
        return count;
    }

    private final static String[] accounts_columns = new String[]
            { KEY_ACCOUNT_ID, KEY_ACCOUNT_NAME, };
    public int loadAccounts(ArrayList<Account> accounts) {
        assert database != null;
        if (accounts == null) return -1;
        int count = 0;

        Cursor c = database.query(TABLE_ACCOUNTS, accounts_columns,
                null, null, null, null, null);
        if (c == null) return -1;
        while(c.moveToNext()) {
            long id = c.getLong(0);
            String name = c.getString(1);
            count++;
        }
        c.close();
        return count;
    }
//    public int loadAccounts(ArrayList<String> accounts) {
//        assert database != null;
//        if (accounts == null) return -1;
//        int count = 0;
//
//        Cursor c = database.query(TABLE_ACCOUNTS, accounts_columns,
//                null, null, null, null, null);
//        if (c == null) return -1;
//        while(c.moveToNext()) {
//            long id = c.getLong(0);
//            String name = c.getString(1);
//            accounts.add(name);
//            count++;
//        }
//        c.close();
//        return count;
//    }

    private final static String[] categories_columns = new String[]
            { KEY_CATEGORY_ID, KEY_CATEGORY_NAME, KEY_CATEGORY_TYPE, };
    public int loadCategories(ArrayList<Category> categories) {
        assert database != null;
        if (categories == null) return -1;
        int count = 0;
        Cursor c = database.query(TABLE_CATEGORIES, categories_columns,
                null, null, null, null, null);
        while(c.moveToNext()){
            long id = c.getLong(0);
            String name = c.getString(1);
            int type = c.getInt(2);
            Category.CategoryType t = (type == TYPE_CATEGORY_INCOME) ?
                    Category.CategoryType.INCOME : Category.CategoryType.OUTLAY;
            Category cate = new Category(id, name, t);
            categories.add(cate);
            count++;
        }
        c.close();
        return count;
    }
//    public int loadCategories(ArrayList<String> categories) {
//        assert database != null;
//        if (categories == null) return -1;
//        int count = 0;
//        Cursor c = database.query(TABLE_CATEGORIES, categories_columns,
//                null, null, null, null, null);
//        while(c.moveToNext()){
//            long id = c.getLong(0);
//            String name = c.getString(1);
//            categories.add(name);
//            count++;
//        }
//        c.close();
//        return count;
//    }

}
