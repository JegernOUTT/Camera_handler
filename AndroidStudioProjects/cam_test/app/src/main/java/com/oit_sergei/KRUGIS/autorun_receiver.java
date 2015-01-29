package com.oit_sergei.KRUGIS;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.view.View;

import android.widget.Toast;

import com.oit_sergei.KRUGIS.services.audio_listener_service;
import com.oit_sergei.KRUGIS.services.camera_listener_service;

import java.util.List;



/**
 * Created by Vahreev on 29.01.2015.
 */
public class autorun_receiver extends BroadcastReceiver {
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


    @Override
    public void onReceive(Context context, Intent intent) {
        onCameraServiceStartClick(context);
        onMicrophoneServiceStartClick(context);
    }

    public void onCameraServiceStartClick(Context context)
    {
        int running_flag = 0;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
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
            alarmManager_camera = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
            intentToFire_camera = new Intent(context, camera_listener_service.class);
            alarmType_camera = AlarmManager.ELAPSED_REALTIME;

            alarmIntent_camera = PendingIntent.getService(context, 0, intentToFire_camera, 0);
            alarmManager_camera.setInexactRepeating(alarmType_camera, SystemClock.elapsedRealtime() + time_camera_update, time_camera_update, alarmIntent_camera);

            Toast.makeText(context, "Camera service is starting now", Toast.LENGTH_SHORT).show();
        } else
        {
            Toast.makeText(context, "Camera service is already started", Toast.LENGTH_SHORT).show();
        }

    }

    public void onMicrophoneServiceStartClick(Context context)
    {
        int running_flag = 0;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
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
            alarmManager_microphone = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
            intentToFire_microphone = new Intent(context, audio_listener_service.class);
            alarmType_microphone = AlarmManager.ELAPSED_REALTIME;

            alarmIntent_microphone = PendingIntent.getService(context, 0, intentToFire_microphone, 0);
            alarmManager_microphone.setRepeating(alarmType_microphone, SystemClock.elapsedRealtime() + time_microphone_update, time_microphone_update, alarmIntent_microphone);

            Toast.makeText(context, "Microphone service is starting now", Toast.LENGTH_SHORT).show();
        } else
        {
            Toast.makeText(context, "Microphone service is already started", Toast.LENGTH_SHORT).show();
        }

    }
}
