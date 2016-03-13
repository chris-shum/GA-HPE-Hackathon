package com.example.android.quicktap;

import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hod.api.hodclient.HODApps;
import hod.api.hodclient.HODClient;
import hod.api.hodclient.IHODClientCallback;
import hod.response.parser.HODErrorCode;
import hod.response.parser.HODErrorObject;
import hod.response.parser.HODResponseParser;
import hod.response.parser.OCRDocumentResponse;

/**
 * Copyright 2016 Boloutare Doubeni
 */
public class BeerBottleTextAnalyzer implements IHODClientCallback {

  private static final String HOD_APP = HODApps.OCR_DOCUMENT;

  private Map<String, Object> mParams = new HashMap<>();
  private HODClient mClient = new HODClient("5bc8beaa-2ff9-4b76-8c62-d5698a2f46bc", this);
  private HODResponseParser mParser = new HODResponseParser();

  private TextResponseListener mListener;

  public interface TextResponseListener {
    void getTheText(String repsonse);
  }
  
  public BeerBottleTextAnalyzer(String file, TextResponseListener listener) {
    mListener = listener;
    mParams.put("file", file);
    mParams.put("mode", "document_photo");
  }

  public void run() {
    mClient.PostRequest(mParams, HOD_APP, HODClient.REQ_MODE.ASYNC);
  }

  @Override
  public void requestCompletedWithContent(String response) {
    OCRDocumentResponse documentResponse = mParser.ParseOCRDocumentResponse(response);
    if (documentResponse != null) {
      StringBuilder text = new StringBuilder();
      for (OCRDocumentResponse.TextBlock block :  documentResponse.text_block) {
        text.append(block.text);
        text.append('\n');
      }
      mListener.getTheText(text.toString());
    } else {
      List<HODErrorObject> errors = mParser.GetLastError();
      for (HODErrorObject err :  errors) {
        switch (err.error) {
          case HODErrorCode.QUEUED:
          case HODErrorCode.IN_PROGRESS:
            /* TODO: sleep  and wait */
            mClient.GetJobStatus(err.jobID);
            return;
          default:
            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append(String.format("Error code: %d\nError Reason: %s\n", err.error, err.reason));
            if (err.detail != null) {
              errorMessage.append(String.format("Error detail: %s\n", err.detail));
            }
            Log.e("ERROR", errorMessage.toString());
        }
      }
    }

  }

  @Override
  public void requestCompletedWithJobID(String response) {
    String jobID = mParser.ParseJobID(response);
    if (jobID.length() > 0) {
      mClient.GetJobStatus(jobID);
    }
  }

  @Override
  public void onErrorOccurred(String errorMessage) {
    Log.e("ERROR", errorMessage);
  }
}
