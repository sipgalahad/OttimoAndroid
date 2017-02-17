package samanasoft.android.kiddielogicpatient;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.CalendarContract;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;

import samanasoft.android.framework.Constant;
import samanasoft.android.framework.DateTime;
import samanasoft.android.framework.Helper;
import samanasoft.android.framework.webservice.WebServiceHelper;
import samanasoft.android.ottimo.dal.DataLayer;
import samanasoft.android.ottimo.dal.DataLayer.Patient;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import android.provider.CalendarContract.Events;


import samanasoft.android.framework.webservice.WebServiceResponse;
import samanasoft.android.ottimo.dal.BusinessLayer;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    private DownloadImageTask mAuthTask3 = null;

    // UI references.
    private AutoCompleteTextView mMedicalNoView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mMedicalNoView = (AutoCompleteTextView) findViewById(R.id.txtMedicalNo);
        mPasswordView = (EditText) findViewById(R.id.txtPassword);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.btnSignIn);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mMedicalNoView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String medicalNo = mMedicalNoView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(medicalNo)) {
            mMedicalNoView.setError(getString(R.string.error_field_required));
            focusView = mMedicalNoView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            String androidID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
            String deviceName = android.os.Build.MODEL;
            String myVersion = android.os.Build.VERSION.RELEASE; // e.g. myVersion := "1.6"
            int sdkVersion = android.os.Build.VERSION.SDK_INT; // e.g. sdkVersion := 8;
            mAuthTask = new UserLoginTask(medicalNo, password, androidID, deviceName, myVersion, sdkVersion, Constant.AppVersion);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 2;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, WebServiceResponsePatient> {

        private final String mMedicalNo;
        private final String mPassword;
        private final String mDeviceID;
        private final String mDeviceName;
        private final String mAndroidVersion;
        private final Integer mSDKVersion;
        private final String mAppVersion;

        UserLoginTask(String medicalNo, String password, String deviceID, String deviceName, String androidVersion, Integer sdkVersion, String appVersion) {
            mMedicalNo = medicalNo;
            mPassword = password;
            mDeviceID = deviceID;
            mDeviceName = deviceName;
            mAndroidVersion = androidVersion;
            mSDKVersion = sdkVersion;
            mAppVersion = appVersion;
        }

        @Override
        protected WebServiceResponsePatient doInBackground(Void... params) {
            try {
                WebServiceResponsePatient result = Login(getBaseContext(), mMedicalNo, mPassword, mDeviceID, mDeviceName, mAndroidVersion, mSDKVersion, mAppVersion);
                return result;
            } catch (Exception ex) {
                Toast.makeText(getBaseContext(), "Get Patient Failed", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final WebServiceResponsePatient result) {
            mAuthTask = null;
            if (result == null) {
                Toast.makeText(getBaseContext(), "Login Gagal. Silakan Cek Koneksi Internet Anda", Toast.LENGTH_SHORT).show();
                showProgress(false);
                mPasswordView.requestFocus();
            } else {
                Patient entity = null;
                if (result.returnObjPatient != null) {
                    @SuppressWarnings("unchecked")
                    List<Patient> lstPatient = (List<Patient>) result.returnObjPatient;
                    for (Patient entity1 : lstPatient) {
                        Log.d("testtest", entity1.PreferredName + "; " + entity1.MedicalNo);
                        entity = entity1;
                    }
                }

                if (entity != null) {
                    Patient tempPatient = BusinessLayer.getPatient(getBaseContext(), entity.MRN);
                    if (tempPatient == null) {
                        entity.LastSyncDateTime = result.timestamp;
                        entity.LastSyncAppointmentDateTime = result.timestamp;
                        BusinessLayer.insertPatient(getBaseContext(), entity);
                        List<DataLayer.Appointment> lstOldAppointment = BusinessLayer.getAppointmentList(getBaseContext(), String.format("MRN = '%1$s'", entity.MRN));
                        for (DataLayer.Appointment entity2 : lstOldAppointment) {
                            BusinessLayer.deleteAppointment(getBaseContext(), entity2.AppointmentID);
                        }

                        @SuppressWarnings("unchecked")
                        List<DataLayer.Appointment> lstAppointment = (List<DataLayer.Appointment>) result.returnObjAppointment;
                        for (DataLayer.Appointment entity2 : lstAppointment) {
                            BusinessLayer.insertAppointment(getBaseContext(), entity2);
                            Helper.insertAppointmentToEventCalender(getBaseContext(), entity2);

                        }
                        Log.d("img", result.returnObjImg);
                        if(!result.returnObjImg.equals("")) {
                            ContextWrapper cw = new ContextWrapper(getApplicationContext());
                            File directory = cw.getDir("Kiddielogic", Context.MODE_PRIVATE);
                            File mypath = new File(directory, entity.MedicalNo + ".jpg");

                            FileOutputStream fos = null;
                            try {
                                fos = new FileOutputStream(mypath);

                                byte[] decodedString = Base64.decode(result.returnObjImg, Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                decodedByte.compress(Bitmap.CompressFormat.PNG, 100, fos);
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    fos.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    //mAuthTask3 = new DownloadImageTask(entity);
                    //mAuthTask3.execute(String.format("%2$s/data/Patient/%1$s/%1$s.jpg", entity.MedicalNo, Constant.Url.APP_DATA_URL));
                    Intent i = new Intent(getBaseContext(), MainActivity.class);
                    i.putExtra("mrn", entity.MRN);
                    startActivity(i);

                } else {
                    showProgress(false);
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private Patient entityPatient;
        public DownloadImageTask(Patient patient) {
            entityPatient = patient;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Log.d("Url", urldisplay);
            return downloadBitmap(urldisplay);
        }

        private Bitmap downloadBitmap(String url) {
            HttpURLConnection urlConnection = null;
            try {
                URL uri = new URL(url);
                urlConnection = (HttpURLConnection) uri.openConnection();
                int statusCode = urlConnection.getResponseCode();
                if (statusCode != HttpURLConnection.HTTP_OK) {
                    return null;
                }

                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                }
            } catch (Exception e) {
                urlConnection.disconnect();
                Log.w("ImageDownloader", "Error downloading image from " + url);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }

        protected void onPostExecute(Bitmap result) {
            mAuthTask3 = null;
            if(result != null) {
                ContextWrapper cw = new ContextWrapper(getApplicationContext());
                // path to /data/data/yourapp/app_data/imageDir
                File directory = cw.getDir("Kiddielogic", Context.MODE_PRIVATE);
                // Create imageDir
                File mypath = new File(directory, entityPatient.MedicalNo + ".jpg");

                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(mypath);
                    // Use the compress method on the BitMap object to write image to the OutputStream
                    result.compress(Bitmap.CompressFormat.PNG, 100, fos);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Log.d("Result Foto", directory.getAbsolutePath());
            }
            else
                Log.d("Result Foto", "Null");
            Intent i = new Intent(getBaseContext(), MainActivity.class);
            i.putExtra("mrn", entityPatient.MRN);
            startActivity(i);
        }

        @Override
        protected void onCancelled() {
            mAuthTask3 = null;
            showProgress(false);
        }
    }
    public WebServiceResponsePatient Login(Context context, String medicalNo, String password, String deviceID, String deviceName, String androidVersion, Integer sdkVersion, String appVersion){
        WebServiceResponsePatient result = new WebServiceResponsePatient();
        try {
            JSONObject response = WebServiceHelper.Login(context, medicalNo, password, deviceID, deviceName, androidVersion, sdkVersion, appVersion);

            JSONArray returnObjAppointment = WebServiceHelper.getCustomReturnObject(response, "ReturnObjAppointment");
            JSONArray returnObjPatient = WebServiceHelper.getCustomReturnObject(response, "ReturnObjPatient");
            String img = response.optString("ReturnObjImage");
            DateTime timestamp = WebServiceHelper.getTimestamp(response);

            List<DataLayer.Patient> lst = new ArrayList<Patient>();
            for (int i = 0; i < returnObjPatient.length();++i){
                JSONObject row = (JSONObject) returnObjPatient.get(i);
                lst.add((DataLayer.Patient)WebServiceHelper.JSONObjectToObject(row, new Patient()));
            }
            List<DataLayer.Appointment> lst2 = new ArrayList<DataLayer.Appointment>();
            for (int i = 0; i < returnObjAppointment.length();++i){
                JSONObject row = (JSONObject) returnObjAppointment.get(i);
                lst2.add((DataLayer.Appointment)WebServiceHelper.JSONObjectToObject(row, new DataLayer.Appointment()));
            }
            result.returnObjPatient = lst;
            result.returnObjAppointment = lst2;
            result.returnObjImg = img;
            result.timestamp = timestamp;
        } catch (Exception e) {
            result = null;
            e.printStackTrace();
        }
        return result;
    }
    public class WebServiceResponsePatient {
        public DateTime timestamp;
        public List<?> returnObjPatient;
        public List<?> returnObjAppointment;
        public String returnObjImg;
    }
}

