package samanasoft.android.kiddielogicpatientalarm;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import samanasoft.android.framework.webservice.WebServiceResponse;
import samanasoft.android.ottimo.dal.BusinessLayer;
import samanasoft.android.ottimo.dal.DataLayer;

public class ChangePasswordActivity extends BaseMainActivity {

    private View mProgressView;
    private View mLoginFormView;
    private EditText mOldPasswordView;
    private EditText mNewPasswordView;
    private EditText mConfirmNewPasswordView;
    private ChangePasswordTask mAuthTask = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataLayer.Patient entity = InitActivity(savedInstanceState, R.layout.activity_change_password);
        mOldPasswordView = (EditText) findViewById(R.id.txtOldPassword);
        mNewPasswordView = (EditText) findViewById(R.id.txtNewPassword);
        mConfirmNewPasswordView = (EditText) findViewById(R.id.txtConfirmNewPassword);
        Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateAppointment();
            }
        });
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void updateAppointment() {
        if (mAuthTask != null) {
            return;
        }
        mConfirmNewPasswordView.setError(null);
        String oldPassword = mOldPasswordView.getText().toString();
        String newPassword = mNewPasswordView.getText().toString();
        String confirmNewPassword = mConfirmNewPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;
        if (!newPassword.equals(confirmNewPassword)) {
            mConfirmNewPasswordView.setError(getString(R.string.error_invalid_confirm_password));
            focusView = mConfirmNewPasswordView;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            showProgress(true);
            mAuthTask = new ChangePasswordTask(MRN, oldPassword, newPassword);
            mAuthTask.execute((Void) null);
        }
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
    public class ChangePasswordTask extends AsyncTask<Void, Void, WebServiceResponse> {

        private final Integer mMRN;
        private final String mOldPassword;
        private final String mNewPassword;

        ChangePasswordTask(Integer MRN, String oldPassword, String newPassword) {
            mMRN = MRN;
            mOldPassword = oldPassword;
            mNewPassword = newPassword;
        }

        @Override
        protected WebServiceResponse doInBackground(Void... params) {
            try {
                WebServiceResponse result = BusinessLayer.ChangePassword(getBaseContext(), mMRN, mOldPassword, mNewPassword);
                return result;
            }
            catch (Exception ex) {
                Toast.makeText(getBaseContext(), "Update Password Failed", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final WebServiceResponse result) {
            mAuthTask = null;
            showProgress(false);
            Toast.makeText(getBaseContext(), "Password Berhasil Diubah", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
