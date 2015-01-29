package com.oit_sergei.KRUGIS.applications_lists;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.oit_sergei.KRUGIS.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;


public class white_list_activity extends Activity {

    ListView listView;

    final String FILENAME_white = "white_list";

    public static final String APP_PREFERENCES = "lists";
    public static final String APP_PREFERENCES_WHITELIST = "white_list";

    SharedPreferences myLists;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_white_list_activity);
        listView = (ListView) findViewById(R.id.whiteListView);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(white_list_activity.this);
                builder.setMessage("You really want to delete item?")
                        .setTitle("Deleting")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                delete_from_white_list(position);
                                List<String> black_list_lst = read_white_file();
                                if (black_list_lst == null)
                                {
                                    black_list_lst = new ArrayList<String>(1);
                                    black_list_lst.add("No elements");
                                }

                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(white_list_activity.this,
                                        android.R.layout.simple_list_item_1, black_list_lst);
                                listView.setAdapter(arrayAdapter);

                                myLists = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return false;
            }
        });

        Intent intent = getIntent();
        String app_name = intent.getStringExtra("App_name_add");

        if (app_name != null)
        {
            List<String> temp = read_white_file();
            int flag = 0;
            if (temp != null)
            {
                for (int i = 0; i < temp.size(); i++)
                {
                    if (temp.get(i).equals(app_name))
                    {
                        flag = 1;
                        Toast.makeText(this, "This application already added", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            if (flag == 0)
            {
                append_white_file(app_name);
            }
        }

        List<String> white_list_lst = read_white_file();
        if (white_list_lst == null)
        {
            white_list_lst = new ArrayList<String>(1);
            white_list_lst.add("No elements");
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, white_list_lst);
        listView.setAdapter(arrayAdapter);

        myLists = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }


    public void delete_from_white_list(int position)
    {
        List <String> white_list = read_white_file();
        white_list.remove(position);

        try
        {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(openFileOutput(FILENAME_white, MODE_PRIVATE)));
            for (int i = 0; i < white_list.size(); i++)
            {
                try
                {
                    bufferedWriter.write(white_list.get(i).toString());
                    bufferedWriter.close();
                } catch (IOException ioe)
                {
                    Toast.makeText(this, "White List write error", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (FileNotFoundException f)
        {
            Toast.makeText(this, "File not found to write white list", Toast.LENGTH_SHORT).show();
        }
    }

    public void append_white_file(String white_list)
    {
        try
        {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(openFileOutput(FILENAME_white, MODE_APPEND)));
            try
            {
                bufferedWriter.append(white_list);
                bufferedWriter.append("\n");
                bufferedWriter.close();
            } catch (IOException ioe)
            {
                Toast.makeText(this, "White List write error", Toast.LENGTH_SHORT).show();
            }

        } catch (FileNotFoundException f)
        {
            Toast.makeText(this, "File not found to write white list", Toast.LENGTH_SHORT).show();
        }
    }

    public List<String> read_white_file()
    {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openFileInput(FILENAME_white)));
            String temp;
            List<String> white_list = new ArrayList<String>();
            while ((temp = bufferedReader.readLine()) != null)
            {
                white_list.add(temp);
            }
            bufferedReader.close();
            return white_list;
        } catch (Exception io)
        {
            Toast.makeText(this, "White List is empty", Toast.LENGTH_SHORT).show();
            return null;
        }

    }
}
