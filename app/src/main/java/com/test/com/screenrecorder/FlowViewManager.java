package com.test.com.screenrecorder;

import android.app.Service;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

public class FlowViewManager {
    private String TAG = "FlowViewManager";
    private static View mView = null;
    private static WindowManager mWindowManager = null;
    private static Context mContext = null;

    public static Boolean isShown = false;

    static WindowManager.LayoutParams params;

    static ScreenRecorder mScreenRecorder;

    static Button mButton;

    static Service mService;

    private static FlowViewManager mFlowViewManager;

    public static FlowViewManager getInstance() {
        if(mFlowViewManager == null) {
            mFlowViewManager = new FlowViewManager();
        }
        return mFlowViewManager;
    }

    private FlowViewManager() {
        Log.d(TAG, "new instance");
    }


    public void showStopRecorderIcon(final Context context) {
        if (isShown) {
            Log.i(TAG, "return cause already shown");
            return;
        }
        isShown = true;
        Log.i(TAG, "showPopupWindow");
        mContext = context.getApplicationContext();
        mWindowManager = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
        try {
            setFlowWindow(mContext);
        } catch (Exception e) {
            Log.d(TAG,"exception e : "+e);
            //TODO : show dialog to ask user give drag view permission
        }

    }


    //設定flow window
    private void setFlowWindow(Context context) {
        mView = setupFlowView(context);
        params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        int flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE  ;
        params.flags = flags;
        params.format = PixelFormat.TRANSLUCENT;
        params.width = 200;
        params.height = 200;
        mWindowManager.addView(mView, params);
        Log.i(TAG, "add view");
    }

    /**
     * 隐藏弹出框
     */
    public void hideStopRecorderIcon() {
        Log.i(TAG, "hide " + isShown + ", " + mView);
        if (isShown && null != mView) {
            Log.i(TAG, "hidePopupWindow");
            mWindowManager.removeView(mView);
            isShown = false;
        }
    }

    private View setupFlowView(final Context context) {
        final View view = LayoutInflater.from(context).inflate(R.layout.flowview, null);
        ImageView flowView = (ImageView)view.findViewById(R.id.flowView);
        flowView.setOnTouchListener(new View.OnTouchListener() {
            int lastX, lastY;
            int paramX, paramY;
            long startTime = 0;
            long endTime = 0;

            @Override
            public boolean onTouch(View arg0, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        Log.d(TAG, "MotionEvent.ACTION_DOWN lastX : " + lastX + " lastY : " + lastY);
                        paramX = params.x;
                        paramY = params.y;
                        startTime = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int dx = (int) event.getRawX() - lastX;
                        int dy = (int) event.getRawY() - lastY;
                        params.x = paramX + dx;
                        params.y = paramY + dy;
                        Log.d(TAG, "MotionEvent.ACTION_MOVE params.x : " + params.x + " params.y : " + params.y);
                        //mView.layout(dx,dy,dx+ view.getWidth(),dy+view.getHeight());
                        mWindowManager.updateViewLayout(mView, params);
                        break;
                    case MotionEvent.ACTION_UP:
                        endTime = System.currentTimeMillis();
                        if (endTime - startTime < 500) {
                            hideStopRecorderIcon();
                            mScreenRecorder.quit();
                            mScreenRecorder = null;
                            mButton.setText("Restart recorder");
                            mService.stopSelf();

                        }
                        startTime = 0;
                        endTime = 0;
                        break;
                }
                return true;
            }
        });
        return view;
    }

    public void setScreenRecorder(ScreenRecorder screenRecorder) {
        Log.d(TAG,"screenRecorder: "+screenRecorder);
        mScreenRecorder = screenRecorder;
        Log.d(TAG, "mScreenRecorder : " + mScreenRecorder);
    }

    public void setButton(Button button) {
        mButton = button;
    }

    public void setService(Service service) {
        mService = service;
    }

}
