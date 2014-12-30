package com.example.oit_sergei.cam_test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class toast_pressed_activity extends ActionBarActivity {

    private PackageInfo camera_blocked_pack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toast_pressed_activity);

        registerReceiver(app_get, new IntentFilter("Camera_unavailable"));
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


    private BroadcastReceiver app_get = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            camera_blocked_pack = intent.getParcelableExtra("App_obj");
            String cam_app_name = new String(camera_blocked_pack.applicationInfo.processName);
            int j = 0;





        }
    };

}
