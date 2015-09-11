package com.example.luanxinglong_xy.eyeposition.util;

import com.example.luanxinglong_xy.eyeposition.activity.CameraActivity;

import java.util.TimerTask;

/**
 * Created by wangjinfa on 2015/9/9.
 */
public class Timer {

    private java.util.Timer timer;

    private long duration;

    private CameraActivity cameraActivity;

    public static boolean canClearFace = false;

    public Timer(long duration, CameraActivity cameraActivity) {
        setCameraActivity(cameraActivity);
        setDuration(duration);
        timer = new java.util.Timer();
    }

    public void setCanClearFace(boolean canClearFace) {
        this.canClearFace = canClearFace;
    }

    public boolean isCanClearFace() {
        return canClearFace;
    }

    public void setCameraActivity(CameraActivity cameraActivity) {
        this.cameraActivity = cameraActivity;
    }

    public void execute() {
        canClearFace = false;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                canClearFace = true;
                cameraActivity.stopGoogleFaceDetect();
            }
        }, duration);
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
    }

}
