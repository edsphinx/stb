package com.nuevoshorizontes.nhstream.Requests;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import com.nuevoshorizontes.nhstream.Utils.DownloadData;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloadAPK extends AsyncTask<String[], String[], String[]> {

    private Context context;
    private android.support.v7.app.AlertDialog alertDialog;

    public DownloadAPK(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected String[] doInBackground(String[]... strings) {


        String result = "";
        String[] resultado = new String[3];
        String versionServer = strings[0][2];
        int download = 0;

        DownloadData connection = new DownloadData();

        //result = connection.run();

        //       new HttpConnection(versionServer);
        //result = connection.getUrl();

        result = "2";
        try {
            int versionServidor = Integer.parseInt(result);
            if (Integer.parseInt(strings[0][1]) != versionServidor) {
                download = 1;
            }
        } catch (Exception e) {

        }


        if (download == 1) {

            int count;

            try {
                strings[0][0] = "http://10.115.0.3:1935/apps/app.apk";
                URL url = new URL(strings[0][0]);
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

                resultado[0] = "1";
                resultado[1] = strings[0][0];
                return resultado;

            } catch (Exception e) {
                resultado[0] = "0";
                resultado[1] = strings[0][0];
                return resultado;
            }
        }
        resultado[0] = "0";
        resultado[1] = strings[0][0];
        return resultado;
    }

    @Override
    protected void onPostExecute(String[] resultado) {
        if (resultado[0] == "1") {

            /*android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context, R.style.full_screen_dialog);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View convertView = (View) inflater.inflate(R.layout.message, null);
            TextView titulo = (TextView) convertView.findViewById(R.id.message_titulo);
            titulo.setText("NOTICE!!!");
            TextView message = (TextView) convertView.findViewById(R.id.message_text);
            message.setText("Updating...");
            Button okButton = (Button) convertView.findViewById(R.id.ok_button);
            okButton.setText("Install New App!!!");
            okButton.requestFocus();
            okButton.requestFocusFromTouch();
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    alertDialog.dismiss();
                }
            });
            builder.setView(convertView);
            alertDialog = builder.show();*/
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/" + "app.apk")), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);



        }

    }


}