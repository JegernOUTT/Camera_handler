package com.oit_sergei.KRUGIS;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.oit_sergei.KRUGIS.applications_lists.black_list_activity;
import com.oit_sergei.KRUGIS.applications_lists.white_list_activity;

public class toast_pressed_activity extends Activity {

    private PackageInfo camera_blocked_pack;
    private PackageInfo microphone_blocked_pack;
    private PackageInfo main_pack;

    private Intent app_intent;

    TextView textView;

    AudioRecord audioRecord;
    Camera camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toast_pressed_activity);
        textView = (TextView) findViewById(R.id.textView);

        app_intent = getIntent();
        PackageManager packageManager = getPackageManager();

        camera_blocked_pack = app_intent.getParcelableExtra("Camera_app");
        if (camera_blocked_pack != null)
        {
            main_pack = camera_blocked_pack;
            textView.setText("KRUGIS report, that this application: " + packageManager.getApplicationLabel(main_pack.applicationInfo)
                    + " used your camera");
        }

        microphone_blocked_pack = app_intent.getParcelableExtra("Microphone_app");
        if (microphone_blocked_pack != null)
        {
            main_pack = microphone_blocked_pack;
            textView.setText("KRUGIS report, that this application: " + packageManager.getApplicationLabel(main_pack.applicationInfo)
                + " used your microphone");
        }


    }

    public void exit_blockCamera(View v)
    {
        PackageManager packageManager = getPackageManager();
        if (main_pack != null)
        {
            ActivityManager activityManager = (ActivityManager)this.getSystemService(ACTIVITY_SERVICE);

            activityManager.restartPackage(main_pack.packageName);
            Toast.makeText(getApplicationContext(), "Application " + packageManager.getApplicationLabel(main_pack.applicationInfo) + " is killed!", Toast.LENGTH_SHORT).show();
        } else Toast.makeText(getApplicationContext(), "There is no application to kill", Toast.LENGTH_SHORT).show();


        if (checkCameraHardware(getApplicationContext()) == true)
        {
            camera = getCameraInstance();
            Toast.makeText(getApplicationContext(), "Camera blocked", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(getApplicationContext(), "Camera hardware unavailable",Toast.LENGTH_SHORT).show();
        if (camera == null)
        {
            Toast.makeText(getApplicationContext(),"Camera unavailable",Toast.LENGTH_SHORT).show();
        }
    }

    public void exit_blockMicro(View v)
    {
        PackageManager packageManager = getPackageManager();
        if (main_pack != null)
        {
            ActivityManager activityManager = (ActivityManager)this.getSystemService(ACTIVITY_SERVICE);

            activityManager.restartPackage(main_pack.packageName);

            Toast.makeText(getApplicationContext(), "Application " + packageManager.getApplicationLabel(main_pack.applicationInfo) + " is killed!", Toast.LENGTH_SHORT).show();
        } else Toast.makeText(getApplicationContext(), "There is no application to kill", Toast.LENGTH_SHORT).show();

        int sampleRate = 44100;
        int channelConfig = AudioFormat.CHANNEL_IN_MONO;
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

        int minInternalBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig,
                audioFormat);
        int internalBufferSize = minInternalBufferSize * 4;
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                sampleRate, channelConfig, audioFormat, internalBufferSize);

        if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED)
        {
            audioRecord.startRecording();

        }

        int temp = audioRecord.getRecordingState();
        if (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING)
        {
            Toast.makeText(getApplicationContext(), "Microphone is blocked", Toast.LENGTH_SHORT).show();
        }

    }

    public void exit_pack(View v)
    {
        PackageManager packageManager = getPackageManager();
        if (main_pack != null)
        {
            ActivityManager activityManager = (ActivityManager)this.getSystemService(ACTIVITY_SERVICE);

            activityManager.restartPackage(main_pack.packageName);
            activityManager.killBackgroundProcesses(main_pack.packageName);

            Toast.makeText(getApplicationContext(), "Application " + packageManager.getApplicationLabel(main_pack.applicationInfo) + " is killed!", Toast.LENGTH_SHORT).show();
        } else Toast.makeText(getApplicationContext(), "There is no application to kill", Toast.LENGTH_SHORT).show();
    }

    public void allowThisTime(View v)
    {
        finish();
    }


    public void add_to_white(View v)
    {
        PackageManager packageManager = getPackageManager();
        Intent add_to_white_intent = new Intent(this, white_list_activity.class);
        add_to_white_intent.putExtra("App_name_add", packageManager.getApplicationLabel(main_pack.applicationInfo));
        startActivity(add_to_white_intent);
    }

    public void add_to_black(View v)
    {
        PackageManager packageManager = getPackageManager();
        Intent add_to_black_intent = new Intent(this, black_list_activity.class);
        add_to_black_intent.putExtra("App_name_add", packageManager.getApplicationLabel(main_pack.applicationInfo));
        startActivity(add_to_black_intent);
    }

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
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

}
