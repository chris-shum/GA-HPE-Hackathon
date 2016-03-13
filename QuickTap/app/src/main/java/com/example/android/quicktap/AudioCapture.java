package com.example.android.quicktap;

import android.app.Activity;
import android.widget.LinearLayout;
import android.os.Bundle;
import android.os.Environment;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.View;
import android.content.Context;
import android.util.Log;
import android.media.MediaRecorder;
import android.media.MediaPlayer;

import com.example.android.quicktap.BreweryDbApi.Beer;
import com.example.android.quicktap.BreweryDbApi.BreweryDbService;
import com.example.android.quicktap.BreweryDbApi.Response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
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
 * Created by charlie on 3/12/16.
 */
public class AudioCapture extends Activity implements IHODClientCallback
{
    HODClient hodClient = new HODClient("5bc8beaa-2ff9-4b76-8c62-d5698a2f46bc", this);

    public static final String BEER_BASE_URL = "http://api.brewerydb.com/v2/";
    public static final String API_KEY = "2b59764ae60e7c21e9ee2f83b428d43c";

    private static final String TAG = AudioCapture.class.getCanonicalName();

    private static final String LOG_TAG = "AudioRecordTest";
    private static String mFileName = null;

    private RecordButton mRecordButton = null;
    private MediaRecorder mRecorder = null;

    private PlayButton   mPlayButton = null;
    private MediaPlayer   mPlayer = null;

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    class RecordButton extends Button {
        boolean mStartRecording = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    setText("Stop recording");
                } else {
                    setText("Start recording");
                }
                mStartRecording = !mStartRecording;
            }
        };

        public RecordButton(Context ctx) {
            super(ctx);
            setText("Start recording");
            setOnClickListener(clicker);
        }
    }

    class PlayButton extends Button {
        boolean mStartPlaying = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    setText("Stop playing");
                } else {
                    setText("Start playing");
                }
                mStartPlaying = !mStartPlaying;
            }
        };

        public PlayButton(Context ctx) {
            super(ctx);
            setText("Start playing");
            setOnClickListener(clicker);
        }
    }

    class SpeechRecognitionButton extends Button {
        OnClickListener clicker = new OnClickListener() {
            @Override
            public void onClick(View v) {
                callSpeechRecognitionApi();
            }
        };

        public SpeechRecognitionButton(Context context) {
            super(context);
            setText("Transcribe");
            setOnClickListener(clicker);
        }
    }

    public AudioCapture() {
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audiocapture.3gp";
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);

        mRecordButton = new RecordButton(this);
        ll.addView(mRecordButton,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        0));
        mPlayButton = new PlayButton(this);
        ll.addView(mPlayButton,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        0));

        SpeechRecognitionButton button = new SpeechRecognitionButton(this);
        ll.addView(button,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        0));

        setContentView(ll);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
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
            String transcription = stringBuilder.toString();
            Log.d(TAG, "speech transcription: " + transcription);

            if (transcription.substring(0, 4).equals("the ")) { // seems like "the" gets weirdly added a lot
                transcription = transcription.substring(4);
            }
            searchBeers(transcription);
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

    public void searchBeers(String query) {
        Log.d(TAG, "searchBeers: starting api call");
        
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

        Call<Response> call = service.getSearchBeersCall(API_KEY, query);

        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                Log.d(TAG, "onResponse: beer api response received");
                
                if (response.body().getBeers().size() > 0) {
                    Beer beer = response.body().getBeers().get(0);

                    Log.d(TAG, "beer: " + beer.getDisplayName());
                    Log.d(TAG, "style: " + beer.getStyle().getShortName());
                    Log.d(TAG, "brewery: " + beer.getBrewery().getShortName());
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
