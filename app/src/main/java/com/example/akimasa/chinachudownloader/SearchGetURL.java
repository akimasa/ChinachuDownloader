package com.example.akimasa.chinachudownloader;

import android.app.Activity;
import android.util.Log;

import org.json.JSONArray;

import java.util.List;

/**
 * Created by akimasa on 16/09/04.
 */
public class SearchGetURL extends GetURL{
    CharSequence title;
    public SearchGetURL(SearchActivity _that,CharSequence _title) {
        super(_that);
        this.title = _title;
    }


    @Override
    protected void AddSomething(JSONArray json, List<Record> recs){
        for (int i = 0; i < json.length(); i++) {
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
