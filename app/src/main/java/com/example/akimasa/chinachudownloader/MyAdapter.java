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
        TextView textView2;
    }
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
            holder.textView2 = (TextView) convertView.findViewById(R.id.textView2);



            // タグにホルダークラスを設定する
            convertView.setTag(holder);
        } else {
            // 2回目以降はビューが設定されている
            // タグからホルダークラスを取得する
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textView.setText(list.get(position).fullTitle);
        holder.textView2.setText(list.get(position).channelName);



        //progress = (ProgressBar) convertView.findViewById(R.id.progressBar);
        //progress = holder.progressBar;



        convertView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                //new AlertDialog.Builder(v.getContext()).setMessage(list.get(position).id).show();
                /*
                Uri uri = Uri.parse("http://akari:bakuhatsu@192.168.0.23:10772/api/recorded/"+list.get(position).id+"/file.m2ts");
                Intent i = new Intent(Intent.ACTION_VIEW,uri);
                i.setComponent(new ComponentName("org.mozilla.firefox", "org.mozilla.firefox.App"));
                v.getContext().startActivity(i);
                */

                File dataDirectory = Environment.getExternalStorageDirectory();
                File directory = new File(dataDirectory.getAbsolutePath() + "/Download");
                File outputFile = new File(directory, list.get(position).id+list.get(position).fullTitle+".ts");

                Log.d("pref",sp.getString("host",null));
                String host = sp.getString("host",null);
                String port = sp.getString("port",null);
                String user = sp.getString("user",null);
                String password = sp.getString("password",null);
                byte[] auth = (user+":"+password).getBytes();
                String authstr = android.util.Base64.encodeToString(auth, Base64.DEFAULT);

                _fileLoader = new AsyncFileLoader("http://"+host+":"+port+"/api/recorded/"+list.get(position).id+"/file", outputFile, authstr);
                final View vu = v;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("onclick","exec run");
                        progress = (ProgressBar) vu.getRootView().findViewById(R.id.progressBar2);
                        progress.setMax(100);
                        progress.setProgress(0);
                        _fileLoader.fullTitle = list.get(position).fullTitle;
                        _fileLoader.outFileName = "/Download/"+list.get(position).id+list.get(position).fullTitle+".ts";
                        _fileLoader.execute();
                        _progressHandler.sendEmptyMessage(0);
                    }
                }).start();


            }
        });
        _progressHandler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (_fileLoader.isCancelled()) {
                    //_progressDialog.dismiss();
                    Log.d("DL", "load canceled");
                } else if (_fileLoader.getStatus() == AsyncTask.Status.FINISHED) {
                    //_progressDialog.dismiss();
                    Log.d("DL", "load finished");
                    /** Create an intent that will be fired when the user clicks the notification.
                     * The intent needs to be packaged into a {@link android.app.PendingIntent} so that the
                     * notification service can fire it on our behalf.
                     */
                    //Intent intent = new Intent(Intent.ACTION_VIEW,
                    //        Uri.parse("http://developer.android.com/reference/android/app/Notification.html"));
                    Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("file:///storage/emulated/0"+_fileLoader.outFileName));
                    intent.setComponent(new ComponentName("org.videolan.vlc", "org.videolan.vlc.gui.video.VideoPlayerActivity"));
                    PendingIntent pendingIntent = PendingIntent.getActivity(_context, 0, intent, 0);

                    /**
                     * Use NotificationCompat.Builder to set up our notification.
                     */
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(_context);

                    /** Set the icon that will appear in the notification bar. This icon also appears
                     * in the lower right hand corner of the notification itself.
                     *
                     * Important note: although you can use any drawable as the small icon, Android
                     * design guidelines state that the icon should be simple and monochrome. Full-color
                     * bitmaps or busy images don't render well on smaller screens and can end up
                     * confusing the user.
                     */
                    builder.setSmallIcon(R.drawable.c);

                    // Set the intent that will fire when the user taps the notification.
                    builder.setContentIntent(pendingIntent);

                    // Set the notification to auto-cancel. This means that the notification will disappear
                    // after the user taps it, rather than remaining until it's explicitly dismissed.
                    builder.setAutoCancel(true);

                    /**
                     *Build the notification's appearance.
                     * Set the large icon, which appears on the left of the notification. In this
                     * sample we'll set the large icon to be the same as our app icon. The app icon is a
                     * reasonable default if you don't have anything more compelling to use as an icon.
                     */
                    builder.setLargeIcon(BitmapFactory.decodeResource(_context.getResources(), R.drawable.c));

                    /**
                     * Set the text of the notification. This sample sets the three most commononly used
                     * text areas:
                     * 1. The content title, which appears in large type at the top of the notification
                     * 2. The content text, which appears in smaller text below the title
                     * 3. The subtext, which appears under the text on newer devices. Devices running
                     *    versions of Android prior to 4.2 will ignore this field, so don't use it for
                     *    anything vital!
                     */
                    builder.setContentTitle("Load finished");
                    builder.setContentText(Integer.toString(position));
                    builder.setSubText(_fileLoader.fullTitle);

                    builder.setDefaults(Notification.DEFAULT_LIGHTS|Notification.DEFAULT_SOUND);


                    /**
                     * Send the notification. This will immediately display the notification icon in the
                     * notification bar.
                     */
                    NotificationManager notificationManager = (NotificationManager) _context.getSystemService(
                            _context.NOTIFICATION_SERVICE);
                    notificationManager.notify(1, builder.build());
                } else {
                    //_progressDialog.setProgress(_fileLoader.getLoadedBytePercent());
                    Log.d("DL", Integer.toString(_fileLoader.getLoadedBytePercent()));
                    progress.setProgress(_fileLoader.getLoadedBytePercent());
                    _progressHandler.sendEmptyMessageDelayed(0, 250);
                }
            }
        };


        return convertView;
    }
}
