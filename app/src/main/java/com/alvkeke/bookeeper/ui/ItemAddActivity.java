package com.alvkeke.bookeeper.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.helper.widget.Flow;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.alvkeke.bookeeper.R;
import com.alvkeke.bookeeper.data.BookManager;

import java.util.ArrayList;


public class ItemAddActivity extends AppCompatActivity {

    private final BookManager bookManager = BookManager.getInstance();
    private Spinner spinner;
    private Button btnItemOk, btnItemCancel, btnItemDate, btnItemTime, btnItemTags;
    private RadioGroup radioItemInout;
    private EditText editItemMoney;

    private ArrayList<String> currentTags = new ArrayList<>();

    class OnItemAddOk implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Log.d("ItemAdd", spinner.getSelectedItem().toString());
            int btnId = radioItemInout.getCheckedRadioButtonId();
            switch (btnId) {
                case R.id.item_add_radio_in:
                    Log.d("ItemAdd", "Income");
                    break;
                case R.id.item_add_radio_out:
                    Log.d("ItemAdd", "Outcome");
                    break;
                default:
                    Log.d("ItemAdd", "No Selected Item");
            }
            Log.d("ItemAdd", "Money: " + editItemMoney.getText().toString());
            StringBuilder sb = new StringBuilder();
            for (String s : currentTags) {
                sb.append(s);
                sb.append(" ");
            }
            Log.d("ItemAdd", "Tags: " + sb);
        }
    }

    class OnTagSelectOk implements View.OnClickListener {

        PopupWindow popupWindow;
        ArrayList<CheckBox> checkBoxes;
        public OnTagSelectOk(PopupWindow popup, ArrayList<CheckBox> cbList) {
            this.popupWindow = popup;
            this.checkBoxes = cbList;
        }

        @Override
        public void onClick(View view) {
            currentTags.clear();
            for (CheckBox cb : checkBoxes) {
                if (cb.isChecked()) {
                    currentTags.add(cb.getText().toString());
                }
            }
            popupWindow.dismiss();
        }
    }

    class OnTagSelectCancel implements View.OnClickListener {

        PopupWindow popupWindow;
        public OnTagSelectCancel(PopupWindow popup) {
            this.popupWindow = popup;
        }

        @Override
        public void onClick(View view) {
            popupWindow.dismiss();
        }
    }

    class OnItemTagsPopup implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Log.e("ItemAdd", "show tags pop window");
            View v = LayoutInflater.from(ItemAddActivity.this)
                    .inflate(R.layout.popup_tags, null, false);

            ConstraintLayout tags_layout = v.findViewById(R.id.popup_tags_container);
            Flow flow = v.findViewById(R.id.popup_tags_flow);
            ArrayList<CheckBox> cbList = new ArrayList<>();
            Button btnTagsOk = v.findViewById(R.id.popup_tags_tag_ok);
            Button btnTagCancel = v.findViewById(R.id.popup_tags_tag_cancel);

            ArrayList<String> tags = bookManager.getTags();
            for (int i=0; i<tags.size(); i++) {
                CheckBox cb = new CheckBox(ItemAddActivity.this);
                cb.setText(tags.get(i));
                cb.setId(View.generateViewId());
                for (String t_cur : currentTags) {
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
                    ViewGroup.LayoutParams.WRAP_CONTENT, true);
            Drawable background = ContextCompat.getDrawable(ItemAddActivity.this,
                    R.drawable.round_coner_popup_tags);
            popup.setBackgroundDrawable(background);
            popup.showAtLocation(v, Gravity.CENTER, 0, 0);


            btnTagsOk.setOnClickListener(new OnTagSelectOk(popup, cbList));
            btnTagCancel.setOnClickListener(new OnTagSelectCancel(popup));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_add);

        spinner = findViewById(R.id.item_add_cate_box);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                bookManager.getCategories());
        spinner.setAdapter(adapter);

        btnItemOk = findViewById(R.id.item_add_ok_btn);
        btnItemCancel = findViewById(R.id.item_add_cancel_btn);
        btnItemDate = findViewById(R.id.item_add_date_btn);
        btnItemTime = findViewById(R.id.item_add_time_btn);
        btnItemTags = findViewById(R.id.item_add_tags_btn);
        radioItemInout = findViewById(R.id.item_add_radio_group);
        editItemMoney = findViewById(R.id.item_add_money_box);

        btnItemOk.setOnClickListener(new OnItemAddOk());
        btnItemTags.setOnClickListener(new OnItemTagsPopup());
    }
}