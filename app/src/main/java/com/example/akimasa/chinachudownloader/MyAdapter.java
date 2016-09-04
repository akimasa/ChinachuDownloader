package com.example.akimasa.chinachudownloader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.AsyncTaskLoader;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by akimasa on 16/07/10.
 */
public class MyAdapter extends BaseAdapter {
    LayoutInflater mInflater;
    private List<Record> list;
    private Context _context;
    private AsyncFileLoader _fileLoader;
    private Handler _progressHandler;
    private ProgressBar progress;
    private SharedPreferences sp;

    class ViewHolder {
        TextView textView;
        TextView date;
        TextView id;
        TextView textView2;
    }

    Hoge hoge = new Hoge();
    DownloadFiles q = new DownloadFiles();
    public MyAdapter(Context context, List<Record> _list, SharedPreferences _sp) {

        // インフレーターを取得する
        this.mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        list = _list;
        sp = _sp;
        _context = context;
    }
    @Override
    public int getCount(){
        return list.size();
    }
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int _position, View convertView, ViewGroup parent){
        final int position = this.getCount()-_position-1;
        // ホルダークラスを定義する
        // ※ ホルダークラスを使うことで再表示時にレイアウト内のビュー検索が無くなり高速化されます
        ViewHolder holder;

        // ビューを設定する
        if (convertView == null) {
            // はじめて呼ばれた時はビューはnullが設定されている
            // ビューに定義したレイアウトをインフレートする
            convertView = this.mInflater.inflate(R.layout.linear_layout, parent,
                    false);

            // ホルダークラスを生成する
            holder = new ViewHolder();
            holder.textView = (TextView) convertView.findViewById(R.id.textView);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.id = (TextView) convertView.findViewById(R.id.id);
            holder.textView2 = (TextView) convertView.findViewById(R.id.textView2);



            // タグにホルダークラスを設定する
            convertView.setTag(holder);
        } else {
            // 2回目以降はビューが設定されている
            // タグからホルダークラスを取得する
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textView.setText(list.get(position).fullTitle);
        holder.date.setText(list.get(position).start);
        holder.id.setText(list.get(position).id);
        holder.textView2.setText(list.get(position).channelName);

        convertView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                download(v, position);
            }
        });
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                        Intent intent = new Intent(_context,SearchActivity.class);
                        String title = list.get(position).fullTitle;
                        title = title.split("　|「| ")[0];
                        Log.d("LongClickTitle",title);
                        intent.putExtra("title",title);
                        _context.startActivity(intent);

                return false;
            }
        });
        return convertView;
    }

    private void download(View v, final int position) {
        File dataDirectory = Environment.getExternalStorageDirectory();
        File directory = new File(dataDirectory.getAbsolutePath() + "/Download");
        File outputFile = new File(directory, list.get(position).id+list.get(position).fullTitle+".ts");

        Log.d("pref",sp.getString("host",null));
        String host = sp.getString("host",null);
        String port = sp.getString("port",null);
        String user = sp.getString("user",null);
        String password = sp.getString("password",null);
        byte[] auth = (user+":"+password).getBytes();
        String authstr = Base64.encodeToString(auth, Base64.DEFAULT);
        Hoge hclone;
        try {
            hclone = hoge.clone();
        } catch (Exception ex) {
            Log.d("cloneex", ex.toString());
            return;
        }
        hclone._fileLoader = new AsyncFileLoader("http://"+host+":"+port+"/api/recorded/"+list.get(position).id+"/file", outputFile, authstr);
        final View vu = v;

                Log.d("onclick","exec run");
                hclone.progress = (ProgressBar) vu.getRootView().findViewById(R.id.progressBar2);
                hclone.progress.setMax(100);
                hclone.progress.setProgress(0);
                hclone._fileLoader.fullTitle = list.get(position).fullTitle;
                hclone._fileLoader.outFileName = "/Download/"+list.get(position).id+list.get(position).fullTitle+".ts";

                hclone._context = _context;
                hclone._progressHandler = new ProgressHandler(hclone,q);

                q.addQ(hclone);

    }
}
