package com.example.akimasa.chinachudownloader;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;

import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {
    android.content.Context ctx;
    boolean adaptercalled = false;
    private final MainActivity self = this;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // SwipeRefreshLayoutの設定
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        //mSwipeRefreshLayout.setColorScheme(R.color.red, R.color.green, R.color.blue, R.color.yellow);

        ListView listview = (ListView) findViewById(R.id.listView);

        ctx = this;
        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(getApplication(),SettingsActivity.class);
                startActivity(intent);
            }
        });

        final Button button2 = (Button) findViewById(R.id.button2);




        final EditText edit = (EditText) findViewById(R.id.editText);
        edit.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                edit.setFocusable(true);
                edit.setFocusableInTouchMode(true);
                edit.requestFocus();
            }
        });
        edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(hasFocus == true){
                    imm.showSoftInput(edit,0);
                } else {
                    imm.hideSoftInputFromWindow(v.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });

        final CharSequence title = edit.getText();
        button2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(getApplication(),SearchActivity.class);
                intent.putExtra("title",title);
                startActivity(intent);
            }
        });
        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                view.setFocusable(true);
                view.setFocusableInTouchMode(true);
                view.requestFocus();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)mSwipeRefreshLayout.getLayoutParams();
                if(firstVisibleItem > 1){
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    mSwipeRefreshLayout.setLayoutParams(params); //causes layout update
                    button.setVisibility(View.INVISIBLE);
                    button2.setVisibility(View.INVISIBLE);
                    edit.setVisibility(View.INVISIBLE);
                } else {
                    params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    mSwipeRefreshLayout.setLayoutParams(params); //causes layout update
                    button.setVisibility(View.VISIBLE);
                    button2.setVisibility(View.VISIBLE);
                    edit.setVisibility(View.VISIBLE);
                }

            }
        });

    }
    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            SetView();
        }
    };
    @Override
    protected void onResume(){
        super.onResume();
        SetView();

    }
    protected void SetView(){
        new Thread(new GetURL(this)).start();
    }

}
