package com.alvkeke.bookeeper.ui;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alvkeke.bookeeper.R;
import com.alvkeke.bookeeper.data.Account;

import java.util.ArrayList;
import java.util.Locale;

public class AccountListAdapter extends RecyclerView.Adapter<AccountListAdapter.ViewHolder> {

    private ArrayList<Account> accounts;
    private OnItemClickListener itemClickListener;

    public interface OnItemClickListener {
        void OnItemClick(View view, int position);
    }

    public AccountListAdapter(ArrayList<Account> accounts) {
        super();
        this.accounts = accounts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.listitem_account_item, parent, false);
        return new ViewHolder(view);
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Account account = accounts.get(position);
        if (account == null) {
            Log.e(this.toString(), "Cannot get Account item in pos: " + position);
            return;
        }

        holder.setName(account.getName());
        holder.setBalance(account.getBalance());

        if (itemClickListener != null) {
            holder.setHolderClickListener(v -> itemClickListener.OnItemClick(v, position));
        }
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final View parent;
        private final TextView tvName;
        private final TextView tvBalance;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            parent = itemView.findViewById(R.id.account_item_parent);
            tvName = itemView.findViewById(R.id.account_item_name);
            tvBalance = itemView.findViewById(R.id.account_item_balance);
        }

        public void setName(String name) {
            tvName.setText(name);
        }

        public void setBalance(long balance) {
            boolean is_neg = false;
            if (balance < 0) {
                is_neg = true;
                balance = -balance;
            }
            tvBalance.setText(String.format(Locale.getDefault(),
                    "%s%d.%02d", is_neg?"-" : "", balance/100, balance%100));
        }
        public void setHolderClickListener(View.OnClickListener clickListener) {
            parent.setOnClickListener(clickListener);
        }
    }
}
