package com.example.oit_sergei.cam_test;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class MainActivity extends ActionBarActivity {

    private Button start;
    private Button stop;
    private Button startCamera;
    private Button stopCamera;
    private Button applications_btn;
    private TextView textView;

    private Camera camera;

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
    /*   PackageManager packageManager = getPackageManager();
        List<ApplicationInfo> applicationInfos = packageManager.getInstalledApplications(0);

        for (int i = 0; i<applicationInfos.size();i++)
        {
            textView.append(applicationInfos.get(i).toString());
            applicationInfos.get(i).describeContents();
            if (applicationInfos.get(i).permission != null)
                textView.append(applicationInfos.get(i).permission);
        }

        */
        Intent intent = new Intent("android.settings.APP_OPS_SETTINGS");
        try {
            startActivity(intent);
        } catch (Exception e) {
            // Cannot launch activity !
            textView.append("Cannot start");
        }


    }


}