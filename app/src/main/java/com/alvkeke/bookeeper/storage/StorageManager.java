package com.alvkeke.bookeeper.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.alvkeke.bookeeper.data.BookItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class StorageManager {

    public final String FILE_DATABASE = "data.sqlite3";
    public final String TABLE_BOOK_ITEMS = "book_items";
    public final String KEY_BOOK_ID = "item_id";
    public final String KEY_BOOK_MONEY = "money";
    public final String KEY_BOOK_TIME = "time";
    public final String KEY_BOOK_ACCOUNT = "account_id";
    public final String KEY_BOOK_CATEGORY = "category_id";
    public final String KEY_BOOK_TAG_IDS = "tag_ids";
    public final String TABLE_CATEGORIES = "categories";
    public final String KEY_CATEGORY_ID = "category_id";
    public final String KEY_CATEGORY_NAME = "category_name";
    public final String TABLE_ACCOUNTS = "accounts";
    public final String KEY_ACCOUNT_ID = "account_id";
    public final String KEY_ACCOUNT_NAME = "account_name";
    public final String TABLE_TAGS = "tags";
    public final String KEY_TAG_ID = "tag_id";
    public final String KEY_TAG_NAME = "tag_name";
    public final String TABLE_MANAGE = "table_manage";
    public final String KEY_VERSION = "version";

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
                KEY_BOOK_TAG_IDS, "TEXT"
        ));
        database.execSQL(String.format(
                "CREATE TABLE IF NOT EXISTS %s (%s %s, %s %s);",
                TABLE_CATEGORIES,
                KEY_CATEGORY_ID, "INTEGER PRIMARY KEY AUTOINCREMENT",
                KEY_CATEGORY_NAME, "TEXT NOT NULL"
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
        database.close();
        new File(database.getPath()).delete();
    }

    public long addBookItem(BookItem item) {
        assert manager != null;
        ContentValues values = new ContentValues();
        values.put(KEY_BOOK_MONEY, item.getMoney());
        values.put(KEY_BOOK_TIME, item.getTime());
        values.put(KEY_BOOK_CATEGORY, item.getCategory());
        values.put(KEY_BOOK_ACCOUNT, item.getAccount());
        return database.insert(TABLE_BOOK_ITEMS, null, values);
    }

    public int loadBookItems(ArrayList<BookItem> items, boolean is_clear) {
        assert manager != null;

        if (is_clear)
            items.clear();

        return 0;
    }

}
