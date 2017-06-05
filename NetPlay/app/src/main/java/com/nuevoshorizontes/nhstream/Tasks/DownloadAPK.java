package com.nuevoshorizontes.nhstream.Tasks;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import com.nuevoshorizontes.nhstream.Constants;
import com.nuevoshorizontes.nhstream.Utils.DownloadData;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloadAPK extends AsyncTask<Void, Void, Boolean> {

    private Context context;

    public DownloadAPK(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {

    }


    @Override
    protected Boolean doInBackground(Void...params) {

        boolean result = false;
        boolean download = false;

        String currentVersion = Constants.version;
        String urlApk = Constants.apk;
        String urlVersion = Constants.version_url;
        String serverVersion  = currentVersion;

        try {
            DownloadData downloadData = new DownloadData();

            String response = downloadData.run(this.context,urlVersion);
            if(!currentVersion.equalsIgnoreCase(response)){
                download = true;
            }

        }
        catch(Exception e){
            e.printStackTrace();
            result = false;
        }


        if (download) {

            int count;

            try {

                //Download APK
                URL url = new URL(Constants.apk);
                URLConnection conection = url.openConnection();
                conection.connect();


                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream to write file
                OutputStream output = new FileOutputStream("/sdcard/app.apk");

                byte data[] = new byte[1024];

                while ((count = input.read(data)) != -1) {
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

                result = true;

            } catch (Exception e) {
                result = false;
            }
        }

        return result;
    }


    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/" + "app.apk")), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

        }

    }


}