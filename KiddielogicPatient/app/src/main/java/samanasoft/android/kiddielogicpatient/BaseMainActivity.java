package samanasoft.android.kiddielogicpatient;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import samanasoft.android.framework.Constant;
import samanasoft.android.framework.DateTime;
import samanasoft.android.ottimo.control.CircularImageView;
import samanasoft.android.ottimo.dal.BusinessLayer;
import samanasoft.android.ottimo.dal.DataLayer;
import samanasoft.android.ottimo.dal.DataLayer.Patient;

public class BaseMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public int MRN = 0;
    public Patient InitActivity(Bundle savedInstanceState, int layout) {
        setContentView(layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent myIntent = getIntent();
        MRN = myIntent.getIntExtra("mrn", 0);
        Patient entity = null;
        if(MRN == 0){
            entity = BusinessLayer.getPatientList(this,"").get(0);
            MRN = entity.MRN;
        }
        else
            entity = BusinessLayer.getPatient(this, MRN);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerLayout = navigationView.getHeaderView(0);
        TextView txtTitle = (TextView) headerLayout.findViewById(R.id.txtTitle);
        TextView txtTitleMedicalNo = (TextView) headerLayout.findViewById(R.id.txtTitleMedicalNo);
        txtTitle.setText(entity.FullName);
        txtTitleMedicalNo.setText(entity.MedicalNo);
        setMessageCenterCounter();

        CircularImageView img = (CircularImageView) headerLayout.findViewById(R.id.imgNavHeaderPatientProfile);
        img.setImageBitmap(loadImageFromStorage(entity));

        return entity;
    }

    protected void setMessageCenterCounter(){
        List<DataLayer.vAppointment> lstAppointment = BusinessLayer.getvAppointmentList(this, String.format("GCAppointmentStatus != '%1$s' AND ReminderDate LIKE '%2$s%%'", Constant.AppointmentStatus.VOID, DateTime.now().toString(Constant.FormatString.DATE_FORMAT_DB)));
        //Log.d("filterExpression", String.format("GCAppointmentStatus = '%1$s' AND StartDate LIKE '%2$s%%'", Constant.AppointmentStatus.OPEN, DateTime.tomorrow().toString(Constant.FormatString.DATE_FORMAT_DB)));
        int appointmentCount = lstAppointment.size();
        if(appointmentCount > 0) {
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            Menu menuNav = navigationView.getMenu();
            MenuItem element = menuNav.findItem(R.id.nav_message_center);
            String before = element.getTitle().toString();

            String counter = Integer.toString(appointmentCount);
            String s = before + "   " + counter + " ";
            SpannableString sColored = new SpannableString(s);

            sColored.setSpan(new BackgroundColorSpan(Color.RED), s.length() - (counter.length() + 2), s.length(), 0);
            sColored.setSpan(new ForegroundColorSpan(Color.WHITE), s.length() - (counter.length() + 2), s.length(), 0);
            element.setTitle(sColored);
        }
        lstAppointment = BusinessLayer.getvAppointmentList(this, String.format("GCAppointmentStatus != '%1$s' AND ReminderDate >= '%2$s'", Constant.AppointmentStatus.VOID, DateTime.now().toString(Constant.FormatString.DATE_FORMAT_DB)));
        //Log.d("filterExpression", String.format("GCAppointmentStatus = '%1$s' AND StartDate LIKE '%2$s%%'", Constant.AppointmentStatus.OPEN, DateTime.tomorrow().toString(Constant.FormatString.DATE_FORMAT_DB)));
        appointmentCount = lstAppointment.size();
        if(appointmentCount > 0) {
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            Menu menuNav = navigationView.getMenu();
            MenuItem element = menuNav.findItem(R.id.nav_appointment);
            String before = element.getTitle().toString();

            String counter = Integer.toString(appointmentCount);
            String s = before + "   " + counter + " ";
            SpannableString sColored = new SpannableString(s);

            sColored.setSpan(new BackgroundColorSpan(Color.RED), s.length() - (counter.length() + 2), s.length(), 0);
            sColored.setSpan(new ForegroundColorSpan(Color.WHITE), s.length() - (counter.length() + 2), s.length(), 0);
            element.setTitle(sColored);
        }

    }

    protected Bitmap loadImageFromStorage(Patient entity)
    {

        try {

            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            // path to /data/data/yourapp/app_data/imageDir
            File directory = cw.getDir("Kiddielogic", Context.MODE_PRIVATE);
            File mypath = new File(directory, entity.MedicalNo + ".jpg");
            Bitmap b = null;
            if(mypath.exists())
                b = BitmapFactory.decodeStream(new FileInputStream(mypath));
            else {
                if(entity.GCSex.equals(Constant.Sex.MALE))
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_appointment) {
            Intent i = new Intent(getBaseContext(), AppointmentActivity.class);
            i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            i.putExtra("mrn", MRN);
            startActivity(i);
            // Handle the camera action
        } else if (id == R.id.nav_personal_data) {
            Intent i = new Intent(getBaseContext(), MainActivity.class);
            i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            i.putExtra("mrn", MRN);
            startActivity(i);

        } else if (id == R.id.nav_change_password) {
            Intent i = new Intent(getBaseContext(), ChangePasswordActivity.class);
            i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            i.putExtra("mrn", MRN);
            startActivity(i);

        } else if (id == R.id.nav_manage_account) {
            Intent i = new Intent(getBaseContext(), ManageAccountActivity.class);
            i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(i);

        } else if (id == R.id.nav_message_center) {
            Intent i = new Intent(getBaseContext(), MessageCenterActivity.class);
            i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            i.putExtra("mrn", MRN);
            startActivity(i);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
