package samanasoft.android.kiddielogicparamedicalarm;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import samanasoft.android.framework.webservice.WebServiceResponse;
import samanasoft.android.ottimo.dal.BusinessLayer;
import samanasoft.android.ottimo.dal.DataLayer.ParamedicMaster;

public class ErrorFeedbackActivity extends BaseMainActivity {

    private SendMessageTask mAuthTask = null;

    // UI references.
    private EditText mMessageView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParamedicMaster entity = InitActivity(savedInstanceState, R.layout.activity_error_feedback);
        // Set up the login form.

        mMessageView = (EditText) findViewById(R.id.txtMessage);

        Button btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new OnClickListener() {
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
        mMessageView.setError(null);

        // Store values at the time of the login attempt.
        String message = mMessageView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid message
        if (TextUtils.isEmpty(message)) {
            mMessageView.setError(getString(R.string.error_field_required));
            focusView = mMessageView;
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

            String deviceID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

            mAuthTask = new SendMessageTask(deviceID, message);
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
    public class SendMessageTask extends AsyncTask<Void, Void, WebServiceResponse> {
        private final String mDeviceID;
        private final String mErrorMessage;

        SendMessageTask(String deviceID, String errorMessage) {
            mDeviceID = deviceID;
            mErrorMessage = errorMessage;
        }

        @Override
        protected WebServiceResponse doInBackground(Void... params) {
            try {
                WebServiceResponse result = BusinessLayer.insertErrorFeedback(getApplicationContext(), mDeviceID, mErrorMessage);
                return result;
            } catch (Exception ex) {
                Toast.makeText(getBaseContext(), "Kirim Error Feedback Gagal. Silakan Cek Koneksi Internet Anda", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final WebServiceResponse result) {
            mAuthTask = null;
            if (result == null) {
                Toast.makeText(getBaseContext(), "Kirim Error Feedback Gagal. Silakan Cek Koneksi Internet Anda", Toast.LENGTH_SHORT).show();
                showProgress(false);
                mMessageView.requestFocus();
            } else {
                Toast.makeText(getBaseContext(), "Data Sudah Berhasil Dikirim. Terima kasih atas partisipasi anda", Toast.LENGTH_LONG).show();
                mMessageView.setText("");
                showProgress(false);
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

