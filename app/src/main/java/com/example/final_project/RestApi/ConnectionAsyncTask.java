package com.example.final_project.RestApi;

import android.app.Activity;
import android.os.AsyncTask;


public class ConnectionAsyncTask extends AsyncTask<String, Void, String> {
    private Activity activity;
    private OnDataReceivedListener listener;
    public ConnectionAsyncTask(Activity activity, OnDataReceivedListener listener) {
        this.activity = activity;
        this.listener = listener;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // You can add any pre-execution tasks here
    }
    @Override
    protected String doInBackground(String... params) {
        return HttpManager.getData(params[0]);
    }
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (listener != null) {
            listener.onDataReceived(s);
        }
    }
    public interface OnDataReceivedListener {
        void onDataReceived(String data);
    }
}

