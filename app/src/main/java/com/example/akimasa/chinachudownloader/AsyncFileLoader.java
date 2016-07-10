package com.example.akimasa.chinachudownloader;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by akimasa on 16/07/10.
 */
public class AsyncFileLoader extends AsyncTask<Void, Void, Boolean> {
    private final String TAG = "AsyncFileLoader";
    private String _urlStr;
    private URL _url;
    private URLConnection _urlConnection;
    private final int TIMEOUT_READ = 5000;
    private final int TIMEOUT_CONNECT = 30000;
    private InputStream _inputStream;
    private BufferedInputStream _bufferedInputStream;
    private FileOutputStream _fileOutputStream;
    private File _outputFile;
    private byte[] buff = new byte[5 * 1024];

    private long _totalByte = 0;
    private long _currentByte = 0;
    private String _auth;
    public String fullTitle;
    public String outFileName;

    public AsyncFileLoader(String urlStr, File outputFile, String auth){
        this._urlStr = urlStr;
        this._outputFile = outputFile;
        this._auth = auth;
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        if(isCancelled()){
            return false;
        }

        try{
            int len;
            while((len = _bufferedInputStream.read(buff)) != -1){
                _fileOutputStream.write(buff, 0, len);
                _currentByte += len;
                //publishProgress();
                if(isCancelled()){
                    break;
                }
            }

        }catch(IOException e){
            Log.d(TAG, "error on read file:" + e.toString());
            return false;
        }
        return true;
    }

    @Override
    protected void onPreExecute(){
        try{
            connect();
        }catch(IOException e){
            Log.d(TAG, "error on preExecute:" + e.toString());
            cancel(true);
        }
    }
    /*
    @Override
    protected void onProgressUpdate(Void... progress){

    }
    */
    @Override
    protected void onPostExecute(Boolean result){
        if(result == true){
            try{
                close();
            }catch(IOException e){
                Log.d(TAG, "error on postExecute:" + e.toString());
            }
        }else{
            Log.d(TAG, "result: load error");
        }
    }

    private void connect() throws IOException
    {
        _url = new URL(_urlStr+".m2ts");
        _urlConnection = _url.openConnection();
        _urlConnection.addRequestProperty("Authorization", "Basic "+this._auth);
        _urlConnection.setReadTimeout(TIMEOUT_READ);
        _urlConnection.setConnectTimeout(TIMEOUT_CONNECT);
        _inputStream = _urlConnection.getInputStream();
        _bufferedInputStream = new BufferedInputStream(_inputStream, 1024 * 5);
        _fileOutputStream = new FileOutputStream(_outputFile);

        //_totalByte = _bufferedInputStream.available(); //this is not work
        _totalByte = _urlConnection.getContentLength();
        if(_totalByte < 0){
            URL url = new URL(_urlStr+".json");
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.addRequestProperty("Authorization", "Basic "+this._auth);
            String str = InputStreamToString(con.getInputStream());
            Log.d("HTTP", str);
            try {
                final JSONObject json = new JSONObject(str);
                long size = Long.parseLong(json.getString("size"));
                Log.d("size", Long.toString(size));
                _totalByte = size;
            } catch (Exception ex) {
                Log.d("AsyncFileLoader ex", ex.toString());
            }
        }
        _currentByte = 0;
    }

    private void close() throws IOException
    {
        _fileOutputStream.flush();
        _fileOutputStream.close();
        _bufferedInputStream.close();
    }

    public int getLoadedBytePercent()
    {
        Log.d(TAG, Long.toString(_currentByte)+"/"+Long.toString(_totalByte));
        if(_totalByte <= 0){
            return 0;
        }
        //Log.d(TAG, Integer.toString(_currentByte) + ":" + Integer.toString(_totalByte));

        return (int)Math.floor(100 * _currentByte/_totalByte);
    }
    static String InputStreamToString(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }
}