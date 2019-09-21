package com.buddiesmap.recommendations;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.buddiesmap.MainActivity.HTTP_RESPONSE;
import static com.buddiesmap.MainActivity.HTTP_RESPONSE_EXTRA;

public class AsyncHTTPRequest extends AsyncTask<Void, Void, String> {

    private static final String REQUEST_METHOD_GET = "GET";
    private static final int READ_TIMEOUT = 15000;
    private static final int CONNECTION_TIMEOUT = 15000;
    private static final String SERVER = "http://10.0.2.2:3000/get";//localhost of the emulator
    //    private static final String SERVER = "http://localhost:3000/get";
//    private int mRequestType = 1;//should be Enum todo

    private WeakReference<Context> mContext;

    public AsyncHTTPRequest(Context context) {
        mContext = new WeakReference<>(context);
    }

    @Override
    protected String doInBackground(Void... params) {
        String result;
        String inputLine;
        HttpURLConnection connection;

        try {
            // connect to the server
            URL myUrl = new URL(SERVER);
            connection = (HttpURLConnection) myUrl.openConnection();
            connection.setRequestMethod(REQUEST_METHOD_GET);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.connect();
        } catch (IOException e) {
            e.printStackTrace();
            return "HTTP Request error";
        }

        try (InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
             BufferedReader reader = new BufferedReader(streamReader)) {
            // get the string from the input stream

            StringBuilder stringBuilder = new StringBuilder();
            while ((inputLine = reader.readLine()) != null) {
                stringBuilder.append(inputLine);
            }
            reader.close();
            streamReader.close();

            result = stringBuilder.toString();

        } catch (IOException e) {
            e.printStackTrace();
            result = "HTTP Stream read error";
        }

        return result;
    }

    protected void onPostExecute(String result) {
        /*super.onPostExecute(result);
        try {
            JSONTokener jsonTokener = new JSONTokener(result);
            JSONArray finalResult = new JSONArray(jsonTokener);

        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }*/

        Intent intent = new Intent(HTTP_RESPONSE);
        intent.putExtra(HTTP_RESPONSE_EXTRA, result);

        LocalBroadcastManager.getInstance(mContext.get()).sendBroadcast(intent);
    }
}

