package com.hecorat.videotimeline;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity implements ControlTimeLine.OnControlTimeLineChanged{
    LinearLayout mMainLayout;
    VideoTimeLine videoTimeLine;
    RelativeLayout mLayoutControl;
    int height = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);
        mMainLayout = (LinearLayout) findViewById(R.id.main_layout);
        mLayoutControl = (RelativeLayout) findViewById(R.id.layout_control);
        String videoPath = Environment.getExternalStorageDirectory()+"/AzRecorderFree/test.mp4";
        videoTimeLine = new VideoTimeLine(this, videoPath, height);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) videoTimeLine.getLayoutParams();
        params.leftMargin = 100;
        mLayoutControl.addView(videoTimeLine,params);
        ControlTimeLine controlTimeLine = new ControlTimeLine(this, 100, 0 , videoTimeLine.width, height);
        mLayoutControl.addView(controlTimeLine);
        final EditText startInput = (EditText) findViewById(R.id.start_time);
        final EditText endInput = (EditText) findViewById(R.id.end_time);
        final Button btnDraw = (Button) findViewById(R.id.btn_draw);
        btnDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int startTime = Integer.parseInt(startInput.getText().toString());
                int endTime = Integer.parseInt(endInput.getText().toString());
                videoTimeLine.drawTimeLine(startTime, endTime);
            }
        });
    }

    @Override
    public void updateTimeLine(int start, int end) {
        videoTimeLine.drawTimeLine(start, end);
    }
}
