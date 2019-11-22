package com.example.myitemtouchhelper1108;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/*
* MainActivity与MyBookAdapter之间的通信
* */
public interface BookItemClickListener {
    //该接口用于需要主动回调拖拽效果的
    void onStartDrag(RecyclerView.ViewHolder viewHolder);

    void selectItem(View view, int position);

    void showBookGroup(String title, ArrayList nameList);
}
