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

    private final int COLOR_STYLE_FONT = 0;
    private final int COLOR_STYLE_BACKGROUND = 1;
    private int colorStyle = COLOR_STYLE_FONT;

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
        Log.e(this.toString(), "set item: " + position);

        holder.setMoney(item.getMoney());
        holder.setTime(item.getTime());
        holder.setCategory(item.getCategory());
        holder.setTags(item.getTags());
    }

    @Override
    public int getItemCount() {
        return bookItems.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        private final RelativeLayout parent;
        private final TextView tvMoney;
        private final TextView tvTime;
        private final TextView tvCategory;
        private final TextView tvTags;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            parent = itemView.findViewById(R.id.book_item_parent);
            tvMoney = itemView.findViewById(R.id.book_item_money);
            tvCategory = itemView.findViewById(R.id.book_item_category);
            tvTime = itemView.findViewById(R.id.book_item_time);
            tvTags = itemView.findViewById(R.id.book_item_tags);
        }


        private void setBackColorForMoney(int money) {
            final int COLOR_INCOME = Color.rgb(255, 236, 236);
            final int COLOR_EXPENSES = Color.rgb(223, 255, 229);
            if (money > 0) {
                parent.setBackgroundColor(COLOR_INCOME);
            } else if (money < 0) {
                parent.setBackgroundColor(COLOR_EXPENSES);
            } else {
                parent.setBackgroundColor(Color.TRANSPARENT);
            }
        }

        private void setFontColorForMoney(int money) {
            final int COLOR_INCOME = Color.GREEN;
            final int COLOR_EXPENSES = Color.RED;
            if (money > 0) {
                tvMoney.setTextColor(COLOR_INCOME);
            } else if (money < 0) {
                tvMoney.setTextColor(COLOR_EXPENSES);
            } else {
                tvMoney.setTextColor(Color.TRANSPARENT);
            }
        }

        private void setColorForMoney(int money) {
            if (colorStyle == COLOR_STYLE_FONT) {
                setFontColorForMoney(money);
            } else if (colorStyle == COLOR_STYLE_BACKGROUND) {
                setBackColorForMoney(money);
            }
        }

        public void setMoney(int money) {
            setColorForMoney(money);
            String sMoney;
            if (money > 0) {
                sMoney = "+" + money;
            } else if (money < 0) {
                sMoney = "" + money;
            } else {
                sMoney = "0";
            }
            tvMoney.setText(sMoney);
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
            StringBuilder sb = new StringBuilder();
            for (String e : tags) {
                sb.append(e);
                sb.append(", ");
            }

            tvTags.setText(sb.toString());
        }
    }

    public void setColorStyle(int colorStyle) {
        this.colorStyle = colorStyle;
    }

}
