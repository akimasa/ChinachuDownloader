package com.example.akimasa.chinachudownloader;

import android.content.Context;
import android.os.Handler;
import android.widget.ProgressBar;

/**
 * Created by akimasa on 16/07/10.
 */
public class Hoge implements Cloneable {
        public Context _context;
        public AsyncFileLoader _fileLoader;
        public Handler _progressHandler;
        public ProgressBar progress;
    public Hoge clone() throws CloneNotSupportedException {
        return (Hoge) super.clone();
    }
}
