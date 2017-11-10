package com.nuevoshorizontes.nhstream.Utils;

import android.util.Log;

/**
 * Created by fonseca on 11/8/17.
 */

public class AliveCheckThread extends Thread {

    private final static String TAG = AliveCheckThread.class.getSimpleName();

    private final Process proc;
    private final NHShutdownThread shutdownThread;

    public AliveCheckThread(Process proc, NHShutdownThread shutdownThread) {
        this.proc = proc;
        this.shutdownThread = shutdownThread;
    }

    @Override
    public void run() {
        try {
            sleep(25000); // wait 15s, because Superuser also has 10s timeout
        } catch (InterruptedException e) {
            Log.i(TAG, "Interrupted.");
            return;
        }
        Log.w(TAG, "Still alive after 15 sec...");
        Utils.dumpProcessOutput(proc);
        proc.destroy();
        shutdownThread.interrupt();
        Log.w(TAG, "Interrupted and destroyed.");

        Utils.killMyProcess();
    }

}
