package com.example.myitemtouchhelper1108;

/*
* MyBookAdapter与MyItemTouchHelperCallback之间的通信
* */
public interface ItemTouchMoveListener {
    void onItemMove(int fromPosition, int toPosition);

    void refreshItem();

    void onItemRemove(int position);
}
