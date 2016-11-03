package com.hecorat.videotimeline;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ControlTimeLine.OnControlTimeLineChanged {
    LinearLayout mMainLayout, mLayoutVideo;
    VideoTimeLine mSelectedTimeLine;
    ArrayList<VideoTimeLine> mListTimeLine;
    RelativeLayout mLayoutControl;
    RelativeLayout.LayoutParams paramsControl;
    int height = 100;
    NewHorizontalScrollView mScrollView;
    ControlTimeLine mControlTimeLine;
    boolean mControlVisiable;
    LinearLayout.LayoutParams params;
    int mCountVideo = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);
        mListTimeLine = new ArrayList<>();
        mMainLayout = (LinearLayout) findViewById(R.id.main_layout);
        mLayoutControl = (RelativeLayout) findViewById(R.id.layout_control);
        mLayoutVideo = (LinearLayout) findViewById(R.id.layout_video);
        String videoPath = Environment.getExternalStorageDirectory()+"/test.mp4";
        for (int i=0; i<mCountVideo; i++) {
            VideoTimeLine videoTimeLine = new VideoTimeLine(this, videoPath, height);
            if (i==0){
                params = (LinearLayout.LayoutParams) videoTimeLine.getLayoutParams();
                params.leftMargin = 100;
            }
            mLayoutVideo.addView(videoTimeLine);
            videoTimeLine.setOnClickListener(onTimeLineClick);
            mListTimeLine.add(videoTimeLine);
        }
        updateLeftPositionTimeLine(mCountVideo-1);
        RelativeLayout.LayoutParams mLayoutVideoParams = (RelativeLayout.LayoutParams) mLayoutVideo.getLayoutParams();
        VideoTimeLine lastTimeLine = mListTimeLine.get(mCountVideo-1);
        mLayoutVideoParams.width = lastTimeLine.width + lastTimeLine.leftPosition;

        mControlTimeLine = new ControlTimeLine(this, 0, 0 , mListTimeLine.get(0).width, height);
        mLayoutControl.addView(mControlTimeLine);

        paramsControl = (RelativeLayout.LayoutParams) mControlTimeLine.getLayoutParams();

        mScrollView = (NewHorizontalScrollView) findViewById(R.id.scroll_view);
        toggleControlTimeLine();

    }

    View.OnClickListener onTimeLineClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mControlVisiable = true;
            toggleControlTimeLine();
            saveTimLineStatus();
            mSelectedTimeLine = (VideoTimeLine) view;
            showControl();
        }
    };

    private int updateLeftPositionTimeLine(int position){
        VideoTimeLine timeLine, prevTimeLine;
        timeLine = mListTimeLine.get(position);

        if (position == 0) {
            timeLine.setLeftPosition(100);
        } else {
            prevTimeLine = mListTimeLine.get(position-1);
            timeLine.setLeftPosition(prevTimeLine.width + updateLeftPositionTimeLine(position-1));
        }
        return timeLine.leftPosition;
    }

    private void showControl(){
        backupTimeLineStatus();
        mControlTimeLine.updateLayout(mControlTimeLine.start, mControlTimeLine.end, true);
    }

    private void backupTimeLineStatus(){
        TimeLineStatus timeLineStatus = mSelectedTimeLine.timeLineStatus;
        paramsControl.leftMargin = timeLineStatus.leftMargin;
        mControlTimeLine.start = timeLineStatus.start;
        mControlTimeLine.end = timeLineStatus.end;
        mControlTimeLine.currentMinPosition = timeLineStatus.currentMinPosition;
        mControlTimeLine.currentMaxPosition = timeLineStatus.currentMaxPosition;
        mControlTimeLine.maxPosition = timeLineStatus.maxPosition;
        log("Start Time: "+timeLineStatus.startTime+" End Time: "+timeLineStatus.endTime);
    }

    private void saveTimLineStatus(){
        if (mSelectedTimeLine == null) {
            return;
        }
        TimeLineStatus timeLineStatus = mSelectedTimeLine.timeLineStatus;
        timeLineStatus.startTime = mSelectedTimeLine.startTime;
        timeLineStatus.endTime = mSelectedTimeLine.endTime;
        timeLineStatus.start = mControlTimeLine.start;
        timeLineStatus.end = mControlTimeLine.end;
        timeLineStatus.currentMinPosition = mControlTimeLine.currentMinPosition;
        timeLineStatus.currentMaxPosition = mControlTimeLine.currentMaxPosition;
        timeLineStatus.maxPosition = mControlTimeLine.maxPosition;
        timeLineStatus.leftMargin = paramsControl.leftMargin;
    }

    private void toggleControlTimeLine(){
        if (mControlVisiable) {
            mControlTimeLine.setVisibility(View.VISIBLE);
            mScrollView.scroll = false;
        } else {
            mControlTimeLine.setVisibility(View.INVISIBLE);
            mScrollView.scroll = true;
        }
    }

    @Override
    public void updateTimeLine(int start, int end) {
        mSelectedTimeLine.drawTimeLine(start, end);
        updateLeftPositionTimeLine(mCountVideo - 1);
    }

    @Override
    public void invisibleControl() {
        mControlVisiable = false;
        toggleControlTimeLine();
    }

    private void log(String msg){
        Log.e("Log for Main Activity", msg);
    }
}
