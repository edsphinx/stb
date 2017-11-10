package com.nuevoshorizontes.nhstream.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.nuevoshorizontes.nhstream.NetplayAplication;

/**
 * Created by fonseca on 11/8/17.
 */

public class NHHdmiListener extends BroadcastReceiver implements OnErrorListener {

    private static String HDMIINTENT = "android.intent.action.HDMI_PLUGGED";

    @Override
    public void onReceive(Context ctxt, Intent receivedIt) {
        String action = receivedIt.getAction();

        if (action.equals(HDMIINTENT)) {
            boolean state = receivedIt.getBooleanExtra("state", false);

            if (state) {
                Log.d("HDMIListner", "BroadcastReceiver.onReceive() : Connected HDMI-TV");
                Log.i("HDMIListner", "BroadcastReceiver.onReceive() : Connected HDMI-TV");
                //Toast.makeText(ctxt, "HDMI >>", Toast.LENGTH_LONG).show();
            } else {
                Log.d("HDMIListner", "HDMI >>: Disconnected HDMI-TV");
                Log.i("HDMIListner", "HDMI >>: Disconnected HDMI-TV");
                //Toast.makeText(ctxt, "HDMI DisConnected>>", Toast.LENGTH_LONG).show();
                try {
                    Process proc = Runtime.getRuntime()
                            .exec(new String[]{ "su", "-c", "reboot -p" });
                    proc.waitFor();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                //new NHShutdownThread(this).start();
                //NetplayAplication.shutdown();
            }
        }
    }

    @Override
    public void onError(String msg) {

    }

    @Override
    public void onError(Exception exc) {

    }

    @Override
    public void onNotRoot() {

    }
}