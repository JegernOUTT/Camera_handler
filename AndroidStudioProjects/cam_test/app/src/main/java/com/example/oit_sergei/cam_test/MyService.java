package com.example.oit_sergei.cam_test;


import android.app.Service;
import android.content.Intent;
import android.hardware.Camera;
import android.os.IBinder;
import android.widget.Toast;

public class MyService extends Service implements Camera.ErrorCallback {

    private String[] cameraList;
    String detailMessage;
    private Camera camera;
    private camera_check cameraCheck;

    public MyService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(this, "Start", Toast.LENGTH_SHORT).show();

        cameraCheck = new camera_check();
        int camera_availability = cameraCheck.camera_checking_process();
        if (camera_availability == 0) {
            Toast.makeText(this, "Camera opened", Toast.LENGTH_SHORT).show();
        } else if (camera_availability == -1) {
            Toast.makeText(this, "Camera unavailable", Toast.LENGTH_SHORT).show();
        } else if (camera_availability == -2) {
            Toast.makeText(this, "Camera error system", Toast.LENGTH_SHORT).show();
        }

        if (cameraCheck.camera_close() == true) {
            Toast.makeText(this, "Camera close OK", Toast.LENGTH_SHORT).show();
        } else if (cameraCheck.camera_close() == false) {
            Toast.makeText(this, "Camera close ERROR", Toast.LENGTH_SHORT).show();
        }

        stopSelf();

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (camera != null) {
            camera.release();
            Toast.makeText(this, "Stopped", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onError(int error, Camera camera) {
        Toast.makeText(this, "Ошика блять!", Toast.LENGTH_SHORT).show();
    }
}