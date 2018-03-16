package samanasoft.android.kiddielogicparamedicalarm;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;

import samanasoft.android.framework.Constant;
import samanasoft.android.framework.DateTime;
import samanasoft.android.framework.Helper;
import samanasoft.android.framework.webservice.WebServiceHelper;
import samanasoft.android.ottimo.dal.DataLayer;
import samanasoft.android.ottimo.dal.DataLayer.ParamedicMaster;

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

import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import samanasoft.android.ottimo.dal.BusinessLayer;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mMedicalNoView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private TextView tvRequestPassword;
    private Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ctx = this;
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

        //boolean isInit = getIntent().getBooleanExtra("isinit", false);
        //if(isInit)
        //    Helper.CopyReadAssets(this);

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

            SharedPreferences prefs = getSharedPreferences(samanasoft.android.ottimo.common.Constant.SharedPreference.NAME, MODE_PRIVATE);
            String FCMToken = prefs.getString(samanasoft.android.ottimo.common.Constant.SharedPreference.FCM_TOKEN, "");
            String deviceID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
            String deviceName = android.os.Build.MODEL;
            String manufacturerName = android.os.Build.MANUFACTURER;
            String myVersion = android.os.Build.VERSION.RELEASE; // e.g. myVersion := "1.6"
            int sdkVersion = android.os.Build.VERSION.SDK_INT; // e.g. sdkVersion := 8;
            mAuthTask = new UserLoginTask(medicalNo, password, deviceID, deviceName, manufacturerName, myVersion, sdkVersion, Constant.AppVersion, FCMToken);
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
    public class UserLoginTask extends AsyncTask<Void, Void, WebServiceResponseParamedic> {

        private final String mUserName;
        private final String mPassword;
        private final String mDeviceID;
        private final String mDeviceName;
        private final String mManufacturerName;
        private final String mAndroidVersion;
        private final Integer mSDKVersion;
        private final String mAppVersion;
        private final String mFCMToken;

        UserLoginTask(String userName, String password, String deviceID, String deviceName, String manufacturerName, String androidVersion, Integer sdkVersion, String appVersion, String FCMToken) {
            mUserName = userName;
            mPassword = password;
            mDeviceID = deviceID;
            mDeviceName = deviceName;
            mManufacturerName = manufacturerName;
            mAndroidVersion = androidVersion;
            mSDKVersion = sdkVersion;
            mAppVersion = appVersion;
            mFCMToken = FCMToken;
        }

        @Override
        protected WebServiceResponseParamedic doInBackground(Void... params) {
            try {
                WebServiceResponseParamedic result = Login(getBaseContext(), mUserName, mPassword, mDeviceID, mDeviceName, mManufacturerName, mAndroidVersion, mSDKVersion, mAppVersion, mFCMToken);
                return result;
            } catch (Exception ex) {
                Toast.makeText(getBaseContext(), "Login Failed", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final WebServiceResponseParamedic result) {
            mAuthTask = null;
            if (result == null) {
                Toast.makeText(getBaseContext(), "Login Gagal. Silakan Cek Koneksi Internet Anda", Toast.LENGTH_SHORT).show();
                showProgress(false);
                mPasswordView.requestFocus();
            } else {
                ParamedicMaster entity = null;
                if (result.returnObjParamedic != null) {
                    @SuppressWarnings("unchecked")
                    List<ParamedicMaster> lstPatient = (List<ParamedicMaster>) result.returnObjParamedic;
                    for (ParamedicMaster entity1 : lstPatient) {
                        Log.d("testtest", entity1.PreferredName + "; " + entity1.UserName);
                        entity = entity1;
                    }
                }

                if (entity != null) {
                    ParamedicMaster tempPatient = BusinessLayer.getParamedicMaster(getBaseContext(), entity.ParamedicID);
                    if (tempPatient == null) {
                        entity.LastSyncDateTime = result.timestamp;
                        BusinessLayer.insertParamedicMaster(getBaseContext(), entity);
                        FirebaseMessaging.getInstance().subscribeToTopic(entity.UserName);

                        if(!result.returnObjImg.equals("")) {
                            ContextWrapper cw = new ContextWrapper(getApplicationContext());
                            File directory = cw.getDir("KiddielogicParamedic", Context.MODE_PRIVATE);
                            File mypath = new File(directory, entity.UserName + ".jpg");

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
                    Log.d("test", "ParamedicID = " +  entity.ParamedicID);
                    i.putExtra("paramedicid", entity.ParamedicID);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    //i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);

                    SharedPreferences sharedPreferences = getSharedPreferences(samanasoft.android.ottimo.common.Constant.SharedPreference.NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("paramedicid", entity.ParamedicID);
                    editor.commit();

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

    public WebServiceResponseParamedic Login(Context context, String medicalNo, String password, String deviceID, String deviceName, String manufacturerName, String androidVersion, Integer sdkVersion, String appVersion, String FCMToken){
        WebServiceResponseParamedic result = new WebServiceResponseParamedic();
        try {
            JSONObject response = WebServiceHelper.Login(context, medicalNo, password, deviceID, deviceName, manufacturerName, androidVersion, sdkVersion, appVersion, FCMToken);

            JSONArray returnObjParamedic = WebServiceHelper.getCustomReturnObject(response, "ReturnObjParamedic");
            String img = response.optString("ReturnObjImage");
            DateTime timestamp = WebServiceHelper.getTimestamp(response);

            List<DataLayer.ParamedicMaster> lst = new ArrayList<ParamedicMaster>();
            for (int i = 0; i < returnObjParamedic.length();++i){
                JSONObject row = (JSONObject) returnObjParamedic.get(i);
                lst.add((DataLayer.ParamedicMaster)WebServiceHelper.JSONObjectToObject(row, new ParamedicMaster()));
            }
            result.returnObjParamedic = lst;
            result.returnObjImg = img;
            result.timestamp = timestamp;
        } catch (Exception e) {
            String stackTrace = e.getStackTrace().toString();
            String message = e.getMessage();
            deviceID = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
            new Helper.InsertErrorLog(ctx, 1, deviceID, message, stackTrace).execute((Void) null);
            result = null;
            e.printStackTrace();
        }
        return result;
    }
    public class WebServiceResponseParamedic {
        public DateTime timestamp;
        public List<?> returnObjParamedic;
        public String returnObjImg;
    }
}

