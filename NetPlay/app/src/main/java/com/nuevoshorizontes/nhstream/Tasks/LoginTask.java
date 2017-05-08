package com.flynetwifi.nhstream.Tasks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.TextInputEditText;

import com.flynetwifi.nhstream.Constants;
import com.flynetwifi.nhstream.MainActivity;
import com.flynetwifi.netplay.R;
import com.flynetwifi.nhstream.Requests.LoginRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Response;

public class LoginTask extends AsyncTask<Void, Void, Boolean> {

    private final String mUsername, mPassword;
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

    @Override
    protected Boolean doInBackground(Void... voids) {
        LoginRequest request = new LoginRequest();
        Boolean result = false;
        try {
            Response session = request.run(Constants.server + Constants.authorization, mUsername, mPassword);
            if (session.isSuccessful() && session.code() == 200) {
                JSONObject object = new JSONObject(session.body().string());
                MainActivity.access_token = object.getString("access_token");
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
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putString("username", mUsernermaInput.getText().toString());
            editor.putString("password", mPasswordInput.getText().toString());
            editor.commit();
            Intent intent = new Intent(mContext, MainActivity.class);
            mContext.startActivity(intent);
        } else {
            mPasswordInput.setError(mContext.getString(R.string.password_error));
            mPasswordInput.requestFocus();
        }
    }

}
