package com.alvkeke.bookeeper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alvkeke.bookeeper.data.BookItem;
import com.alvkeke.bookeeper.ui.ItemAddActivity;
import com.alvkeke.bookeeper.ui.booklist.BookListAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity {



    private void randomFillBook(ArrayList<BookItem> bookItems) {
        if (bookItems == null) return;

        String[] categories = new String[]{"餐饮", "衣物穿戴", "交通", "住宿", "娱乐", "医疗", "电子产品", "互联网服务"};
        Random random = new Random();

        for (int i=0; i<30; i++) {
            int money = random.nextInt(200) - 100;
            BookItem item = new BookItem(money, new Date().getTime(), categories[random.nextInt(categories.length)]);
            for (int j=0; j<i; j++)
                item.addTag("tag"+j);
            Log.e(this.toString(), "create book item: " + i);
            bookItems.add(item);
        }
    }


    static class BtnAddItemClickListener implements View.OnClickListener {

        private final Context ctx;
        public BtnAddItemClickListener(Context ctx) {
            this.ctx = ctx;
        }
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(ctx, ItemAddActivity.class);
            ctx.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView bookList = findViewById(R.id.list_book_list);
        Button btnAddItem = findViewById(R.id.btn_add_item);

        btnAddItem.setOnClickListener(new BtnAddItemClickListener(this));

        ArrayList<BookItem> bookItems = new ArrayList<>();
        BookListAdapter adapter = new BookListAdapter(bookItems);
        bookList.setAdapter(adapter);
        bookList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        bookList.setLayoutManager(new LinearLayoutManager(this));
        randomFillBook(bookItems);

    }
}