package com.msdpe.shortifierdemo;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import com.msdpe.shortifierdemo.misc.Constants;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class UrlDetailsActivity extends Activity {
	
	private EditText mTxtUrlSlug;
	private EditText mTxtFullUrl;
	private EditText mTxtShortyUrl;
	private Button mBtnGoToUrl;
	private Button mBtnSaveUrl;
	private boolean mIsAddingNewUrl;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_url_details);
		
		// Get controls we'll need regardless of whether we're adding or viewing
		mTxtUrlSlug = (EditText) findViewById(R.id.txtUrlSlug);
		mTxtFullUrl = (EditText) findViewById(R.id.txtFullUrl);
		mTxtShortyUrl = (EditText) findViewById(R.id.txtShortyUrl);
		mBtnGoToUrl = (Button) findViewById(R.id.btnGoToUrl);
		mBtnSaveUrl = (Button) findViewById(R.id.btnSaveUrl);
		
		//Get extra data from intent
		Intent intent = getIntent();		
		mIsAddingNewUrl = intent.getBooleanExtra("AddingNewUrl", false);
		
		if (mIsAddingNewUrl) {
			TextView lblShortyUrl = (TextView) findViewById(R.id.lblShortyUrl);
			TextView lblGoToUrl = (TextView) findViewById(R.id.lblGoToUrl);
			lblShortyUrl.setVisibility(View.GONE);
			mTxtShortyUrl.setVisibility(View.GONE);
			lblGoToUrl.setVisibility(View.GONE);
			mBtnGoToUrl.setVisibility(View.GONE);	
			mBtnSaveUrl.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					SaveUrl(mTxtUrlSlug.getText().toString(), mTxtFullUrl
							.getText().toString());
				}
			});
		} else {
			final String urlSlug = intent.getStringExtra("UrlSlug");
			final String fullUrl = intent.getStringExtra("FullUrl");
			//Set our text fields and disable them
			mTxtUrlSlug.setText(urlSlug);
			mTxtUrlSlug.setFocusable(false);
			mTxtFullUrl.setText(fullUrl);
			mTxtFullUrl.setFocusable(false);
			mTxtShortyUrl.setText(Constants.kShortifierRootUrl + urlSlug);
			mTxtShortyUrl.setFocusable(false);
			mBtnSaveUrl.setVisibility(View.GONE);
			mBtnGoToUrl.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//Fire intent to view URL in web browser
					Intent webIntent = new Intent(Intent.ACTION_VIEW);
					webIntent.setData(Uri.parse
							(Constants.kShortifierRootUrl + urlSlug));
					startActivity(webIntent);
				}
			});
		}
	}
	
	protected void SaveUrl(String urlSlug, String fullUrl) {		
		new AddUrlTask(this).execute(urlSlug, fullUrl);
	}
	
	private class AddUrlTask extends AsyncTask<String, Void, String> {
		
		private Activity mContext;
		
		public AddUrlTask(Activity activity) {
			mContext = activity;
		}
		
	    @Override
	    protected String doInBackground(String... params) {	     
	        JSONObject jsonUrl = new JSONObject();
			try {
				jsonUrl.put("key", "my_key");
				jsonUrl.put("url_slug", params[0]);
				jsonUrl.put("url", params[1]);
			} catch (JSONException e) {
				Log.e("UrlDetailsActivity", "Error creating JSON object: " 
						+ e.getMessage());
			}
			Log.i("UrlDetailsActivity", "JSON: " + jsonUrl.toString());
	        
	        HttpURLConnection urlConnection = null;
			try {
				URL url = new URL(Constants.kAddUrl);
				 urlConnection= (HttpURLConnection) url//
						.openConnection();
				urlConnection.setDoOutput(true);
				urlConnection.setDoInput(true);
				urlConnection.setRequestMethod("POST");
				urlConnection.addRequestProperty("Content-Type", "application/json");
				urlConnection.setRequestProperty("Content-Length", "" + 
			               Integer.toString(jsonUrl.toString().getBytes().length));			
				byte[] bytes = jsonUrl.toString().getBytes("UTF-8");			
				//Write JSON to Server
			      DataOutputStream wr = new DataOutputStream (
			                  urlConnection.getOutputStream ());
			      wr.writeBytes(jsonUrl.toString());
			      wr.flush ();
			      wr.close ();
				//Get response code
				int response = urlConnection.getResponseCode();
				//Read response
				InputStream inputStream = 
						new BufferedInputStream(urlConnection.getInputStream());
				BufferedReader bufferedReader = 
						new BufferedReader(new InputStreamReader(inputStream));
				StringBuilder stringBuilderResult = new StringBuilder();
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					stringBuilderResult.append(line);
				}
				JSONObject statusObject = new JSONObject(stringBuilderResult.toString());
				String status = statusObject.getString("Status");
				return status;
				
			} catch (IOException e) {			
				Log.e("UrlDetailsActivity", "IO Exeception: " + e.getMessage());
				e.printStackTrace();
				return "IOERROR";
			} catch (JSONException e) {
				Log.e("UrlDetailsActivity", "JSON Exception: " + e.getMessage());
				e.printStackTrace();
				return "JSONERROR";
			} finally {
				urlConnection.disconnect();
			}
	    }

	    @Override
	    protected void onPostExecute(String status) {
	        //Do something with result
	    	if (status.equals("SUCCESS")) {
				Toast.makeText(getApplicationContext(), 
						"URL Created Successfully", Toast.LENGTH_SHORT).show();
				mContext.finishActivity(1);
				finish();
			} else if (status.equals("Already Exists")) {
				Toast.makeText(getApplicationContext(), 
						"A URL with this SLUG already exists", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(), 
						"There was an error creating the Shorty URL(1): " 
						+ status, Toast.LENGTH_SHORT).show();
			}
	    	final int test = 5;
	    	switch (5) {
	    	case test: 
	    		break;
	    	}
	    }
	}
}
