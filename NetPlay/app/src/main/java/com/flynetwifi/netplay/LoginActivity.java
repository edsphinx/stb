package com.flynetwifi.netplay;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.flynetwifi.netplay.Tasks.LoginTask;

public class LoginActivity extends Activity {

    SharedPreferences settings;

    private LoginTask mAuthTask = null;

    private TextInputEditText mUsuarioView;
    private TextInputEditText mClaveView;

    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);
        mContext = this;
        mUsuarioView = (TextInputEditText) findViewById(R.id.usuario);


        mClaveView = (TextInputEditText) findViewById(R.id.password);


        Button mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });



        settings = getSharedPreferences("settings", 0);

        String usuario = settings.getString("username", "null");
        String clave = settings.getString("password", "null");
        //mac = settings.getString("mac", "null");


        if (usuario.contentEquals("null")) {
            usuario = "";
        }
        mUsuarioView.setText(usuario);


        if (clave.contentEquals("null")) {
            clave = "";
        }
        mClaveView.setText(clave);
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        mUsuarioView.setError(null);
        mClaveView.setError(null);

        String usuario = mUsuarioView.getText().toString();
        String clave = mClaveView.getText().toString();



        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(clave)) {
            mClaveView.setError("Clave invalida");
            focusView = mClaveView;
            cancel = true;
        }

        if (TextUtils.isEmpty(usuario)) {
            mUsuarioView.setError("Usuario Requerido");
            focusView = mUsuarioView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            mAuthTask = new LoginTask(usuario, clave, mContext,
                    mUsuarioView, mClaveView, settings);
            mAuthTask.execute((Void) null);
            mAuthTask = null;
        }
    }
}
