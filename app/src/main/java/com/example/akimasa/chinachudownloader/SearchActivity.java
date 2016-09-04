package com.example.akimasa.chinachudownloader;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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