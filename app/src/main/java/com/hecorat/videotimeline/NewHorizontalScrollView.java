package com.hecorat.videotimeline;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 * Created by bkmsx on 01/11/2016.
 */
public class NewHorizontalScrollView extends HorizontalScrollView{

    float startY;


    public NewHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e)
    {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = e.getX();
                log("scrollview intercept down");
                break;
            case MotionEvent.ACTION_MOVE:
                log("scrollview intercept move");
                break;
            case MotionEvent.ACTION_UP:
                log("scrollview intercept up");
                break;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                log("scrollview ontouch down");
                break;
            case MotionEvent.ACTION_MOVE:
                log("scrollview ontouch move");
                break;
            case MotionEvent.ACTION_UP:
                log("scrollview ontouch up");
                break;
        }
        return super.onTouchEvent(e);
    }

    private void log(String msg){
        Log.e("Horizotal Scrollview", msg);
    }
}
