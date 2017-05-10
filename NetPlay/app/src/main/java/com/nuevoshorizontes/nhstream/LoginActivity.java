package com.nuevoshorizontes.nhstream;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import com.nuevoshorizontes.nhstream.Tasks.LoginTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends Activity {


    private SharedPreferences settings;

    //AsyncTask for Login
    private LoginTask mAuthTask = null;

    //TextInputs for Username & Password
    private TextInputEditText mUsuarioView;
    private TextInputEditText mClaveView;

    public String usuario, clave;

    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Make activity fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);
        mContext = this;
        mUsuarioView = (TextInputEditText) findViewById(R.id.usuario);
        mClaveView = (TextInputEditText) findViewById(R.id.password);


        AppCompatButton mSignInButton = (AppCompatButton) findViewById(R.id.sign_in_button);
        //Make an attemptLogin onClickEvent of mSignInButton
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        //Get SharedPreferences
        settings = getSharedPreferences("settings", 0);

        String usuario = "";settings.getString("username", "null");
        String clave = ""; settings.getString("password", "null");
        //mac = settings.getString("mac", "null");


        /**
         * IF the "user" preferences is setted we update the Username/Password View
         */
        if (usuario.contentEquals("null")) {
            usuario = "";
        }
        mUsuarioView.setText(usuario);


        if (clave.contentEquals("null")) {
            clave = "";
        }
        mClaveView.setText(clave);

        /**
         * If the username/password input is setted we request the focus to the signing button &
         * try and attemptLogin
         */
        if (!usuario.contentEquals("") && !clave.contentEquals("")) {
            mSignInButton.requestFocus();
            mSignInButton.requestFocusFromTouch();
            attemptLogin();
        }

        /** If the user/password is empty we request to ISP the password with the mac of STB */
        if (usuario.contentEquals("") && clave.contentEquals("")) {
            getUsuarioClave();
        }

    }

    private void attemptLogin() {
        //This prevent create multiples threads
        if (mAuthTask != null) {
            return;
        }

        //Reset errors of Username/Password
        mUsuarioView.setError(null);
        mClaveView.setError(null);

        //Get the username/password
        String usuario = mUsuarioView.getText().toString();
        String clave = mClaveView.getText().toString();



        boolean cancel = false;
        View focusView = null;

        // Basic Validations
        // Username/Password can't be empty
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
            //Run Login Task
            mAuthTask = new LoginTask(mContext,
                    mUsuarioView, mClaveView, settings);
            mAuthTask.execute((Void) null);
            mAuthTask = null;
        }
    }

    /**
     * Description: Request Username/Password with Mac Address
     */
    private void getUsuarioClave() {
        //Reset Username/Password
        usuario = "";
        clave = "";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //Get the mac Address of Eth0
                String mac = getMacAddress();
                final OkHttpClient client = new OkHttpClient();

                //Request to Credentials add mac in the URL
                String url = Constants.credentials + mac;
                String result = "";

                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("Accept", "application/json; q=0.5")
                        .addHeader("Authorization", "Bearer " + Constants.bearer)
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    result = response.body().string();
                    // If the result != and empty array
                    if(result != "[]") {

                        Gson gson = new Gson();
                        Type listType = new TypeToken<HashMap<String, String>>() {
                        }.getType();
                        HashMap<String, String> posts = (HashMap<String, String>) gson.fromJson(result, listType);
                        for (HashMap.Entry<String, String> entry : posts.entrySet()) {
                            //Set Username/password
                            if (entry.getKey().contentEquals("username")) {
                                usuario = entry.getValue();
                            } else if (entry.getKey().contentEquals("password")) {
                                clave = entry.getValue();
                            }
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        try{
            thread.join();
            //Set Username/Password on the view
            mUsuarioView.setText(usuario);
            mClaveView.setText(clave);
            attemptLogin();
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    // Get the MAC address of Eth0
    public String getMacAddress() {
        try {
            return loadFileAsString("/sys/class/net/eth0/address")
                    .toUpperCase().substring(0, 17);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Description: Return a String from a FilePath
     * @param filePath
     * @return
     * @throws java.io.IOException
     */
    public static String loadFileAsString(String filePath) throws java.io.IOException {
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        reader.close();
        return fileData.toString();
    }

}
