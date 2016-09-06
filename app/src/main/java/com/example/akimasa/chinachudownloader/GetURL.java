package com.example.akimasa.chinachudownloader;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Base64;
import android.util.Log;
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

/**
 * Created by akimasa on 16/09/04.
 */
public class GetURL implements Runnable {

    android.content.Context ctx;
    Activity activity;
    boolean adaptercalled;
    SwipeRefreshLayout mSwipeRefreshLayout;
    public GetURL() {

    }
    public GetURL(MainActivity _that) {
        this.ctx = _that.ctx;
        this.activity = (Activity) _that;
        this.adaptercalled = _that.adaptercalled;
        this.mSwipeRefreshLayout = _that.mSwipeRefreshLayout;

    }

    public void run(){

        try {
            final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.ctx);
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
            con.addRequestProperty("Authorization", "Basic "+authstr);
            String str = InputStreamToString(con.getInputStream());
            Log.d("HTTP", str);
            final JSONArray json = new JSONArray(str);
            final List<Record> recs = new ArrayList<Record>();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MyAdapter adapter = new MyAdapter(ctx, recs, sp);
                    if(!adaptercalled) {
                        ListView listView = (ListView) activity.findViewById(R.id.listView);
                        listView.setAdapter(adapter);
                        adaptercalled = true;
                        Log.d("hoge","adapter first time call");
                    }

                    AddSomething(json,recs);

                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });

        } catch(Exception ex) {
            Log.d("ex", ex.toString());
        }
    }
    protected void AddSomething(JSONArray json, List<Record> recs){
        for (int i = 0; i < json.length(); i++) {
            try {
                recs.add(new Record(json.getJSONObject(i).getString("fullTitle"),
                        json.getJSONObject(i).getJSONObject("channel").getString("name"),
                        json.getJSONObject(i).getString("id"),
                        json.getJSONObject(i).getLong("start")));


            } catch (Exception ex) {

            }
        }
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
