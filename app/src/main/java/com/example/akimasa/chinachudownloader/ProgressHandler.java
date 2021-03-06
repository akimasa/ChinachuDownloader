package com.example.akimasa.chinachudownloader;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.ProgressBar;

import javax.net.ssl.HandshakeCompletedListener;

/**
 * Created by akimasa on 16/07/10.
 */
public class ProgressHandler extends Handler {

    Hoge _hoge;
    DownloadFiles _downloadFiles;
    ProgressHandler(Hoge hoge, DownloadFiles downloadFiles){
        _hoge = hoge;
        _downloadFiles = downloadFiles;
    }

    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (_hoge._fileLoader.isCancelled()) {
            //_progressDialog.dismiss();
            Log.d("DL", "load canceled");
        } else if (_hoge._fileLoader.getStatus() == AsyncTask.Status.FINISHED) {
            //_progressDialog.dismiss();
            Log.d("DL", "load finished");
            _downloadFiles.downNext();
            /** Create an intent that will be fired when the user clicks the notification.
             * The intent needs to be packaged into a {@link android.app.PendingIntent} so that the
             * notification service can fire it on our behalf.
             */
            //Intent intent = new Intent(Intent.ACTION_VIEW,
            //        Uri.parse("http://developer.android.com/reference/android/app/Notification.html"));
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("file:///storage/emulated/0"+_hoge._fileLoader.outFileName));
            intent.setComponent(new ComponentName("org.videolan.vlc", "org.videolan.vlc.gui.video.VideoPlayerActivity"));
            PendingIntent pendingIntent = PendingIntent.getActivity(_hoge._context, 0, intent, 0);

            /**
             * Use NotificationCompat.Builder to set up our notification.
             */
            NotificationCompat.Builder builder = new NotificationCompat.Builder(_hoge._context);

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
            builder.setLargeIcon(BitmapFactory.decodeResource(_hoge._context.getResources(), R.drawable.c));

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
            builder.setContentText(_hoge._fileLoader.fullTitle);
            //builder.setSubText(_hoge._fileLoader.fullTitle);

            builder.setDefaults(Notification.DEFAULT_LIGHTS|Notification.DEFAULT_SOUND);


            /**
             * Send the notification. This will immediately display the notification icon in the
             * notification bar.
             */
            NotificationManager notificationManager = (NotificationManager) _hoge._context.getSystemService(
                    _hoge._context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, builder.build());
        } else {
            //_progressDialog.setProgress(_fileLoader.getLoadedBytePercent());
            Log.d("DL", Integer.toString(_hoge._fileLoader.getLoadedBytePercent()));
            _hoge.progress.setProgress(_hoge._fileLoader.getLoadedBytePercent());
            _hoge._progressHandler.sendEmptyMessageDelayed(0, 250);
        }
    }
}
