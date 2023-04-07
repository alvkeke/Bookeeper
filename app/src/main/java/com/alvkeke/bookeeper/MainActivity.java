package com.alvkeke.bookeeper;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.alvkeke.bookeeper.ui.ItemDetailActivity;
import com.alvkeke.bookeeper.ui.booklist.BookListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity {


    private final BookManager bookManager = BookManager.getInstance();
    private RecyclerView bookItemList;
    private BookListAdapter bookItemListAdapter;
    private Button btnAddItem;

    private MenuItem menuItemDelete, menuItemDeselect, menuItemSelectAll;

    private void randomFillAccounts(ArrayList<String> accounts) {
        String[] c = new String[] {"支付宝", "微信支付", "银行卡", "信用卡"};
        accounts.addAll(Arrays.asList(c));
    }
    private void randomFillTags(ArrayList<String> tags) {
        for (int i=0; i<20; i++) {
            String s = "tag" + i;
            tags.add(s);
        }
    }
    private void randomFillCateIn(ArrayList<String> categories) {
        String[] c = new String[]{"工资", "红包", "利息", "退款", "生活费", "报销", "奖金", "还钱"};
        categories.addAll(Arrays.asList(c));
    }
    private void randomFillCateOut(ArrayList<String> categories) {
        String[] c = new String[]{"餐饮", "衣物穿戴", "交通", "住宿", "娱乐", "医疗", "电子产品", "互联网服务"};
        categories.addAll(Arrays.asList(c));
    }
    private void randomFillBook(ArrayList<BookItem> bookItems) {
        if (bookItems == null) return;

        ArrayList<String> cates_out = bookManager.getOutlayCategories();
        ArrayList<String> cates_in = bookManager.getIncomeCategories();
        ArrayList<String> cates;
        ArrayList<String> accounts = bookManager.getAccounts();
        Random random = new Random();

        for (int i=0; i<5; i++) {
            int money = random.nextInt(200) - 100;
            if (money > 0)
                cates = cates_in;
            else
                cates = cates_out;

            BookItem item = new BookItem(money * 100, random.nextLong() % new Date().getTime(),
                    cates.get(random.nextInt(cates.size())),
                    accounts.get(random.nextInt(accounts.size())));
            if (bookManager.getTags() != null)
            {
                for (String tag : bookManager.getTags()) {
                    if (random.nextInt(5) == 0)
                        item.addTag(tag);
                }
            }
            Log.e(this.toString(), "create book item: " + i);
            bookItems.add(item);
        }
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
    class BookItemClickListener implements BookListAdapter.OnItemLongClickListener,
            BookListAdapter.OnItemClickListener{
        private final BookListAdapter adapter;
        private boolean isSelectMode = false;
        public BookItemClickListener(BookListAdapter adapter) {
            this.adapter = adapter;
        }
        private final CharSequence[] bookMenu = new CharSequence[]
                {"修改", "删除", "取消"};
        private void showItemMenu(int pos) {
            BookManager manager = BookManager.getInstance();
            if (pos >= manager.getBookItems().size())
            {
                Log.e(MainActivity.this.toString(), "Cannot get book item at: " + pos);
                return;
            }
            BookItem item = manager.getBookItems().get(pos);

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(item.getCategory() + "-" + item.getAccount() + ":" + item.getMoneyString())
                    .setItems(bookMenu, (dialogInterface, i) -> {
                        Log.d("Main", "index: " + i);
                        switch (i) {
                            case 0:
                                Intent intent = new Intent(MainActivity.this, ItemDetailActivity.class);
                                intent.putExtra(ItemDetailActivity.INTENT_ITEM_INDEX, pos);
                                itemDetailActivityLauncher.launch(intent);
                                break;
                            case 1:
                                manager.getBookItems().remove(pos);
                                MainActivity.this.bookItemListAdapter.notifyItemRemoved(pos);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bookItemList = findViewById(R.id.list_book_list);
        btnAddItem = findViewById(R.id.btn_add_item);

        btnAddItem.setOnClickListener(new BtnAddItemClickListener());

        bookItemListAdapter = new BookListAdapter(bookManager.getBookItems());
        bookItemList.setAdapter(bookItemListAdapter);
        bookItemList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        bookItemList.setLayoutManager(new LinearLayoutManager(this));
        BookItemClickListener bookItemClickListener = new BookItemClickListener(bookItemListAdapter);
        bookItemListAdapter.setItemClickListener(bookItemClickListener);
        bookItemListAdapter.setItemLongClickListener(bookItemClickListener);

        randomFillAccounts(bookManager.getAccounts());
        randomFillTags(bookManager.getTags());
        randomFillCateIn(bookManager.getIncomeCategories());
        randomFillCateOut(bookManager.getOutlayCategories());
        randomFillBook(bookManager.getBookItems());

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
                    bookItemListAdapter.deleteSelectedItems();
                    bookItemListAdapter.notifyDataSetChanged();
                    reSetMenuVisible();
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }
    private void onMenuSelectAll() {
        bookItemListAdapter.itemSelectAll();
        bookItemListAdapter.notifyDataSetChanged();
        reSetMenuVisible();
    }
    private void onMenuDeselect() {
        bookItemListAdapter.itemSelectClear();
        bookItemListAdapter.notifyDataSetChanged();
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