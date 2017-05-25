package com.nuevoshorizontes.nhstream;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.nuevoshorizontes.nhstream.Requests.DownloadAPK;

import java.util.Timer;
import java.util.TimerTask;

public class UpdateActivity extends Activity {


    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        context = getBaseContext();
        //verificarVersion();
    }

    public void verificarVersion() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {

                try {

                    String[] urlToDownload = new String[3];

                    urlToDownload[0] = Constants.server + "/multimedia/apk/ctn.apk";
                    urlToDownload[1] = String.valueOf(Constants.version);
                    urlToDownload[2] = Constants.server + "/app/generalidades/version";
                    new DownloadAPK(context).execute(urlToDownload);

                } catch (Exception e) {
                    Log.w("ERROR::::::::", e.toString());
                }

            }
        }, 0, 43200000);
    }

}