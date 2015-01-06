package com.example.oit_sergei.cam_test;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oit_sergei.cam_test.services.audio_listener_service;
import com.example.oit_sergei.cam_test.services.camera_listener_service;

import java.util.Calendar;


public class MainActivity extends ActionBarActivity {

    private Button start;
    private Button stop;
    private Button blockCamera;
    private Button unlockCamera;
    private Button blockMicro;
    private Button unlockMicro;


    private TextView textView;
    public static final String PARAM_PINTENT = "pendingIntent";
    public final static String PARAM_RESULT = "result";
    private PackageInfo camera_blocked_pack = new PackageInfo();

    private Camera camera;
    private AudioRecord audioRecord;


    private Thread myThread_microphone = new Thread(new Runnable() {
        @Override
        public void run() {

        }
    });

    private AlarmManager alarmManager_camera;
    private AlarmManager alarmManager_microphone;
    private PendingIntent alarmIntent_camera;
    private PendingIntent alarmIntent_microphone;
    private Intent intentToFire_camera;
    private Intent intentToFire_microphone;

    long time_camera_update = 500;
    long time_microphone_update = 1000;
    private int alarmType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = (Button) findViewById(R.id.start_cam_btn);
        stop = (Button) findViewById(R.id.stop_cam_btn);
        blockCamera = (Button) findViewById(R.id.block_cam_btn);
        unlockCamera = (Button) findViewById(R.id.unlock_cam_btn);
        blockMicro = (Button) findViewById(R.id.block_micro_btn);
        unlockMicro = (Button) findViewById(R.id.unlock_micro_btn);

    }

    public void onCameraServiceStartClick(View v)
    {

        try {
            Calendar cal = Calendar.getInstance();
            alarmManager_camera = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            intentToFire_camera = new Intent(this, camera_listener_service.class);
            alarmType = AlarmManager.ELAPSED_REALTIME;

            alarmIntent_camera = PendingIntent.getService(this, 0, intentToFire_camera, 0);
            alarmManager_camera.setRepeating(alarmType, cal.getTimeInMillis() + 1000, time_camera_update, alarmIntent_camera);
        } catch (Exception s)
        {
            Toast.makeText(this, s.toString(), Toast.LENGTH_SHORT).show();
        }

        Toast.makeText(getApplicationContext(), "Service is starting now...", Toast.LENGTH_SHORT).show();
    }

    public void onCameraServiceStopClick(View v)
    {
        try {
            stopService(new Intent(this, camera_listener_service.class));
            alarmManager_camera = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmIntent_camera != null)
            {
                alarmManager_camera.cancel(alarmIntent_camera);
            } else
            {
                intentToFire_camera = new Intent(getApplicationContext(), camera_listener_service.class);
                alarmIntent_camera = PendingIntent.getService(this, 0, intentToFire_camera, 0);
                alarmManager_camera.cancel(alarmIntent_camera);
            }

            Toast.makeText(getApplicationContext(), "Service closed", Toast.LENGTH_SHORT).show();
        } catch (RuntimeException re)
        {
            Toast.makeText(getApplicationContext(), "Can not close the service",Toast.LENGTH_SHORT).show();
        }

    }

    public void onBlockCameraClick(View V)
    {
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

    public void onUnlockCameraClick(View V)
    {
        if (camera != null)
        {
            camera.release();
            Toast.makeText(getApplicationContext(), "Camera unlocked", Toast.LENGTH_SHORT).show();
        }
    }

    public void onMicrophoneServiceStartClick(View v)
    {
        alarmManager_microphone = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        intentToFire_microphone = new Intent(getApplicationContext(), audio_listener_service.class);
        alarmType = AlarmManager.ELAPSED_REALTIME;

        alarmIntent_microphone = PendingIntent.getService(getApplicationContext(), 0, intentToFire_microphone, 0);
        alarmManager_microphone.setInexactRepeating(alarmType, time_microphone_update, time_microphone_update, alarmIntent_microphone);
        Toast.makeText(getApplicationContext(), "Service is starting now...", Toast.LENGTH_SHORT).show();
    }

    public void onMicrophoneServiceStopClick(View v)
    {
        try {
            stopService(new Intent(this, camera_listener_service.class));
            alarmManager_microphone = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            if (alarmIntent_microphone != null)
            {
                alarmManager_microphone.cancel(alarmIntent_microphone);
            } else
            {
                intentToFire_microphone = new Intent(getApplicationContext(), audio_listener_service.class);
                alarmIntent_microphone = PendingIntent.getService(this, 0, intentToFire_microphone, 0);
                alarmManager_microphone.cancel(alarmIntent_microphone);
            }

            Toast.makeText(getApplicationContext(), "Service closed", Toast.LENGTH_SHORT).show();
        } catch (RuntimeException re)
        {
            Toast.makeText(getApplicationContext(), "Can not close the service",Toast.LENGTH_SHORT).show();
        }
    }

    public void onBlockMicroClick(View v)
    {

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

    public void onUnlockMicroClick(View v)
    {
        if (audioRecord != null) {
            audioRecord.stop();
            Toast.makeText(getApplicationContext(), "Microphone is unblocked", Toast.LENGTH_SHORT).show();
            audioRecord.release();
        }
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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





}