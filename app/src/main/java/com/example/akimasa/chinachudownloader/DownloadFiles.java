package com.example.akimasa.chinachudownloader;

import android.util.Log;

import java.util.Queue;
import java.util.ArrayDeque;

/**
 * Created by akimasa on 16/07/10.
 */
public class DownloadFiles {
    public Queue<Hoge> queue = new ArrayDeque<Hoge>();
    public boolean running = false;
    public void addQ(Hoge hoge){
        queue.add(hoge);
        if(!running){
            running = true;
            this.downNext();
        }
    }
    public void downNext(){
        final Hoge item = queue.poll();
        if(item == null){
            running = false;
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("DownloadFiles","start run");
                item._fileLoader.execute();
                item._progressHandler.sendEmptyMessage(0);
            }
        }).start();
    }
}
