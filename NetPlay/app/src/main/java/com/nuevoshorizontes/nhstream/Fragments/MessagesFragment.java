package com.nuevoshorizontes.nhstream.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.app.VerticalGridFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.FocusHighlight;
import android.support.v17.leanback.widget.VerticalGridPresenter;

import com.nuevoshorizontes.nhstream.Cards.MessagesCard;
import com.nuevoshorizontes.nhstream.Constants;
import com.nuevoshorizontes.nhstream.Presenters.MessagesPresenter;
import com.nuevoshorizontes.nhstream.R;
import com.nuevoshorizontes.nhstream.Utils.DownloadData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nuevoshorizontes.nhstream.MainActivity.access_token;

public class MessagesFragment extends VerticalGridFragment
{

    private static final int COLUMNS = 1;
    private static final int ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_LARGE;
    public static final String TAG = "MensajesFragment";

    private Map<String, MessagesCard> data = new HashMap<>();
    private ArrayObjectAdapter mAdapter;

    public void onCreate(Bundle savedInstaceState) {
        super.onCreate(savedInstaceState);
        setTitle(getString(R.string.title_activity_messages));
        setupRowAdapter();
    }

    private void setupRowAdapter() {
        VerticalGridPresenter presenter = new VerticalGridPresenter(ZOOM_FACTOR);
        presenter.setNumberOfColumns(COLUMNS);
        setGridPresenter(presenter);

        MessagesPresenter mensajesPresenter = new MessagesPresenter();
        mAdapter = new ArrayObjectAdapter(mensajesPresenter);
        setAdapter(mAdapter);

        prepareEntranceTransition();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                createRows();
                startEntranceTransition();
            }
        }, 1000);
    }

    private void createRows() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                    DownloadData downloadData = new DownloadData();
                    String response = downloadData.run(getActivity().getBaseContext(), access_token, false, Constants.server + Constants.messages);

                    Gson gson = new Gson();
                    Type mensajesCardType;
                    mensajesCardType = new TypeToken<HashMap<String, MessagesCard>>() {
                    }
                            .getType();

                    data = gson.fromJson(response, mensajesCardType);

            }
        });


        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        test();
    }

    private void test() {
        List<MessagesCard> list = new ArrayList<>();
        for (HashMap.Entry<String, MessagesCard> entry : data.entrySet()) {
            MessagesCard card =  entry.getValue();
            list.add(card);
        }
        mAdapter.addAll(0, list);
    }

}
