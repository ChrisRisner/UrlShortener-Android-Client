package com.msdpe.shortifierdemo.services;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/***
 * This class is a generic Result Receiver and can be used by 
 * any class that needs to call an IntentService.
 *
 */
public class ServiceResultReceiver extends ResultReceiver {
	private Receiver mReceiver;
	 
    public ServiceResultReceiver(Handler handler) {
        super(handler);
    }
 
    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }
 
    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle resultBundle);
    }
 
    @Override
    protected void onReceiveResult(int resultCode, Bundle resultBundle) {
        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultBundle);
        }
    }
}