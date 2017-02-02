package samanasoft.android.kiddielogicpatient;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import samanasoft.android.ottimo.dal.DataLayer.Patient;
import java.util.List;

import samanasoft.android.framework.Constant;
import samanasoft.android.framework.DateTime;
import samanasoft.android.ottimo.dal.BusinessLayer;

public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final android.net.NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifi.isAvailable() || mobile.isAvailable()) {
            String filterExpression = String.format("LastSyncDateTime < '%1$s 00:00:00'", DateTime.now().toString(Constant.FormatString.DATE_FORMAT_DB));
            List<Patient> lstPatient = BusinessLayer.getPatientList(context, filterExpression);
            if(lstPatient.size() > 0){
                Intent eventService = new Intent(context, SchedulerEventLoadDataService.class);
                context.startService(eventService);
            }

        }
    }
}

