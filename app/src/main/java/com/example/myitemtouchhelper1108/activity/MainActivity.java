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

import com.example.myitemtouchhelper1108.adapter.BookGroupAdapter;
import com.example.myitemtouchhelper1108.adapter.MyItemTouchHelperCallback;
import com.example.myitemtouchhelper1108.NewItemGroupListener;
import com.example.myitemtouchhelper1108.R;
import com.example.myitemtouchhelper1108.StartDragListener;
import com.example.myitemtouchhelper1108.adapter.MyBookAdapter;
import com.example.myitemtouchhelper1108.model.BookBean;
import com.example.myitemtouchhelper1108.view.CustomPopupWindow;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements StartDragListener, NewItemGroupListener, View.OnClickListener {
    private RecyclerView mRecyclerView;
    private ArrayList<BookBean> mList;
    private LinearLayout mLinearLayoutNormal, mLinearLayoutEdit;
    private MyBookAdapter mAdapter;
    private ItemTouchHelper itemTouchHelper;
    private Toolbar mToolbar;
    private TextView mTvDelete, mTvDownload;
    private PopupWindow mPopupWindow;
    private CustomPopupWindow mCustomPopupWindow;

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
        mTvDelete = findViewById(R.id.tv_delete);
        mTvDownload = findViewById(R.id.tv_download);

        mPopupWindow = new PopupWindow(this);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mAdapter = new MyBookAdapter(getData(), this);
        mRecyclerView.setAdapter(mAdapter);
        ItemTouchHelper.Callback callback = new MyItemTouchHelperCallback(mAdapter, this);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        setSupportActionBar(mToolbar);
    }

    private void initListener() {
        mTvDelete.setOnClickListener(this);
        mTvDownload.setOnClickListener(this);
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
    public void NewItemGroup(int currentPosition, int targetPosition) {
        Log.d("vonzc67", "current = " + currentPosition + ", target = " + targetPosition);
        showGroupPopupWindow();//展示合并书本的弹出框
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
            case R.id.tv_delete:
                ArrayList<Integer> all = mAdapter.getAllHaveSelectItem();
                if (all.size() == 0) {
                    Toast.makeText(this, "请选择需要删除的书本", Toast.LENGTH_SHORT).show();
                } else {
                    int i = 0;//连续删除的话每次数据会减少一个，为了删除正确的位置，每次删完，位置-1
                    for (Integer integer : all) {
                        mAdapter.onItemRemove(integer - i);
                        i++;
                    }
                    //TODO 这里应该还要改，目前的动画效果不好
                    mAdapter.notifyItemRangeChanged(0, mList.size());
                    mAdapter.setAllSelect(false);
                    mAdapter.clearAllHaveSelectItem();//清除保存的位置
                }
                break;
            case R.id.tv_download:
                showDownloadPopupWindow();//展示下方的缓存弹出框
                break;
            case R.id.tv_close_window:
                mCustomPopupWindow.dismiss();
        }
    }
    //展示合并书本的window
    private void showGroupPopupWindow(){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels * 3/4;
        mCustomPopupWindow = new CustomPopupWindow.Builder(this)
                .setContentViewId(R.layout.popupwindow_group)
                .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setWidth(width)
                .build();
        mCustomPopupWindow.setOnClickListener(R.id.tv_close_window, this);
        RecyclerView mWindowRecyclerView = (RecyclerView) mCustomPopupWindow.getItemView(R.id.rv_popupwindow);
        mWindowRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        BookGroupAdapter bookGroupAdapter = new BookGroupAdapter();
        mWindowRecyclerView.setAdapter(bookGroupAdapter);
        View parentView = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_main, null);
        mCustomPopupWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
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
}
