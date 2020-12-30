package com.example.assignment2.controller.RouteService;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchURL extends AsyncTask<String,Void,String> {

    private Context context;
    protected String directionMode = "driving";

    public FetchURL(Context context){
        this.context = context;
    }

    @Override
    protected String doInBackground(String... strings) {
        String data = "";
//        directionMode = strings[1];
        try {
            data = downloadUrl(strings[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        PointsParser parserTask = new PointsParser(context,directionMode);
        parserTask.execute(s);
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder buffer = new StringBuilder();
            String line;
            while((line = bufferedReader.readLine())!= null){
                buffer.append(line);
            }
            data = buffer.toString();
            Log.d("hello", "Downloaded URL: " + data);
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            inputStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}
//This code can be found at:
//https://github.com/Vysh01/android-maps-directions.git
//With the little modification and added the direction API key, it can draw the routing for the users
