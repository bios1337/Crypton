package com.example.bios.crypton.viewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class BottomNavigationViewPager extends ViewPager {

    private boolean isPagingEnabled;

    public BottomNavigationViewPager(Context context) {
        super(context);
        this.isPagingEnabled = true;
    }

    public BottomNavigationViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.isPagingEnabled = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public boolean executeKeyEvent(KeyEvent event) {
        return isPagingEnabled && super.executeKeyEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return false;
    }

}
