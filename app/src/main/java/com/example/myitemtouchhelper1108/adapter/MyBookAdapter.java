package com.example.myitemtouchhelper1108.adapter;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.example.myitemtouchhelper1108.BookItemClickListener;
import com.example.myitemtouchhelper1108.ItemTouchMoveListener;
import com.example.myitemtouchhelper1108.R;
import com.example.myitemtouchhelper1108.model.BookBean;
import com.example.myitemtouchhelper1108.view.MyBookView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/*
* 书架的adapter
* */

public class MyBookAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchMoveListener {
    private List<BookBean> mList;
    private List<MyBookView>mBookViewList = new ArrayList<>();
    private BookItemClickListener mBookItemListener;
    public static final int TYPE_ONE = 1;//添加书本按钮的类型
    public static final int TYPE_TWO = 2;//普通书本类型
    public static final int TYPE_THREE = 3;//多本书合在一起的类型
    private ArrayList<Integer> allHaveSelectItem = new ArrayList<>();
    private ArrayList<Integer> allGroupPosition = new ArrayList<>();//所有文件夹的位置
    private boolean isAllselectOpen = false;

    public MyBookAdapter(List<BookBean> list, BookItemClickListener dragListener) {
        mList = list;
        mBookItemListener = dragListener;
    }

    //第一种ViewHolder，添加书本按钮的类型
    class MyViewHolder extends ViewHolder {
        private ImageView image;
        private TextView text;
        private ViewHolder mViewHolder;
        private int mPositon;
        //绑定控件
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.iv_image);
            text = itemView.findViewById(R.id.tv_text);
        }
        void bindData(ViewHolder holder,int position) {
            mViewHolder = holder;
            mPositon = position;
            text.setText(mList.get(position).getName());
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//类型一的点击事件
                    //TODO 实际项目中点击这里要跳转到添加的界面
                    Log.d("vonzc","onClick: " + mList.size());
                    addNewBook(mList.size()-1);//暂时-1是因为最后一个是添加按钮
                }
            });
        }
    }
    //第二种ViewHolder，普通书本类型
    class MyViewHolder2 extends ViewHolder {
        private ImageView mImageTwo;
        private TextView mTextTwo;
        private ViewHolder mViewHolder2;
        private int mPosition2;
        private MyBookView mBookView;
        //绑定控件
        public MyViewHolder2(@NonNull View itemView) {
            super(itemView);
            mImageTwo = itemView.findViewById(R.id.iv_image_two);
            mTextTwo = itemView.findViewById(R.id.tv_text_two);
            mBookView = itemView.findViewById(R.id.my_book_view);
        }
        void bindData(ViewHolder holder, int position) {
            mViewHolder2 = holder;
            mPosition2 = position;
            mTextTwo.setText(mList.get(position).getName());
            if (isAllselectOpen) {//设置按钮状态
                mBookView.setSelectButtonType(1);
            } else {
                mBookView.setSelectButtonType(0);
            }
            mImageTwo.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {//类型二的长按点击事件
                    Log.d("vonzc","onLongClick: 长按发生点击事件：" + mPosition2);
                    if (!isAllselectOpen) {
                        mBookItemListener.onStartDrag(mViewHolder2);
                        //出现最下面的选择图标
                        //mDragListener.selectItem(v, 2);
                        //showAllSelectButton();暂时不需要长按进入编辑 TODO
                        return true;
                    } else {
                        return false;
                    }
                }
            });
            mBookViewList.add(mBookView);
            mImageTwo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//类型二的点击事件
                    if (mBookView.isSelectButtonVisible() || mBookView.isSelectButtonBlueVisible()){
                        Log.d("vonzc","onClick: 选中了" + mPosition2);
                        mBookView.changeSelectButton();
                        if (mBookView.isSelectButtonBlueVisible()) {
                            allHaveSelectItem.add(mPosition2);
                        } else {
                            Iterator<Integer> iterator = allHaveSelectItem.iterator();//遍历
                            while (iterator.hasNext()) {
                                Integer integer = iterator.next();
                                if (integer == mPosition2) {
                                    iterator.remove();
                                }
                            }
                        }
                    }
                }
            });

        }
    }
    //第三种类型，文件夹类型（个人使用bookGroup之类的来命名）
    class MyViewHolder3 extends ViewHolder {
        private ImageView mImageThree;
        private TextView mTextThree;
        private ViewHolder mViewHolder3;
        private MyBookView mBookView3;
        private int mPosition3;

        public MyViewHolder3(@NonNull View itemView) {
            super(itemView);
            mImageThree = itemView.findViewById(R.id.iv_image_three);
            mTextThree = itemView.findViewById(R.id.tv_text_three);
            mBookView3 = itemView.findViewById(R.id.my_book_view_three);
        }

        void bindData(ViewHolder holder, int position) {
            mViewHolder3 = holder;
            mPosition3 = position;
            mTextThree.setText(mList.get(position).getName());
            if (isAllselectOpen) {//设置按钮状态
                mBookView3.setSelectButtonType(1);
            } else {
                mBookView3.setSelectButtonType(0);
            }
            mImageThree.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {//类型三的长按点击事件
                    if (!isAllselectOpen) {
                        mBookItemListener.onStartDrag(mViewHolder3);
                        //出现最下面的选择图标
                        //mDragListener.selectItem(v, 2);
                        //showAllSelectButton();暂时不需要长按进入编辑 TODO
                        return true;
                    } else {
                        return false;
                    }
                }
            });
            mBookViewList.add(mBookView3);
            mImageThree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//类型三的点击事件
                    if (mBookView3.isSelectButtonVisible() || mBookView3.isSelectButtonBlueVisible()){
                        Log.d("vonzc","onClick: 选中了" + mPosition3);
                        mBookView3.changeSelectButton();
                        if (mBookView3.isSelectButtonBlueVisible()) {
                            allHaveSelectItem.add(mPosition3);
                        } else {
                            Iterator<Integer> iterator = allHaveSelectItem.iterator();//遍历
                            while (iterator.hasNext()) {
                                Integer integer = iterator.next();
                                if (integer == mPosition3) {
                                    iterator.remove();
                                }
                            }
                        }
                    } else {
                        String title = mList.get(mPosition3).getName();
                        ArrayList nameList = mList.get(mPosition3).getNameList();
                        Log.d("vonzc11", "这个文件夹里有" + nameList);
                        mBookItemListener.showBookGroup(title, nameList);
                    }
                }
            });
        }
    }

    //注意ViewHolder的使用，不要粗心了
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_ONE:
                return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false));
            case TYPE_TWO:
                return new MyViewHolder2(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_two, parent, false));
            case TYPE_THREE:
                return new MyViewHolder3(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_three, parent, false));
        }
        return null;//TODO null不知道要不要处理一下
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
            ((MyViewHolder) holder).bindData(holder, position);
        } else if (holder instanceof MyViewHolder2) {
            ((MyViewHolder2) holder).bindData(holder, position);
        } else if (holder instanceof MyViewHolder3) {
            ((MyViewHolder3) holder).bindData(holder, position);
        }
    }
    //TODO 判断item的type,之后还有判断接受数据的逻辑,因为type是根据数据源来决定的
    @Override
    public int getItemViewType(int position) {
        if (mList.get(position).getName().contains("第10条")) {
            return TYPE_ONE;
        } else if (mList.get(position).getName().contains("文件夹")) {
            return TYPE_THREE;
        } else {
            return TYPE_TWO;
        }
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0:mList.size();
    }

    //实现接口ItemTouchMoveListener里的移动item方法
    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        //Collections.swap(mList, fromPostion, toPosition);这种交换数据的方法在有多种type的时候是会让效果不正确的
        //数据更换,将原位置的删除掉，在新的位置添加这个数据
        mList.add(toPosition,mList.remove(fromPosition));
        //交换位置的关键方法
        notifyItemMoved(fromPosition,toPosition);
    }

    //实现接口ItemTouchMoveListener里的刷新方法
    @Override
    public void refreshItem() {
        notifyDataSetChanged();
    }

    //实现接口ItemTouchMoveListener里的删除方法
    @Override
    public void onItemRemove(int position) {
        if (mList.size() > position) {
            mList.remove(position);
            notifyItemRemoved(position);
        }
    }

    private void addNewBook(int position) {
        BookBean mBookBean = new BookBean();
        mBookBean.setName("新书本");
        mList.add(position, mBookBean);
        notifyItemInserted(position);
    }
    //展示所有选择按钮
    public void showAllSelectButton() {
        if (!isAllselectOpen) {
            for (MyBookView myBookView : mBookViewList) {
                myBookView.setSelectButtonType(1);
            }
        }
        setAllSelect(true);
    }
    //隐藏所有选择按钮
    public void hideAllSelectButton() {
        if (isAllselectOpen) {
            for (MyBookView myBookView : mBookViewList) {
                myBookView.setSelectButtonType(0);
            }
        }
        setAllSelect(false);
    }
    //返回当前被选中的item的位置
    public ArrayList<Integer> getAllHaveSelectItem() {
        //从小到大排序
        Collections.sort(allHaveSelectItem);
        return allHaveSelectItem;
    }

    public void clearAllHaveSelectItem () {
        allHaveSelectItem.clear();
    }

    public void setAllSelect(boolean selectOpen) {
        isAllselectOpen = selectOpen;
    }
    //获取当前所有的文件夹的位置
    public ArrayList<Integer> getAllGroupPosition() {

        int i = 0;
        for (BookBean bean: mList) {
            if (bean.getBookType() == 3) {
                allGroupPosition.add(i);
            }
            i++;
        }
        return allGroupPosition;
    }

    public void clearAllGroupPosition() {
        allGroupPosition.clear();
    }
}
