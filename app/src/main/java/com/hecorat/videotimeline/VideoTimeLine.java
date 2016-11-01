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
    int width, height, halfWidth, halfHeight;
    Rect rectBackground, leftThumb, rightThumb;
    Paint paint;
    MediaMetadataRetriever retriever;
    int durationVideo;
    Bitmap defaultBitmap;
    LinearLayout.LayoutParams params;
    int startTime, endTime;
    int startPosition;


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

        halfWidth = width/2;
        halfHeight = height/2;

        leftThumb = new Rect(-30, halfHeight -30, 30, halfHeight+30);

        paint = new Paint();

        params = new LinearLayout.LayoutParams(width, height);
        setLayoutParams(params);
        defaultBitmap = createDefaultBitmap();
        drawTimeLine(startTime, endTime);

        new AsyncTaskExtractFrame().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
        //size can be customized
        Bitmap bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        //Draw black background
        paint.setColor(Color.BLACK);
        canvas.drawRect(0, 0, bitmap.getWidth(), bitmap.getHeight(), paint);
        //draw white text
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
