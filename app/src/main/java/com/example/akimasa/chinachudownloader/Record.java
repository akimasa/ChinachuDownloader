package com.example.akimasa.chinachudownloader;

import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Created by akimasa on 16/07/10.
 */
public class Record {
    public String fullTitle;
    public String channelName;
    public String id;
    public String start;
    public Record(String _fullTitle, String _channelName, String _id,Long _start){
        this.fullTitle = _fullTitle;
        this.channelName = _channelName;
        this.id = _id;
        Date startDate = new Date(_start);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        this.start = sdf.format(startDate);
    }
}
