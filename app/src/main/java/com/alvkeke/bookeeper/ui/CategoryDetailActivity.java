package com.alvkeke.bookeeper.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alvkeke.bookeeper.R;
import com.alvkeke.bookeeper.data.BookManager;
import com.alvkeke.bookeeper.data.Category;
import com.alvkeke.bookeeper.storage.StorageManager;

import java.util.ArrayList;

public class CategoryDetailActivity extends AppCompatActivity {

    public static final String INTENT_CATEGORY_INDEX = "INTENT_CATEGORY_INDEX";
    public static final String INTENT_CATEGORY_TYPE_CHANGED = "INTENT_CATEGORY_TYPE_CHANGED";
    public static final String INTENT_CATEGORY_TYPE = "INTENT_CATEGORY_TYPE";
    public static final int INTENT_CATEGORY_TYPE_IN = 0;
    public static final int INTENT_CATEGORY_TYPE_OUT = 1;

    private EditText etName;
    private Button btnOk, btnCancel;
    private RadioGroup radioGroupType;
    private RadioButton radioButtonIn, radioButtonOut;

    private int targetIndex;
    private int targetType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail);

        etName = findViewById(R.id.category_name);
        btnOk = findViewById(R.id.btn_category_ok);
        btnCancel = findViewById(R.id.btn_category_cancel);
        radioGroupType = findViewById(R.id.radio_group_category_type);
        radioButtonIn = findViewById(R.id.radio_category_in);
        radioButtonOut = findViewById(R.id.radio_category_out);

        Intent intent = getIntent();
        targetIndex = intent.getIntExtra(INTENT_CATEGORY_INDEX, -1);
        targetType = intent.getIntExtra(INTENT_CATEGORY_TYPE, -1);

        BookManager manager = BookManager.getInstance();

        if (targetIndex != -1) {
            ArrayList<Category> list;
            if (targetType == INTENT_CATEGORY_TYPE_IN) {
                list = manager.getCategoriesIncome();
            } else if (targetType == INTENT_CATEGORY_TYPE_OUT) {
                list = manager.getCategoriesOutlay();
            } else {
                assert false; return;
            }
            Category e = list.get(targetIndex);
            etName.setText(e.getName());
            Category.CategoryType type = e.getType();
            switch (type) {
                case INCOME:
                    radioButtonIn.setChecked(true);
                    break;
                case OUTLAY:
                    radioButtonOut.setChecked(true);
                    break;
            }
        }

        btnOk.setOnClickListener(new OkClickListener());
        btnCancel.setOnClickListener(new CancelClickListener());
    }

    class CancelClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        }
    }

    class OkClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            BookManager manager = BookManager.getInstance();
            StorageManager storageManager = StorageManager.getInstance(CategoryDetailActivity.this);
            Intent intent = new Intent();

            Category.CategoryType categoryType;
            int radioId = radioGroupType.getCheckedRadioButtonId();
            if (radioId == R.id.radio_category_in) {
                categoryType = Category.CategoryType.INCOME;
                intent.putExtra(INTENT_CATEGORY_TYPE, INTENT_CATEGORY_TYPE_IN);
            } else if (radioId == R.id.radio_category_out) {
                categoryType = Category.CategoryType.OUTLAY;
                intent.putExtra(INTENT_CATEGORY_TYPE, INTENT_CATEGORY_TYPE_OUT);
            } else {
                Toast.makeText(CategoryDetailActivity.this,
                        "Please select category type", Toast.LENGTH_SHORT).show();
                return;
            }

            Category category;
            if (targetIndex == -1) {
                category = new Category(String.valueOf(etName.getText()), categoryType);
                if (categoryType == Category.CategoryType.INCOME) {
                    targetIndex = manager.getCategoriesIncome().size();
                    manager.getCategoriesIncome().add(category);
                } else {
                    targetIndex = manager.getCategoriesOutlay().size();
                    manager.getCategoriesOutlay().add(category);
                }
                category.setName(String.valueOf(etName.getText()));
                category.setId(storageManager.addCategory(category));
            } else {
                if (targetType == INTENT_CATEGORY_TYPE_IN) {
                    category = manager.getCategoriesIncome().get(targetIndex);
                    if (categoryType == Category.CategoryType.OUTLAY) {
                        intent.putExtra(INTENT_CATEGORY_TYPE_CHANGED, targetIndex);
                        manager.getCategoriesIncome().remove(category);
                        manager.getCategoriesOutlay().add(category);
                        targetIndex = manager.getCategoriesOutlay().size();
                    }
                } else {
                    category = manager.getCategoriesOutlay().get(targetIndex);
                    if (categoryType == Category.CategoryType.INCOME) {
                        intent.putExtra(INTENT_CATEGORY_TYPE_CHANGED, targetIndex);
                        manager.getCategoriesOutlay().remove(category);
                        manager.getCategoriesIncome().add(category);
                        targetIndex = manager.getCategoriesIncome().size();
                    }
                }
                category.setType(categoryType);
                category.setName(String.valueOf(etName.getText()));
                storageManager.modifyCategory(category);
            }

            intent.putExtra(INTENT_CATEGORY_INDEX, targetIndex);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

}