package com.alvkeke.bookeeper.ui;

import android.content.Intent;
import android.os.Bundle;
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

import com.alvkeke.bookeeper.R;
import com.alvkeke.bookeeper.data.BookManager;

public class CategoryMgmtActivity extends AppCompatActivity {

    private RecyclerView listCategoryIn, listCategoryOut;
    private CategoryListAdapter adapterIn, adapterOut;
    private Button btnCategoryAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_mgmt);

        listCategoryIn = findViewById(R.id.list_category_in);
        listCategoryOut = findViewById(R.id.list_category_out);
        btnCategoryAdd = findViewById(R.id.category_add_button);

        BookManager manager = BookManager.getInstance();

        adapterIn = new CategoryListAdapter(manager.getCategoriesIncome());
        listCategoryIn.setLayoutManager(new LinearLayoutManager(this));
        listCategoryIn.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        listCategoryIn.setAdapter(adapterIn);

        adapterOut = new CategoryListAdapter(manager.getCategoriesOutlay());
        listCategoryOut.setLayoutManager(new LinearLayoutManager(this));
        listCategoryOut.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        listCategoryOut.setAdapter(adapterOut);

        btnCategoryAdd.setOnClickListener(new BtnCategoryAddClickListener());
        adapterIn.setItemClickListener(new CategoryItemClickListener(
                CategoryDetailActivity.INTENT_CATEGORY_TYPE_IN));
        adapterOut.setItemClickListener(new CategoryItemClickListener(
                CategoryDetailActivity.INTENT_CATEGORY_TYPE_OUT));
    }

    class CategoryItemClickListener implements CategoryListAdapter.OnItemClickListener {
        private final int type;
        CategoryItemClickListener(int categoryType) {
            this.type = categoryType;
        }
        @Override
        public void OnItemClick(View view, int position) {
            Intent intent = new Intent(CategoryMgmtActivity.this,
                    CategoryDetailActivity.class);
            intent.putExtra(CategoryDetailActivity.INTENT_CATEGORY_INDEX, position);
            intent.putExtra(CategoryDetailActivity.INTENT_CATEGORY_TYPE, type);
            categoryDetailActivityLauncher.launch(intent);
        }
    }

    ActivityResultLauncher<Intent> categoryDetailActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent intent = result.getData();
                        assert intent != null;
                        int index = intent.getIntExtra(CategoryDetailActivity.INTENT_CATEGORY_INDEX, -1);
                        assert index != -1;
                        int type = intent.getIntExtra(CategoryDetailActivity.INTENT_CATEGORY_TYPE, -1);
                        int changedIdx = intent.getIntExtra(CategoryDetailActivity.INTENT_CATEGORY_TYPE_CHANGED, -1);

                        if (type == CategoryDetailActivity.INTENT_CATEGORY_TYPE_IN) {
                            adapterIn.notifyItemChanged(index);
                            if (changedIdx != -1) {
                                adapterOut.notifyItemRemoved(changedIdx);
                            }
                        } else if (type == CategoryDetailActivity.INTENT_CATEGORY_TYPE_OUT) {
                            adapterOut.notifyItemChanged(index);
                            if (changedIdx != -1) {
                                adapterIn.notifyItemRemoved(changedIdx);
                            }
                        } else {
                            assert false;
                        }

                    }
                }
            });

    class BtnCategoryAddClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(CategoryMgmtActivity.this, CategoryDetailActivity.class);
            categoryDetailActivityLauncher.launch(intent);
        }
    }

}