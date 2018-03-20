package samanasoft.android.kiddielogicparamedicalarm;



import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import samanasoft.android.framework.Constant;
import samanasoft.android.framework.DateTime;
import samanasoft.android.framework.webservice.WebServiceHelper;
import samanasoft.android.framework.webservice.WebServiceResponse;
import samanasoft.android.ottimo.dal.BusinessLayer;
import samanasoft.android.ottimo.dal.DataLayer;
import samanasoft.android.ottimo.dal.DataLayer.ParamedicMaster;
import samanasoft.android.ottimo.dal.DataLayer.Patient;

/**
 * Created by Ari on 2/2/2015.
 */
public class PatientActivity extends BaseMainActivity {

    private ListView lvwView;
    ScrollView scrlMain;
    private View mProgressView;
    Integer ParamedicID = null;
    Integer MRN = null;
    LoadPatientTask mAuthTask = null;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();
    private Query listRoom = null;
    private ArrayList<String> list_of_rooms = new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParamedicMaster entity = InitActivity(savedInstanceState, R.layout.activity_patient);
        ParamedicID = entity.ParamedicID;

        listRoom = root.orderByChild("paramedicid").equalTo(ParamedicID);
        lvwView = (ListView)findViewById(R.id.lvwPatient);
        lvwView.setDivider(null);
        lvwView.setDividerHeight(0);
        mProgressView = findViewById(R.id.login_progress);
        //List<Appointment> lstAppointment = BusinessLayer.getAppointmentList(this, String.format("MRN = '%1$s'", entity.MRN));

        String filterExpression = String.format("");
        List<DataLayer.Patient> lstEntity = BusinessLayer.getPatientList(getBaseContext(), filterExpression);
        Log.d("test", "" + lstEntity.size());
        if (lstEntity.size() > 0)
            fillListLabResult(lstEntity);
        else {
            showProgress(true);
            mAuthTask = new LoadPatientTask();
            mAuthTask.execute((Void) null);
        }

        listRoom.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Set<String> set = new HashSet<String>();
                Iterator i = dataSnapshot.getChildren().iterator();
                while (i.hasNext()) {
                    String temp =((DataSnapshot) i.next()).getKey();
                    set.add(temp);
                    Log.d("test", "temp : " + temp);
                }
                Log.d("test", "gaga : " + set.size());
                list_of_rooms.clear();
                list_of_rooms.addAll(set);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_soap, menu);
        return true;
    }

    private void loadPatientData() {
        if (mAuthTask != null) {
            return;
        }

        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        showProgress(true);
        mAuthTask = new LoadPatientTask();
        mAuthTask.execute((Void) null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            loadPatientData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private PatientInformationAdapter adapter;

    private void fillListLabResult(List<Patient> lstLabResult){
        adapter = new PatientInformationAdapter(getBaseContext(), lstLabResult);
        lvwView.setAdapter(adapter);
        lvwView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                Patient entity = (Patient) myAdapter.getItemAtPosition(myItemInt);

                String roomName = ParamedicID + "_" + entity.MRN;
                if (!list_of_rooms.contains(roomName)) {
                    Log.d("Test", "masuk bikin map baru");
                    Map<String,Object> map = new HashMap<String, Object>();
                    //String temp_key = root.push().getKey();
                    map.put(roomName, "");
                    root.updateChildren(map);

                    DatabaseReference room_root = root.child(roomName);
                    Map<String, Object> map2 = new HashMap<String, Object>();
                    map2.put("paramedicid", ParamedicID);
                    map2.put("mrn", entity.MRN);
                    map2.put("message", "");
                    room_root.updateChildren(map2);
                }

                Intent i = new Intent(getBaseContext(), PatientChatActivity.class);
                i.putExtra("paramedicid", ParamedicID);
                i.putExtra("mrn", entity.MRN);
                startActivity(i);
            }
        });
    }

    //region Adapter
    private class PatientInformationAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Context context;
        private List<Patient> lstView;

        public PatientInformationAdapter(Context context, List<Patient> lstView1) {
            mInflater = LayoutInflater.from(context);
            this.context = context;
            lstView = lstView1;
        }
        public int getCount() {
            return lstView.size();
        }
        public Object getItem(int position) {
            return lstView.get(position);
        }
        public long getItemId(int position) {
            return position;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            final Patient entity = lstView.get(position);
            Log.d("position", position + "");
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.template_patient_information, null);

                holder.txtPatientName = (TextView) convertView.findViewById(R.id.txtPatientName);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // Bind the data efficiently with the holder.
            holder.txtPatientName.setText(entity.FullName);

            return convertView;

        }

    }
    private static class ViewHolder {
        TextView txtPatientName;
    }
    //endregion

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class LoadPatientTask extends AsyncTask<Void, Void, WebServiceResponse> {
        LoadPatientTask() {

        }

        @Override
        protected WebServiceResponse doInBackground(Void... params) {
            String filterExpression = String.format("MRN IN (SELECT MRN FROM PatientParamedicChatRoom WHERE ParamedicID = %1$s AND IsActive = 1)", ParamedicID);
            Log.d("test",filterExpression);
            try {
                WebServiceResponse result = BusinessLayer.getWebServiceListPatient(getBaseContext(), filterExpression);
                return result;
            } catch (Exception ex) {
                //Toast.makeText(getBaseContext(), "Get LabResult Failed", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final WebServiceResponse result) {
            mAuthTask = null;
            showProgress(false);
            Log.d("test","haha");
            if (result == null) {
                Toast.makeText(getBaseContext(), "Update Data Gagal. Silakan Cek Koneksi Internet Anda", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("test","haha");
                if (result.returnObj != null) {
                    Log.d("test", "masuk kok");

                    try {
                        String filterExpression = String.format("");
                        List<DataLayer.Patient> lstEntity = BusinessLayer.getPatientList(getBaseContext(), filterExpression);
                        for (DataLayer.Patient entity : lstEntity) {
                            BusinessLayer.deletePatient(getBaseContext(), entity.MRN);
                        }
                        List<DataLayer.Patient> lstNewEntity = (List<DataLayer.Patient>) result.returnObj;
                        for (DataLayer.Patient entity : lstNewEntity) {
                            BusinessLayer.insertPatient(getBaseContext(), entity);
                        }
                        filterExpression = String.format("");
                        lstEntity = BusinessLayer.getPatientList(getBaseContext(), filterExpression);
                        fillListLabResult(lstEntity);
                    }
                    catch (Exception e){
                        Log.d("test",e.getStackTrace().toString());
                    }
                }
            }
        }

        @Override
        protected void onCancelled() {
            //mAuthTask = null;
            //showProgress(false);
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

            lvwView.setVisibility(show ? View.GONE : View.VISIBLE);
            lvwView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    lvwView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            lvwView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
