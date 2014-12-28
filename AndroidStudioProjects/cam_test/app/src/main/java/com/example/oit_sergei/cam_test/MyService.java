package com.example.oit_sergei.cam_test;


import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MyService extends Service  {

    private String[] cameraList;
    private String detailMessage;
    private Camera camera;
    private camera_check cameraCheck;
    private CountDownTimer timer;
    private long timer_count;
    private String camera_permisson = new String("android.permission.CAMERA");
    private static int first_cycle_flag;
    private PackageInfo camera_blocked_pack;


    public MyService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        Toast.makeText(this, "Start", Toast.LENGTH_SHORT).show();
        PendingIntent pendingIntent = intent.getParcelableExtra(MainActivity.PARAM_PINTENT);

        cameraCheck = new camera_check();
        int camera_availability = cameraCheck.camera_checking_process();
        if (camera_availability == 0) {
//            Toast.makeText(this, "Camera opened", Toast.LENGTH_SHORT).show();
            if (cameraCheck.camera_close() == true) {
//                Toast.makeText(this, "Camera close OK", Toast.LENGTH_SHORT).show();
            } else if (cameraCheck.camera_close() == false) {
//                Toast.makeText(this, "Camera close ERROR", Toast.LENGTH_SHORT).show();
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
                    Intent i = new Intent("Camera_unavailable");
                    i.putExtra("App_obj", camera_blocked_pack);
                    sendBroadcast(i);
                    stopSelf();
                }

            }


        } else if (camera_availability == -2) {
            Toast.makeText(this, "Camera error system", Toast.LENGTH_SHORT).show();
        }
//        stopSelf();

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

    private long start_timer(int millis_count)
    {
        timer = new CountDownTimer(millis_count, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timer_count = 1000 - millisUntilFinished;
            }

            @Override
            public void onFinish() {
                timer.cancel();
            }
        };

        return timer_count;
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
        for (int i = 0; i < packageInfos_running.size(); i++)
        {
            for (int j = 0; j < runningServiceInfos.size(); j++)
            {
                flag = 0;

                if (packageInfos_running.get(i).applicationInfo.processName.equals(runningServiceInfos.get(j).process))
                {
                    flag = 1;
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
            return packageInfos_running.get(0);
        } else if (packageInfos_running == null)
        {
            if (runningServiceInfos_checked != null)
            {
                long min_time = runningServiceInfos_checked.get(0).lastActivityTime;
                int min_i = 0;
                for (int i = 1; i < runningServiceInfos_checked.size(); i++)
                {
                    if (runningServiceInfos_checked.get(i).lastActivityTime < min_time)
                    {
                        min_time = runningServiceInfos_checked.get(i).lastActivityTime;
                        min_i = i;
                    }
                }

                for (int i = 0; i < packageInfos_running.size(); i++)
                {
                    if (packageInfos_running.get(i).packageName.equals(runningServiceInfos_checked.get(min_i).clientPackage))
                    {
                        return packageInfos_running.get(i);
                    }
                }
            }
        }
            return null;

    }
}