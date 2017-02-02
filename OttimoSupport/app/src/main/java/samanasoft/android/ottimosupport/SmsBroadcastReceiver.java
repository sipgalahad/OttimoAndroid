package samanasoft.android.ottimosupport;

/**
 * Created by Ari on 8/12/2015.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import samanasoft.android.framework.DateTime;
import samanasoft.android.ottimo.dal.BusinessLayer;
import samanasoft.android.ottimo.dal.DataLayer.Appointment;

public class SmsBroadcastReceiver extends BroadcastReceiver {

    public static final String SMS_BUNDLE = "pdus";

    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();
        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            String smsMessageStr = "";
            for (int i = 0; i < sms.length; ++i) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                String smsBody = smsMessage.getMessageBody().toString();
                String address = smsMessage.getOriginatingAddress();

                smsMessageStr += "SMS From: " + address + "\n";
                smsMessageStr += smsBody + "\n";

                Log.d("test", address);
                Log.d("test", smsBody);

                ThreadSendSMS thread = new ThreadSendSMS(context, address, smsBody);
                thread.start();
            }
            Toast.makeText(context, smsMessageStr, Toast.LENGTH_SHORT).show();
        }
    }

    private class ThreadSendSMS extends Thread{
        private Context context;
        private String mobilePhoneNo;
        private String replyText;
        ThreadSendSMS(Context context, String mobilePhoneNo, String replyText){
            this.context = context;
            this.mobilePhoneNo = mobilePhoneNo;
            this.replyText = replyText;
        }

        @Override
        public void run() {
            try {
                BusinessLayer.postAppointmentAnswer(context, mobilePhoneNo, replyText);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
