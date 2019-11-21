package com.example.myitemtouchhelper1108;

/*
* MainActivity与MyItemTouchHelperCallBack之间的通信
* */
public interface NewItemGroupListener {
    void newItemGroup(int currentPostition, int targetPosition);
}
