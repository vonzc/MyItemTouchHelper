package com.example.myitemtouchhelper1108.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

public class CustomPopupWindow {
    private PopupWindow mPopupWindow;
    private View contentView;
    private static Context mContext;

    public CustomPopupWindow(Builder builder) {
        contentView = LayoutInflater.from(mContext).inflate(builder.contentViewId, null);
        mPopupWindow = new PopupWindow(contentView, builder.width, builder.height);
        //设置点击外部可以取消，必须和下面这个方法配合才有效
        mPopupWindow.setOutsideTouchable(true);
        //设置一个空背景,设置了这个背景之后，设置点击外部取消才有效
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //Popupwindow可以点击,PopupWindow弹出后，所有的触屏和物理按键都有PopupWindows处理。
        // 其他任何事件的响应都必须发生在PopupWindow消失之后， （home  等系统层面的事件除外）。
        // 比如这样一个PopupWindow出现的时候，按back键首先是让PopupWindow消失，
        // 第二次按才是退出activity，
        mPopupWindow.setFocusable(true);
        mPopupWindow.setAnimationStyle(builder.animStyle);//设置动画效果
    }

    //关闭popupWindow
    public void dismiss() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }
    //显示位置的方法，相对于窗体的位置
    public CustomPopupWindow showAtLocation(View view,int gravity, int x, int y) {
        if (mPopupWindow != null) {
            mPopupWindow.showAtLocation(view, gravity, x, y);
        }
        return this;
    }

    //根据id寻找view
    public View getItemView(int viewId) {
        if (mPopupWindow != null) {
            return contentView.findViewById(viewId);
        }
        return null;
    }

    public void setOnClickListener(int viewId, View.OnClickListener listener) {
        View view = getItemView(viewId);
        view.setOnClickListener(listener);
    }

    public void setOnDismissListener(PopupWindow.OnDismissListener listener) {
        mPopupWindow.setOnDismissListener(listener);
    }

    public static class Builder {
        private int contentViewId;
        private int width;
        private int height;
        private int animStyle;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder setContentViewId(int contentViewId) {
            this.contentViewId = contentViewId;
            return this;
        }

        public Builder setWidth(int width) {
            this.width = width;
            return this;
        }

        public Builder setHeight(int height) {
            this.height = height;
            return this;
        }

        public Builder setAnimStyle(int animStyle) {
            this.animStyle = animStyle;
            return this;
        }

        public CustomPopupWindow build() {
            return new CustomPopupWindow(this);
        }
    }
}
