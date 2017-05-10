/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.nuevoshorizontes.nhstream.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.text.InputType;

import com.nuevoshorizontes.nhstream.Constants;
import com.nuevoshorizontes.nhstream.MainActivity;
import com.nuevoshorizontes.nhstream.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



public class AccountProfileWizardFragment extends GuidedStepFragment {

    private GuidedAction passwordAction;

    @Override
    public int onProvideTheme() {
        return R.style.Theme_Wizard;
    }

    @NonNull
    @Override
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
        GuidanceStylist.Guidance guidance = new GuidanceStylist.Guidance(
                getString(R.string.account_pin_request_long),
                "",
                "", null);
        return guidance;
    }

    @Override
    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
        passwordAction = new GuidedAction.Builder(getActivity())
                .id(0)
                .title("")
                .editable(true)
                .description(getString(R.string.account_pin_request))
                .editInputType(InputType.TYPE_CLASS_NUMBER)
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .icon(getResources().getDrawable(R.drawable.ic_enhanced_encryption))
                .build();

        actions.add(passwordAction);
        GuidedAction action = new GuidedAction.Builder(getActivity())
                .id(1)
                .title(getString(R.string.enter))
                .editable(false)
                .description("")
                .build();
        actions.add(action);

    }

    @Override
    public void onGuidedActionClicked(final GuidedAction action) {

        if (action.getId() == 1) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String result = "";
                    OkHttpClient client = new OkHttpClient();
                    FormBody.Builder formBuilder = new FormBody.Builder()
                            .add("clave", passwordAction.getTitle().toString());
                    RequestBody formBody = formBuilder.build();

                    Request request = new Request.Builder()
                            .url(Constants.server + Constants.cuenta_confirmacion
                                    + MainActivity.access_token)
                            .addHeader("Accept", "application/json; q=0.5")
                            .addHeader("Authorization", "Bearer " + MainActivity.access_token)
                            .post(formBody)
                            .build();
                    try (
                            Response response = client.newCall(request).execute()
                    ) {
                        result = response.body().string();

                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            if (jsonObject.getString("estado").contentEquals("1")) {
                                SharedPreferences loginSettings = getActivity().getBaseContext()
                                        .getSharedPreferences("loginSettings", 0);
                                SharedPreferences.Editor editor = loginSettings.edit();
                                editor.putString("user_profile", String.valueOf(1));
                                editor.putString("user_type", jsonObject.getString("estado"));
                                editor.commit();

                                MainActivity.user_profile = String.valueOf(1);
                                MainActivity.user_type = String.valueOf(1);
                                getActivity().finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            });
            thread.start();

            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
