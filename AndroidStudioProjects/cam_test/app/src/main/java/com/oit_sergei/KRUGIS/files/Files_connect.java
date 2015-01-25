package com.oit_sergei.KRUGIS.files;

import android.app.Activity;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;


public class Files_connect extends Activity {

    final String FILENAME_white = "white_list";
    final String FILENAME_black = "black_list";

    public void rewrite_white_list(List<String> white_list)
    {
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

    public void rewrite_black_list(List<String> black_list)
    {
        try
        {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(openFileOutput(FILENAME_black, MODE_PRIVATE)));
            for (int i = 0; i < black_list.size(); i++)
            {
                try
                {
                    bufferedWriter.write(black_list.get(i).toString());
                    bufferedWriter.close();
                } catch (IOException ioe)
                {
                    Toast.makeText(this, "Black List write error", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (FileNotFoundException f)
        {
            Toast.makeText(this, "File not found to write black list", Toast.LENGTH_SHORT).show();
        }
    }

    public void append_white_file(String white_list)
    {
        try
        {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(openFileOutput(FILENAME_white, MODE_PRIVATE)));
            try
            {
                bufferedWriter.append(white_list);
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

    public void append_black_file(String black_list)
    {
        try
        {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(openFileOutput(FILENAME_black, MODE_PRIVATE)));

            try
            {
                bufferedWriter.append(black_list);
                bufferedWriter.close();
            } catch (IOException ioe)
            {
                Toast.makeText(this, "Black List write error", Toast.LENGTH_SHORT).show();
            }

        } catch (FileNotFoundException f)
        {
            Toast.makeText(this, "File not found to write black list", Toast.LENGTH_SHORT).show();
        }
    }

    public List<String> read_white_file()
    {
        try {
            openFileInput(FILENAME_white);
        } catch (FileNotFoundException e) {
            String s = "No elements";
            append_white_file(s);
            e.printStackTrace();
        }

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openFileInput(FILENAME_white)));
            String temp;
            List<String> white_list = new ArrayList<>();
            while ((temp = bufferedReader.readLine()) != null)
            {
                white_list.add(temp);
            }
            bufferedReader.close();
            return white_list;
        } catch (Exception io)
        {
            Toast.makeText(this, "White List read error", Toast.LENGTH_SHORT).show();
            return null;
        }

    }

    public List<String> read_black_file()
    {
        try {
            openFileInput(FILENAME_black);
        } catch (FileNotFoundException e) {
            String s = "No elements";
            append_black_file(s);
            e.printStackTrace();
        }


        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openFileInput(FILENAME_black)));
            String temp;
            List<String> black_list = new ArrayList<>();
            while ((temp = bufferedReader.readLine()) != null)
            {
                black_list.add(temp);
            }
            bufferedReader.close();
            return black_list;
        } catch (IOException io)
        {
            Toast.makeText(this, "Black List read error", Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}
