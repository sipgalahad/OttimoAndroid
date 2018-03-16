package samanasoft.android.kiddielogicpatientalarm;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import samanasoft.android.framework.Constant;
import samanasoft.android.framework.DateTime;
import samanasoft.android.framework.webservice.WebServiceHelper;
import samanasoft.android.framework.webservice.WebServiceResponse;
import samanasoft.android.ottimo.dal.BusinessLayer;
import samanasoft.android.ottimo.dal.DataLayer;
import samanasoft.android.ottimo.dal.DataLayer.Patient;
import samanasoft.android.ottimo.dal.DataLayer.VaccinationShotDt;
import samanasoft.android.ottimo.dal.DataLayer.vVaccinationType;

public class CDCGrowthChartActivity extends BaseMainActivity {

    private View mProgressView;
    private Spinner spnType;
    private ImageView imgCDCGrowthChart;
    private Patient entity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        entity = InitActivity(savedInstanceState, R.layout.activity_cdcgrowthchart);

        imgCDCGrowthChart = (ImageView) findViewById(R.id.imgCDCGrowthChart);

        DateTime now = DateTime.now();
        int ageInMonth = 12*(now.Year - entity.DateOfBirth.Year) + now.Month - entity.DateOfBirth.Month;
        List<DataLayer.Variable> lstCDCGrowthChartType = new ArrayList<DataLayer.Variable>();
        if(ageInMonth <= 36) {
            lstCDCGrowthChartType.add(3, new DataLayer.Variable("W_36M", "Berat Badan - 36 Bulan"));
            lstCDCGrowthChartType.add(4, new DataLayer.Variable("C_36M", "Lingkar Kepala - 36 Bulan"));
            lstCDCGrowthChartType.add(5, new DataLayer.Variable("H_36M", "Tinggi - 36 Bulan"));
        }
        else {
            lstCDCGrowthChartType.add(0, new DataLayer.Variable("W_20Y", "Berat Badan - 20 Tahun"));
            lstCDCGrowthChartType.add(1, new DataLayer.Variable("H_20Y", "Tinggi - 20 Tahun"));
            lstCDCGrowthChartType.add(2, new DataLayer.Variable("B_20Y", "BMI - 20 Tahun"));
        }

        //Log.d("testVaksin",lstVaccinationType.get(0).VaccinationTypeID + "");

        TypeAdapter TypeAdapter = new TypeAdapter(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, lstCDCGrowthChartType);
        spnType = (Spinner) findViewById(R.id.spnType);
        spnType.setAdapter(TypeAdapter); // Set the custom adapter to the spinner
        spnType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //refreshListVaccination();
                setImgCDCGrowthChartSrc();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mProgressView = findViewById(R.id.login_progress);
        setImgCDCGrowthChartSrc();
    }

    private void setImgCDCGrowthChartSrc(){
        DataLayer.Variable ent = (DataLayer.Variable) spnType.getSelectedItem();

        Bitmap b = loadImageFromStorage(ent.Code);
        if(b != null) {
            Log.d("Bitmap nya", ent.Code);
            imgCDCGrowthChart.setImageBitmap(b);
        }
        else
            Log.d("Bitmap nya", "null");
    }

    protected Bitmap loadImageFromStorage(String type)
    {

        try {

            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            // path to /data/data/yourapp/app_data/imageDir
            File directory = cw.getDir("Kiddielogic", Context.MODE_PRIVATE);
            File mypath = new File(directory, entity.MedicalNo + "_" + type + ".jpg");
            Bitmap b = null;
            if(mypath.exists())
                b = BitmapFactory.decodeStream(new FileInputStream(mypath));

            return b;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_appointment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            loadAppointmentData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private LoadCDCGrowthChartTask mAuthTask = null;
    private void loadAppointmentData() {
        if (mAuthTask != null) {
            return;
        }

        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        showProgress(true);
        mAuthTask = new LoadCDCGrowthChartTask(entity);
        mAuthTask.execute((Void) null);
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

            imgCDCGrowthChart.setVisibility(show ? View.GONE : View.VISIBLE);
            imgCDCGrowthChart.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    imgCDCGrowthChart.setVisibility(show ? View.GONE : View.VISIBLE);
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
            imgCDCGrowthChart.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class LoadCDCGrowthChartTask extends AsyncTask<Void, Void, WebServiceResponsePatient> {
        private Patient entityPatient;
        LoadCDCGrowthChartTask(Patient entity1) {
            this.entityPatient = entity1;
        }

        @Override
        protected WebServiceResponsePatient doInBackground(Void... params) {
            try {
                WebServiceResponsePatient result = SyncCDCGrowthChart(getBaseContext(), entityPatient.MRN, entityPatient.LastSyncLabResultDateTime.toString(Constant.FormatString.DATE_TIME_FORMAT_DB));
                return result;
            }
            catch (Exception ex) {
                Toast.makeText(getBaseContext(), "Get LabResult Failed", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final WebServiceResponsePatient result) {
            mAuthTask = null;
            showProgress(false);

            if (result == null) {
                Toast.makeText(getBaseContext(), "Update Data Gagal. Silakan Cek Koneksi Internet Anda", Toast.LENGTH_SHORT).show();
            } else {

                if (result.returnObjCDCGrowthChart != null) {
                    @SuppressWarnings("unchecked")
                    List<DataLayer.Variable> lstCDCGrowthChart = (List<DataLayer.Variable>) result.returnObjCDCGrowthChart;
                    Log.d("lstCDCGrowthChart", lstCDCGrowthChart.size() + ";");
                    for (DataLayer.Variable entity2 : lstCDCGrowthChart) {
                        if(!entity2.Value.equals("")) {
                            Log.d("haha", "hahahaha");
                            ContextWrapper cw = new ContextWrapper(getApplicationContext());
                            File directory = cw.getDir("Kiddielogic", Context.MODE_PRIVATE);
                            File mypath = new File(directory, entity.MedicalNo + "_" + entity2.Code + ".jpg");

                            FileOutputStream fos = null;
                            try {
                                fos = new FileOutputStream(mypath);

                                byte[] decodedString = Base64.decode(entity2.Value, Base64.DEFAULT);
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

                    setImgCDCGrowthChartSrc();
                    Patient entityPatient = BusinessLayer.getPatient(getBaseContext(), MRN);
                    entityPatient.LastSyncCDCGrowthChartDateTime = result.timestamp;
                    BusinessLayer.updatePatient(getBaseContext(), entityPatient);
                }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    public class TypeAdapter extends ArrayAdapter<DataLayer.Variable> {

        // Your sent context
        private Context context;
        // Your custom values for the spinner (User)
        private List<DataLayer.Variable> values;

        public TypeAdapter(Context context, int textViewResourceId,List<DataLayer.Variable> values) {
            super(context, textViewResourceId, values);
            this.context = context;
            this.values = values;
        }

        public int getCount(){
            return values.size();
        }

        public DataLayer.Variable getItem(int position){
            return values.get(position);
        }


        // And the "magic" goes here
        // This is for the "passive" state of the spinner
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
            TextView label = new TextView(context);
            label.setTextColor(Color.BLACK);
            // Then you can get the current item using the values array (Users array) and the current position
            // You can NOW reference each method you has created in your bean object (User class)
            label.setText(values.get(position).Value);

            // And finally return your dynamic (or custom) view for each spinner item
            return label;
        }

        // And here is when the "chooser" is popped up
        // Normally is the same view, but you can customize it if you want
        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            TextView label = new TextView(context);
            label.setTextColor(Color.BLACK);
            label.setText(values.get(position).Value);

            return label;
        }
    }
    //endregion

    public WebServiceResponsePatient SyncCDCGrowthChart(Context context, Integer MRN, String CDCGrowthChartLastUpdatedDate){
        WebServiceResponsePatient result = new WebServiceResponsePatient();
        try {
            JSONObject response = WebServiceHelper.SyncCDCGrowthChart(context, MRN, CDCGrowthChartLastUpdatedDate);

            JSONArray returnObjCDCGrowthChart = WebServiceHelper.getCustomReturnObject(response, "ReturnObjCDCGrowthChart");
            DateTime timestamp = WebServiceHelper.getTimestamp(response);

            List<DataLayer.Variable> lst7 = new ArrayList<DataLayer.Variable>();
            for (int i = 0; i < returnObjCDCGrowthChart.length();++i){
                JSONObject row = (JSONObject) returnObjCDCGrowthChart.get(i);
                lst7.add((DataLayer.Variable)WebServiceHelper.JSONObjectToObject(row, new DataLayer.Variable()));
            }
            result.returnObjCDCGrowthChart = lst7;
            result.timestamp = timestamp;
        } catch (Exception e) {
            result = null;
            e.printStackTrace();
        }
        return result;
    }
    public class WebServiceResponsePatient {
        public DateTime timestamp;
        public List<?> returnObjCDCGrowthChart;
    }
}
