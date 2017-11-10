package com.nuevoshorizontes.nhstream.Utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import android.util.Log;

public class Utils {
    public static int convertDpToPixel(Context ctx, int dp) {
        float density = ctx.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    public static String inputStreamToString(InputStream inputStream) {
        try {
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes, 0, bytes.length);
            String json = new String(bytes);
            return json;
        } catch (IOException e) {
            return null;
        }
    }

    public static Uri getResourceUri(Context context, int resID) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                context.getResources().getResourcePackageName(resID) + '/' +
                context.getResources().getResourceTypeName(resID) + '/' +
                context.getResources().getResourceEntryName(resID));
    }

    public static String readAll(InputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalArgumentException("inputStream cannot be null.");
        }
        try {
            int size = inputStream.available();
            if (size == 0) {
                return "";
            }
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream), size);
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
            return total.toString();
        } catch (IOException e) {
            Log.e("Shutdown STB", "Error reading from stream.", e);
            return "";
        }
    }

    /**
     * Dumps the process output, that is input and error stream.
     *
     * @param process
     * @return The error output for further analysis, or an empty string.
     */
    public static String dumpProcessOutput(Process process) {
        if (process == null) {
            throw new IllegalArgumentException("process cannot be null.");
        }
        String stdOut = readAll(process.getInputStream());
        String stdErr = readAll(process.getErrorStream());
        if (stdOut.length() > 0) {
            Log.i("Shutdown STB", "Process console output: \n" + stdOut);
        }
        if (stdErr.length() > 0) {
            Log.e("Shutdown STB", "Process error output: \n" + stdErr);
        }
        return stdErr;
    }

    public static void killMyProcess() {
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
    }
}
