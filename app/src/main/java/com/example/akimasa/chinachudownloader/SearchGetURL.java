package com.example.akimasa.chinachudownloader;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by akimasa on 16/09/04.
 */
public class SearchGetURL extends GetURL{
    SearchActivity that;
    CharSequence title;
    public SearchGetURL(SearchActivity _that,CharSequence _title) {
        this.that = _that;
        this.title = _title;
        this.ctx = _that.ctx;
        this.activity = (Activity) _that;
        this.adaptercalled = _that.adaptercalled;
        this.mSwipeRefreshLayout = _that.mSwipeRefreshLayout;
        Log.d("SearchGetURL",title.toString());
    }


    @Override
    protected void AddSomething(JSONArray json, List<Record> recs){
        Log.d("AddSomething",json.toString());
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
                if (json.getJSONObject(i).getString("fullTitle").contains(this.title)){

                    recs.add(new Record(json.getJSONObject(i).getString("fullTitle"),
                            json.getJSONObject(i).getJSONObject("channel").getString("name"),
                            json.getJSONObject(i).getString("id"),
                            json.getJSONObject(i).getLong("start")));
                }
            } catch (Exception ex) {

            }
        }
    }
}
