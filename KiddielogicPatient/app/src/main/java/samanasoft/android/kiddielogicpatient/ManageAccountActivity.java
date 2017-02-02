package samanasoft.android.kiddielogicpatient;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import samanasoft.android.framework.Constant;
import samanasoft.android.ottimo.dal.BusinessLayer;
import samanasoft.android.ottimo.dal.DataLayer.Appointment;
import samanasoft.android.ottimo.dal.DataLayer.Patient;

public class ManageAccountActivity extends AppCompatActivity {

    List<Patient> lstPatient = null;
    Patient patient = null;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_account);
        fillListView();
    }

    private void fillListView(){
        lstPatient = BusinessLayer.getPatientList(this, "");
        List<String> lstEntity = new ArrayList<String>();
        for(Patient patient : lstPatient){
            lstEntity.add(patient.FullName + " (" + patient.MedicalNo + ")");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_single_choice,
                android.R.id.text1, lstEntity);
        ListView listView = (ListView)findViewById(R.id.lvwPatient);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                patient = lstPatient.get(myItemInt);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manage_account, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_account) {
            Intent i = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(i);
            return true;
        }
        else if (id == R.id.action_choose_account) {
            if(patient != null) {
                Intent i = new Intent(getBaseContext(), MainActivity.class);
                i.putExtra("mrn", patient.MRN);
                startActivity(i);
            }
            else
                Toast.makeText(getBaseContext(), "Silakan Pilih Akun Terlebih Dahulu", Toast.LENGTH_SHORT).show();
        }
        else if (id == R.id.action_delete_account) {
            if(patient != null) {
                List<Appointment> lstAppointment = BusinessLayer.getAppointmentList(this, String.format("MRN = '%1$s'", patient.MRN));
                for(Appointment appointment : lstAppointment){
                    BusinessLayer.deleteAppointment(getBaseContext(), appointment.AppointmentID);
                }
                BusinessLayer.deletePatient(getBaseContext(), patient.MRN);
                //Delete Photo
                ContextWrapper cw = new ContextWrapper(getApplicationContext());
                File directory = cw.getDir("Kiddielogic", Context.MODE_PRIVATE);
                File mypath = new File(directory, patient.MedicalNo + ".jpg");
                if(mypath.exists())
                    mypath.delete();
                fillListView();
            }
            else
                Toast.makeText(getBaseContext(), "Silakan Pilih Akun Terlebih Dahulu", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }
}

