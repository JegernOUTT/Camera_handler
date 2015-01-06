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
import android.os.IBinder;
import android.widget.Toast;

import com.example.oit_sergei.cam_test.MainActivity;
import com.example.oit_sergei.cam_test.R;
import com.example.oit_sergei.cam_test.checking_resource.audio_check;
import com.example.oit_sergei.cam_test.toast_pressed_activity;

import java.util.ArrayList;
import java.util.List;

public class audio_listener_service extends IntentService {

    private String[] cameraList;
    private String detailMessage;
    private audio_check audioCheck;
    private String microphone_permisson = new String("android.permission.RECORD_AUDIO");
    private static int first_cycle_flag;
    private PackageInfo audio_blocked_pack = new PackageInfo();
    private PackageInfo result_app_service = new PackageInfo();
    private PackageInfo result_app_activity = new PackageInfo();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public audio_listener_service(String name) {
        super(name);
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

//        Toast.makeText(getApplicationContext(), "Start", Toast.LENGTH_SHORT).show();
        PendingIntent pendingIntent = intent.getParcelableExtra(MainActivity.PARAM_PINTENT);

        audioCheck = new audio_check();
        int audio_availability = audioCheck.audio_checking_process();
        if (audio_availability == 0) {
//            Toast.makeText(getApplicationContext(), "Camera opened", Toast.LENGTH_SHORT).show();
            boolean close_cheking = audioCheck.audio_close();
            if (close_cheking == true) {
//                Toast.makeText(getApplicationContext(), "Camera close OK", Toast.LENGTH_SHORT).show();
            } else if (close_cheking == false) {
//                Toast.makeText(getApplicationContext(), "Camera close ERROR", Toast.LENGTH_SHORT).show();
            }
            first_cycle_flag = 1;

        } else if (audio_availability != 0) {
//            Toast.makeText(this, "Camera unavailable", Toast.LENGTH_SHORT).show();
            if (first_cycle_flag == 1)
            {
                audio_blocked_pack = application_resolve();

                if (audio_blocked_pack != null)
                {
                    first_cycle_flag = 0;

                    Intent i = new Intent("Microphone_unavailable")
                            .putExtra("Microphone_app", audio_blocked_pack);

                    sendBroadcast(i);
                    send_notification(audio_blocked_pack);

                    stopSelf();
                }
            }


        } else if (audio_availability == -2) {
            Toast.makeText(getApplicationContext(), "Microphone error system", Toast.LENGTH_SHORT).show();
        }
        stopSelf();

    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

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

        //Создание контейнеров для отсеяных по microphone permission processes и services
        List <PackageInfo> packageInfos_running = new ArrayList<>();
        List <ActivityManager.RunningAppProcessInfo> RunningAppProcessInfo_checked = new ArrayList<>();
        List <ActivityManager.RunningServiceInfo> runningServiceInfos_checked = new ArrayList<>();

        //Проверка Running Tasks для финального отбора activity
        List <ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(Integer.MAX_VALUE);

        //Отсеивание всех запущенных активити по microphone permission
        for (int i = 0; i < runningAppProcessInfos.size(); i++)
        {
            for (int j = 0; j < packageInfos.size(); j++)
            {
                flag = 0;

                if (runningAppProcessInfos.get(i).processName.equals(packageInfos.get(j).applicationInfo.processName) && packageInfos.get(j).requestedPermissions != null)
                {
                    for (int k = 0; k < packageInfos.get(j).requestedPermissions.length; k++)
                    {
                        if (packageInfos.get(j).requestedPermissions[k].toString().equals(microphone_permisson))
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

        //Отсеивание всех запущенных сервисов по microphone permission
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

                long min_time = 1500;
                int min_i = 1500;
                for (int i = 1; i < runningServiceInfos_checked.size(); i++)
                {
                    if ((Math.abs(runningServiceInfos_checked.get(i).lastActivityTime - runningServiceInfos_checked.get(my_process).lastActivityTime) < min_time)
                            && (i != my_process))
                    {
                        min_time = Math.abs(runningServiceInfos_checked.get(i).lastActivityTime - runningServiceInfos_checked.get(my_process).lastActivityTime);
                        min_i = i;
                    }
                }

                if (min_i <= 1000)
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

    private static final int NOTIFY_ID = 102;
    public void send_notification(PackageInfo pack_app)
    {
        Intent notification_intent = new Intent(getApplicationContext(), toast_pressed_activity.class)
                .putExtra("Pack_app", pack_app);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notification_intent, PendingIntent.FLAG_CANCEL_CURRENT);


        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_launcher)

                .setTicker("Microphone was opened")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                        //.setContentTitle(res.getString(R.string.notifytitle)) // Заголовок уведомления
                .setContentTitle("Microphone was opened")
                        //.setContentText(res.getString(R.string.notifytext))
                .setContentText("Check app: " + pack_app.packageName); // Текст уведомленимя

        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_ALL;


        NotificationManager notificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFY_ID, notification);
    }

}
