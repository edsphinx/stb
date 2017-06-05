package com.nuevoshorizontes.nhstream;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.nuevoshorizontes.nhstream.Tasks.DownloadAPK;

import java.util.Timer;
import java.util.TimerTask;


public class UpdateService extends Service {
    public Context context;



    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        verificarVersion();

    }

    public void verificarVersion() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {


                try {
                    new DownloadAPK(getBaseContext()).execute();

                } catch (Exception e) {
                    Log.w("ERROR::::::::", e.toString());
                }

            }
        }, 43200000, 43200000);//900000);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}