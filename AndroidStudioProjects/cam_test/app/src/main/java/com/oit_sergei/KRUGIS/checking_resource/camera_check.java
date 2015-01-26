package com.oit_sergei.KRUGIS.checking_resource;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;

/**
 * Created by oit-sergei on 20.12.2014.
 */
public class camera_check {
    private Camera camera;

    public camera_check()
    {
        camera = null;
    }

    public int camera_checking_process()
    {
        if (true)
        {
            camera = getCameraInstance();
        } else
            return -2;
        if (camera == null)
        {
            return -1;
        } else
        {
            return 0;
        }
    }

    public boolean camera_close()
    {
        if (camera != null)
        {
            camera.release();
            return true;
        } else return false;
    }

    //Checking IsCamera
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    //Checking availability of camera
    public Camera getCameraInstance(){
        try {
            camera = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return camera; // returns null if camera is unavailable
    }
}
