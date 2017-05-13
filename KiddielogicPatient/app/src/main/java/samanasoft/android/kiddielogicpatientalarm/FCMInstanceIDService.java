package samanasoft.android.kiddielogicpatientalarm;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import samanasoft.android.framework.webservice.WebServiceResponse;
import samanasoft.android.ottimo.common.Constant;
import samanasoft.android.ottimo.dal.BusinessLayer;

/**
 * Created by DEV_ARI on 5/8/2017.
 */
public class FCMInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "FCMInstanceIDService";

    @Override
    public void onTokenRefresh() {

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        SharedPreferences prefs = getSharedPreferences(Constant.SharedPreference.NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constant.SharedPreference.FCM_TOKEN, refreshedToken);
        editor.commit();

        sendRegistrationToServer(refreshedToken);

    }

    private void sendRegistrationToServer(String token) {
        String deviceID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        new UpdateDeviceFCMToken(getApplicationContext(), deviceID, token).execute((Void) null);
    }

    public static class UpdateDeviceFCMToken extends AsyncTask<Void, Void, WebServiceResponse> {

        private final String deviceID;
        private final String newFCMToken;
        private final Context ctx;

        UpdateDeviceFCMToken(Context mCtx, String mDeviceID, String mNewFCMToken) {
            deviceID = mDeviceID;
            newFCMToken = mNewFCMToken;
            ctx = mCtx;
        }

        @Override
        protected WebServiceResponse doInBackground(Void... params) {
            try {
                WebServiceResponse result = BusinessLayer.updateDeviceFCMToken(ctx, deviceID, newFCMToken);
                return result;
            }
            catch (Exception ex) {
                Toast.makeText(ctx, "Update Token Fail", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final WebServiceResponse result) {
        }

        @Override
        protected void onCancelled() {
        }
    }
}
