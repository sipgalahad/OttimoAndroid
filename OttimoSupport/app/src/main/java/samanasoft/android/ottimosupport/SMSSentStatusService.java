package samanasoft.android.ottimosupport;

import android.app.Activity;
import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.util.Log;

import samanasoft.android.ottimo.common.Constant;
import samanasoft.android.ottimo.dal.BusinessLayer;

/**
 * Created by DEV_ARI on 5/23/2016.
 */

public class SMSSentStatusService extends Service {
    public SMSSentStatusService() {
        super();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    BroadcastReceiver sentReceiver;
    BroadcastReceiver deliveredReceiver;
    @Override
    public void onCreate() {
        String SENT = "sent";
        String DELIVERED = "delivered";
// ---when the SMS has been sent---

        sentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int appointmentID = intent.getIntExtra("appointmentID", -1);
                String mobilePhoneNo = intent.getStringExtra("mobilePhoneNo");
                String status = "";
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        status = Constant.SMSLogStatus.SMS_SENT;
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        status = Constant.SMSLogStatus.FAILED;
                        Log.d("SMS Failed", "Generic Failuere");
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        status = Constant.SMSLogStatus.FAILED;
                        Log.d("SMS Failed", "No Service");
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        status = Constant.SMSLogStatus.FAILED;
                        Log.d("SMS Failed", "Error Null PDU");
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        status = Constant.SMSLogStatus.FAILED;
                        Log.d("SMS Failed", "Radio Off");
                        break;
                    default:
                        status = Constant.SMSLogStatus.FAILED;
                        Log.d("SMS Failed", "Default");
                        break;
                }

                Log.d("SMS Sent", status);

                ThreadUpdateAppointmentStatus thread = new ThreadUpdateAppointmentStatus(getBaseContext(), appointmentID, mobilePhoneNo, status);
                thread.start();
            }
        };
        registerReceiver(sentReceiver, new IntentFilter(SENT));

        // ---when the SMS has been delivered---
        deliveredReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent intent) {
                int appointmentID = intent.getIntExtra("appointmentID", -1);
                String mobilePhoneNo = intent.getStringExtra("mobilePhoneNo");
                String status = "";
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        status = Constant.SMSLogStatus.SMS_DELIVERED;
                        break;
                    case Activity.RESULT_CANCELED:
                        status = Constant.SMSLogStatus.FAILED;
                        break;
                }
                ThreadUpdateAppointmentStatus thread = new ThreadUpdateAppointmentStatus(getBaseContext(), appointmentID, mobilePhoneNo, status);
                thread.start();
            }
        };
        registerReceiver(deliveredReceiver, new IntentFilter(DELIVERED));

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(sentReceiver);
        unregisterReceiver(deliveredReceiver);
        super.onDestroy();
    }

    private class ThreadUpdateAppointmentStatus extends Thread{
        private Context context;
        private int appointmentID;
        private String mobilePhoneNo;
        private String GCSMSLogStatus;
        ThreadUpdateAppointmentStatus(Context context, int appointmentID, String mobilePhoneNo, String GCSMSLogStatus){
            this.context = context;
            this.appointmentID = appointmentID;
            this.mobilePhoneNo = mobilePhoneNo;
            this.GCSMSLogStatus = GCSMSLogStatus;
        }

        @Override
        public void run() {
            try {
                BusinessLayer.updateAppointmentStatusSMS(context, appointmentID, mobilePhoneNo, GCSMSLogStatus);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}