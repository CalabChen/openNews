package com.example.opennews.api;

import android.util.Log;

import com.example.opennews.model.hot.HotNewsResponse;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HotNewsApiService {

    private static final String TAG = "HotNewsApiService";
    private static final String API_URL = "https://dabenshi.cn/other/api/hot.php?type=toutiaoHot";

    public static HotNewsResponse fetchHotNews() {
        HttpURLConnection connection = null;
        StringBuilder response = new StringBuilder();

        try {
            URL url = new URL(API_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
            } else {
                Log.e(TAG, "Failed to fetch news data: HTTP error code " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return new Gson().fromJson(response.toString(), HotNewsResponse.class);
    }
}