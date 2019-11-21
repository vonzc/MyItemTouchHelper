package com.example.myitemtouchhelper1108.model;

import java.util.ArrayList;

public class BookBean {
    private String name;
    private ArrayList<String> nameList;
    private int bookType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getNameList() {
        return nameList;
    }

    public void setNameList(ArrayList<String> nameList) {
        this.nameList = nameList;
    }

    public int getBookType() {
        return bookType;
    }

    public void setBookType(int bookType) {
        this.bookType = bookType;
    }
}
