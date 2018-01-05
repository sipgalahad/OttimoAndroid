package samanasoft.android.kiddielogicpatientalarm;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
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

import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import samanasoft.android.framework.Constant;
import samanasoft.android.framework.DateTime;
import samanasoft.android.framework.Helper;
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
        SharedPreferences sharedPreferences = getSharedPreferences(samanasoft.android.ottimo.common.Constant.SharedPreference.NAME, MODE_PRIVATE);
        MRN = sharedPreferences.getInt("mrn", 0);
        Log.d("test", "MRN Login = " + MRN);
        boolean isGotoMessageCenter = myIntent.getBooleanExtra("isGotoMessageCenter", false);
        if(isGotoMessageCenter) {
            Intent i = new Intent(getBaseContext(), MessageCenterActivity.class);
            i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            i.putExtra("mrn", MRN);
            startActivity(i);
        }
        boolean isGoToAppointment = myIntent.getBooleanExtra("isGoToAppointment", false);
        if(isGoToAppointment) {
            Intent i = new Intent(getBaseContext(), AppointmentActivity.class);
            i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            i.putExtra("mrn", MRN);
            startActivity(i);
        }
        boolean isGoToLabResult = myIntent.getBooleanExtra("isGoToLabResult", false);
        Integer labResultID = myIntent.getIntExtra("labresultid", 0);
        if(isGoToLabResult) {
            Intent i = new Intent(getBaseContext(), LabResultActivity.class);
            i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            i.putExtra("mrn", MRN);
            i.putExtra("labresultid", labResultID);
            i.putExtra("isGoToLabResultDt", true);
            startActivity(i);
        }
        boolean isGoToLabResultDt = myIntent.getBooleanExtra("isGoToLabResultDt", false);
        if(isGoToLabResultDt) {
            Intent i = new Intent(getBaseContext(), LabResultDtActivity.class);
            i.putExtra("mrn", MRN);
            i.putExtra("labresultid", labResultID);
            startActivity(i);
        }
        boolean isGoToAnnouncement = myIntent.getBooleanExtra("isGoToAnnouncement", false);
        Integer announcementID = myIntent.getIntExtra("announcementid", 0);
        if(isGoToAnnouncement) {
            Intent i = new Intent(getBaseContext(), AnnouncementActivity.class);
            i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            i.putExtra("mrn", MRN);
            i.putExtra("announcementid", announcementID);
            i.putExtra("isGoToAnnouncementDt", true);
            startActivity(i);
        }
        boolean isGoToAnnouncementDt = myIntent.getBooleanExtra("isGoToAnnouncementDt", false);
        if(isGoToAnnouncementDt) {
            Intent i = new Intent(getBaseContext(), AnnouncementDtActivity.class);
            i.putExtra("mrn", MRN);
            i.putExtra("announcementid", announcementID);
            startActivity(i);
        }
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
        String dtNow = DateTime.now().toString(Constant.FormatString.DATE_FORMAT_DB) + " 00:00:00";
        List<DataLayer.vAppointment> lstAppointment = BusinessLayer.getvAppointmentList(this, String.format("(GCAppointmentStatus != '%1$s' AND ('%2$s' BETWEEN ReminderDate AND StartDate)) OR (GCAppointmentStatus IN ('%3$s','%4$s') AND StartDate >= '%2$s')", Constant.AppointmentStatus.VOID, dtNow, Constant.AppointmentStatus.SEND_CONFIRMATION, Constant.AppointmentStatus.CONFIRMED));
        //Log.d("filterExpression", String.format("GCAppointmentStatus = '%1$s' AND StartDate LIKE '%2$s%%'", Constant.AppointmentStatus.OPEN, DateTime.tomorrow().toString(Constant.FormatString.DATE_FORMAT_DB)));
        int appointmentCount = lstAppointment.size();
        if(appointmentCount > 0)
            addCounterDigit(R.id.nav_message_center, "Message Center", appointmentCount);

        lstAppointment = BusinessLayer.getvAppointmentList(this, String.format("GCAppointmentStatus != '%1$s' AND StartDate >= '%2$s' AND MRN = '%3$s'", Constant.AppointmentStatus.VOID, DateTime.now().toString(Constant.FormatString.DATE_FORMAT_DB), MRN));
        //Log.d("filterExpression", String.format("GCAppointmentStatus = '%1$s' AND StartDate LIKE '%2$s%%'", Constant.AppointmentStatus.OPEN, DateTime.tomorrow().toString(Constant.FormatString.DATE_FORMAT_DB)));
        appointmentCount = lstAppointment.size();
        if(appointmentCount > 0)
            addCounterDigit(R.id.nav_appointment, "My Appointment", appointmentCount);

        addCounterDigitAnnouncement(R.id.nav_announcement, "Announcement", Constant.AnnouncementType.ANNOUNCEMENT);
        addCounterDigitAnnouncement(R.id.nav_news, "News", Constant.AnnouncementType.NEWS);
        addCounterDigitAnnouncement(R.id.nav_advertisement, "Advertisement",Constant.AnnouncementType.ADVERTISEMENT);
    }

    private void addCounterDigitAnnouncement(int id, String originalText, String GCAnnouncementType){
        List<DataLayer.Announcement> lstAnnouncement = BusinessLayer.getAnnouncementList(this, String.format("'%1$s' BETWEEN StartDate AND EndDate AND GCAnnouncementType = '%2$s' ORDER BY StartDate", DateTime.now().toString(Constant.FormatString.DATE_FORMAT_DB) + " 00:00:00", GCAnnouncementType));
        int count = lstAnnouncement.size();
        if(count > 0)
            addCounterDigit(id, originalText, count);
    }

    private void addCounterDigit(int id, String originalText, int count){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu menuNav = navigationView.getMenu();
        MenuItem element = menuNav.findItem(id);

        String counter = Integer.toString(count);
        String s = originalText + "   " + counter + " ";
        SpannableString sColored = new SpannableString(s);

        sColored.setSpan(new BackgroundColorSpan(Color.RED), s.length() - (counter.length() + 2), s.length(), 0);
        sColored.setSpan(new ForegroundColorSpan(Color.WHITE), s.length() - (counter.length() + 2), s.length(), 0);
        element.setTitle(sColored);
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
        } else if (id == R.id.nav_logout) {
            Patient patient = BusinessLayer.getPatient(this, MRN);
            List<DataLayer.Appointment> lstAppointment = BusinessLayer.getAppointmentList(this, String.format("MRN = '%1$s'", MRN));
            for(DataLayer.Appointment appointment : lstAppointment){
                Helper.deleteAppointmentFromEventCalender(getBaseContext(), appointment);
                BusinessLayer.deleteAppointment(getBaseContext(), appointment.AppointmentID);
            }
            List<DataLayer.VaccinationShotDt> lstOldVaccination = BusinessLayer.getVaccinationShotDtList(getBaseContext(), String.format("MRN = '%1$s'", MRN));
            for (DataLayer.VaccinationShotDt entity2 : lstOldVaccination) {
                BusinessLayer.deleteVaccinationShotDt(getBaseContext(), entity2.Type, entity2.ID);
            }
            List<DataLayer.LaboratoryResultHd> lstOldLabResultHd = BusinessLayer.getLaboratoryResultHdList(getBaseContext(), String.format("MRN = '%1$s'", MRN));
            for (DataLayer.LaboratoryResultHd entity2 : lstOldLabResultHd) {
                BusinessLayer.deleteLaboratoryResultHd(getBaseContext(), entity2.ID);
            }
            List<DataLayer.LaboratoryResultDt> lstOldLabResultDt = BusinessLayer.getLaboratoryResultDtList(getBaseContext(), String.format("MRN = '%1$s'", MRN));
            for (DataLayer.LaboratoryResultDt entity2 : lstOldLabResultDt) {
                BusinessLayer.deleteLaboratoryResultDt(getBaseContext(), entity2.LaboratoryResultDtID);
            }
            BusinessLayer.deletePatient(getBaseContext(), MRN);
            FirebaseMessaging.getInstance().unsubscribeFromTopic(patient.MedicalNo);
            //Delete Photo
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("Kiddielogic", Context.MODE_PRIVATE);
            File mypath = new File(directory, patient.MedicalNo + ".jpg");
            if(mypath.exists())
                mypath.delete();

            List<Patient> lstPatient = BusinessLayer.getPatientList(getBaseContext(), "");
            if (lstPatient.size() == 1) {
                Intent i = new Intent(getBaseContext(), MainActivity.class);
                i.putExtra("mrn", lstPatient.get(0).MRN);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(i);
            } else if (lstPatient.size() > 1) {
                Intent i = new Intent(getBaseContext(), ManageAccountActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            } else {
                Intent i = new Intent(getBaseContext(), LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra("isinit", true);
                startActivity(i);
            }
            finish();
        } else if (id == R.id.nav_labresult) {
            Intent i = new Intent(getBaseContext(), LabResultActivity.class);
            i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            i.putExtra("mrn", MRN);
            startActivity(i);
        } else if (id == R.id.nav_vaccination) {
            Intent i = new Intent(getBaseContext(), VaccinationActivity.class);
            i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            i.putExtra("mrn", MRN);
            startActivity(i);
        } else if (id == R.id.nav_personal_data) {
            Intent i = new Intent(getBaseContext(), MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            //i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            i.putExtra("mrn", MRN);
            startActivity(i);

        } else if (id == R.id.nav_change_password) {
            Intent i = new Intent(getBaseContext(), ChangePasswordActivity.class);
            i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            i.putExtra("mrn", MRN);
            startActivity(i);

        } else if (id == R.id.nav_manage_account) {
            Intent i = new Intent(getBaseContext(), ManageAccountActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(i);
            finish();

        } else if (id == R.id.nav_message_center) {
            Intent i = new Intent(getBaseContext(), MessageCenterActivity.class);
            i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            i.putExtra("mrn", MRN);
            startActivity(i);
        } else if (id == R.id.nav_error_feedback) {
            Intent i = new Intent(getBaseContext(), ErrorFeedbackActivity.class);
            i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            i.putExtra("mrn", MRN);
            startActivity(i);
        } else if (id == R.id.nav_announcement) {
            Intent i = new Intent(getBaseContext(), AnnouncementActivity.class);
            i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            i.putExtra("mrn", MRN);
            startActivity(i);
        } else if (id == R.id.nav_news) {
            Intent i = new Intent(getBaseContext(), NewsActivity.class);
            i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            i.putExtra("mrn", MRN);
            startActivity(i);
        } else if (id == R.id.nav_advertisement) {
            Intent i = new Intent(getBaseContext(), AdvertisementActivity.class);
            i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            i.putExtra("mrn", MRN);
            startActivity(i);
        } else if (id == R.id.nav_user_help) {
            Helper.CopyReadAssets(this);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
