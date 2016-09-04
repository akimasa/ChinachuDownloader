package com.example.akimasa.chinachudownloader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends MainActivity {
    private CharSequence title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText edit = (EditText) findViewById(R.id.editText);
        edit.setVisibility(View.INVISIBLE);
        Button button2 = (Button) findViewById(R.id.button2);
        button2.setText("Back");
        button2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                finish();
            }
        });
        title = getIntent().getCharSequenceExtra("title");
        Log.d("title",title.toString());
    }
    @Override
    protected void SetView(){
        new Thread(new SearchGetURL(this,title)).start();
    }
    // InputStream -> String
    static String InputStreamToString(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }
}