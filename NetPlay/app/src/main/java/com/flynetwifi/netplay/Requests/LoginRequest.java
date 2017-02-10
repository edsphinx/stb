package com.flynetwifi.netplay.Requests;


import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginRequest {

    OkHttpClient client = new OkHttpClient();


    public Response run(String url, String username, String password) throws IOException {
        RequestBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .add("grant_type", "password")
                .add("client_id", "2")
                .add("client_secret", "yWKEnM9boo4NfSv1jQPqceBVnSOgtBvLevyHZIIF")
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        Response response = client.newCall(request).execute();
        return response;
    }
}
