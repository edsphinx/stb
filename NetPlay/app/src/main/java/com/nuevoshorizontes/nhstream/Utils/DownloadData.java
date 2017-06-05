package com.nuevoshorizontes.nhstream.Utils;


import android.content.Context;
import android.content.SharedPreferences;

import com.nuevoshorizontes.nhstream.MainActivity;
import com.nuevoshorizontes.nhstream.Tasks.LoginTask;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadData {
    private final OkHttpClient client = new OkHttpClient();

    private SharedPreferences settings;

    private LoginTask mAuthTask = null;

    public String run(Context mContext, String url){
        //settings = getSharedPreferences("settings", 0);
        String result = "";
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json; q=0.5")
                .addHeader("Authorization", "Bearer " + MainActivity.access_token)
                .build();
        try (Response response = client.newCall(request).execute()) {
            result =  response.body().string();
            //result.split(":");
            //if(result == "{\"error\":\"Unauthenticated.\"}" ){
            //if(result=="error"?true:false)
            if(result.contains("Unauthenticated.")){
                mAuthTask = new LoginTask(mContext,
                         MainActivity.userName, MainActivity.passWord, settings);
                mAuthTask.execute((Void) null);
                mAuthTask = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

//    public String run(String url){
//        //settings = getSharedPreferences("settings", 0);
//        String result = "";
//        Request request = new Request.Builder()
//                .url(url)
//                .addHeader("Accept", "application/json; q=0.5")
//                .addHeader("Authorization", "Bearer " + MainActivity.access_token)
//                .build();
//        try (Response response = client.newCall(request).execute()) {
//            result =  response.body().string();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return result;
//    }
}
