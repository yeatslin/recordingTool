package com.test.com.screenrecorder;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ViewService extends Service {
    FlowViewManager mFlowViewManager;
    public ViewService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        mFlowViewManager = FlowViewManager.getInstance();
        mFlowViewManager.showStopRecorderIcon(ViewService.this);
        mFlowViewManager.setService(this);

    }

}
