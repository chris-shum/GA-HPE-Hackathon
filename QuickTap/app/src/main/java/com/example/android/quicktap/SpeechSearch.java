package com.example.android.quicktap;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import com.example.android.quicktap.BreweryDbApi.Beer;
import com.example.android.quicktap.BreweryDbApi.BreweryDbService;
import com.example.android.quicktap.BreweryDbApi.Response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hod.api.hodclient.HODApps;
import hod.api.hodclient.HODClient;
import hod.api.hodclient.IHODClientCallback;
import hod.response.parser.HODResponseParser;
import hod.response.parser.SpeechRecognitionResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by charlie on 3/13/16.
 */
public class SpeechSearch implements IHODClientCallback {
    public static final String BEER_BASE_URL = "http://api.brewerydb.com/v2/";
    public static final String BEER_API_KEY = "2b59764ae60e7c21e9ee2f83b428d43c";
    private static final String TAG = SpeechSearch.class.getCanonicalName();

    private Context mContext;
    private MediaRecorder mRecorder;
    private static String mFileName = null;
    private String mQueryText;
    private HODClient hodClient = new HODClient("5bc8beaa-2ff9-4b76-8c62-d5698a2f46bc", this);

    public SpeechSearch(Context context) {
        mContext = context;
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audiocapture.3gp";
    }

    public interface OnSpeechSearchResultListener {
        void onSpeechSearchResult(String queryText, List<Beer> beers);
    }

    public void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "startRecording: prepare() failed", e);
        }

        mRecorder.start();
    }

    public void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;

        callSpeechRecognitionApi();
    }

    public void release() {
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
    }

    public void callSpeechRecognitionApi() {
        Log.d(TAG, "callSpeechRecognitionApi: starting call");

        String hodApp = HODApps.RECOGNIZE_SPEECH;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("file", mFileName);
        hodClient.PostRequest(params, hodApp, HODClient.REQ_MODE.ASYNC);
    }

    @Override
    public void requestCompletedWithContent(String response) {
        Log.d(TAG, "requestCompletedWithContent: response received");

        HODResponseParser parser = new HODResponseParser();
        SpeechRecognitionResponse speechResponse = parser.ParseSpeechRecognitionResponse(response);
        if (speechResponse != null) {
            StringBuilder stringBuilder = new StringBuilder();
            for (SpeechRecognitionResponse.Document doc : speechResponse.document) {
                stringBuilder.append(doc.content);
            }
            mQueryText = stringBuilder.toString();
            Log.d(TAG, "speech transcription: " + mQueryText);

            if (mQueryText.length() > 3 && mQueryText.substring(0, 4).equals("the ")) {
                // seems like "the" shows up in the start of the transcription a lot
                mQueryText = mQueryText.substring(4);
            }
            searchBeers();
        }
    }

    @Override
    public void requestCompletedWithJobID(String response) {
        Log.d(TAG, "requestCompletedWithJobID: job id received");

        HODResponseParser parser = new HODResponseParser();
        String jobId = parser.ParseJobID(response);
        if (!jobId.isEmpty()) {
            hodClient.GetJobResult(jobId);
        }
    }

    @Override
    public void onErrorOccurred(String errorMessage) {
        Log.d(TAG, "onErrorOccurred: " + errorMessage);
    }

    public void searchBeers() {
        Log.d(TAG, "searchBeers: starting api call");

        String query = mQueryText;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BEER_BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        BreweryDbService service = retrofit.create(BreweryDbService.class);

        try {
            query = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            query = query.replace(' ', '+'); // backup if url encoding fails for some reason
        }

        Call<Response> call = service.getSearchBeersCall(BEER_API_KEY, query);

        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                Log.d(TAG, "onResponse: beer api response received");

                if (response.body() != null && response.body().getBeers() != null
                        && response.body().getBeers().size() > 0) {
                    Beer beer = response.body().getBeers().get(0);

                    Log.d(TAG, "beer: " + beer.getDisplayName());
                    Log.d(TAG, "style: " + beer.getStyle().getShortName());
                    Log.d(TAG, "brewery: " + beer.getBrewery().getShortName());

                    ((MainActivity) mContext).onSpeechSearchResult(mQueryText, response.body().getBeers());
                } else {
                    ((MainActivity) mContext).onSpeechSearchResult(mQueryText, new ArrayList<Beer>());
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
