package com.example.myitemtouchhelper1108;

public interface ItemTouchMoveListener {
    void onItemMove(int fromPosition, int toPosition);

    void refreshItem();

    void onItemRemove(int position);
}
