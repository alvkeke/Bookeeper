package com.alvkeke.bookeeper.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alvkeke.bookeeper.R;
import com.alvkeke.bookeeper.data.Account;
import com.alvkeke.bookeeper.data.BookManager;
import com.alvkeke.bookeeper.storage.StorageManager;

import java.util.Locale;

public class AccountDetailActivity extends AppCompatActivity {

    public static final String INTENT_ACCOUNT_INDEX = "INTENT_ACCOUNT_INDEX";
    private final BookManager bookManager = BookManager.getInstance();
    private int targetIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_detail);


        Intent intent = getIntent();
        targetIndex = intent.getIntExtra(INTENT_ACCOUNT_INDEX, -1);

        Button btnOK, btnCancel;
        EditText etName, etBalance;

        btnOK = findViewById(R.id.btn_account_ok);
        btnCancel = findViewById(R.id.btn_account_cancel);
        etName = findViewById(R.id.account_name);
        etBalance = findViewById(R.id.account_balance);

        Log.e(this.toString(), "targetIndex: " + targetIndex);

        if (targetIndex != -1) {
            BookManager manager = BookManager.getInstance();
            Account e = manager.getAccounts().get(targetIndex);
            etName.setText(e.getName());
            long balance = e.getBalance();
            String s_balance = String.format(Locale.getDefault(),
                    "%d.%02d", balance/100, Math.abs(balance)%100);
            etBalance.setText(s_balance);
        }


        btnCancel.setOnClickListener(v -> finish());
        btnOK.setOnClickListener(v -> {
            String newName = String.valueOf(etName.getText());
            long balance;
            boolean is_neg = false;
            String s_balance = String.valueOf(etBalance.getText());

            int idx = s_balance.lastIndexOf('-');
            switch (idx) {
                case 0:
                    is_neg = true;
                    s_balance = s_balance.substring(1);
                case -1:
                    break;
                default:
                    Toast.makeText(this, "Incorrect Number!!", Toast.LENGTH_SHORT).show();
                    return;
            }
            if (s_balance.indexOf('.') != s_balance.lastIndexOf('.')) {
                Toast.makeText(this, "Incorrect Number!!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (s_balance.length() == 0) {
                balance = 0;
            } else if (s_balance.indexOf('.') == -1) {
                balance = Long.parseLong(s_balance) * (is_neg ? -1 : 1) * 100;
            } else {
                String[] ss = s_balance.split("\\.");
                balance = Long.parseLong(ss[0]) * 100;
                if (ss.length == 2) {
                    if (ss[1].length() < 2) {
                        balance += Long.parseLong(ss[1]) * 10;
                    } else {
                        balance += Long.parseLong(ss[1].substring(0, 2));
                    }
                    if (is_neg) {
                        balance *= -1;
                    }
                }
            }

            StorageManager sm = StorageManager.getInstance(this);
            Account e;
            if (targetIndex == -1) {
                e = new Account(newName);
                targetIndex = BookManager.getInstance().getAccounts().size();
                bookManager.getAccounts().add(e);
                e.setBalance(balance);
                sm.addAccount(e.getName(), balance);
            } else {
                e = BookManager.getInstance().getAccounts().get(targetIndex);
                e.setName(newName);
                e.setBalance(balance);
                sm.modifyAccount(e);
            }

            Intent intentResult = new Intent();
            intentResult.putExtra(INTENT_ACCOUNT_INDEX, targetIndex);
            AccountDetailActivity.this.setResult(RESULT_OK, intentResult);
            AccountDetailActivity.this.finish();
        });

    }
}