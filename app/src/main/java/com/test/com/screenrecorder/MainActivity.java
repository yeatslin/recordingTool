package com.test.com.screenrecorder;

import android.app.Activity;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Environment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends Activity {

    String TAG = "ScreenRecorder";


    private static final int REQUEST_CODE = 1;
    private MediaProjectionManager mMediaProjectionManager;
    private ScreenRecorder mRecorder;
    private Button mButton;
    FlowViewManager mFlowViewManager;
    int mWidth;
    int mHeight;
    int mBitrate;
    File mFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFlowViewManager = FlowViewManager.getInstance();
        mButton = (Button) findViewById(R.id.button);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //stop recording
                if (mRecorder != null) {
                    mRecorder.quit();
                    mRecorder = null;
                    mButton.setText("Restart recorder");
                } else {
                    //start recording
                    Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();
                    startActivityForResult(captureIntent, REQUEST_CODE);
                }
            }
        });
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        MediaProjection mediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
        if (mediaProjection == null) {
            Log.e(TAG, "media projection is null");
            return;
        }
        initialParameter();
        mRecorder = new ScreenRecorder(mWidth, mHeight, mBitrate, 1, mediaProjection, mFile.getAbsolutePath());
        mRecorder.start();
        mButton.setText("Stop Recorder");
        mFlowViewManager.setScreenRecorder(mRecorder);
        mFlowViewManager.setButton(mButton);
        //start flow icon
        Intent intent = new Intent(MainActivity.this, ViewService.class);
        startService(intent);
        Toast.makeText(this, "Screen recorder is running...", Toast.LENGTH_SHORT).show();
        //move activity to background
        moveTaskToBack(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mRecorder != null){
            mRecorder.quit();
            mRecorder = null;
        }
    }

    private void initialParameter() {
        try {
            // video size
            mWidth = 1280;
            mHeight = 720;
            mFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "",
                    "record-" + mWidth + "x" + mHeight + "-" + System.currentTimeMillis() + ".mp4");
            Log.d("ScreenRecorder", "file is exist : " + mFile.exists());
            mBitrate = 6000000;
        } catch(Exception e) {
            Log.d(TAG,"exception : "+e);
            //TODO : show dialog to notify user give app write permission in android M
        }

    }
}
