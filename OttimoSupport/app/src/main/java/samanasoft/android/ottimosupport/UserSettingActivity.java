package samanasoft.android.ottimosupport;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.Window;

import samanasoft.android.ottimo.common.AlarmHelper;
import samanasoft.android.ottimo.common.Constant;

public class UserSettingActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private EditTextPreference prefAppointmentReminderTime;
    private EditTextPreference prefAppointmentReminderMessage;
    private EditTextPreference prefWebServiceUrl;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        PreferenceManager manager = getPreferenceManager();
        manager.setSharedPreferencesName(Constant.SharedPreference.NAME);

        addPreferencesFromResource(R.layout.settings);
        prefAppointmentReminderTime = (EditTextPreference)getPreferenceScreen().findPreference(Constant.SharedPreference.APPOINTMENT_REMINDER_TIME);
        prefAppointmentReminderMessage = (EditTextPreference)getPreferenceScreen().findPreference(Constant.SharedPreference.APPOINTMENT_REMINDER_MESSAGE);
        prefWebServiceUrl = (EditTextPreference)getPreferenceScreen().findPreference(Constant.SharedPreference.WEB_SERVICE_URL);

        SharedPreferences prefs = getSharedPreferences(Constant.SharedPreference.NAME, MODE_PRIVATE);
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        prefAppointmentReminderMessage.setText(prefs.getString(Constant.SharedPreference.APPOINTMENT_REMINDER_MESSAGE, ""));
        prefAppointmentReminderTime.setText(prefs.getString(Constant.SharedPreference.APPOINTMENT_REMINDER_TIME, ""));
        prefWebServiceUrl.setText(prefs.getString(Constant.SharedPreference.WEB_SERVICE_URL, ""));
    }

    @Override
    protected void onResume() {
        super.onResume();

        initPrefValue();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    private void initPrefValue(){
        String reminderTime = prefAppointmentReminderTime.getText();
        String reminderMessage = prefAppointmentReminderMessage.getText();
        String webServiceUrl = prefWebServiceUrl.getText();
        prefAppointmentReminderTime.setSummary(reminderTime);
        prefAppointmentReminderMessage.setSummary(reminderMessage);
        prefWebServiceUrl.setSummary(webServiceUrl);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }


    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Update summary value
        if (key.equals(Constant.SharedPreference.APPOINTMENT_REMINDER_TIME)) {
            String ss = sharedPreferences.getString(Constant.SharedPreference.APPOINTMENT_REMINDER_TIME, "");
            prefAppointmentReminderTime.setSummary(ss);

            AlarmHelper.startAlarm(this);
        }
        else if (key.equals(Constant.SharedPreference.APPOINTMENT_REMINDER_MESSAGE)) {
            String ss = sharedPreferences.getString(Constant.SharedPreference.APPOINTMENT_REMINDER_MESSAGE, "");
            prefAppointmentReminderMessage.setSummary(ss);
        }
        else if (key.equals(Constant.SharedPreference.WEB_SERVICE_URL)) {
            String ss = sharedPreferences.getString(Constant.SharedPreference.WEB_SERVICE_URL, "");
            prefWebServiceUrl.setSummary(ss);
        }
    }
}
