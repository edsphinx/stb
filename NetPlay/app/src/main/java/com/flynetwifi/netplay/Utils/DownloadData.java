package com.flynetwifi.netplay.Utils;


import com.flynetwifi.netplay.MainActivity;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadData {
    OkHttpClient client = new OkHttpClient();

    public String run(String url){
        String result = "";
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json; q=0.5")
                .addHeader("Authorization", "Bearer " + MainActivity.access_token)
                .build();
        try (
                Response response = client.newCall(request).execute()) {
            result =  response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
