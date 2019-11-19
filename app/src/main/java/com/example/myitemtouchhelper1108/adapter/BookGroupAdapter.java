package com.example.myitemtouchhelper1108.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myitemtouchhelper1108.R;


public class BookGroupAdapter extends RecyclerView.Adapter<BookGroupAdapter.ViewHolder> {

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mIvBookGroup;
        TextView mTvBookGroup;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mIvBookGroup = itemView.findViewById(R.id.iv_book_group);
            mTvBookGroup = itemView.findViewById(R.id.tv_book_group);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_group, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mIvBookGroup.setImageResource(R.mipmap.ic_launcher);
        holder.mTvBookGroup.setText("文件夹");
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
