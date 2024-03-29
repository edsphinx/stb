package com.nuevoshorizontes.nhstream.Tasks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.TextInputEditText;

import com.nuevoshorizontes.nhstream.Constants;
import com.nuevoshorizontes.nhstream.MainActivity;
import com.nuevoshorizontes.nhstream.R;
import com.nuevoshorizontes.nhstream.Requests.LoginRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Response;

public class LoginTask extends AsyncTask<Void, Void, Boolean> {

    private final String mUsername, mPassword;
    private String mToken;
    private final Context mContext;
    private TextInputEditText mUsernermaInput, mPasswordInput;
    private SharedPreferences mSettings;

    public LoginTask(Context context, TextInputEditText usernameInput,
                     TextInputEditText passwordInput, SharedPreferences settings) {
        mUsername = usernameInput.getText().toString();
        mPassword = passwordInput.getText().toString();
        mContext = context;
        mUsernermaInput = usernameInput;
        mPasswordInput = passwordInput;
        mSettings = settings;
    }

    public LoginTask(Context context, String usernameInput,
                     String passwordInput, SharedPreferences settings) {
        mUsername = usernameInput;
        mPassword = passwordInput;
        mContext = context;
        //mUsernermaInput = usernameInput;
        //mPasswordInput = passwordInput;
        mSettings = settings;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        LoginRequest request = new LoginRequest();
        Boolean result = false;
        try {
            Response session = request.run(Constants.server + Constants.authorization, mUsername, mPassword);
            if (session.isSuccessful() && session.code() == 200) {
                JSONObject object = new JSONObject(session.body().string());
                mToken = object.getString("access_token");
                MainActivity.access_token = mToken;
                MainActivity.refresh_token = object.getString("refresh_token");

                result = true;
            }

        } catch (IOException e) {

        } catch (JSONException e) {

        }


        return result;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if (success) {
            if(mSettings!=null && mContext!=null) {
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString("username", mUsernermaInput.getText().toString());
                editor.putString("password", mPasswordInput.getText().toString());
                editor.putString("token", mToken);
                editor.commit();
                Intent intent = new Intent(mContext, MainActivity.class);
                mContext.startActivity(intent);
            }
        } else {
            if(mContext != null && mPasswordInput != null) {
                mPasswordInput.setError(mContext.getString(R.string.password_error));
                mPasswordInput.requestFocus();
            }
        }
    }

}
