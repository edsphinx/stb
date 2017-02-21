package com.flynetwifi.netplay;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.app.ActivityOptionsCompat;

import com.flynetwifi.netplay.Fragments.MenuFragment;
import com.flynetwifi.netplay.Requests.LoginRequest;
import com.flynetwifi.netplay.Utils.DownloadData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import okhttp3.Response;

public class MainActivity extends Activity{

    //Token's Variable
    public static String access_token = "";
    public static String refresh_token = "";
    public static String user_profile = "";
    public static String user_type = "";
    public static String mac = "";
    private int code = 401;

    private boolean mReboot = false;

    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mHandler.removeCallbacks(this);
            mHandler.postDelayed(this, 10000);


            //Update MAC Active
            mac = getMacAddress();
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    DownloadData downloadData = new DownloadData();
                    String response = downloadData.run(Constants.server + "/stb/perfiles/stb/" + mac);

                }
            });
            thread.start();

            // /multimedia/stb


            if(mReboot == true){
                reboot();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if(savedInstanceState == null){
            SharedPreferences loginSettings = getSharedPreferences("loginSettings", 0);
            SharedPreferences.Editor editor = loginSettings.edit();
            editor.putString("access_token", "");
            editor.putString("refresh_token", "");
            editor.putString("user_profile", "");
            editor.putString("user_type", "");
            editor.commit();

            Fragment fragment = new MenuFragment();
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment)
                    .commit();
        }

        mHandler.postDelayed(mRunnable, 10000);


    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    @Override
    protected void onResume(){
        super.onResume();

        SharedPreferences loginSettings = getSharedPreferences("loginSettings", 0);
        access_token = loginSettings.getString("access_token", "");
        user_profile = loginSettings.getString("user_profile", "");
        user_type = loginSettings.getString("user_type", "");
        if (access_token != "") {
            launch();
        } else if (!login()) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }
        else{
            launch();
        }
    }

    private void launch(){
        Intent intent = null;
        if(access_token == ""){
            intent = new Intent(this.getBaseContext(),
                    LoginActivity.class);

        }
        else if (user_profile == "") {
            intent = new Intent(this.getBaseContext(),
                    ProfileActivity.class);

        }

        if(intent != null) {
            Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(this)
                    .toBundle();
            startActivity(intent, bundle);
        }

    }

    private boolean login(){
        SharedPreferences settings = getSharedPreferences("settings", 0);

        String username = settings.getString("username", "null");
        String password = settings.getString("password", "null");


        if (!username.contentEquals("null") && !password.contentEquals("null")) {
            return attempLogin(username, password);
        }
        return false;
    }

    private boolean attempLogin(final String username, final String password){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                LoginRequest request = new LoginRequest();
                try {
                    Response session = request.run(Constants.server + "/oauth/token/", username,
                            password);
                    code = session.code();
                    if (session.isSuccessful() && session.code() == 200) {
                        JSONObject object = new JSONObject(session.body().string());
                        access_token = object.getString("access_token");
                        refresh_token = object.getString("refresh_token");

                        SharedPreferences loginSettings = getSharedPreferences("loginSettings", 0);
                        SharedPreferences.Editor editor = loginSettings.edit();
                        editor.putString("access_token", access_token);
                        editor.putString("refresh_token", refresh_token);
                        editor.commit();
                        ;

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
            if (code == 200) {
                return true;
            }

        } catch (InterruptedException e) {
        }
        return false;
    }

    public void reboot(){
        PowerManager pm = (PowerManager) getSystemService(this.POWER_SERVICE);
        pm.reboot(null);
    }

    public String getMacAddress(){
        try {
            return loadFileAsString("/sys/class/net/eth0/address")
                    .toUpperCase().substring(0, 17);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String loadFileAsString(String filePath) throws java.io.IOException{
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead=0;
        while((numRead=reader.read(buf)) != -1){
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        reader.close();
        return fileData.toString();
    }


}
