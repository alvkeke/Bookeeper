package com.alvkeke.bookeeper;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alvkeke.bookeeper.data.BookItem;
import com.alvkeke.bookeeper.data.BookManager;
import com.alvkeke.bookeeper.data.Category;
import com.alvkeke.bookeeper.storage.StorageManager;
import com.alvkeke.bookeeper.ui.AccountDetailActivity;
import com.alvkeke.bookeeper.ui.AccountListAdapter;
import com.alvkeke.bookeeper.ui.CategoryMgmtActivity;
import com.alvkeke.bookeeper.ui.ItemDetailActivity;
import com.alvkeke.bookeeper.ui.BookListAdapter;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    private final BookManager bookManager = BookManager.getInstance();
    private StorageManager storageManager; // = StorageManager.getInstance();
    private RecyclerView bookItemList, accountList;
    private BookListAdapter bookItemListAdapter;
    private AccountListAdapter accountListAdapter;
    private Button btnAddItem, btnCateMgmt;

    private MenuItem menuItemDelete, menuItemDeselect, menuItemSelectAll;

    void randomFillDatabase() {
        // Fill Accounts
        String[] c = new String[] {"支付宝", "微信支付", "银行卡", "信用卡"};
        for (String s : c) {
            storageManager.addAccount(s, 0);
        }
        // Fill Category
        c = new String[]{"工资", "红包", "利息", "退款", "生活费", "报销", "奖金", "还钱"};
        for (String s : c) {
            Category x = new Category(s, Category.CategoryType.INCOME);
            storageManager.addCategory(x);
        }
        c = new String[]{"餐饮", "衣物穿戴", "交通", "住宿", "娱乐", "医疗", "电子产品", "互联网服务"};
        for (String s : c) {
            Category x = new Category(s, Category.CategoryType.OUTLAY);
            storageManager.addCategory(x);
        }

        return;
//    private void randomFillBook(ArrayList<BookItem> bookItems) {
//        if (bookItems == null) return;
//
//        ArrayList<String> cates_out = bookManager.getOutlayCategories();
//        ArrayList<String> cates_in = bookManager.getIncomeCategories();
//        ArrayList<String> cates;
//        ArrayList<String> accounts = bookManager.getStringAccounts();
//        Random random = new Random();
//
//        for (int i=0; i<5; i++) {
//            int money = random.nextInt(200) - 100;
//            if (money > 0)
//                cates = cates_in;
//            else
//                cates = cates_out;
//
//            BookItem item = new BookItem(money * 100, random.nextLong() % new Date().getTime(),
//                    cates.get(random.nextInt(cates.size())),
//                    accounts.get(random.nextInt(accounts.size())));
//                    0, 0);
//            if (bookManager.getTags() != null)
//            {
//                for (String tag : bookManager.getTags()) {
//                    if (random.nextInt(5) == 0)
//                        item.addTag(tag);
//                }
//            }
//            Log.e(this.toString(), "create book item: " + i);
//            bookItems.add(item);
//        }
//    }

    }

    ActivityResultLauncher<Intent> itemDetailActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent intent = result.getData();
                        assert intent != null;
                        int index = intent.getIntExtra(ItemDetailActivity.INTENT_ITEM_INDEX, -1);
                        assert index != -1;
                        bookItemListAdapter.notifyItemChanged(index);
                    }
                }
            });

    class BtnAddItemClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this, ItemDetailActivity.class);
            itemDetailActivityLauncher.launch(intent);
        }

    }
    private void reSetMenuVisible() {
        int count = bookItemListAdapter.getSelectedItemCount();
        if (count == 0) {
            menuItemDelete.setVisible(false);
            menuItemSelectAll.setVisible(false);
            menuItemDeselect.setVisible(false);
        } else {
            menuItemDelete.setVisible(true);
            menuItemSelectAll.setVisible(true);
            menuItemDeselect.setVisible(true);
        }
    }
    private boolean isSelectMode = false;
    class BookItemClickListener implements BookListAdapter.OnItemLongClickListener,
            BookListAdapter.OnItemClickListener{
        private final BookListAdapter adapter;
        public BookItemClickListener(BookListAdapter adapter) {
            this.adapter = adapter;
        }
        private final CharSequence[] bookMenu = new CharSequence[]
                {"Edit", "Remove", "Cancel"};
        private void showItemMenu(int pos) {
            BookManager manager = BookManager.getInstance();
            if (pos >= manager.getBookItems().size())
            {
                Log.e(MainActivity.this.toString(), "Cannot get book item at: " + pos);
                return;
            }
            BookItem item = manager.getBookItems().get(pos);

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            String title = String.format(Locale.getDefault(),
                    "%s - %s : %s",
                    bookManager.getCategoryById(item.getCategoryId()),
                    bookManager.getAccountById(item.getAccountId()),
                    item.getMoneyString());
            builder.setTitle(title)
                    .setItems(bookMenu, (dialogInterface, i) -> {
                        Log.d("Main", "index: " + i);
                        switch (i) {
                            case 0:
                                Intent intent = new Intent(MainActivity.this, ItemDetailActivity.class);
                                intent.putExtra(ItemDetailActivity.INTENT_ITEM_INDEX, pos);
                                itemDetailActivityLauncher.launch(intent);
                                break;
                            case 1:
                                BookItem e = manager.getBookItems().get(pos);
                                storageManager.delBookItem(e.getId());
                                manager.getBookItems().remove(pos);
                                bookItemListAdapter.notifyItemRemoved(pos);
                                bookItemListAdapter.notifyItemRangeChanged(pos,
                                        bookItemListAdapter.getItemCount());
                            case 2:
                            default:
                        }
                    });
            builder.create().show();
        }
        private void toggleItemSelect(int pos) {
            adapter.itemSelectToggle(pos);
            adapter.notifyItemChanged(pos);
        }
        @Override
        public void OnItemClick(View view, int position) {
            if (isSelectMode) {
                toggleItemSelect(position);
                if (adapter.getSelectedItemCount() == 0)
                    isSelectMode = false;
                reSetMenuVisible();
            } else {
                showItemMenu(position);
            }
        }

        @Override
        public boolean OnItemLongClick(View view, int position) {
            if (!isSelectMode) isSelectMode = true;
            toggleItemSelect(position);
            reSetMenuVisible();
            return true;
        }
    }
    ActivityResultLauncher<Intent> accountDetailActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent intent = result.getData();
                        assert intent != null;
                        int index = intent.getIntExtra(AccountDetailActivity.INTENT_ACCOUNT_INDEX, -1);
                        assert index != -1;
                        accountListAdapter.notifyItemChanged(index);
                    }
                }
            });
    class AccountItemClickListener implements AccountListAdapter.OnItemClickListener {

        @Override
        public void OnItemClick(View view, int position) {
            Intent intent = new Intent(MainActivity.this, AccountDetailActivity.class);
            intent.putExtra(AccountDetailActivity.INTENT_ACCOUNT_INDEX, position);
            accountDetailActivityLauncher.launch(intent);
        }

    }

    class AccountAddBtnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, AccountDetailActivity.class);
            accountDetailActivityLauncher.launch(intent);
        }
    }
    ActivityResultLauncher<Intent> categoryMgmtActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent intent = result.getData();
                        assert intent != null;
//                        int index = intent.getIntExtra(AccountDetailActivity.INTENT_ACCOUNT_INDEX, -1);
//                        assert index != -1;
//                        accountListAdapter.notifyItemChanged(index);
                    }
                }
            });
    class BtnCateMgmtClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, CategoryMgmtActivity.class);
            categoryMgmtActivityLauncher.launch(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bookItemList = findViewById(R.id.list_book_list);
        accountList = findViewById(R.id.drawer_list_account);
        btnAddItem = findViewById(R.id.btn_add_item);
        btnCateMgmt = findViewById(R.id.category_setting_button);

        btnCateMgmt.setOnClickListener(new BtnCateMgmtClickListener());
        btnAddItem.setOnClickListener(new BtnAddItemClickListener());

        bookItemListAdapter = new BookListAdapter(bookManager.getBookItems());
        bookItemList.setAdapter(bookItemListAdapter);
        bookItemList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        bookItemList.setLayoutManager(new LinearLayoutManager(this));
        BookItemClickListener bookItemClickListener = new BookItemClickListener(bookItemListAdapter);
        bookItemListAdapter.setItemClickListener(bookItemClickListener);
        bookItemListAdapter.setItemLongClickListener(bookItemClickListener);

        accountListAdapter = new AccountListAdapter(bookManager.getAccounts());
        accountList.setAdapter(accountListAdapter);
        accountList.setLayoutManager(new LinearLayoutManager(this));
        accountList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        accountListAdapter.setItemClickListener(new AccountItemClickListener());
        accountListAdapter.setTailButtonClickListener(new AccountAddBtnClickListener());
        accountListAdapter.setTailButtonName("Add Account");

        storageManager = StorageManager.getInstance(this);
//        storageManager.storageDestroy();
//        finish();

        // XXX: debug only
        // randomFillDatabase();

        storageManager.loadBookItems(bookManager.getBookItems());
        storageManager.loadAccounts(bookManager.getAccounts());
        ArrayList<Category> categories = new ArrayList<>();
        storageManager.loadCategories(categories);
        bookManager.sortCategories(categories);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_activity_top, menu);
        menuItemDelete = menu.findItem(R.id.menu_item_main_delete);
        menuItemSelectAll = menu.findItem(R.id.menu_item_main_select_all);
        menuItemDeselect = menu.findItem(R.id.menu_item_main_deselect);
        return true; //super.onCreateOptionsMenu(menu);
    }
    private void onMenuDelete() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Delete Book Items")
                .setMessage("Are you really to delete these item(s)?")
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    int[] item_idx = bookItemListAdapter.getSelectedItemIndex();
                    ArrayList<BookItem> items = bookManager.getBookItemsWithIndex(item_idx);
                    int min_idx = item_idx[0];
                    for (BookItem e : items) {
                        int idx = bookManager.getBookItems().indexOf(e);
                        min_idx = Math.min(idx, min_idx);
                        bookManager.getBookItems().remove(e);
                        bookItemListAdapter.notifyItemRemoved(idx);
                        storageManager.delBookItem(e.getId());
                    }
                    bookItemListAdapter.notifyItemRangeChanged(min_idx,
                            bookItemListAdapter.getItemCount());
                    bookItemListAdapter.itemSelectClear();
                    isSelectMode = false;
                    reSetMenuVisible();
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }
    private void onMenuSelectAll() {
        bookItemListAdapter.itemSelectAll();
        bookItemListAdapter.notifyItemRangeChanged(0, bookItemListAdapter.getItemCount());
        reSetMenuVisible();
    }
    private void onMenuDeselect() {
        bookItemListAdapter.itemSelectClear();
        bookItemListAdapter.notifyItemRangeChanged(0, bookItemListAdapter.getItemCount());
        reSetMenuVisible();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_item_main_delete)
                onMenuDelete();
        if (id == R.id.menu_item_main_select_all)
                onMenuSelectAll();
        if (id == R.id.menu_item_main_deselect)
                onMenuDeselect();
        return super.onOptionsItemSelected(item);
    }
}