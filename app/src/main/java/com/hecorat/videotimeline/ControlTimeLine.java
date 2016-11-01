package com.hecorat.videotimeline;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by bkmsx on 01/11/2016.
 */
public class ControlTimeLine extends ImageView {
    int width, height;
    int x, y;
    int thumbWidth = 30;
    int lineWeight = 4;
    int round = 10;
    int minPosition, maxPosition, currentMinPosition, currentMaxPosition;
    int start, end;
    int startTime, endTime;
    RectF thumbLeft, thumbRight;
    Rect lineAbove, lineBelow;
    Paint paint;
    LinearLayout.LayoutParams params;
    OnControlTimeLineChanged mOnControlTimeLineChanged;

    public ControlTimeLine(Context context, int x, int y, int widthTimeLine, int heightTimeLine) {
        super(context);
        this.x = x;
        this.y = y;
        width = widthTimeLine + thumbWidth * 2;
        height = heightTimeLine;
        minPosition = x;
        maxPosition = x + widthTimeLine;
        currentMaxPosition = maxPosition;
        currentMinPosition = 0;
        start = minPosition;
        end = maxPosition;
        updateLayout(minPosition, maxPosition);
        paint = new Paint();
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
        setLayoutParams(params);
        setOnTouchListener(onTouchListener);
        mOnControlTimeLineChanged = (OnControlTimeLineChanged) context;
    }

    private void updateLayout(int start, int end) {
        thumbLeft = new RectF(start - thumbWidth, 0, start, height);
        thumbRight = new RectF(end, 0, end + thumbWidth, height);
        lineAbove = new Rect(start - thumbWidth / 2, 0, end + thumbWidth / 2, lineWeight);
        lineBelow = new Rect(start - thumbWidth / 2, height - lineWeight, end + thumbWidth / 2, height);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.CYAN);
        canvas.drawRect(lineAbove, paint);
        canvas.drawRect(lineBelow, paint);
        canvas.drawRoundRect(thumbLeft, round, round, paint);
        canvas.drawRoundRect(thumbRight, round, round, paint);
    }

    public interface OnControlTimeLineChanged {
        void updateTimeLine(int start, int end);
    }

    private void log(String msg) {
        Log.e("Log for Control", msg);
    }

    OnTouchListener onTouchListener = new OnTouchListener() {
        float oldX, oldY, moveX, moveY;
        float epsX = 100;
        float epsY = 20;
        int touch = 0;
        int TOUCH_LEFT = 1;
        int TOUCH_RIGHT = 2;
        int startPosition, endPosition;
        boolean negativeTouch = false;

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    oldX = motionEvent.getX();
                    oldY = motionEvent.getY();
                    if (oldX < start + epsX && oldX > start - epsX && oldY > -epsY && oldY < height + epsY) {
                        touch = TOUCH_LEFT;
                    }
                    else if (oldX > end - epsX && oldX < end + epsX && oldY > -epsY && oldY < height + epsY) {
                        touch = TOUCH_RIGHT;
                    }
                    return true;
                case MotionEvent.ACTION_MOVE:
                    moveX = motionEvent.getX() - oldX;
                    moveY = motionEvent.getY() - oldY;
                    if (touch == TOUCH_LEFT) {
                        startPosition = start + (int) moveX;
                        endPosition = end;
                        if (startPosition > end) {
                            startPosition = end;
                        }
                        if (startPosition < minPosition) {

                            if (currentMinPosition == 0) {
                                startPosition = minPosition;
                            } else {
                                negativeTouch = true;
                            }
                        }
                        log("Left: current max position: " + currentMaxPosition);
                        log("Left: start: " + startPosition + " end: " + endPosition);
                        updateLayout(startPosition, endPosition);
                    }
                    if (touch == TOUCH_RIGHT) {
                        startPosition = start;
                        endPosition = end + (int) moveX;
                        if (endPosition > currentMaxPosition) {
                            endPosition = currentMaxPosition;
                        }
                        if (endPosition < minPosition) {
                            endPosition = minPosition;
                        }
                        log("Right: start: " + startPosition + " end: " + endPosition);
                        updateLayout(startPosition, endPosition);
                    }
                    return true;
                case MotionEvent.ACTION_UP:
                    int currentDuration = endPosition - startPosition;
                    start = minPosition;
                    end = start + currentDuration;
                    if (touch == TOUCH_LEFT) {
                        currentMaxPosition -= moveX;
                        currentMinPosition += moveX;
                    }
                    if (currentMaxPosition > maxPosition) {
                        currentMaxPosition = maxPosition;
                        currentMinPosition = 0;
                    }
                    if (end>maxPosition) {
                        end = maxPosition;
                    }
                    currentDuration = end - start;
                    startTime = currentMinPosition*Constants.SCALE_VALUE;
                    endTime = (currentMinPosition + currentDuration) * Constants.SCALE_VALUE;
                    log("Start time: "+startTime+" End time: "+endTime);
                    mOnControlTimeLineChanged.updateTimeLine(startTime, endTime);
                    updateLayout(start, end);
                    touch = 0;
                    return true;
                default:
                    break;
            }
            return false;
        }
    };
}