package com.oit_sergei.KRUGIS;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.oit_sergei.KRUGIS.applications_lists.black_list_activity;
import com.oit_sergei.KRUGIS.applications_lists.white_list_activity;
import com.oit_sergei.KRUGIS.services.audio_listener_service;
import com.oit_sergei.KRUGIS.services.camera_listener_service;

import java.util.List;


public class MainActivity extends ActionBarActivity {

    Button start;
    Button stop;
    Button blockCamera;
    Button unlockCamera;
    Button blockMicro;
    Button unlockMicro;
    Button startBlackList;
    Button startWhiteList;


    private TextView textView_cam;
    private TextView textView_mic;
    public static final String PARAM_PINTENT = "pendingIntent";


    private Camera camera;
    private AudioRecord audioRecord;

    private AlarmManager alarmManager_camera;
    private AlarmManager alarmManager_microphone;
    private PendingIntent alarmIntent_camera;
    private PendingIntent alarmIntent_microphone;
    private Intent intentToFire_camera;
    private Intent intentToFire_microphone;

    long time_camera_update = 500;
    long time_microphone_update = 1000;
    int alarmType_microphone;
    int alarmType_camera;

    static int camera_state = 0;
    static int microphone_state = 0;


    @Override
    protected void onResume() {
        super.onResume();
        is_camera_service_running();
        is_microphone_service_running();
    }

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
        startWhiteList = (Button) findViewById(R.id.show_white_btn);
        startBlackList = (Button) findViewById(R.id.show_black_btn);

        textView_cam = (TextView) findViewById(R.id.textView2);
        textView_mic = (TextView) findViewById(R.id.textView3);

        is_camera_service_running();
        is_microphone_service_running();
    }

    public void onCameraServiceStartClick(View v)
    {
        int running_flag = 0;
        ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServiceInfos = activityManager.getRunningServices(Integer.MAX_VALUE);
        for (int i = 0; i < runningServiceInfos.size(); i++)
        {
            if (runningServiceInfos.get(i).service.getClassName().equals("com.oit_sergei.KRUGIS.services.camera_listener_service"))
            {
                running_flag = 1;
            }
        }

        if (running_flag == 0)
        {
            alarmManager_camera = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            intentToFire_camera = new Intent(getApplicationContext(), camera_listener_service.class);
            alarmType_camera = AlarmManager.ELAPSED_REALTIME;

            alarmIntent_camera = PendingIntent.getService(getApplicationContext(), 0, intentToFire_camera, 0);
            alarmManager_camera.setInexactRepeating(alarmType_camera, SystemClock.elapsedRealtime() + time_camera_update, time_camera_update, alarmIntent_camera);

            Toast.makeText(getApplicationContext(), "Camera service is starting now", Toast.LENGTH_SHORT).show();
        } else
        {
            Toast.makeText(getApplicationContext(), "Camera service is already started", Toast.LENGTH_SHORT).show();
        }

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
        if (checkCameraHardware(getApplicationContext()) && (camera_state != 1))
        {
            camera = getCameraInstance();
            Toast.makeText(getApplicationContext(), "Camera blocked", Toast.LENGTH_SHORT).show();
            camera_state = 1;
        } else if (camera_state == 1)
        {
            Toast.makeText(getApplicationContext(), "Camera already blocked", Toast.LENGTH_SHORT).show();
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
            camera_state = 0;
        }
    }

    public void onMicrophoneServiceStartClick(View v)
    {
        int running_flag = 0;
        ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServiceInfos = activityManager.getRunningServices(Integer.MAX_VALUE);
        for (int i = 0; i < runningServiceInfos.size(); i++)
        {
            if (runningServiceInfos.get(i).service.getClassName().equals("com.oit_sergei.KRUGIS.services.audio_listener_service"))
            {
                running_flag = 1;
            }
        }

        if (running_flag == 0)
        {
            alarmManager_microphone = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            intentToFire_microphone = new Intent(this, audio_listener_service.class);
            alarmType_microphone = AlarmManager.ELAPSED_REALTIME;

            alarmIntent_microphone = PendingIntent.getService(this, 0, intentToFire_microphone, 0);
            alarmManager_microphone.setRepeating(alarmType_microphone, SystemClock.elapsedRealtime() + time_microphone_update, time_microphone_update, alarmIntent_microphone);

            Toast.makeText(getApplicationContext(), "Microphone service is starting now", Toast.LENGTH_SHORT).show();
        } else
        {
            Toast.makeText(getApplicationContext(), "Microphone service is already started", Toast.LENGTH_SHORT).show();
        }

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
        if (microphone_state == 0)
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
                microphone_state = 1;
            }

            int temp = audioRecord.getRecordingState();
            if (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING)
            {
                Toast.makeText(getApplicationContext(), "Microphone is blocked", Toast.LENGTH_SHORT).show();
            }
        } else
        {
            Toast.makeText(getApplicationContext(), "Microphone is already blocked", Toast.LENGTH_SHORT).show();
        }

    }

    public void onUnlockMicroClick(View v)
    {
        if (audioRecord != null) {
            audioRecord.stop();
            Toast.makeText(getApplicationContext(), "Microphone is unblocked", Toast.LENGTH_SHORT).show();
            microphone_state = 0;
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

    public void onStartBlackList(View v)
    {
        Intent intent = new Intent(this, black_list_activity.class);
        startActivity(intent);
    }

    public void onStartWhiteList(View v)
    {
        Intent intent = new Intent(this, white_list_activity.class);
        startActivity(intent);
    }

    public void update_services_status(View v)
    {
        is_camera_service_running();
        is_microphone_service_running();
    }

    public void is_camera_service_running()
    {
        int running_flag = 0;
        ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServiceInfos = activityManager.getRunningServices(Integer.MAX_VALUE);
        for (int i = 0; i < runningServiceInfos.size(); i++)
        {
            if (runningServiceInfos.get(i).service.getClassName().equals("com.oit_sergei.KRUGIS.services.camera_listener_service"))
            {
                running_flag = 1;
                textView_cam.setText("Camera service is running now");
            }
        }

        if (running_flag == 0)
        {
            textView_cam.setText("Camera service is not running");
        }

    }

    public void is_microphone_service_running()
    {
        int running_flag = 0;
        ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServiceInfos = activityManager.getRunningServices(Integer.MAX_VALUE);
        for (int i = 0; i < runningServiceInfos.size(); i++)
        {
            if (runningServiceInfos.get(i).service.getClassName().equals("com.oit_sergei.KRUGIS.services.audio_listener_service"))
            {
                running_flag = 1;
                textView_mic.setText("Microphone service is running now");
            }
        }

        if (running_flag == 0)
        {
            textView_mic.setText("Microphone service is not running");
        }

    }

}