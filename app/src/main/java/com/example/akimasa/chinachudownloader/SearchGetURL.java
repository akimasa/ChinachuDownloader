package com.example.akimasa.chinachudownloader;

import android.util.Log;

import org.json.JSONArray;

import java.util.List;

/**
 * Created by akimasa on 16/09/04.
 */
public class SearchGetURL extends GetURL{
    MainActivity that;
    CharSequence title;
    public SearchGetURL(MainActivity _that,CharSequence _title) {
        this.that = _that;
        this.title = _title;
        Log.d("SearchGetURL",title.toString());
    }
    @Override
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
