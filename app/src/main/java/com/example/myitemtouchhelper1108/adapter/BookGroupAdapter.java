package com.example.myitemtouchhelper1108.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myitemtouchhelper1108.GroupSelectBookListener;
import com.example.myitemtouchhelper1108.R;
import com.example.myitemtouchhelper1108.view.CustomPopupWindow;

import java.util.ArrayList;


public class BookGroupAdapter extends RecyclerView.Adapter<BookGroupAdapter.ViewHolder> {

    private ArrayList<Integer> allGroupBook;
    private String groupName;
    private GroupSelectBookListener groupSelectBookListener;
    private CustomPopupWindow mCustomPopupWindow;

    public BookGroupAdapter (ArrayList<Integer> allBook, CustomPopupWindow customPopupWindow, GroupSelectBookListener listener) {
        allGroupBook = allBook;
        mCustomPopupWindow = customPopupWindow;
        groupSelectBookListener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mIvBookGroup;
        TextView mTvBookGroup;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mIvBookGroup = itemView.findViewById(R.id.iv_book_group);
            mTvBookGroup = itemView.findViewById(R.id.tv_book_group);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_group, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.mIvBookGroup.setImageResource(R.mipmap.ic_launcher);
        holder.mTvBookGroup.setText("文件夹:分组"  + position);
        Log.d("vonzc10", "BookGroupAdapter: allBook = " + allGroupBook);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupName = holder.mTvBookGroup.getText().toString();
                Log.d("vonzc9", "onClick: 合并" + groupName);
                mCustomPopupWindow.dismiss();
                groupSelectBookListener.groupSelectBook(groupName, allGroupBook);
            }
        });
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
