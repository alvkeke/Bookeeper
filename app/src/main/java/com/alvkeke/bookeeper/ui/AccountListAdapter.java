package com.alvkeke.bookeeper.ui;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alvkeke.bookeeper.R;
import com.alvkeke.bookeeper.data.Account;

import java.util.ArrayList;
import java.util.Locale;

public class AccountListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Account> accounts;
    private OnItemClickListener itemClickListener;
    private View.OnClickListener tailButtonClickListener = null;
    private String tailButtonName = "";

    public interface OnItemClickListener {
        void OnItemClick(View view, int position);
    }

    public AccountListAdapter(ArrayList<Account> accounts) {
        super();
        this.accounts = accounts;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_ACCOUNT: {
                View view = LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.listitem_account_item, parent, false);
                return new AccountViewHolder(view);
            }
            case VIEW_TYPE_TAIL_BUTTON: {
                View view = LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.listitem_account_tail_btn, parent, false);
                return new TailButtonViewHolder(view);
            }
        }
        Log.e(this.toString(), "View Type not found : " + viewType);
        assert false;   // panic
        return null;
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setTailButtonClickListener(View.OnClickListener tailButtonClickListener) {
        this.tailButtonClickListener = tailButtonClickListener;
    }

    public void setTailButtonName(String tailButtonName) {
        this.tailButtonName = tailButtonName;
    }

    private final int VIEW_TYPE_ACCOUNT = 0;
    private final int VIEW_TYPE_TAIL_BUTTON = 1;
    @Override
    public int getItemViewType(int position) {
        if (position == accounts.size())
            return VIEW_TYPE_TAIL_BUTTON;
        return VIEW_TYPE_ACCOUNT;
    }

    private void BindAccountViewHolder(AccountViewHolder holder, int position) {
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
    private void BindTailButtonViewHolder(TailButtonViewHolder holder, int pos) {
        holder.setButtonName(tailButtonName);
        holder.setClickListener(tailButtonClickListener);
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_ACCOUNT:
                BindAccountViewHolder((AccountViewHolder) holder, position);
                break;
            case VIEW_TYPE_TAIL_BUTTON:
                BindTailButtonViewHolder((TailButtonViewHolder) holder, position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return accounts.size() + 1;
    }

    public class AccountViewHolder extends RecyclerView.ViewHolder {
        private final View parent;
        private final TextView tvName;
        private final TextView tvBalance;
        public AccountViewHolder(@NonNull View itemView) {
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

    public class TailButtonViewHolder extends RecyclerView.ViewHolder {
        private final Button btn;
        public TailButtonViewHolder(@NonNull View itemView) {
            super(itemView);

            btn = itemView.findViewById(R.id.account_tail_button);
        }
        public void setClickListener(View.OnClickListener listener) {
            if (listener == null) return;
            btn.setOnClickListener(listener);
        }
        public void setButtonName(String name) {
            btn.setText(name);
        }
    }
}
