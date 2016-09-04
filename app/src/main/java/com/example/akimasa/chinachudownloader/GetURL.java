package com.example.akimasa.chinachudownloader;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Base64;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;

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
    /*
    boolean adaptercalled;
    android.content.Context ctx;
    SwipeRefreshLayout mSwipeRefreshLayout;
    */
    MainActivity that;
    public GetURL() {

    }
    public GetURL(MainActivity _that) {
        this.that = _that;
        /*
        this.adaptercalled = _adaptercalled;
        this.ctx = _ctx;
        this.mSwipeRefreshLayout = _mSwipeRefreshLayout;
        */
    }
    @Override
    public void run(){

        try {
            final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(that);
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
            that.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MyAdapter adapter = new MyAdapter(that, recs, sp);
                    if(!that.adaptercalled) {
                        ListView listView = (ListView) that.findViewById(R.id.listView);
                        listView.setAdapter(adapter);
                        that.adaptercalled = true;
                        Log.d("hoge","adapter first time call");
                    }
                    //Log.d("json", json.length()+"");
                    /*
                    // ListView に表示する文字列を生成
                    final List<Map<String, String>> list = new ArrayList<Map<String, String>>();
                    */
                    AddSomething(json,recs);

                    that.mSwipeRefreshLayout.setRefreshing(false);
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
    protected void AddSomething(JSONArray json, List<Record> recs){
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
