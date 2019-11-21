package com.example.myitemtouchhelper1108.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Service;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myitemtouchhelper1108.GroupSelectBookListener;
import com.example.myitemtouchhelper1108.adapter.BookGroupAdapter;
import com.example.myitemtouchhelper1108.adapter.MyItemTouchHelperCallback;
import com.example.myitemtouchhelper1108.NewItemGroupListener;
import com.example.myitemtouchhelper1108.R;
import com.example.myitemtouchhelper1108.StartDragListener;
import com.example.myitemtouchhelper1108.adapter.MyBookAdapter;
import com.example.myitemtouchhelper1108.model.BookBean;
import com.example.myitemtouchhelper1108.view.CustomPopupWindow;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements StartDragListener, NewItemGroupListener, View.OnClickListener, GroupSelectBookListener {
    private RecyclerView mRecyclerView, mWindowRecyclerView;
    private ArrayList<BookBean> mList;
    private LinearLayout mLinearLayoutNormal, mLinearLayoutEdit;
    private MyBookAdapter mAdapter;
    private ItemTouchHelper itemTouchHelper;
    private Toolbar mToolbar;
    private TextView mTvDownload, mTvGroup, mTvDelete;
    private CustomPopupWindow mCustomPopupWindow;
    private int windowWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();
    }

    private void initView() {
        mLinearLayoutNormal = findViewById(R.id.ll_bottom_normal);
        mLinearLayoutEdit = findViewById(R.id.ll_bottom_edit);
        mRecyclerView = findViewById(R.id.recycler_view);
        mToolbar = findViewById(R.id.toolbar);
        mTvDownload = findViewById(R.id.tv_download);
        mTvGroup = findViewById(R.id.tv_group);
        mTvDelete = findViewById(R.id.tv_delete);

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mAdapter = new MyBookAdapter(getData(), this);
        mRecyclerView.setAdapter(mAdapter);
        ItemTouchHelper.Callback callback = new MyItemTouchHelperCallback(mAdapter, this);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        setSupportActionBar(mToolbar);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        windowWidth = dm.widthPixels * 3/4;
    }

    private void initListener() {
        mTvDownload.setOnClickListener(this);
        mTvGroup.setOnClickListener(this);
        mTvDelete.setOnClickListener(this);
    }

    private List<BookBean> getData() {
        //recyclerview的数据
        mList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            BookBean mBookBean = new BookBean();
            mBookBean.setName("第" + i + "条");
            mList.add(mBookBean);
        }
        return  mList;
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        //震动效果
        Vibrator vibrator = (Vibrator)this.getSystemService(Service.VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(1000);
        }
        itemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void selectItem(View view, int position) {
        mLinearLayoutEdit.setVisibility(View.VISIBLE);
        mLinearLayoutNormal.setVisibility(View.GONE);
    }

    @Override//合并功能
    public void newItemGroup(int currentPosition, int targetPosition) {
        ArrayList<Integer> allgroupPosition = mAdapter.getAllGroupPosition();
        Log.d("vonzc67", "current = " + currentPosition + ", target = " + targetPosition);
        if (targetPosition == mAdapter.getItemCount() - 1) {
            Log.d("vonzck", "目标是最后一个，type为1");
        } else if (allgroupPosition.contains(targetPosition)) {
            Log.d("vonzc11", "目标是一个文件夹");
        }else {
            ArrayList<Integer> allGroupBook = new ArrayList<>();
            allGroupBook.add(currentPosition);
            allGroupBook.add(targetPosition);
            showGroupPopupWindow(allGroupBook);//展示合并书本的弹出框
        }
        mAdapter.clearAllGroupPosition();
    }
    //返回按键的事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mLinearLayoutEdit.getVisibility() == View.VISIBLE){
            mLinearLayoutEdit.setVisibility(View.GONE);
            mLinearLayoutNormal.setVisibility(View.VISIBLE);
            mAdapter.hideAllSelectButton();
            mAdapter.clearAllHaveSelectItem();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
    //创建右上角菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    //右上角的点击事件
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editor :
                mLinearLayoutEdit.setVisibility(View.VISIBLE);
                mLinearLayoutNormal.setVisibility(View.GONE);
                mAdapter.showAllSelectButton();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_download:
                showDownloadPopupWindow();//展示下方的缓存弹出框
                break;
            case R.id.tv_group:
                ArrayList<Integer> allGroup = mAdapter.getAllHaveSelectItem();
                if (allGroup.size() == 0) {
                    Toast.makeText(this, "请选择需要合并的书本", Toast.LENGTH_SHORT).show();
                } else {
                    //TODO 多本合并
                    showGroupPopupWindow(allGroup);
                }
                break;
            case R.id.tv_delete:
                ArrayList<Integer> allDelete = mAdapter.getAllHaveSelectItem();
                if (allDelete.size() == 0) {
                    Toast.makeText(this, "请选择需要删除的书本", Toast.LENGTH_SHORT).show();
                } else {
                    int i = 0;//连续删除的话每次数据会减少一个，为了删除正确的位置，每次删完，位置-1
                    for (Integer integer : allDelete) {
                        mAdapter.onItemRemove(integer - i);
                        i++;
                    }
                    mAdapter.notifyItemRangeChanged(0, mList.size());
                    mAdapter.setAllSelect(false);
                    mAdapter.clearAllHaveSelectItem();//清除保存的位置
                }
                break;
            case R.id.tv_close_window:
                mCustomPopupWindow.dismiss();
        }
    }
    //展示离线缓存的window
    private void showDownloadPopupWindow() {
        mCustomPopupWindow = new CustomPopupWindow.Builder(this)
                .setContentViewId(R.layout.popupwindow_download)
                .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .build();
        View parentView = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_main, null);
        mCustomPopupWindow.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
    }

    //展示合并书本的window
    private void showGroupPopupWindow(ArrayList<Integer> allGroupBook){
        mCustomPopupWindow = new CustomPopupWindow.Builder(this)
                .setContentViewId(R.layout.popupwindow_group)
                .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setWidth(windowWidth)
                .build();
        mCustomPopupWindow.setOnClickListener(R.id.tv_close_window, this);
        //配置popupWindow中的recyclerview
        initBookGroupAdapter(allGroupBook);
        //popupWIndow显示在MainActivity上
        View parentView = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_main, null);
        mCustomPopupWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
    }

    //合并书本的window中的recyclerview配置
    private void initBookGroupAdapter(ArrayList<Integer> allGroupBook) {
        mWindowRecyclerView = (RecyclerView) mCustomPopupWindow.getItemView(R.id.rv_popupwindow);
        mWindowRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        BookGroupAdapter bookGroupAdapter = new BookGroupAdapter(allGroupBook, mCustomPopupWindow, this);
        mWindowRecyclerView.setAdapter(bookGroupAdapter);
    }

    @Override//合并书本的实现
    public void groupSelectBook(String groupName, ArrayList<Integer> allGroupBook) {
        ArrayList<Integer> allGroupPosition = mAdapter.getAllGroupPosition();
        Log.d("vonzc11", "AllGroupPosition = " + allGroupPosition);
        Collections.sort(allGroupBook);
        int min = Collections.min(allGroupBook);
        boolean isHaveGroup = false;
        int groupPosition = 0;
        for (Integer integer: allGroupPosition) {//先进行位置的判断
            if (mList.get(integer).getName().equals(groupName)) {
                isHaveGroup = true;
                groupPosition = integer;
            }
        }
        ArrayList<String> nameList = new ArrayList<>();
        int i = 0;
        for (Integer integer: allGroupBook) {
            int position = integer;
            if (mList.size() > position) {
                String name = mList.get(position - i).getName();
                nameList.add(name);
                mAdapter.onItemRemove(position - i);
            }
            i++;
        }
        mAdapter.clearAllGroupPosition();//清除保存的文件夹位置
        if (isHaveGroup) {
            ArrayList<String> oldList = mList.get(groupPosition).getNameList();
            Log.d("vonzc", "oldList = " + oldList);
            oldList.addAll(nameList);
            mList.get(groupPosition).setNameList(oldList);
        } else {
            BookBean newBookGroup = new BookBean();
            newBookGroup.setName(groupName);
            newBookGroup.setNameList(nameList);
            newBookGroup.setBookType(3);
            mList.add(min, newBookGroup);
        }
        mAdapter.notifyItemRangeChanged(0, mList.size());
        mAdapter.clearAllHaveSelectItem();//清除保存的已选择位置
        Log.d("vonzc11", "finally: " + mList.size());
    }

}
