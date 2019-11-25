package com.example.myitemtouchhelper1108.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.myitemtouchhelper1108.R;

public class MyBookView extends LinearLayout {
    private TextView mTvSelect, mTvSelectBlue;

    public MyBookView(Context context) {
        super(context);
    }

    public MyBookView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyBookView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        addSelectButtonToLayout();
    }

    private void addSelectButtonToLayout() {
        mTvSelect = new TextView(getContext());
        LinearLayout.LayoutParams params = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mTvSelect.setLayoutParams(params);
        mTvSelect.setGravity(Gravity.CENTER);
        mTvSelect.setText("未选择");
        mTvSelect.setTextColor(Color.BLACK);
        mTvSelect.setTextSize(16);
        addView(mTvSelect);

        mTvSelectBlue = new TextView(getContext());
        mTvSelectBlue.setLayoutParams(params);
        mTvSelectBlue.setGravity(Gravity.CENTER);
        mTvSelectBlue.setText("已选择");
        mTvSelectBlue.setTextColor(Color.BLUE);
        mTvSelectBlue.setTextSize(16);
        addView(mTvSelectBlue);
    }

    public void setSelectButtonType (int selectButtonType) {
        switch (selectButtonType) {
            case 0:
                //都不显示状态
                mTvSelect.setVisibility(View.INVISIBLE);
                mTvSelectBlue.setVisibility(View.GONE);
                break;
            case 1:
                //显示未选择状态
                mTvSelect.setVisibility(View.VISIBLE);
                mTvSelectBlue.setVisibility(View.GONE);
                break;
            case 2:
                //显示选中状态
                mTvSelect.setVisibility(View.GONE);
                mTvSelectBlue.setVisibility(View.VISIBLE);
                break;
                default:
                    break;
        }
    }
    /*//展示单个选择按钮
    public void showSelectButton() {
        if(isSelectButtonVisible()) {
            return;
        }
        mTvSelect.setVisibility(View.VISIBLE);
    }
    //隐藏单个选择按钮
    public void hideSelectButton() {
        if(!isSelectButtonVisible() && !isSelectButtonBlueVisible()) {
            return;
        }
        mTvSelect.setVisibility(View.GONE);
        mTvSelectBlue.setVisibility(View.GONE);
    }*/

    public void changeSelectButton() {
        if (isSelectButtonBlueVisible()) {
            setSelectButtonType(1);
        } else {
            setSelectButtonType(2);
        }
    }


    //当前选择按钮是否显示中
    public boolean isSelectButtonVisible() {
        return mTvSelect.getVisibility() == View.VISIBLE;
    }

    public boolean isSelectButtonBlueVisible() {
        return mTvSelectBlue.getVisibility() == View.VISIBLE;
    }
}
