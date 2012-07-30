package com.msdpe.shortifierdemo.services;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.msdpe.shortifierdemo.misc.Constants;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

public class UrlFetchService extends IntentService {

	// Status Constants
	public static final int STATUS_RUNNING = 0x1;
	public static final int STATUS_FINISHED = 0x2;
	public static final int STATUS_SUCCESS = 0x3;
	public static final int STATUS_ERROR = 0x4;
	// Command Constants
	public static final int PERFORM_SERVICE_ACTIVITY = 0x5;

	public static final String COMMAND_KEY = "service_command";
	public static final String RECEIVER_KEY = "serivce_receiver";
	public static final String SERVICE_WAS_SUCCESS_KEY = "service_was_success";

	private ResultReceiver mReceiver;
	
	public UrlFetchService() {
		super("UrlFetchService");
	}

	public UrlFetchService(String name) {
		super(name);
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {		
		this.mReceiver = intent.getParcelableExtra(RECEIVER_KEY);
		int command = intent.getIntExtra(COMMAND_KEY, PERFORM_SERVICE_ACTIVITY);
		if (this.mReceiver != null)
			this.mReceiver.send(STATUS_RUNNING, Bundle.EMPTY);
		switch (command) {
			case PERFORM_SERVICE_ACTIVITY:
				fetchUrls(intent);
				break;
			default:
				if (this.mReceiver != null)
					mReceiver.send(STATUS_FINISHED, Bundle.EMPTY);
		}
		this.stopSelf();
	}

	private void fetchUrls(Intent intent) {
		boolean fetchFailed = false;
		HashMap<String, String> urlMap = new HashMap<String, String>();
		try {
			URL url = new URL(Constants.kGetAllUrl);
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			try {
				InputStream in = new BufferedInputStream(
						urlConnection.getInputStream());				
				BufferedReader bufferReader = new BufferedReader(new InputStreamReader(in));
				StringBuilder stringBuilderResponse = new StringBuilder();
				String line;
				while ((line = bufferReader.readLine()) != null) {
					stringBuilderResponse.append(line);
				}
				//Java needs brackets to surround the JSON so we're adding them manually
				JSONArray jsonArray = new JSONArray("[" + stringBuilderResponse.toString()
						+ "]");
				//Get the array of URLs
				JSONObject urls = jsonArray.getJSONObject(0).getJSONObject(
						"Urls");
				//Iterate over all of the URLs and add them to the URL hashmap
				Iterator iter = urls.keys();
				while (iter.hasNext()) {
					String key = (String) iter.next();
					String value = urls.getString(key);
					urlMap.put(key, value);
				}	
			} catch (Exception ex) {
				Log.e("UrlFetchService", "Error getting JSON from Server: " + ex.getMessage());
				fetchFailed = true;
			} finally {
				urlConnection.disconnect();
			}
		} catch (Exception ex) {
			Log.e("UrlFetchService", "Error opening HTTP Connection: " + ex.getMessage());
			fetchFailed = true;
		}
		//Provided a result receiver was sent in, send a response back
		if (mReceiver != null) {
			if (fetchFailed) { // error
				mReceiver.send(STATUS_ERROR, Bundle.EMPTY);
				this.stopSelf();
				mReceiver.send(STATUS_FINISHED, Bundle.EMPTY);
			} else {
				Bundle bundle = new Bundle();
				bundle.putBoolean(SERVICE_WAS_SUCCESS_KEY, true);
				//put the urlMap into the bundle
				bundle.putSerializable("urlMap", urlMap);
				mReceiver.send(STATUS_SUCCESS, bundle);
				this.stopSelf();
				mReceiver.send(STATUS_FINISHED, Bundle.EMPTY);
			}
		} else {
			this.stopSelf();
		}
	}
}
