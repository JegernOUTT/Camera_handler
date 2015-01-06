package com.example.oit_sergei.cam_test.services;


import android.app.ActivityManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.CountDownTimer;
import android.widget.Toast;

import com.example.oit_sergei.cam_test.MainActivity;
import com.example.oit_sergei.cam_test.R;
import com.example.oit_sergei.cam_test.checking_resource.camera_check;
import com.example.oit_sergei.cam_test.toast_pressed_activity;

import java.util.ArrayList;
import java.util.List;

public class camera_listener_service extends IntentService {

    private String[] cameraList;
    private String detailMessage;
    private Camera camera;
    private camera_check cameraCheck;
    private CountDownTimer timer;
    private long timer_count;
    private String camera_permisson = new String("android.permission.CAMERA");
    private static int first_cycle_flag;
    private PackageInfo  camera_blocked_pack = new PackageInfo();
    private PackageInfo result_app_service = new PackageInfo();
    private PackageInfo result_app_activity = new PackageInfo();


    public camera_listener_service(String name) {
        super("camera_service");
    }

    @Override
    public void onCreate() {Toast.makeText(getApplicationContext(), "Start", Toast.LENGTH_SHORT).show();
        super.onCreate();
        Toast.makeText(getApplicationContext(), "Start", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Toast.makeText(getApplicationContext(), "Start", Toast.LENGTH_SHORT).show();
        PendingIntent pendingIntent = intent.getParcelableExtra(MainActivity.PARAM_PINTENT);

        cameraCheck = new camera_check();
        int camera_availability = cameraCheck.camera_checking_process();
        boolean close_check = cameraCheck.camera_close();
        if (camera_availability == 0) {
//            Toast.makeText(getApplicationContext(), "Camera opened", Toast.LENGTH_SHORT).show();
            if (close_check == true) {
//                Toast.makeText(getApplicationContext(), "Camera close OK", Toast.LENGTH_SHORT).show();
            } else if (close_check == false) {
//                Toast.makeText(getApplicationContext(), "Camera close ERROR", Toast.LENGTH_SHORT).show();
            }
            first_cycle_flag = 1;

        } else if (camera_availability == -1) {
//            Toast.makeText(this, "Camera unavailable", Toast.LENGTH_SHORT).show();
            if (first_cycle_flag == 1)
            {
                camera_blocked_pack = application_resolve();

                if (camera_blocked_pack != null)
                {
                    first_cycle_flag = 0;

                    Intent i = new Intent("Camera_unavailable")
                            .putExtra("Camera_app", camera_blocked_pack);

                    sendBroadcast(i);
                    send_notification(camera_blocked_pack);

                    stopSelf();
                }
            }

        } else if (camera_availability == -2) {
            Toast.makeText(getApplicationContext(), "Camera error system", Toast.LENGTH_SHORT).show();
        }
        stopSelf();

    }


    public PackageInfo application_resolve()
    {
        PackageManager packageManager = getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(4096);

        int flag = 0;
        int run_index = 0;

        ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);

        //Создание контейнеров для running processes и running services
        List <ActivityManager.RunningAppProcessInfo> runningAppProcessInfos = activityManager.getRunningAppProcesses();
        List <ActivityManager.RunningServiceInfo> runningServiceInfos = activityManager.getRunningServices(Integer.MAX_VALUE);

        //Создание контейнеров для отсеяных по camera permission processes и services
        List <PackageInfo> packageInfos_running = new ArrayList<>();
        List <ActivityManager.RunningAppProcessInfo> RunningAppProcessInfo_checked = new ArrayList<>();
        List <ActivityManager.RunningServiceInfo> runningServiceInfos_checked = new ArrayList<>();

        //Проверка Running Tasks для финального отбора activity
        List <ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(Integer.MAX_VALUE);

        //Отсеивание всех запущенных активити по camera permission
        for (int i = 0; i < runningAppProcessInfos.size(); i++)
        {
            for (int j = 0; j < packageInfos.size(); j++)
            {
                flag = 0;

                if (runningAppProcessInfos.get(i).processName.equals(packageInfos.get(j).applicationInfo.processName) && packageInfos.get(j).requestedPermissions != null)
                {
                    for (int k = 0; k < packageInfos.get(j).requestedPermissions.length; k++)
                    {
                        if (packageInfos.get(j).requestedPermissions[k].toString().equals(camera_permisson))
                        {
                            flag = 1;
                        }
                    }
                }

                if (flag == 1)
                {
                    packageInfos_running.add(run_index, packageInfos.get(j));
                    RunningAppProcessInfo_checked.add(run_index, runningAppProcessInfos.get(i));
                    run_index++;
                }
            }
        }

        //Отсеивание всех запущенных сервисов по camera permission
        run_index = 0;
        for (int i = 0; i < RunningAppProcessInfo_checked.size(); i++)
        {
            for (int j = 0; j < runningServiceInfos.size(); j++)
            {
                flag = 0;

                if (RunningAppProcessInfo_checked.get(i).processName.equals(runningServiceInfos.get(j).process))
                {
                    flag = 1;
                }

                if ( (runningServiceInfos.get(j).process.equals("com.google.android.gms")) &&
                        (runningServiceInfos.get(j).process.equals("com.android.phone")) )
                {
                    flag = 0;
                }

                if (flag == 1)
                {
                    runningServiceInfos_checked.add(run_index, runningServiceInfos.get(j));
                    run_index++;
                }
            }
        }

        //Возврат значений. У сервисов по минимальному времени, у активити по первому в очереди. Если не найдено - возврат null
        if (packageInfos_running != null)
        {
            if (runningServiceInfos_checked != null)
            {
                int my_process = 0;

                for (int i = 1; i < runningServiceInfos_checked.size(); i++)
                {
                    if (runningServiceInfos_checked.get(i).process == "com.example.oit_sergei.cam_test")
                    {
                        my_process = i;
                    }
                };

                long min_time = 1000;
                int min_i = 1000;
                for (int i = 1; i < runningServiceInfos_checked.size(); i++)
                {
                    if ((Math.abs(runningServiceInfos_checked.get(i).lastActivityTime - runningServiceInfos_checked.get(my_process).lastActivityTime) < min_time)
                            && (i != my_process))
                    {
                        min_time = Math.abs(runningServiceInfos_checked.get(i).lastActivityTime - runningServiceInfos_checked.get(my_process).lastActivityTime);
                        min_i = i;
                    }
                }

                if (min_i <= 500)
                {
                    for (int i = 0; i < packageInfos_running.size(); i++)
                    {
                        if (packageInfos_running.get(i).packageName.equals(runningServiceInfos_checked.get(min_i).process))
                        {
                            //On first place service search
                            result_app_service = packageInfos_running.get(i);
                        }
                    }
                    return result_app_service;
                } else
                {
                    for (int i = 0; i < packageInfos_running.size(); i++)
                    {
                        if (!(packageInfos_running.get(i).packageName.equals("com.example.oit_sergei.cam_test")))
                        {
                            return packageInfos_running.get(i);
                        }
                    }
                    return packageInfos_running.get(0);
                }

            } else return null;

        } else return null;


    }

    private static final int NOTIFY_ID = 101;
    public void send_notification(PackageInfo pack_app)
    {
        Intent notification_intent = new Intent(getApplicationContext(), toast_pressed_activity.class)
                .putExtra("Pack_app", pack_app);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notification_intent, PendingIntent.FLAG_CANCEL_CURRENT);


        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_launcher)

                .setTicker("Camera was opened")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                        //.setContentTitle(res.getString(R.string.notifytitle)) // Заголовок уведомления
                .setContentTitle("Camera was opened")
                        //.setContentText(res.getString(R.string.notifytext))
                .setContentText("Check app: " + pack_app.packageName); // Текст уведомленимя

        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_ALL;


        NotificationManager notificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFY_ID, notification);
    }
}