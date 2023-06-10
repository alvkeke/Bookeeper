package com.alvkeke.bookeeper.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alvkeke.bookeeper.R;
import com.alvkeke.bookeeper.data.Category;

import java.util.ArrayList;

public class CategoryListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Category> categories;
    private OnItemClickListener itemClickListener;

    public interface OnItemClickListener {
        void OnItemClick(View view, int position);
    }

    public CategoryListAdapter(ArrayList<Category> categories) {
        super();
        this.categories = categories;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Category e = categories.get(position);

        CategoryViewHolder holder1 = (CategoryViewHolder) holder;
        holder1.setName(e.getName());
        if (itemClickListener != null) {
            holder1.setHolderClickListener(v -> itemClickListener.OnItemClick(v, position));
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {

        private View parent;
        private TextView tvName;
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.category_item_container);
            tvName = itemView.findViewById(R.id.category_item_name);
        }

        public void setName(String name) {
            tvName.setText(name);
        }

        public void setHolderClickListener(View.OnClickListener listener) {
            parent.setOnClickListener(listener);
        }
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
