package samanasoft.android.kiddielogicpatientalarm;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import samanasoft.android.framework.webservice.WebServiceHelper;


/**
 * A RequestPassword screen that offers RequestPassword via email/password.
 */
public class RequestPasswordActivity extends AppCompatActivity {

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
     * Keep track of the RequestPassword task to ensure we can cancel it if requested.
     */
    private UserRequestPasswordTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mMedicalNoView;
    private EditText mEmailAddressView;
    private View mProgressView;
    private View mRequestPasswordFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_password);

        Intent myIntent = getIntent();
        String medicalNo = myIntent.getStringExtra("medicalNo");

        // Set up the RequestPassword form.
        mMedicalNoView = (AutoCompleteTextView) findViewById(R.id.txtMedicalNo);
        mMedicalNoView.setText(medicalNo);
        mEmailAddressView = (EditText) findViewById(R.id.txtEmail);

        Button mEmailSignInButton = (Button) findViewById(R.id.btnCommit);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRequestPassword();
            }
        });

        mRequestPasswordFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the RequestPassword form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual RequestPassword attempt is made.
     */
    private void attemptRequestPassword() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mMedicalNoView.setError(null);
        mEmailAddressView.setError(null);

        // Store values at the time of the RequestPassword attempt.
        String medicalNo = mMedicalNoView.getText().toString();
        String emailAddress = mEmailAddressView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(emailAddress) && !isPasswordValid(emailAddress)) {
            mEmailAddressView.setError(getString(R.string.error_invalid_password));
            focusView = mEmailAddressView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(medicalNo)) {
            mMedicalNoView.setError(getString(R.string.error_field_required));
            focusView = mMedicalNoView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt RequestPassword and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user RequestPassword attempt.
            showProgress(true);
            mAuthTask = new UserRequestPasswordTask(medicalNo, emailAddress);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 2;
    }

    /**
     * Shows the progress UI and hides the RequestPassword form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRequestPasswordFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRequestPasswordFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRequestPasswordFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mRequestPasswordFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous RequestPassword/registration task used to authenticate
     * the user.
     */
    public class UserRequestPasswordTask extends AsyncTask<Void, Void, String> {

        private final String mMedicalNo;
        private final String mEmailAddress;

        UserRequestPasswordTask(String medicalNo, String emailAddress) {
            mMedicalNo = medicalNo;
            mEmailAddress = emailAddress;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String result = RequestPassword(getBaseContext(), mMedicalNo, mEmailAddress);
                return result;
            } catch (Exception ex) {
                Toast.makeText(getBaseContext(), "Request Password Failed", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final String result) {
            mAuthTask = null;
            if (result == null) {
                Toast.makeText(getBaseContext(), "Request Password Gagal. Silakan Cek Koneksi Internet Anda", Toast.LENGTH_SHORT).show();
                showProgress(false);
                mEmailAddressView.requestFocus();
            } else {
                if(result.equals("1")) {
                    Toast.makeText(getBaseContext(), "Permintaan Password Berhasil. Silakan Cek Email Anda Untuk Mengetahui Password Anda.", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getBaseContext(), LoginActivity.class);
                    startActivity(i);
                }
                else {
                    Toast.makeText(getBaseContext(), "No RM dan Email Tidak Cocok.", Toast.LENGTH_SHORT).show();

                    showProgress(false);
                    mEmailAddressView.setError(getString(R.string.error_incorrect_medicalno_email));
                    mEmailAddressView.requestFocus();
                }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
    public String RequestPassword(Context context, String medicalNo, String emailAddress){
        String result = "";
        try {
            JSONObject response = WebServiceHelper.RequestPassword(context, medicalNo, emailAddress);

            result = response.optString("Result");
        } catch (Exception e) {
            result = null;
            e.printStackTrace();
        }
        return result;
    }
}

