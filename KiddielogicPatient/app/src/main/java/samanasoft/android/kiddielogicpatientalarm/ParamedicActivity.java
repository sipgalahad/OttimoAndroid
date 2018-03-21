package samanasoft.android.kiddielogicpatientalarm;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import samanasoft.android.framework.Constant;
import samanasoft.android.framework.DateTime;
import samanasoft.android.framework.webservice.WebServiceHelper;
import samanasoft.android.framework.webservice.WebServiceResponse;
import samanasoft.android.ottimo.control.CircularImageView;
import samanasoft.android.ottimo.dal.BusinessLayer;
import samanasoft.android.ottimo.dal.DataLayer;
import samanasoft.android.ottimo.dal.DataLayer.ParamedicMaster;
import samanasoft.android.ottimo.dal.DataLayer.Patient;

/**
 * Created by Ari on 2/2/2015.
 */
public class ParamedicActivity extends BaseMainActivity {

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
        Patient entity = InitActivity(savedInstanceState, R.layout.activity_paramedic);
        MRN = entity.MRN;

        listRoom = root.orderByChild("mrn").equalTo(MRN);
        lvwView = (ListView)findViewById(R.id.lvwPatient);
        lvwView.setDivider(null);
        lvwView.setDividerHeight(0);
        lvwView.setVisibility(View.INVISIBLE);
        mProgressView = findViewById(R.id.login_progress);
        //List<Appointment> lstAppointment = BusinessLayer.getAppointmentList(this, String.format("MRN = '%1$s'", entity.MRN));

        String filterExpression = String.format("");
        List<ParamedicMaster> lstEntity = BusinessLayer.getParamedicMasterList(getBaseContext(), filterExpression);
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
                lvwView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_paramedic, menu);
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

    private void fillListLabResult(List<ParamedicMaster> lstLabResult){
        adapter = new PatientInformationAdapter(getBaseContext(), lstLabResult);
        lvwView.setAdapter(adapter);
        lvwView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                ParamedicMaster entity = (ParamedicMaster) myAdapter.getItemAtPosition(myItemInt);

                String roomName = entity.ParamedicID + "_" + MRN;
                if (!list_of_rooms.contains(roomName)) {
                    Log.d("Test", "masuk bikin map baru");
                    Map<String,Object> map = new HashMap<String, Object>();
                    //String temp_key = root.push().getKey();
                    map.put(roomName, "");
                    root.updateChildren(map);

                    DatabaseReference room_root = root.child(roomName);
                    Map<String, Object> map2 = new HashMap<String, Object>();
                    map2.put("paramedicid", entity.ParamedicID);
                    map2.put("mrn", MRN);
                    map2.put("message", "");
                    room_root.updateChildren(map2);
                }

                Intent i = new Intent(getBaseContext(), ParamedicChatActivity.class);
                i.putExtra("paramedicid", entity.ParamedicID);
                i.putExtra("mrn", MRN);
                startActivity(i);
            }
        });
    }

    //region Adapter
    private class PatientInformationAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Context context;
        private List<ParamedicMaster> lstView;

        public PatientInformationAdapter(Context context, List<ParamedicMaster> lstView1) {
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
            final ParamedicMaster entity = lstView.get(position);
            Log.d("position", position + "");
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.template_paramedic_information, null);

                holder.txtPatientName = (TextView) convertView.findViewById(R.id.txtPatientName);
                holder.imgProfile = (CircularImageView) convertView.findViewById(R.id.imgProfile);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // Bind the data efficiently with the holder.
            holder.txtPatientName.setText(entity.ParamedicName);
            holder.imgProfile.setImageBitmap(loadImageFromStorage(entity));

            return convertView;

        }

    }

    protected Bitmap loadImageFromStorage(ParamedicMaster entity)
    {

        try {

            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            // path to /data/data/yourapp/app_data/imageDir
            File directory = cw.getDir("Kiddielogic", Context.MODE_PRIVATE);
            Log.d("pathload", "PM" + entity.ParamedicCode + ".jpg");
            File mypath = new File(directory, "PM" + entity.ParamedicCode + ".jpg");
            Bitmap b = null;
            if(mypath.exists())
                b = BitmapFactory.decodeStream(new FileInputStream(mypath));
            else {
                if(entity.GCGender.equals(Constant.Sex.MALE))
                    b = BitmapFactory.decodeResource(getResources(), R.drawable.patient_male);
                else
                    b = BitmapFactory.decodeResource(getResources(), R.drawable.patient_female);
            }
            return b;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private static class ViewHolder {
        TextView txtPatientName;
        CircularImageView imgProfile;
    }
    //endregion

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class LoadPatientTask extends AsyncTask<Void, Void, WebServiceResponsePatient> {
        LoadPatientTask() {

        }

        @Override
        protected WebServiceResponsePatient doInBackground(Void... params) {
            //String filterExpression = String.format("ParamedicID IN (SELECT ParamedicID FROM PatientParamedicChatRoom WHERE MRN = %1$s AND IsActive = 1)", MRN);
            //Log.d("test",filterExpression);
            try {
                WebServiceResponsePatient result = LoadParamedicMasterList(getBaseContext(), MRN);
                return result;
            } catch (Exception ex) {
                //Toast.makeText(getBaseContext(), "Get LabResult Failed", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final WebServiceResponsePatient result) {
            mAuthTask = null;
            showProgress(false);
            Log.d("test","haha");
            if (result == null) {
                Toast.makeText(getBaseContext(), "Update Data Gagal. Silakan Cek Koneksi Internet Anda", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("test", "haha");
                if (result.returnObjParamedic != null) {
                    String filterExpression = String.format("");
                    List<DataLayer.ParamedicMaster> lstEntity = BusinessLayer.getParamedicMasterList(getBaseContext(), filterExpression);
                    for (DataLayer.ParamedicMaster entity : lstEntity) {
                        BusinessLayer.deleteParamedicMaster(getBaseContext(), entity.ParamedicID);
                    }

                    @SuppressWarnings("unchecked")
                    List<ParamedicMaster> lstParamedic = (List<ParamedicMaster>) result.returnObjParamedic;
                    int ctr = 0;
                    for (ParamedicMaster entity : lstParamedic) {
                        ParamedicMaster tempParamedic = BusinessLayer.getParamedicMaster(getBaseContext(), entity.ParamedicID);
                        if (tempParamedic == null) {
                            entity.LastSyncDateTime = result.timestamp;

                            BusinessLayer.insertParamedicMaster(getBaseContext(), entity);
                            FirebaseMessaging.getInstance().subscribeToTopic(entity.ParamedicCode);

                            String returnObjImg = result.returnObjImg.get(ctr);
                            if (!returnObjImg.equals("")) {
                                ContextWrapper cw = new ContextWrapper(getApplicationContext());
                                File directory = cw.getDir("Kiddielogic", Context.MODE_PRIVATE);
                                File mypath = new File(directory, "PM" + entity.ParamedicCode + ".jpg");
                                Log.d("path", "PM" + entity.ParamedicCode + ".jpg");

                                FileOutputStream fos = null;
                                try {
                                    fos = new FileOutputStream(mypath);

                                    byte[] decodedString = Base64.decode(returnObjImg, Base64.DEFAULT);
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
                            ctr++;
                        }
                    }
                    fillListLabResult(lstParamedic);
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

    public WebServiceResponsePatient LoadParamedicMasterList(Context context, Integer MRN){
        WebServiceResponsePatient result = new WebServiceResponsePatient();
        try {
            JSONObject response = WebServiceHelper.LoadParamedicMasterList(context, MRN);

            JSONArray returnObjParamedic = WebServiceHelper.getCustomReturnObject(response, "ReturnObjParamedic");
            JSONArray returnObjImg = WebServiceHelper.getCustomReturnObject(response, "ReturnObjImage");
            DateTime timestamp = WebServiceHelper.getTimestamp(response);

            List<DataLayer.ParamedicMaster> lst = new ArrayList<ParamedicMaster>();
            for (int i = 0; i < returnObjParamedic.length();++i){
                JSONObject row = (JSONObject) returnObjParamedic.get(i);
                lst.add((DataLayer.ParamedicMaster)WebServiceHelper.JSONObjectToObject(row, new ParamedicMaster()));
            }
            List<String> lst2 = new ArrayList();
            for (int i = 0; i < returnObjImg.length();++i){
                lst2.add(returnObjImg.get(i).toString());
            }
            result.returnObjParamedic = lst;
            result.returnObjImg = lst2;
            result.timestamp = timestamp;
        } catch (Exception e) {
            result = null;
            e.printStackTrace();
        }
        return result;
    }
    public class WebServiceResponsePatient {
        public DateTime timestamp;
        public List<?> returnObjParamedic;
        public List<String> returnObjImg;
    }
}
