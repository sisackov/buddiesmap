package com.buddiesmap.recommendations;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AsyncHTTPPostRequest extends AsyncTask<Void, Void, String> {
    private static final String REQUEST_METHOD_POST = "POST";
    private static final String SERVER = "http://10.0.2.2:3000/save";//localhost of the emulator
    private final JSONObject mJson;

    public AsyncHTTPPostRequest(JSONObject json) {
        mJson = json;
    }

    @Override
    protected String doInBackground(Void... params) {
        String result = "";

        try {
            URL url = new URL(SERVER);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(REQUEST_METHOD_POST);
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            Log.i("JSON", mJson.toString());
            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.writeBytes(mJson.toString());

            os.flush();
            os.close();

            Log.i("STATUS", String.valueOf(conn.getResponseCode()));
            Log.i("MSG", conn.getResponseMessage());

            result = conn.getResponseMessage();
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}

