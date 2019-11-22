package com.example.myitemtouchhelper1108.adapter;

import android.graphics.Canvas;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myitemtouchhelper1108.ItemTouchMoveListener;
import com.example.myitemtouchhelper1108.NewItemGroupListener;

import java.util.List;

import static com.example.myitemtouchhelper1108.adapter.MyBookAdapter.TYPE_ONE;

/*
* 实现RecyclerView移动item时的动画效果的类
* */

public class MyItemTouchHelperCallback extends ItemTouchHelper.Callback {
    private ItemTouchMoveListener moveListener;
    private NewItemGroupListener mNewItemGroupListener;
    private int currentPosition, targetPosition;//记录合并的两个item的position
    private boolean isMoved = false;//判段当前是否onMoved过了，用于clearview时执行刷新
    private boolean isGroup = false;//判断当前的移动距离是否可以合并
    private boolean canDrop = false;//判断当前是否属于canDropOver状态
    private int groupType = 0;//合并有三种类型，1为横向，2位竖向，3为斜向
    private String moveX = "";
    private String moveY = "";

    public MyItemTouchHelperCallback(ItemTouchMoveListener moveListener, NewItemGroupListener newItemGroupListener) {
        this.moveListener = moveListener;
        mNewItemGroupListener = newItemGroupListener;
    }

    //Callback回调监听时先调用的，用来判断当前是什么动作，比如判断方向（
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        //要监听的拖拽方向是哪两个方向
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        //要监听的swipe侧滑方向是哪个方向,0表示暂时不处理
        int swiperFlags = 0;
        return makeMovementFlags(dragFlags, swiperFlags);
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        // 监听侧滑，因为上面侧滑设置了0，暂时功能不能启用
        moveListener.onItemRemove(viewHolder.getAdapterPosition());
    }

    @Override
    public boolean isLongPressDragEnabled() {
        // 是否允许长按拖拽效果
        return false;
    }

    @Override
    public boolean canDropOver(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder current, @NonNull RecyclerView.ViewHolder target) {
        if (current.getItemViewType() == 2) {
            canDrop = true;
            Log.d("vonzc2", "canDropOver状态："+ currentPosition + "=====" + targetPosition + " " + canDrop );

        }
        return super.canDropOver(recyclerView, current, target);
    }

    @Override
    public RecyclerView.ViewHolder chooseDropTarget(@NonNull RecyclerView.ViewHolder selected, @NonNull List<RecyclerView.ViewHolder> dropTargets, int curX, int curY) {
        //在这里判断合并的两个位置
        if (selected.getItemViewType() == 2) {
            int positionX = 0, positionY = 0;//目标item在第X列，第Y行,因为默认为0，所以从1开始计数
            currentPosition = selected.getAdapterPosition();
            int adapterX = currentPosition % 3 + 1;//获取当前列数
            int adapterY = currentPosition / 3 + 1;//获取当前行数
            if (isGroup) {
            /*if (curX < width) {   /废弃方案
                positionX = 1;
            } else if (curX > width && curX < width * 7/3) {// 7/3指的是一个item的宽度加上之前限制的3/4个屏幕
                positionX = 2;
            } else if (curX > width * 7/3) {
                positionX = 3;
            }*/
                switch (groupType) {
                    case 1://横向移动
                        positionY = adapterY;
                        if (moveX.equals("bigX")) {
                            positionX = adapterX + 1;
                        } else if (moveX.equals("smallX")) {
                            positionX = adapterX - 1;
                        }
                        break;
                    case 2://竖向移动
                        positionX = adapterX;
                        if (moveY.equals("bigY")) {
                            positionY = adapterY + 1;
                        } else if (moveY.equals("smallY")) {
                            positionY = adapterY - 1;
                        }
                        break;
                    case 3://斜向移动
                        if (moveY.equals("bigY")) {
                            positionY = adapterY + 1;
                        } else if (moveY.equals("smallY")) {
                            positionY = adapterY - 1;
                        }
                        if (moveX.equals("bigX")) {
                            positionX = adapterX + 1;
                        } else if (moveX.equals("smallX")) {
                            positionX = adapterX - 1;
                        }
                        break;
                }
                //自己写的位置判断方法与chooseDropOver的调用时机不完全一致，所以可能会出现
                // 这个方法执行了，然而groupType不为自己设置的1或2或3，于是目标成了第0行，第0列，
                // 后面造成了数组中出现了-4这种数，于是造成了越界，所以这里避开都为0的情况
                if (positionX != 0 && positionY != 0) {
                    Log.d("vonzc6", "当前item在第" + positionY + "行" + ", 第" + positionX + "列");
                    targetPosition = (positionY - 1) * 3 + positionX - 1;
                }
            } else {
                Log.d("vonzc6", "暂时不能合并");
            }
            groupType = 0;
        }
        return super.chooseDropTarget(selected, dropTargets, curX, curY);
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null) {
            //Log.d("vonzc", "onSelectedChanged" );
            //选中的时候，放大图标
            viewHolder.itemView.setScaleX(1.3f);
            viewHolder.itemView.setScaleY(1.3f);
        } else {
            //这个方法在刚选中时调用，放开也会调用一次（此时viewHolder为空)，此时执行合并操作
            //Log.d("vonzc", "onSelectedChanged: 为空 " );
            if ( isGroup && canDrop) {
                //itemgroup(书本整合，将整合的item的位置传过去)
                mNewItemGroupListener.newItemGroup(currentPosition, targetPosition);
            }
            isGroup = false;
            canDrop = false;
            currentPosition = 0;
            targetPosition = 0;
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        if (target.getItemViewType() == TYPE_ONE) {
            return false;
        }
        canDrop = false;
        Log.d("vonzc","from = " + viewHolder.getAdapterPosition() + "to = " + target.getAdapterPosition() + "当前合并状态" + isGroup);
        moveListener.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onMoved(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, int fromPos, @NonNull RecyclerView.ViewHolder target, int toPos, int x, int y) {
        //移动完成了
        isMoved = true;
        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        //通俗点说是拖动的动画效果，打日志的话会看到一堆日志onDraw
        //Log.d("vonzc3", "onChildDraw: " + "dx = " + dX + ", dy = " + dY);
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        //大概是一小部分动画效果结束，打日志的话会看到一堆日志onChildDrawOver，和onChildDraw一样多
        float width = viewHolder.itemView.getWidth() * 3 / 4;
        float height = viewHolder.itemView.getHeight() * 3 / 4;
        float widthMin = viewHolder.itemView.getWidth() / 10;
        float heightMin = viewHolder.itemView.getHeight() / 10;

        if ((dX > width || dX < -width) && (dY < heightMin && dY > -heightMin)) {
            groupType = 1;
        }

        if ((dX < widthMin && dX > -widthMin) && (dY > height || dY < -height)) {
            groupType = 2;
        }

        if ((dX > width || dX < -width) && (dY > height || dY < -height)) {
            groupType = 3;
        }
        if (groupType == 1 || groupType == 2 || groupType == 3) {
            isGroup = true;
            //Log.d("vonzc5", "确认合并: width = " + width + "，dX = " + dX + "；height = " + height + ", dY = "+ dY + ",类型为：" + groupType);
        } else {
            isGroup = false;
            //Log.d("vonzc5", "不能合并: width = " + width + "，dX = " + dX + "；height = " + height + ", dY = "+ dY);
        }

        if (dX >= 0) {
            moveX = "bigX";//移动的横坐标增大
        } else {
            moveX = "smallX";//移动的横坐标减小
        }

        if (dY >= 0) {
            moveY = "bigY";//移动的纵坐标增大
        } else {
            moveY = "smallY";//移动的纵坐标减小
        }
        //Log.d("vonzcxy", "onChildDrawOver: " + "dx = " + dX + ", dy = " + dY +", width = " + viewHolder.itemView.getWidth() + ", height = " + viewHolder.itemView.getHeight());
        super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        //放开的最后会执行这里，将本来放大的图标变回去
        //Log.d("vonzc", "clearView: ");
        viewHolder.itemView.setScaleX(1f);
        viewHolder.itemView.setScaleY(1f);
        //刷新列表，在这里执行不会影响动画效果，并且还能正确刷新!!!!!
        if (isMoved) {
            moveListener.refreshItem();
        }
        isMoved = false;
        super.clearView(recyclerView, viewHolder);
    }

    @Override
    public float getMoveThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return super.getMoveThreshold(viewHolder);
    }
}
