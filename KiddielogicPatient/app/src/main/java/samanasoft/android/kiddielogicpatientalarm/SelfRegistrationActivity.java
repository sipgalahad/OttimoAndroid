package samanasoft.android.kiddielogicpatientalarm;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import samanasoft.android.ottimo.dal.BusinessLayer;
import samanasoft.android.ottimo.dal.DataLayer;

public class SelfRegistrationActivity extends BaseMainActivity {

    private View mProgressView;
    private View mLoginFormView;
    private DataLayer.Patient entity = null;
    private EditText txtCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        entity = InitActivity(savedInstanceState, R.layout.activity_self_registration);
        txtCode = (EditText) findViewById(R.id.txtCode);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        Button btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://114.199.103.10:8080/Kiddielogic/BridgingServer/Program/Mobile/SelfRegistrationEntry.aspx?id="+ entity.MedicalNo + "|" + txtCode.getText()));
                //startActivity(browserIntent);
                showProgress(true);
                WebView mWebview  = new WebView(getBaseContext());

                mWebview.getSettings().setJavaScriptEnabled(true); // enable javascript

                mWebview.setWebViewClient(new WebViewClient() {
                    @SuppressWarnings("deprecation")
                    @Override
                    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                        Toast.makeText(getBaseContext(), description, Toast.LENGTH_SHORT).show();
                        showProgress(false);
                    }

                    @TargetApi(android.os.Build.VERSION_CODES.M)
                    @Override
                    public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                        // Redirect to deprecated method, so you can use it in all SDK versions
                        onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
                        showProgress(false);
                    }
                    public void onPageFinished(WebView view, String url) {
                        // do your stuff here
                        txtCode.setText("");
                        showProgress(false);
                        Toast.makeText(getBaseContext(), "Data Sudah Terkirim, Silakan Cek di Komputer", Toast.LENGTH_SHORT).show();
                    }
                });

                mWebview.loadUrl("http://114.199.103.10:8080/KiddielogicTest/BridgingServer/Program/Mobile/SelfRegistration/SelfRegistrationEntry.aspx?id=" + entity.MedicalNo + "|" + txtCode.getText());
                Log.d("url", "http://114.199.103.10:8080/KiddielogicTest/BridgingServer/Program/Mobile/SelfRegistration/SelfRegistrationEntry.aspx?id=" + entity.MedicalNo + "|" + txtCode.getText());
                //setContentView(mWebview);
            }
        });
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
}
