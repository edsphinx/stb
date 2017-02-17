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

package com.flynetwifi.netplay.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.support.v4.app.ActivityOptionsCompat;

import com.flynetwifi.netplay.Constants;
import com.flynetwifi.netplay.MainActivity;
import com.flynetwifi.netplay.MusicActivity;
import com.flynetwifi.netplay.R;

import java.io.IOException;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MusicPlaylistWizardFragment extends GuidedStepFragment {

    private static final int ACTION_ID_BUY_HD = 1;
    private static final int ACTION_ID_BUY_SD = ACTION_ID_BUY_HD + 1;
    private GuidedAction playlistName;

    @Override
    public int onProvideTheme() {
        return R.style.Theme_Wizard;
    }

    @NonNull
    @Override
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
        GuidanceStylist.Guidance guidance = new GuidanceStylist.Guidance("Nombre de Playlist",
                "",
                "", null);
        return guidance;
    }

    @Override
    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
        playlistName = new GuidedAction.Builder(getActivity())
                .id(0)
                .title("")
                .editable(true)
                .description("Nombre Playlist")
                .icon(getResources().getDrawable(R.drawable.ic_music_video))
                .build();

        actions.add(playlistName);
        GuidedAction action = new GuidedAction.Builder(getActivity())
                .id(1)
                .title("Guardar")
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
                            .add("nombre", playlistName.getTitle().toString());
                    RequestBody formBody = formBuilder.build();

                    Request request = new Request.Builder()
                            .url(Constants.server + "/stb/musica/videos/playlist/guardar/"
                                    + MainActivity.user_profile)
                            .addHeader("Accept", "application/json; q=0.5")
                            .addHeader("Authorization", "Bearer " + MainActivity.access_token)
                            .post(formBody)
                            .build();
                    try (
                            Response response = client.newCall(request).execute()
                    ) {
                    } catch (IOException e) {
                        e.printStackTrace();
                    }



                }
            });
            thread.start();

            try {
                thread.join();

                Intent intent = null;
                intent = new Intent(getActivity().getBaseContext(),
                        MusicActivity.class);
                intent.putExtra("user_profile", MainActivity.user_profile );
                intent.putExtra("user_type", MainActivity.user_type );
                intent.putExtra("access_token", MainActivity.access_token );
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity())
                        .toBundle();
                startActivity(intent, bundle);
                getActivity().finish();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
