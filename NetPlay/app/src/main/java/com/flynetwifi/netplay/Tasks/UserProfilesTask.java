package com.flynetwifi.netplay.Tasks;

import android.os.AsyncTask;

import com.flynetwifi.netplay.Constants;
import com.flynetwifi.netplay.Fragments.ProfileFragment;
import com.flynetwifi.netplay.MainActivity;
import com.flynetwifi.netplay.Utils.DownloadData;

public class UserProfilesTask extends AsyncTask<String, String, String> {

    private ProfileFragment profileFragment;

    public UserProfilesTask(ProfileFragment profileFragment) {
        this.profileFragment = profileFragment;
    }

    @Override
    protected String doInBackground(String... strings) {

        DownloadData downloadData = new DownloadData();
        String response = downloadData.run(Constants.server + Constants.profiles + MainActivity.access_token);

        return response;
    }


    @Override
    protected void onPostExecute(String response) {
        //profileFragment.createRows(response);
    }
}
