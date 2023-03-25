package com.alvkeke.bookeeper.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.helper.widget.Flow;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.alvkeke.bookeeper.R;
import com.alvkeke.bookeeper.data.BookItem;
import com.alvkeke.bookeeper.data.BookManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class ItemDetailActivity extends AppCompatActivity {

    public static final String INTENT_ITEM_INDEX = "INTENT_ITEM_INDEX";

    private final BookManager bookManager = BookManager.getInstance();
    private int targetIndex;
    private Spinner spinnerCategory, spinnerAccount;
    private Button btnItemOk, btnItemCancel, btnItemDate, btnItemTime, btnItemTags;
    private RadioGroup radioItemInout;
    private RadioButton radioIncome, radioOutlay;
    private EditText editItemMoney;
    private Date newItemDateTime;

    private ArrayAdapter<String> spinnerAdapterIncome, spinnerAdapterOutlay, spinnerAdapterAccount;
    private final ArrayList<String> selectedTags = new ArrayList<>();

    class OnItemAddCancel implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            ItemDetailActivity.this.setResult(RESULT_CANCELED, intent);
            ItemDetailActivity.this.finish();
        }
    }
    class OnItemAddOk implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String category = spinnerCategory.getSelectedItem().toString();
            String account = spinnerAccount.getSelectedItem().toString();
            String[] ssMoney = editItemMoney.getText().toString().split("\\.");
            if (ssMoney.length <= 0) {
                Log.e("ItemAdd", "Not a correct value of money");
                return;
            }
            int money = 0;
            if (ssMoney[0].length() != 0){
                money = Integer.parseInt(ssMoney[0]);
                money *= 100;
            }
            if (ssMoney.length>1) {
                String s_money_2nd = (ssMoney[1] + "00").substring(0, 2);
                money += Integer.parseInt(s_money_2nd);
            }
            int btnId = radioItemInout.getCheckedRadioButtonId();
            if (btnId == R.id.item_add_radio_out) {
                money *= -1;
            }
            BookItem item;
            if (targetIndex == -1) {
                item = new BookItem(money, newItemDateTime.getTime(), category, account);
                targetIndex = bookManager.getBookItems().size();
                bookManager.addBookItem(item);
            } else {
                if (targetIndex >= bookManager.getBookItems().size()) {
                    Log.e("ItemAdd", "index out of bound : " + targetIndex);
                    return;
                }
                item = bookManager.getBookItems().get(targetIndex);
                item.setMoney(money);
                item.setTime(newItemDateTime.getTime());
                item.setCategory(category);
                item.setAccount(account);
            }
            item.setTagList(selectedTags);

            Intent intent = new Intent();
            intent.putExtra(INTENT_ITEM_INDEX, targetIndex);
            ItemDetailActivity.this.setResult(RESULT_OK, intent);
            ItemDetailActivity.this.finish();
        }
    }

    class OnItemTagsPopup implements View.OnClickListener, View.OnLongClickListener {

        private void popup() {
            View v = LayoutInflater.from(ItemDetailActivity.this)
                    .inflate(R.layout.popup_tags, null, false);

            ConstraintLayout tags_layout = v.findViewById(R.id.popup_tags_container);
            Flow flow = v.findViewById(R.id.popup_tags_flow);
            Button btnTagsOk = v.findViewById(R.id.popup_tags_tag_ok);
            Button btnTagCancel = v.findViewById(R.id.popup_tags_tag_cancel);
            ArrayList<CheckBox> cbList = new ArrayList<>();

            ArrayList<String> tags = bookManager.getTags();
            for (int i=0; i<tags.size(); i++) {
                CheckBox cb = new CheckBox(ItemDetailActivity.this);
                cb.setText(tags.get(i));
                cb.setId(View.generateViewId());
                for (String t_cur : selectedTags) {
                    if (t_cur.equals(tags.get(i))) {
                        cb.setChecked(true);
                        break;
                    }
                }
                tags_layout.addView(cb, i);
                flow.addView(cb);
                cbList.add(cb);
            }

            PopupWindow popup = new PopupWindow(v,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, false);
            Drawable background = ContextCompat.getDrawable(ItemDetailActivity.this,
                    R.drawable.round_coner_popup_tags);
            popup.setBackgroundDrawable(background);
            popup.showAtLocation(v, Gravity.CENTER, 0, 0);

            btnTagsOk.setOnClickListener(new OnTagSelectOk(popup, cbList));
            btnTagCancel.setOnClickListener(new OnTagSelectCancel(popup));
        }

        @Override
        public void onClick(View view) {
            popup();
        }

        @Override
        public boolean onLongClick(View view) {
            selectedTags.clear();
            popup();
            return true;
        }

        private class OnTagSelectOk implements View.OnClickListener {

            PopupWindow popupWindow;
            ArrayList<CheckBox> checkBoxes;
            public OnTagSelectOk(PopupWindow popup, ArrayList<CheckBox> cbList) {
                this.popupWindow = popup;
                this.checkBoxes = cbList;
            }

            @Override
            public void onClick(View view) {
                selectedTags.clear();
                for (CheckBox cb : checkBoxes) {
                    if (cb.isChecked()) {
                        selectedTags.add(cb.getText().toString());
                    }
                }
                popupWindow.dismiss();
            }
        }

        private class OnTagSelectCancel implements View.OnClickListener {

            PopupWindow popupWindow;
            public OnTagSelectCancel(PopupWindow popup) {
                this.popupWindow = popup;
            }

            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        }

    }

    class OnInOutChanged implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            if (i == radioIncome.getId()) {
                spinnerCategory.setAdapter(spinnerAdapterIncome);
            } else if (i == radioOutlay.getId()) {
                spinnerCategory.setAdapter(spinnerAdapterOutlay);
            }
        }
    }

    class OnBtnDateClick implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

        @Override
        public void onClick(View view) {
            int year, month, day;

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(newItemDateTime);
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(ItemDetailActivity.this,
                    this, year, month, day);
            dialog.show();
        }
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            Calendar calendar = Calendar.getInstance();
            // to keep the hours/minutes data
            calendar.setTime(newItemDateTime);
            calendar.set(i, i1, i2);
            newItemDateTime = calendar.getTime();
            fillDateTimeBtnTitle(newItemDateTime);
        }
    }

    class OnBtnTimeClick implements View.OnClickListener, TimePickerDialog.OnTimeSetListener {

        @Override
        public void onClick(View view) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(newItemDateTime);
            TimePickerDialog dialog = new TimePickerDialog(ItemDetailActivity.this,
                    this,
                    calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
                    true);
            dialog.show();
        }

        @Override
        public void onTimeSet(TimePicker timePicker, int i, int i1) {
            Calendar calendar = Calendar.getInstance();
            // to keep the years/month/days data
            calendar.setTime(newItemDateTime);
            calendar.set(Calendar.HOUR_OF_DAY, i);
            calendar.set(Calendar.MINUTE, i1);
            newItemDateTime = calendar.getTime();
            fillDateTimeBtnTitle(newItemDateTime);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_add);

        spinnerCategory = findViewById(R.id.item_add_cate_box);
        spinnerAccount = findViewById(R.id.item_add_accounts);
        btnItemOk = findViewById(R.id.item_add_ok_btn);
        btnItemCancel = findViewById(R.id.item_add_cancel_btn);
        btnItemDate = findViewById(R.id.item_add_date_btn);
        btnItemTime = findViewById(R.id.item_add_time_btn);
        btnItemTags = findViewById(R.id.item_add_tags_btn);
        radioItemInout = findViewById(R.id.item_add_radio_group);
        radioOutlay = findViewById(R.id.item_add_radio_out);
        radioIncome = findViewById(R.id.item_add_radio_in);
        editItemMoney = findViewById(R.id.item_add_money_box);

        // initialize 2 spinner adapters
        spinnerAdapterOutlay = new ArrayAdapter<>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                bookManager.getOutlayCategories());
        spinnerAdapterIncome = new ArrayAdapter<>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                bookManager.getIncomeCategories());
        spinnerAdapterAccount = new ArrayAdapter<>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                bookManager.getAccounts());

        // outlay by default
        spinnerCategory.setAdapter(spinnerAdapterOutlay);
        spinnerAccount.setAdapter(spinnerAdapterAccount);

        radioItemInout.setOnCheckedChangeListener(new OnInOutChanged());
        btnItemOk.setOnClickListener(new OnItemAddOk());
        btnItemCancel.setOnClickListener(new OnItemAddCancel());
        btnItemTags.setOnClickListener(new OnItemTagsPopup());
        btnItemTags.setOnLongClickListener(new OnItemTagsPopup());
        btnItemDate.setOnClickListener(new OnBtnDateClick());
        btnItemTime.setOnClickListener(new OnBtnTimeClick());

        Intent intent = getIntent();
        targetIndex = intent.getIntExtra(INTENT_ITEM_INDEX, -1);
        if (targetIndex != -1) {
            // newDate will be update in this function
            fillItemData(targetIndex);
        } else {
            newItemDateTime = new Date();
        }
        fillDateTimeBtnTitle(newItemDateTime);

        // auto popup the input method
        if (editItemMoney.requestFocus()) {
            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    void fillDateTimeBtnTitle(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        btnItemDate.setText(String.format(Locale.getDefault(), "%04d-%02d-%02d",
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)));
        btnItemTime.setText(String.format(Locale.getDefault(), "%02d:%02d",
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
    }

    void fillItemData(int pos) {
        BookItem item = bookManager.getBookItems().get(pos);
        newItemDateTime = new Date(item.getTime());
        String sMoney = item.getMoneyString().substring(1);
        editItemMoney.setText(sMoney);
        for (int i = 0; i<spinnerAdapterAccount.getCount(); i++) {
            if (spinnerAdapterAccount.getItem(i).equals(item.getAccount())) {
                spinnerAccount.setSelection(i);
                break;
            }
        }

        ArrayAdapter<String> inoutAdapter;
        if (item.getMoney() > 0) {
            radioIncome.setChecked(true);
            inoutAdapter = spinnerAdapterIncome;
        } else {
            radioOutlay.setChecked(true);
            inoutAdapter = spinnerAdapterOutlay;
        }

        for (int i=0; i<inoutAdapter.getCount(); i++) {
            if (inoutAdapter.getItem(i).equals(item.getCategory())) {
                spinnerCategory.setSelection(i);
                break;
            }
        }

        selectedTags.addAll(item.getTags());
    }
}
