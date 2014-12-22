package com.example.oit_sergei.cam_test;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private Button start;
    private Button stop;
    private Button startCamera;
    private Button stopCamera;
    private Button applications_btn;
    private TextView textView;

    private Camera camera;
    String camera_permisson = new String("android.permission.CAMERA");

    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    private Intent intentToFire;
    long time = 10000;
    private int alarmType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = (Button) findViewById(R.id.button);
        stop = (Button) findViewById(R.id.button2);
        startCamera = (Button) findViewById(R.id.button4);
        stopCamera = (Button) findViewById(R.id.button5);
        applications_btn = (Button) findViewById(R.id.button3);
        textView = (TextView) findViewById(R.id.textView);


        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        intentToFire = new Intent(this, MyService.class);
        alarmIntent = PendingIntent.getService(this, 0, intentToFire, 0);
    }

    public void onStartClick(View v)
    {
        alarmType = AlarmManager.ELAPSED_REALTIME;
        alarmManager.setInexactRepeating(alarmType, time, time, alarmIntent);
    }

    public void onStopClick(View v)
    {
        try {
            stopService(intentToFire);
        } catch (RuntimeException re)
        {
            Toast.makeText(this, "Невозможно закрыть сервис. Нулевой указатель",Toast.LENGTH_SHORT).show();
        }
        alarmManager.cancel(alarmIntent);
    }

    public void onStartCameraClick(View V)
    {
        if (checkCameraHardware(getApplicationContext()) == true)
        {
            camera = getCameraInstance();
        } else
            Toast.makeText(this, "Camera Hardware Unavailable",Toast.LENGTH_SHORT).show();
        if (camera == null)
        {
            Toast.makeText(this,"Camera Unavailable",Toast.LENGTH_SHORT).show();
        }

    }


    public void onStopCameraClick(View V)
    {
        if (camera != null)
        {
            camera.release();
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


    public void application_resolve(View v)
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

        for (int i = 0; i < runningServiceInfos_checked.size(); i++)
        {
            textView.append(runningServiceInfos_checked.get(i).process);
            textView.append("\n");
        }





    }


}