package com.example.akimasa.chinachudownloader;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.support.v7.view.menu.ExpandedMenuView;
import android.support.v7.widget.ButtonBarLayout;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.widget.SimpleAdapter;
import android.widget.TextView;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    android.content.Context ctx;
    boolean adaptercalled = false;
    private final MainActivity self = this;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // SwipeRefreshLayoutの設定
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        //mSwipeRefreshLayout.setColorScheme(R.color.red, R.color.green, R.color.blue, R.color.yellow);


        ctx = this;
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){



                //Intent intent = new Intent(getApplication(),SettingsActivity.class);
                //startActivity(intent);
            }
        });


    }
    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            // 3秒待機
            /*
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(false);
                    SetView();
                }
            }, 3000);
            */
            //mSwipeRefreshLayout.setRefreshing(false);
            SetView();
        }
    };
    @Override
    protected void onResume(){
        super.onResume();
        SetView();

    }
    private void SetView(){



        new Thread(new Runnable() {


            @Override
            public void run() {
                try {
                    final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
                    //Log.d("pref",sp.getString("host",null));
                    String host = sp.getString("host",null);
                    String port = sp.getString("port",null);
                    String user = sp.getString("user",null);
                    String password = sp.getString("password",null);
                    // 大阪の天気予報XMLデータ
                    URL url = new URL("http://"+host+":"+port+"/api/recorded.json");
                    HttpURLConnection con = (HttpURLConnection)url.openConnection();
                    byte[] auth = (user+":"+password).getBytes();
                    String authstr = android.util.Base64.encodeToString(auth, Base64.DEFAULT);
                    Log.d("BASE64",authstr);
                    //con.addRequestProperty("Authorization", "Basic YWthcmk6YmFrdWhhdHN1");
                    con.addRequestProperty("Authorization", "Basic "+authstr);
                    String str = InputStreamToString(con.getInputStream());
                    Log.d("HTTP", str);
                    final JSONArray json = new JSONArray(str);
                    final List<Record> recs = new ArrayList<Record>();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MyAdapter adapter = new MyAdapter(ctx, recs, sp);
                            if(!adaptercalled) {
                                ListView listView = (ListView) findViewById(R.id.listView);
                                listView.setAdapter(adapter);
                                adaptercalled = true;
                                Log.d("hoge","adapter first time call");
                            }
                            //Log.d("json", json.length()+"");
                    /*
                    // ListView に表示する文字列を生成
                    final List<Map<String, String>> list = new ArrayList<Map<String, String>>();
                    */

                            for (int i = 0; i < json.length(); i++) {
                                //Log.d("array item",json.getJSONObject(i).getString("fullTitle"));
                                //Log.d("array ch",json.getJSONObject(i).getJSONObject("channel").getString("name"));
                                //Log.d("array id",json.getJSONObject(i).getString("id"));
                                //http://skylark-host.local:10772/api/recorded/gr1048-5wd/file.m2ts
                        /*
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("main", json.getJSONObject(i).getString("fullTitle"));
                        map.put("sub", json.getJSONObject(i).getString("id"));
                        list.add(map);
                        */
                                try {
                                    recs.add(new Record(json.getJSONObject(i).getString("fullTitle"),
                                            json.getJSONObject(i).getJSONObject("channel").getString("name"),
                                            json.getJSONObject(i).getString("id"),
                                            json.getJSONObject(i).getLong("start")));


                                } catch (Exception ex) {

                                }
                            }
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    });



                    /*
                    // ListView に設定するデータ (アダプタ) を生成する (テキスト 2 行表示リスト)
                    final SimpleAdapter adapter = new SimpleAdapter(
                            that,
                            list,
                            android.R.layout.simple_list_item_2,
                            new String[] {"main", "sub"},
                            new int[] {android.R.id.text1, android.R.id.text2}
                    );


                    final ListView listView = (ListView) findViewById(R.id.listView);
                    listView.setAdapter(adapter);
                    */
                } catch(Exception ex) {
                    Log.d("ex", ex.toString());
                }
            }
        }).start();
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
