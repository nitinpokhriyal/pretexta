package com.collabra.pretexta.service;

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class IncomingCall extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		try {
			TelephonyManager telMgr = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			PretextaPhoneListener phoneListener = new PretextaPhoneListener(
					context);
			telMgr.listen(phoneListener, phoneListener.LISTEN_CALL_STATE);
		} catch (Exception e) {
			Log.e("error on receive", ""+ e);
		}
	}

	private class PretextaPhoneListener extends PhoneStateListener {
		Context mContext = null;

		public PretextaPhoneListener(Context context) {
			mContext = context;
		}

		public void onCallStateChanged(int state, String incomingNumber) {
			// default implementation empty
			Log.d("PretextaPhoneListener", state + "   incoming no:"
					+ incomingNumber);

			if (state == 1) {
				int duration = 500;
				String msg=getContactIdFromNumber(incomingNumber);
				Toast toast = Toast.makeText(mContext, msg, duration);
				//toast.show();
			}
		}
		
		private String getContactIdFromNumber(String contactNumber) {
			StringBuilder callLogInfo=new StringBuilder();
			Cursor cursor = mContext.getContentResolver().query(
					CallLog.Calls.CONTENT_URI,
					new String[] { CallLog.Calls.DATE, CallLog.Calls.DURATION,
	                        CallLog.Calls.NUMBER, CallLog.Calls._ID , CallLog.Calls.TYPE},
	                        CallLog.Calls.NUMBER + "=?",  new String[] { contactNumber},
	                        CallLog.Calls.DATE + " desc");
			
			int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
		    int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
		    int date = cursor.getColumnIndex(CallLog.Calls.DATE);
		    int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);     
			while (cursor.moveToNext()) {
				 String phNumber = cursor.getString(number);
			        String callType = cursor.getString(type);
			        String callDate = cursor.getString(date);
			        Date callDayTime = new Date(Long.valueOf(callDate));
			        String callDuration = cursor.getString(duration);
			        String dir = null;
			        
			        int dircode = Integer.parseInt(callType);
			        switch (dircode) {
			        case CallLog.Calls.OUTGOING_TYPE:
			            dir = "OUTGOING";
			            break;
			        case CallLog.Calls.INCOMING_TYPE:
			            dir = "INCOMING";
			            break;

			        case CallLog.Calls.MISSED_TYPE:
			            dir = "MISSED";
			            break;
			        }
			        callLogInfo.append("phone: " + phNumber + " type : " + dir + " duration :" + callDuration +"\n");
			        Log.i("dir", dir);
			    }
			   cursor.close();
		    return callLogInfo.toString();
		}
	}
}
