package com.nuevoshorizontes.nhstream;

import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;
import com.nuevoshorizontes.nhstream.Utils.OnErrorListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;

//import com.nuevoshorizontes.nhstream.Utils.NHShutdownThread;

/**
 * Created by fonseca on 4/4/17.
 */

public class NetplayAplication extends Application {

    public static final String TAG = "NHSTREAMAplication";
    protected String userAgent;

    private final float CHECK_MEMORY_FREQ_SECONDS = 3.0f;
    private final float LOW_MEMORY_THRESHOLD_PERCENT = 5.0f; // Available %
    private Handler memoryHandler_;

    private static List<IMemoryInfo> memInfoList = new ArrayList<IMemoryInfo>();

    private static OnErrorListener errorListener;

    public NetplayAplication() {
        errorListener = new OnErrorListener() {
            @Override
            public void onError(String msg) {

            }

            @Override
            public void onError(Exception exc) {

            }

            @Override
            public void onNotRoot() {

            }
        };
    }



    public static abstract interface IMemoryInfo {
        public void goodTimeToReleaseMemory();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        userAgent = Util.getUserAgent(this, "ExoPlayerDemo");
        clearData();
        memoryHandler_ = new Handler();
        checkAppMemory();
        Fresco.initialize(this);
    }

    public DataSource.Factory buildDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultDataSourceFactory(this, bandwidthMeter,
                buildHttpDataSourceFactory(bandwidthMeter));
    }

    private void clearData() {
        try {
            Process proc = Runtime.getRuntime()
                    .exec(new String[]{"su", "-c", "clear com.nuevoshorizontes.nhstream"});
            proc.waitFor();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //    public static void shutdown() {
    //        new NHShutdownThread(errorListener).start();
    //    }

    public HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultHttpDataSourceFactory(userAgent, bandwidthMeter);
    }

    public boolean useExtensionRenderers() {
        return BuildConfig.FLAVOR.equals("withExtensions");
    }


    public void checkAppMemory(){
        // Get app memory info
        long available = Runtime.getRuntime().maxMemory();
        long used = Runtime.getRuntime().totalMemory();

        // Check for & and handle low memory state
        float percentAvailable = 100f * (1f - ((float) used / available ));
        if( percentAvailable <= LOW_MEMORY_THRESHOLD_PERCENT )
            handleLowMemory();

        // Repeat after a delay
        memoryHandler_.postDelayed( new Runnable(){ public void run() {
            checkAppMemory();
        }}, (int)(CHECK_MEMORY_FREQ_SECONDS * 1000) );
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.w(TAG, "onLowMemory() -> handleLowMemory!!!");
        handleLowMemory();

    }

    public void handleLowMemory(){

        // Free Memory Here
        trimCache(this);
        onTrimMemory(ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
//don't compare with == as intermediate stages also can be reported, always better to check >= or <=
        if (level >= ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW) {
            try {
                // Activity at the front will get earliest than activity at the
                // back
                for (int i = memInfoList.size() - 1; i >= 0; i--) {
                    try {
                        memInfoList.get(i).goodTimeToReleaseMemory();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param implementor
     *            Durante los eventos de memoria estan escuchando a todos los interesados(supuestamente)
     */
    public static void registerMemoryListener(IMemoryInfo implementor) {
        memInfoList.add(implementor);
    }

    public static void unregisterMemoryListener(IMemoryInfo implementor) {
        memInfoList.remove(implementor);
    }

    public static void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        }
        else {
            return false;
        }
    }
}
