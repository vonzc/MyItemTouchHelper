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
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myitemtouchhelper1108.BookItemClickListener;
import com.example.myitemtouchhelper1108.GroupSelectBookListener;
import com.example.myitemtouchhelper1108.adapter.BookGroupAdapter;
import com.example.myitemtouchhelper1108.adapter.MyGroupAdapter;
import com.example.myitemtouchhelper1108.adapter.MyItemTouchHelperCallback;
import com.example.myitemtouchhelper1108.NewItemGroupListener;
import com.example.myitemtouchhelper1108.R;
import com.example.myitemtouchhelper1108.adapter.MyBookAdapter;
import com.example.myitemtouchhelper1108.model.BookBean;
import com.example.myitemtouchhelper1108.view.CustomPopupWindow;


import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements BookItemClickListener, NewItemGroupListener, View.OnClickListener, GroupSelectBookListener, PopupWindow.OnDismissListener {
    private RecyclerView mRecyclerView, mBookGroupRecyclerView, mShowBookRecyclerView;
    private ArrayList<BookBean> mList;
    private ArrayList<String> mList2;
    private LinearLayout mLinearLayoutNormal, mLinearLayoutEdit;
    private MyBookAdapter mAdapter;
    private BookGroupAdapter mBookGroupAdapter;
    private MyGroupAdapter mGroupAdapter;
    private ItemTouchHelper itemTouchHelper;
    private Toolbar mToolbar;
    private TextView mTvDownload, mTvGroup, mTvDelete;
    private CustomPopupWindow mCustomPopupWindow;
    private int windowWidth;
    private ArrayList<Integer> allGroupNameSave;//从文件夹窗口换到新建窗口，暂存合并的书籍
    private EditText mEditTextGroupName;
    private View mParentView;
    private Animation animationIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();
        initAnimation();
    }

    private void initView() {
        mLinearLayoutNormal = findViewById(R.id.ll_bottom_normal);
        mLinearLayoutEdit = findViewById(R.id.ll_bottom_edit);
        mRecyclerView = findViewById(R.id.recycler_view);
        mToolbar = findViewById(R.id.toolbar);
        mTvDownload = findViewById(R.id.tv_download);
        mTvGroup = findViewById(R.id.tv_group);
        mTvDelete = findViewById(R.id.tv_delete);
        getData();
        getData2();

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mAdapter = new MyBookAdapter(mList, this);
        mRecyclerView.setAdapter(mAdapter);
        ItemTouchHelper.Callback callback = new MyItemTouchHelperCallback(mAdapter, this);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        setSupportActionBar(mToolbar);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        windowWidth = dm.widthPixels * 5/8;
        mParentView = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_main, null);
    }

    private void initListener() {
        mTvDownload.setOnClickListener(this);
        mTvGroup.setOnClickListener(this);
        mTvDelete.setOnClickListener(this);
    }

    private void initAnimation() {
        animationIn = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bottom_in);
        animationIn.setDuration(240);

    }

    private void getData() {//recyclerview的数据
        mList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            BookBean mBookBean = new BookBean();
            mBookBean.setName("第" + i + "条");
            mList.add(mBookBean);
        }
    }

    private void getData2() {//popupWindow里的recyclerview的数据
        mList2 = new ArrayList<>();
        for (int i = 0; i < 3 ; i++) {
            mList2.add("文件夹：" + i);
        }
    }

    @Override//开始拖动
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        //震动效果
/*        Vibrator vibrator = (Vibrator)this.getSystemService(Service.VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(1000);
        }*/
        itemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void selectItem(View view, int position) {
        mLinearLayoutEdit.setVisibility(View.VISIBLE);
        mLinearLayoutNormal.setVisibility(View.GONE);
    }

    @Override//展示合并书本的popupWindow
    public void showBookGroup(String title, ArrayList nameList ) {
        showBookInGroupPopupWindow(title, nameList);
    }

    @Override//拖动合并功能
    public void newItemGroup(int currentPosition, int targetPosition) {
        ArrayList<Integer> allgroupPosition = mAdapter.getAllGroupPosition();
        Log.d("vonzc", "current = " + currentPosition + ", target = " + targetPosition);
        if (targetPosition == mAdapter.getItemCount() - 1) {
            Log.d("vonzc", "目标是最后一个，type为1");
        } else if (allgroupPosition.contains(targetPosition)) {
            Log.d("vonzc", "目标是一个文件夹,暂时没有实现移动功能");
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
            mLinearLayoutNormal.startAnimation(animationIn);
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
                mLinearLayoutNormal.setVisibility(View.GONE);
                mLinearLayoutEdit.startAnimation(animationIn);
                mLinearLayoutEdit.setVisibility(View.VISIBLE);
                mAdapter.showAllSelectButton();
                break;
                default:
                    break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_download://缓存按钮
                if (mAdapter.getAllHaveSelectItem().size() == 0) {
                    Toast.makeText(this, "请选择需要缓存的书本", Toast.LENGTH_SHORT).show();
                } else {
                    showDownloadPopupWindow();//展示下方的缓存弹出框
                }
                break;
            case R.id.tv_download_confirm://确认缓存
                // TODO 执行缓存！
                Toast.makeText(this, "执行缓存！", Toast.LENGTH_SHORT).show();
                mAdapter.setAllSelect(false);
                mLinearLayoutEdit.setVisibility(View.GONE);
                mLinearLayoutNormal.setVisibility(View.VISIBLE);
                mAdapter.clearAllHaveSelectItem();//清除保存的位置
                mAdapter.notifyDataSetChanged();//执行缓存不会改变视图，所以用这个方法
                mCustomPopupWindow.dismiss();
                break;
            case R.id.tv_download_cancel://取消缓存
                mCustomPopupWindow.dismiss();
                break;
            case R.id.tv_group://打开合并窗口
                ArrayList<Integer> allGroup = mAdapter.getAllHaveSelectItem();
                if (allGroup.size() == 0) {
                    Toast.makeText(this, "请选择需要合并的书本", Toast.LENGTH_SHORT).show();
                } else {
                    //TODO 多本合并
                    showGroupPopupWindow(allGroup);
                }
                break;
            case R.id.tv_close_window://关闭合并窗口
                mCustomPopupWindow.dismiss();
                //allGroupNameSave.clear();//不清楚把这个删了怎么就allGruop为空了
                break;
            case R.id.ll_new_group://弹出新建文件夹窗口
                mCustomPopupWindow.dismiss();
                showNewNamePopupWindow();
                break;
            case R.id.tv_close_nameWindow://取消新建文件夹窗口
                mCustomPopupWindow.dismiss();
                showGroupPopupWindow(allGroupNameSave);
                break;
            case R.id.tv_confirm://确认生成新文件夹
                if ("".equals(mEditTextGroupName.getText().toString())) {
                    Toast.makeText(this, "请输入文件夹的名称", Toast.LENGTH_SHORT).show();
                    break;
                }
                String newGroupName = "文件夹：" + mEditTextGroupName.getText().toString();
                int end = mBookGroupAdapter.getItemCount();
                mBookGroupAdapter.addNewBookGroup(newGroupName, end);
                mCustomPopupWindow.dismiss();
                showGroupPopupWindow(allGroupNameSave);
                break;
            case R.id.tv_delete://移除按钮
                if (mAdapter.getAllHaveSelectItem().size() == 0) {
                    Toast.makeText(this, "请选择需要删除的书本", Toast.LENGTH_SHORT).show();
                } else {
                    showDeletePopupWindow();//展现删除的窗口
                }
                break;
            case R.id.tv_delete_item://删除所选item
                ArrayList<Integer> allDeleteItem = mAdapter.getAllHaveSelectItem();
                int i = 0;//连续删除的话每次数据会减少一个，为了删除正确的位置，每次删完，位置-1
                for (Integer integer : allDeleteItem) {
                    mAdapter.onItemRemove(integer - i);
                        i++;
                    }
                mAdapter.notifyItemRangeChanged(0, mList.size());
                mAdapter.setAllSelect(false);
                mLinearLayoutEdit.setVisibility(View.GONE);
                mLinearLayoutNormal.setVisibility(View.VISIBLE);
                mAdapter.clearAllHaveSelectItem();//清除保存的位置
                mCustomPopupWindow.dismiss();
                break;
            case R.id.tv_delete_cancel://取消删除
                mCustomPopupWindow.dismiss();
                break;
            case R.id.tv_close_book_show://关闭文件夹窗口
                mCustomPopupWindow.dismiss();
                break;
                default:
                    break;
        }
    }

    @Override
    public void onDismiss() {
        bgAlpha(1.0f);
    }
    //展示确认缓存的window
    private void showDownloadPopupWindow() {
        mCustomPopupWindow = new CustomPopupWindow.Builder(this)
                .setContentViewId(R.layout.popupwindow_download)
                .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .build();
        mCustomPopupWindow.setOnClickListener(R.id.tv_download_confirm, this);
        mCustomPopupWindow.setOnClickListener(R.id.tv_download_cancel, this);
        bgAlpha(0.618f);
        mCustomPopupWindow.setOnDismissListener(this);
        //popupWIndow显示在MainActivity上
        mCustomPopupWindow.showAtLocation(mParentView, Gravity.BOTTOM, 0, 0);
    }

    //展示合并书本的window
    private void showGroupPopupWindow(ArrayList<Integer> allGroupBook){
        mCustomPopupWindow = new CustomPopupWindow.Builder(this)
                .setContentViewId(R.layout.popupwindow_group)
                .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setWidth(windowWidth)
                .build();
        mCustomPopupWindow.setOnClickListener(R.id.tv_close_window, this);
        mCustomPopupWindow.setOnClickListener(R.id.ll_new_group, this);
        bgAlpha(0.618f);
        mCustomPopupWindow.setOnDismissListener(this);
        //配置popupWindow中的recyclerview
        initBookGroupAdapter(allGroupBook);
        allGroupNameSave = allGroupBook;
        //popupWIndow显示在MainActivity上
        mCustomPopupWindow.showAtLocation(mParentView, Gravity.CENTER, 0, 0);
    }

    //初始化合并书本窗口中的recyclerview
    private void initBookGroupAdapter(ArrayList<Integer> allGroupBook) {
        mBookGroupRecyclerView = (RecyclerView) mCustomPopupWindow.getItemView(R.id.rv_popupwindow);
        mBookGroupRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBookGroupAdapter = new BookGroupAdapter(mList2, allGroupBook, mCustomPopupWindow, this);
        mBookGroupRecyclerView.setAdapter(mBookGroupAdapter);
    }
    //展示新建书本文件夹的窗口
    private void showNewNamePopupWindow() {
        mCustomPopupWindow = new CustomPopupWindow.Builder(this)
                .setContentViewId(R.layout.popupwindow_new_name)
                .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setWidth(windowWidth)
                .build();
        mEditTextGroupName = (EditText) mCustomPopupWindow.getItemView(R.id.et_group_name);
        mCustomPopupWindow.setOnClickListener(R.id.tv_close_nameWindow, this);
        mCustomPopupWindow.setOnClickListener(R.id.tv_confirm, this);
        bgAlpha(0.618f);
        mCustomPopupWindow.setOnDismissListener(this);
        //popupWIndow显示在MainActivity上
        mCustomPopupWindow.showAtLocation(mParentView, Gravity.CENTER, 0, 0);
    }
    //展示确认删除的窗口
    private void showDeletePopupWindow() {
        mCustomPopupWindow = new CustomPopupWindow.Builder(this)
                .setContentViewId(R.layout.popupwindow_delete)
                .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .build();
        mCustomPopupWindow.setOnClickListener(R.id.tv_delete_item, this);
        mCustomPopupWindow.setOnClickListener(R.id.tv_delete_cancel, this);
        bgAlpha(0.618f);
        mCustomPopupWindow.setOnDismissListener(this);
        //popupWIndow显示在MainActivity上
        mCustomPopupWindow.showAtLocation(mParentView, Gravity.BOTTOM, 0, 0);
    }
    //展示文件夹内部书本的window
    private void showBookInGroupPopupWindow(String title, ArrayList nameList) {
        mCustomPopupWindow = new CustomPopupWindow.Builder(this)
                .setContentViewId(R.layout.popupwindow_show_book)
                .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setWidth(ViewGroup.LayoutParams.WRAP_CONTENT)
                .build();
        TextView mShowBookTitle = (TextView) mCustomPopupWindow.getItemView(R.id.tv_show_book_title);
        mShowBookTitle.setText(title);
        mCustomPopupWindow.setOnClickListener(R.id.tv_close_book_show, this);
        initMyGroupAdapter(nameList);
        bgAlpha(0.618f);
        mCustomPopupWindow.setOnDismissListener(this);
        //popupWIndow显示在MainActivity上
        mCustomPopupWindow.showAtLocation(mParentView, Gravity.CENTER, 0, 0);
    }
    //初始化文件夹窗口的recyclerview
    private void initMyGroupAdapter(ArrayList nameList){
        mShowBookRecyclerView = (RecyclerView) mCustomPopupWindow.getItemView(R.id.rv_show_book);
        mShowBookRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mGroupAdapter = new MyGroupAdapter(nameList);
        mShowBookRecyclerView.setAdapter(mGroupAdapter);
    }
    @Override//合并书本的实现
    public void groupSelectBook(String groupName, ArrayList<Integer> allGroupBook) {
        ArrayList<Integer> allGroupPosition = mAdapter.getAllGroupPosition();
        Log.d("vonzc", "groupName = " + groupName + ", allGroupBook = " + allGroupBook +", AllGroupPosition = " + allGroupPosition);
        Collections.sort(allGroupBook);
        int min = Collections.min(allGroupBook);
        boolean isHaveGroup = false;
        int groupPosition = 0;
        for (Integer integer: allGroupPosition) {//先进行位置的判断，将存在的文件夹名称与目标文件夹名称对比
            if (mList.get(integer).getName().equals(groupName)) {
                isHaveGroup = true;//表明目标是一个文件夹
                groupPosition = integer;
            }
        }
        ArrayList<String> nameList = new ArrayList<>();
        int i = 0;
        for (Integer integer: allGroupBook) {//将被合并的移除
            int position = integer;
            if (mList.size() > position - i) {
                String name = mList.get(position - i).getName();
                nameList.add(name);
                mAdapter.onItemRemove(position - i);
            }
            if (isHaveGroup && position - i < groupPosition) {//如果文件夹在书本的后面，groupPosition会递减，所以要判断
                groupPosition = groupPosition - 1;
            }
            i++;
        }
        mAdapter.clearAllGroupPosition();//清除保存的文件夹位置
        if (isHaveGroup) {//如果目标文件夹当前已经存在
            ArrayList<String> oldList = mList.get(groupPosition).getNameList();
            Log.d("vonzc", "oldList = " + oldList);
            oldList.addAll(nameList);//TODO oldList可能为null的异常
            mList.get(groupPosition).setNameList(oldList);
        } else {//合并的文件夹之前不存在的情况
            BookBean newBookGroup = new BookBean();
            newBookGroup.setName(groupName);
            newBookGroup.setNameList(nameList);
            newBookGroup.setBookType(3);
            mList.add(min, newBookGroup);
        }
        //执行完成后刷新之类的操作
        mAdapter.notifyItemRangeChanged(0, mList.size());
        mAdapter.setAllSelect(false);//退出编辑状态
        mLinearLayoutEdit.setVisibility(View.GONE);
        mLinearLayoutNormal.setVisibility(View.VISIBLE);
        mAdapter.clearAllHaveSelectItem();//清除保存的已选择位置
        Log.d("vonzc", "finally: " + mList.size());
    }

    //调整透明度
    private void bgAlpha(float f) {
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.alpha = f;
        getWindow().setAttributes(layoutParams);
    }
}
