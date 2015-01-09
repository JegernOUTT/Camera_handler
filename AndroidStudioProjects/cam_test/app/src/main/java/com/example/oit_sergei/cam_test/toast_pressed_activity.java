package com.example.oit_sergei.cam_test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class toast_pressed_activity extends ActionBarActivity {

    private PackageInfo camera_blocked_pack;
    private PackageInfo microphone_blocked_pack;
    private PackageInfo main_pack;

    AudioRecord audioRecord;
    Camera camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toast_pressed_activity);

        registerReceiver(app_get_camera, new IntentFilter("Camera_unavailable"));
        registerReceiver(app_get_microphone, new IntentFilter("Microphone_unavailable"));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toast_pressed_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void exit_blockCamera(View v)
    {
        if (main_pack != null)
        {
            android.os.Process.killProcess(main_pack.applicationInfo.uid);
            Toast.makeText(getApplicationContext(), "Application " + main_pack.applicationInfo.processName + " is killed!", Toast.LENGTH_SHORT).show();
        } else Toast.makeText(getApplicationContext(), "There is no application to kill", Toast.LENGTH_SHORT).show();


        if (checkCameraHardware(getApplicationContext()) == true)
        {
            camera = getCameraInstance();
            Toast.makeText(getApplicationContext(), "Camera blocked!!!", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(getApplicationContext(), "Camera Hardware Unavailable",Toast.LENGTH_SHORT).show();
        if (camera == null)
        {
            Toast.makeText(getApplicationContext(),"Camera Unavailable",Toast.LENGTH_SHORT).show();
        }
    }

    public void exit_blockMicro(View v)
    {
        if (main_pack != null)
        {
            android.os.Process.killProcess(main_pack.applicationInfo.uid);
            Toast.makeText(getApplicationContext(), "Application " + main_pack.applicationInfo.processName + " is killed!", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getApplicationContext(), "Microphone is blocked!!!", Toast.LENGTH_SHORT).show();
        }

    }

    public void exit_pack(View v)
    {
        if (main_pack != null)
        {
            android.os.Process.killProcess(main_pack.applicationInfo.uid);
            Toast.makeText(getApplicationContext(), "Application " + main_pack.applicationInfo.processName + " is killed!", Toast.LENGTH_SHORT).show();
        } else Toast.makeText(getApplicationContext(), "There is no application to kill", Toast.LENGTH_SHORT).show();
    }

    private BroadcastReceiver app_get_camera = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            camera_blocked_pack = intent.getParcelableExtra("Camera_app");
            main_pack = camera_blocked_pack;
            Toast.makeText(getApplicationContext(), camera_blocked_pack.packageName, Toast.LENGTH_LONG).show();
        }
    };

    private BroadcastReceiver app_get_microphone = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            microphone_blocked_pack = intent.getParcelableExtra("Microphone_app");
            main_pack = microphone_blocked_pack;
            Toast.makeText(getApplicationContext(), microphone_blocked_pack.packageName, Toast.LENGTH_LONG).show();
        }
    };

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
