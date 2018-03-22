package samanasoft.android.kiddielogicpatientalarm;
import android.annotation.TargetApi;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class SelfRegistrationScanActivity extends AppCompatActivity implements ZBarScannerView.ResultHandler {
    private ZBarScannerView mScannerView;


    private String medicalNo;
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        Intent myIntent = getIntent();
        medicalNo = myIntent.getStringExtra("medicalno");

        mScannerView = new ZBarScannerView(this);    // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(me.dm7.barcodescanner.zbar.Result result) {
        // Do something with the result here
        Log.v("kkkk", result.getContents()); // Prints scan results
        Log.v("uuuu", result.getBarcodeFormat().getName()); // Prints the scan format (qrcode, pdf417 etc.)

        SelfRegistrationActivity.tvresult.setText(result.getContents());

        mScannerView.setVisibility(View.INVISIBLE);

        WebView mWebview  = new WebView(getBaseContext());

        mWebview.getSettings().setJavaScriptEnabled(true); // enable javascript

        mWebview.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(getBaseContext(), description, Toast.LENGTH_SHORT).show();
            }

            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                // Redirect to deprecated method, so you can use it in all SDK versions
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
            }

            public void onPageFinished(WebView view, String url) {
                // do your stuff here
                Toast.makeText(getBaseContext(), "Data Sudah Terkirim, Silakan Cek di Komputer", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });


        mWebview.loadUrl("http://kcc_sunter.ddns.us:8083/KiddielogicTest/BridgingServer/Program/Mobile/SelfRegistration/SelfRegistrationEntry.aspx?id=" + medicalNo + "|" + result.getContents());
        Toast.makeText(getBaseContext(), "Data Sedang Dikirim. Harap Tunggu Sebentar", Toast.LENGTH_SHORT).show();
        //Log.d("url", "http://kcc_sunter.ddns.us:8083/KiddielogicTest/BridgingServer/Program/Mobile/SelfRegistration/SelfRegistrationEntry.aspx?id=" + medicalNo + "|" + result.getContents());

        //onBackPressed();
        //mScannerView.setVisibility(View.INVISIBLE);

        // If you would like to resume scanning, call this method below:
        //mScannerView.resumeCameraPreview(this);
    }
}
