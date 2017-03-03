package com.flynetwifi.netplay;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.flynetwifi.netplay.Tasks.LoginTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends Activity {

    private SharedPreferences settings;

    private LoginTask mAuthTask = null;

    private TextInputEditText mUsuarioView;
    private TextInputEditText mClaveView;

    public String usuario, clave;

    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);
        mContext = this;
        mUsuarioView = (TextInputEditText) findViewById(R.id.usuario);
        mClaveView = (TextInputEditText) findViewById(R.id.password);


        AppCompatButton mSignInButton = (AppCompatButton) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        settings = getSharedPreferences("settings", 0);

        String usuario = settings.getString("username", "null");
        String clave = settings.getString("password", "null");
        //mac = settings.getString("mac", "null");

        if (usuario.contentEquals("null")) {
            usuario = "";
        }
        mUsuarioView.setText(usuario);


        if (clave.contentEquals("null")) {
            clave = "";
        }
        mClaveView.setText(clave);

        if (!usuario.contentEquals("") && !clave.contentEquals("")) {
            mSignInButton.requestFocus();
            mSignInButton.requestFocusFromTouch();
        }

        /** Obtener Usuario y Clave de Usuario por MAC */
        if (usuario.contentEquals("") && clave.contentEquals("")) {
            getUsuarioClave();
        }

    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        mUsuarioView.setError(null);
        mClaveView.setError(null);

        String usuario = mUsuarioView.getText().toString();
        String clave = mClaveView.getText().toString();


        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(clave)) {
            mClaveView.setError(getString(R.string.password_error));
            focusView = mClaveView;
            cancel = true;
        }

        if (TextUtils.isEmpty(usuario)) {
            mUsuarioView.setError(getString(R.string.user_required));
            focusView = mUsuarioView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            mAuthTask = new LoginTask(mContext,
                    mUsuarioView, mClaveView, settings);
            mAuthTask.execute((Void) null);
            mAuthTask = null;
        }
    }

    private void getUsuarioClave() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "http://10.115.0.3:8080/stb/cuenta/login/D0:76:58:06:EC:39";
                String bearer = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6IjRhYzdjYjc1MzU5NjAwNTk3ZTg5NzQzZjFlNzFkZjc3NmEzZjNmZTM4NGMxODllNWE5NWFlMWZiZDg5ZTY5MjYyNzRmYzAxYTU1MzEwOTVkIn0.eyJhdWQiOiI3IiwianRpIjoiNGFjN2NiNzUzNTk2MDA1OTdlODk3NDNmMWU3MWRmNzc2YTNmM2ZlMzg0YzE4OWU1YTk1YWUxZmJkODllNjkyNjI3NGZjMDFhNTUzMTA5NWQiLCJpYXQiOjE0ODg1ODA2MzQsIm5iZiI6MTQ4ODU4MDYzNCwiZXhwIjoxNTIwMTE2NjM0LCJzdWIiOiIxIiwic2NvcGVzIjpbXX0.b5JNXeJD1UgV42ygq6WyjsAwKckbpsQDq1U2o5ckeT4QguRvo0yXNJ5DGwXPUvxyhGMHrIs1zxVm2k8tvOzYKhL8l4ahHAMS_c4G1rAemPdOA-z_tlZc37LwGKRb6mJj0_XI0K-GCNWibeLmjmxKXvRIzhgOj0Im6mde4HUGTkMeGJb50IQIQlzZnRDGmOlFghQL62OqDCFyR6BxsU1E1xUyuxnh0rxK5jiOw56JACJ3ylcB0vO0Hs0g56gq0as_Ic_VetdaqWynGJMX5TZz-stHxl_r4b4McWcAg4Uj2klsjSrF86qb4GjoXRDaOmBGCpDHhArKKvu9QzNkr-s95SuG8JGwwOrFPkSuhM6JHZ6RRt-iEPqhH-OiTWTsB5Pn06ce7FURVJ_scJVgjwVGxXJ4k69qSBuLXQEj-AFHRKWyR_ept2kPiyb1lQ2jJEygn_y5n2mHeNFVMEad_V0QeZ1eqrm-Gs33iIKmgS8TlIKlPLExhs_-6IZrtH2-DWfXhpuqSXwOBwkpxAYNipJXDVwTOfNafSgqLxqNPpd5Mv6tmGuOZwzCiP1YRIOifjgnc0eW6LSHW3s312IgOyovIWiXdHix6lc_j3A3ttKiAT5KRhnWB1wWT20ldUe1QQ5RvY3w-CI-fJPZA31PO7RRCgvGedEFNqwCLcSnsV1QJVs";

                final OkHttpClient client = new OkHttpClient();

                String result = "";
                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("Accept", "application/json; q=0.5")
                        .addHeader("Authorization", "Bearer " + bearer)
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    result = response.body().string();

                    Gson gson = new Gson();
                    Type listType = new TypeToken<HashMap<String, String>>() {
                    }.getType();
                    HashMap<String, String> posts = (HashMap<String, String>) gson.fromJson(result, listType);
                    for (HashMap.Entry<String, String> entry : posts.entrySet()) {
                        Log.w("TEST", entry.getKey());

                        if (entry.getKey().contentEquals("username")) {
                            usuario = entry.getValue();
                        } else if (entry.getKey().contentEquals("password")) {
                            clave = entry.getValue();
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        usuario = "";
        clave = "";

        thread.start();

        try{
            thread.join();
            mUsuarioView.setText(usuario);
            mClaveView.setText(clave);
        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
