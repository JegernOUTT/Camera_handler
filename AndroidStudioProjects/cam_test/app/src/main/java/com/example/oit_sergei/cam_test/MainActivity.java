package com.example.oit_sergei.cam_test;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import java.util.StringTokenizer;


public class MainActivity extends ActionBarActivity {

    private Button start;
    private Button stop;
    private Button blockCamera;
    private Button unlockCamera;

    private TextView textView;
    public static final String PARAM_PINTENT = "pendingIntent";
    public final static String PARAM_RESULT = "result";
    private PackageInfo camera_blocked_pack;

    private Camera camera;

    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    private Intent intentToFire;
    long time = 1000;
    private int alarmType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = (Button) findViewById(R.id.start_cam_btn);
        stop = (Button) findViewById(R.id.stop_cam_btn);
        blockCamera = (Button) findViewById(R.id.block_cam_btn);
        unlockCamera = (Button) findViewById(R.id.unlock_cam_btn);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        intentToFire = new Intent(this, MyService.class);

        alarmType = AlarmManager.ELAPSED_REALTIME;

        registerReceiver(app_get, new IntentFilter("Camera_unavailable"));
    }

    public void onStartClick(View v)
    {
        alarmIntent = PendingIntent.getService(this, 0, intentToFire, 0);
        alarmManager.setInexactRepeating(alarmType, time, time, alarmIntent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Toast.makeText(getApplicationContext(), "GOOD NEWS", Toast.LENGTH_SHORT).show();

    }

    public void onStopClick(View v)
    {
        try {
            stopService(new Intent(this, MyService.class));
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(alarmIntent);
            Toast.makeText(this, "Service closed", Toast.LENGTH_SHORT).show();
        } catch (RuntimeException re)
        {
            Toast.makeText(this, "Can not close the service",Toast.LENGTH_SHORT).show();
        }

    }

    public void onBlockCameraClick(View V)
    {
        if (checkCameraHardware(getApplicationContext()) == true)
        {
            camera = getCameraInstance();
            Toast.makeText(this, "Camera blocked!!!", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, "Camera Hardware Unavailable",Toast.LENGTH_SHORT).show();
        if (camera == null)
        {
            Toast.makeText(this,"Camera Unavailable",Toast.LENGTH_SHORT).show();
        }

    }


    public void onUnlockCameraClick(View V)
    {
        if (camera != null)
        {
            camera.release();
            Toast.makeText(this, "Camera unlocked", Toast.LENGTH_SHORT).show();
        }
    }


    private BroadcastReceiver app_get = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            camera_blocked_pack = intent.getParcelableExtra("App_obj");
            String cam_app_name = new String(camera_blocked_pack.applicationInfo.processName);
            int j = 0;

            String szDelimeters = ".";
            String app_name = new String();
            StringTokenizer stringTokenizer = new StringTokenizer(camera_blocked_pack.applicationInfo.processName, szDelimeters, true);
            while (stringTokenizer.hasMoreTokens())
            {
                app_name = stringTokenizer.nextToken();
            }

            Toast.makeText(getApplicationContext(), "Camera is opened in " + app_name + " application. If you want to edit permissions, touch the toast.", Toast.LENGTH_LONG).show();
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