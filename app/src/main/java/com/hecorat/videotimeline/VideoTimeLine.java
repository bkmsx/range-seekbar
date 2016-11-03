package com.hecorat.videotimeline;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by bkmsx on 31/10/2016.
 */
public class VideoTimeLine extends ImageView {
    int width, height;
    Rect rectBackground;
    Paint paint;
    MediaMetadataRetriever retriever;
    int durationVideo;
    Bitmap defaultBitmap;
    LinearLayout.LayoutParams params;
    int startTime, endTime;
    int startPosition;
    int leftPosition;
    TimeLineStatus timeLineStatus;

    ArrayList<Bitmap> listBitmap;
    public VideoTimeLine(Context context, String videoPath, int height) {
        super(context);
        retriever = new MediaMetadataRetriever();
        retriever.setDataSource(videoPath);
        listBitmap = new ArrayList<>();
        durationVideo = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));

        startTime = 0;
        endTime = durationVideo;

        this.height = height;

        paint = new Paint();

        params = new LinearLayout.LayoutParams(width, height);
        setLayoutParams(params);
        defaultBitmap = createDefaultBitmap();
        drawTimeLine(startTime, endTime);
        initTimeLineStatus();

        new AsyncTaskExtractFrame().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void initTimeLineStatus(){
        timeLineStatus = new TimeLineStatus();
        timeLineStatus.start = 0;
        timeLineStatus.end = width;
        timeLineStatus.currentMinPosition = 0;
        timeLineStatus.currentMaxPosition = width;
        timeLineStatus.maxPosition = width;
        timeLineStatus.startTime = 0;
        timeLineStatus.endTime = endTime;
    }

    public void drawTimeLine(int startTime, int endTime) {
        startPosition = startTime/Constants.SCALE_VALUE;
        this.startTime = startTime;
        this.endTime = endTime;
        width = (endTime - startTime)/Constants.SCALE_VALUE;
        rectBackground = new Rect(0, 0, width, height);
        params.width = width;
        log("Start: "+startTime+" End: "+endTime);
        invalidate();
    }

    public void setLeftPosition(int value) {
        leftPosition = value;
        timeLineStatus.leftMargin = value - ControlTimeLine.THUMB_WIDTH;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setLayoutParams(params);
        paint.setColor(Color.BLUE);
        canvas.drawRect(rectBackground, paint);
        for (int i=0; i<listBitmap.size(); i++){
            canvas.drawBitmap(listBitmap.get(i), i*150 - startPosition, 0, paint);
        }
    }

    private Bitmap createDefaultBitmap(){
        Paint paint = new Paint();
        Bitmap bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        paint.setColor(Color.BLACK);
        canvas.drawRect(0, 0, bitmap.getWidth(), bitmap.getHeight(), paint);
        return bitmap;
    }

    private class AsyncTaskExtractFrame extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            int microDuration = durationVideo * 1000;
            int timeStamp = 0;
            while (timeStamp < microDuration) {
                Bitmap bitmap = null;
                int currentTimeStamp = timeStamp;
                while (bitmap==null && currentTimeStamp < Math.min(timeStamp+2900000, microDuration)) {
                    bitmap = retriever.getFrameAtTime(timeStamp, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                    currentTimeStamp += 60000;
                }
                if (bitmap == null) {
                    bitmap = defaultBitmap;
                }
                Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, 150, height, false);
                listBitmap.add(scaleBitmap);
                publishProgress();
                timeStamp += 3000000;
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            invalidate();
        }
    }

    private void log(String msg){
        Log.e("Video TimeLine",msg);
    }
}
