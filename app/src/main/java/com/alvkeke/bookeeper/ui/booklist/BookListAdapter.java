package com.alvkeke.bookeeper.ui.booklist;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alvkeke.bookeeper.R;
import com.alvkeke.bookeeper.data.BookItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.ViewHolder> {

    private final ArrayList<BookItem> bookItems;
    private OnItemClickListener itemClickListener;
    private OnItemLongClickListener itemLongClickListener;

    public interface OnItemClickListener {
        void OnItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        boolean OnItemLongClick(View view, int position);
    }

    enum MoneyColorStyle {
        COLOR_STYLE_FONT,
        COLOR_STYLE_BACKGROUND,
    }
    private MoneyColorStyle colorStyle = MoneyColorStyle.COLOR_STYLE_FONT;

    public BookListAdapter(ArrayList<BookItem> bookItems) {
        super();
        this.bookItems = bookItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.listitem_book_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookItem item = bookItems.get(position);
        if (item == null) {
            Log.e(this.toString(), "Cannot get BookItem in pos: " + position);
            return;
        }

        holder.setAccount(item.getAccount());
        holder.setMoney(item.getMoney(), item.getMoneyString());
        holder.setTime(item.getTime());
        holder.setCategory(item.getCategory());
        holder.setTags(item.getTags());
        if (itemClickListener != null) {
            holder.setHolderClickListener(view -> itemClickListener.OnItemClick(view, position));
        }
        if (itemLongClickListener != null) {
            holder.setHolderLongClickListener(view ->
                    itemLongClickListener.OnItemLongClick(view, position));
        }
    }

    @Override
    public int getItemCount() {
        return bookItems.size();
    }

    public void setColorStyle(MoneyColorStyle colorStyle) {
        this.colorStyle = colorStyle;
    }

    public void setItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setItemLongClickListener(OnItemLongClickListener listener) {
        this.itemLongClickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private final RelativeLayout parent;
        private final TextView tvAccount;
        private final TextView tvMoney;
        private final TextView tvTime;
        private final TextView tvCategory;
        private final TextView tvTags;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            parent = itemView.findViewById(R.id.book_item_parent);
            tvAccount = itemView.findViewById(R.id.book_item_account);
            tvMoney = itemView.findViewById(R.id.book_item_money);
            tvCategory = itemView.findViewById(R.id.book_item_category);
            tvTime = itemView.findViewById(R.id.book_item_time);
            tvTags = itemView.findViewById(R.id.book_item_tags);
        }

        public void setHolderClickListener(View.OnClickListener clickListener) {
            parent.setOnClickListener(clickListener);
        }

        public void setHolderLongClickListener(View.OnLongClickListener listener) {
            parent.setOnLongClickListener(listener);
        }

        /**
         * Set money for money filed
         * @param money real number of money
         * @param sMoney use specific string if sMoney != null, otherwise generate a new string
         */
        public void setMoney(int money, String sMoney) {
            setColorForMoney(money);
            if (sMoney != null) {
                tvMoney.setText(sMoney);
            } else {
                char c_sign;
                if (money < 0)
                    c_sign = '-';
                else
                    c_sign = '+';

                money = Math.abs(money);
                tvMoney.setText(String.format(Locale.getDefault(),
                        "%c%d.%02d", c_sign, money/100, money%100));
            }
        }
        private void setBackColorForMoney(int money) {
            final int COLOR_INCOME = Color.rgb(255, 236, 236);
            final int COLOR_OUTLAY = Color.rgb(223, 255, 229);
            if (money > 0) {
                parent.setBackgroundColor(COLOR_INCOME);
            } else {
                parent.setBackgroundColor(COLOR_OUTLAY);
            }
        }

        private void setFontColorForMoney(int money) {
            final int COLOR_INCOME = Color.GREEN;
            final int COLOR_OUTLAY = Color.RED;
            if (money > 0) {
                tvMoney.setTextColor(COLOR_INCOME);
            } else {
                tvMoney.setTextColor(COLOR_OUTLAY);
            }
        }

        private void setColorForMoney(int money) {
            if (colorStyle == MoneyColorStyle.COLOR_STYLE_FONT) {
                setFontColorForMoney(money);
            } else if (colorStyle == MoneyColorStyle.COLOR_STYLE_BACKGROUND) {
                setBackColorForMoney(money);
            }
        }

        public void setAccount(String account) {
            if (account != null) tvAccount.setText(account);
        }

        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINESE);
        private String formatTime(long time) {
            return sdf.format(new Date(time));
        }

        public void setTime(long time) {
            tvTime.setText(formatTime(time));
        }

        public void setCategory(String category) {
            tvCategory.setText(category);
        }

        public void setTags(ArrayList<String> tags) {
            if (tags == null)
                return;
            StringBuilder sb = new StringBuilder();
            for (String e : tags) {
                sb.append(e);
                sb.append(", ");
            }

            tvTags.setText(sb.toString());
        }
    }
}
