package com.alvkeke.bookeeper;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alvkeke.bookeeper.data.BookItem;
import com.alvkeke.bookeeper.data.BookManager;
import com.alvkeke.bookeeper.ui.ItemAddActivity;
import com.alvkeke.bookeeper.ui.booklist.BookListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity {


    private final BookManager bookManager = BookManager.getInstance();
    private RecyclerView bookItemList;
    private BookListAdapter bookItemListAdapter;

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

        for (int i=0; i<30; i++) {
            int money = random.nextInt(200) - 100;
            if (money > 0)
                cates = cates_in;
            else
                cates = cates_out;

            BookItem item = new BookItem(money * 100, new Date().getTime(),
                    cates.get(random.nextInt(cates.size())),
                    accounts.get(random.nextInt(accounts.size())));
            for (int j=0; j<i; j++)
                item.addTag("tag"+j);
            Log.e(this.toString(), "create book item: " + i);
            bookItems.add(item);
        }
    }


    class BtnAddItemClickListener implements View.OnClickListener {

        ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        bookItemListAdapter.notifyDataSetChanged();
                    }
                }
        });

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this, ItemAddActivity.class);
            launcher.launch(intent);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bookItemList = findViewById(R.id.list_book_list);
        Button btnAddItem = findViewById(R.id.btn_add_item);

        btnAddItem.setOnClickListener(new BtnAddItemClickListener());

        bookItemListAdapter = new BookListAdapter(bookManager.getBookItems());
        bookItemList.setAdapter(bookItemListAdapter);
        bookItemList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        bookItemList.setLayoutManager(new LinearLayoutManager(this));

        randomFillAccounts(bookManager.getAccounts());
        randomFillTags(bookManager.getTags());
        randomFillCateIn(bookManager.getIncomeCategories());
        randomFillCateOut(bookManager.getOutlayCategories());
        randomFillBook(bookManager.getBookItems());

    }
}