package com.msdpe.shortifierdemo;

import java.util.HashMap;
import java.util.TreeSet;

import com.msdpe.shortifierdemo.services.ServiceResultReceiver;
import com.msdpe.shortifierdemo.services.UrlFetchService;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.support.v4.app.NavUtils;

public class MainActivity extends ListActivity implements ServiceResultReceiver.Receiver {

	private ServiceResultReceiver mReceiver;
	private HashMap<String, String> mUrlMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        //Create a result receiver to handle service result call backs
		mReceiver = new ServiceResultReceiver(new Handler());
		mReceiver.setReceiver(this);		
        startUrlFetchService();
        
        getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//Convert the tapped view into a TextView
		        TextView tv = (TextView) view;        
		        //Load the details intent for this specific slug
		        Intent urlDetailsIntent = new Intent(getApplicationContext(),
		            UrlDetailsActivity.class);
		        urlDetailsIntent.putExtra("UrlSlug", tv.getText().toString());
		        //We need to get the Full URL somehow and send it as an extra
		        urlDetailsIntent.putExtra("FullUrl",
		                mUrlMap.get(tv.getText().toString()));
		        urlDetailsIntent.putExtra("AddingNewUrl", false);
		        startActivity(urlDetailsIntent);
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case (R.id.menu_add_url):
			Intent urlDetailsIntent = new Intent(getApplicationContext(),
					UrlDetailsActivity.class);
			urlDetailsIntent.putExtra("AddingNewUrl", true);
			startActivityForResult(urlDetailsIntent, 1);
			return true;			
		default:
			return super.onOptionsItemSelected(item);
		}
	}
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == 1) {
    		startUrlFetchService();
    	}
    	else 
    		super.onActivityResult(requestCode, resultCode, data);
    }

	private void startUrlFetchService() {
		final Intent serviceIntent = new Intent(Intent.ACTION_SYNC, null, 
		        getApplicationContext(), UrlFetchService.class);
		// put the specifics for the submission service commands
		serviceIntent.putExtra(UrlFetchService.RECEIVER_KEY, mReceiver);
		serviceIntent.putExtra(UrlFetchService.COMMAND_KEY, UrlFetchService.PERFORM_SERVICE_ACTIVITY);
		//Start the service
		startService(serviceIntent);
	}

    public void onReceiveResult(int resultCode, Bundle resultBundle) {
		switch (resultCode) {
	    case UrlFetchService.STATUS_RUNNING:
	        // Don't do anything, the service is running
	        break;
	    case UrlFetchService.STATUS_SUCCESS:
	        boolean wasSuccess = resultBundle
	                .getBoolean(UrlFetchService.SERVICE_WAS_SUCCESS_KEY);
	        if (wasSuccess) {
	        	//Success, update the ListView
	        	mUrlMap = (HashMap<String, String>) 
	        			resultBundle.getSerializable("urlMap");
	            showUrlsInListView(mUrlMap);
	        } else {
	            // Failure, show error message
	            Toast.makeText(getApplicationContext(),
	                    "There was an error fetching the URL data.  Please try again later."
	            		, Toast.LENGTH_LONG).show();
	        }
	        break;
	    case UrlFetchService.STATUS_FINISHED:
	        break;
	    case UrlFetchService.STATUS_ERROR:
	    	//Error returned from service, show and error message
	        Toast.makeText(getApplicationContext(), "There was an error fetching the URL data."
	        		+"Please try again later.",
	                Toast.LENGTH_LONG).show();
	        break;
	    }
	}

	private void showUrlsInListView(HashMap<String, String> urlMap) {				
		TreeSet<String> treeSetKeys = new TreeSet<String>(urlMap.keySet());
		String[] keys = (String[]) treeSetKeys.toArray(new String[treeSetKeys.size()]);
		ArrayAdapter adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, keys);
		setListAdapter(adapter);
	}

    
}
