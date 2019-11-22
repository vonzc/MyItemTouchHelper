package com.example.myitemtouchhelper1108.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myitemtouchhelper1108.R;

import java.util.ArrayList;

public class MyGroupAdapter extends RecyclerView.Adapter<MyGroupAdapter.GroupViewHolder> {

    private ArrayList mBookNameList;

    public MyGroupAdapter(ArrayList nameList) {
        mBookNameList = nameList;
    }

    class GroupViewHolder extends RecyclerView.ViewHolder {
        ImageView mIvShowBook;
        TextView mTvShowBook;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            mIvShowBook = itemView.findViewById(R.id.iv_show_book);
            mTvShowBook = itemView.findViewById(R.id.tv_show_book);
        }
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_show_book, parent, false);
        GroupViewHolder holder = new GroupViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        holder.mIvShowBook.setImageResource(R.mipmap.ic_launcher);
        holder.mTvShowBook.setText(mBookNameList.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return mBookNameList == null ? 0:mBookNameList.size();    }


}
