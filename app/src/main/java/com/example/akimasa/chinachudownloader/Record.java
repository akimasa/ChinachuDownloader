package com.example.akimasa.chinachudownloader;

/**
 * Created by akimasa on 16/07/10.
 */
public class Record {
    public String fullTitle;
    public String channelName;
    public String id;
    public Record(String _fullTitle, String _channelName, String _id){
        this.fullTitle = _fullTitle;
        this.channelName = _channelName;
        this.id = _id;
    }
}
