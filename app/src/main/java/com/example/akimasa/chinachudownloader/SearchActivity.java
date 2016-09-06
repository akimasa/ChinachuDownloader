package com.example.akimasa.chinachudownloader;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
}