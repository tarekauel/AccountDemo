package de.orangecode.accountdemo.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkHelper {

    public static final String INVALID_TOKEN = "INVALID_TOKEN";

    public static String doRequest(Context context, String resourceURI, String method, String data) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String ip = prefs.getString("ip", "10.0.2.2");
        String port = prefs.getString("port", "8080");
        Log.i("de.orangecode.accountdemo.android.log", ip);
        String baseURI = "http://"+ip+":"+port+"/AccountDemoServer/";
        try {
            URL requestURL = new URL(baseURI + resourceURI);
            HttpURLConnection connection = (HttpURLConnection) requestURL.openConnection();
            connection.setUseCaches(false);
            connection.setRequestMethod(method);
            connection.setDoInput(true);

            if (data != null && (method.equals("POST") || method.equals("PUT"))) {
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Content-Length", Integer.toString(data.getBytes().length));
                DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
                writer.writeBytes(data);
                writer.flush();
                writer.close();
            }
            connection.connect();

            BufferedReader reader;
            try {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } catch (FileNotFoundException e) {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            } catch (IOException e) {
                reader = null;
            }

            if (connection.getResponseCode() == 200) {
                return reader.readLine();
            } else if (connection.getResponseCode() == 401) {
                return INVALID_TOKEN;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
}
