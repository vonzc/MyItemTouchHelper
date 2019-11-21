package com.example.myitemtouchhelper1108;

import java.util.ArrayList;

/*
* MainActivity与BookGroupAdapter之间的通信
* */
public interface GroupSelectBookListener {
    void groupSelectBook(String groupName, ArrayList<Integer> allGroupBook);
}
