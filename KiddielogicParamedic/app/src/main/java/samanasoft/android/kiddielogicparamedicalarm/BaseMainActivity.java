package samanasoft.android.kiddielogicparamedicalarm;

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
import java.util.ArrayList;
import java.util.List;

import samanasoft.android.framework.Constant;
import samanasoft.android.framework.DateTime;
import samanasoft.android.framework.Helper;
import samanasoft.android.ottimo.control.CircularImageView;
import samanasoft.android.ottimo.dal.BusinessLayer;
import samanasoft.android.ottimo.dal.DataLayer;
import samanasoft.android.ottimo.dal.DataLayer.ParamedicMaster;

public class BaseMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public int ParamedicID = 0;
    public ParamedicMaster InitActivity(Bundle savedInstanceState, int layout) {
        setContentView(layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent myIntent = getIntent();
        SharedPreferences sharedPreferences = getSharedPreferences(samanasoft.android.ottimo.common.Constant.SharedPreference.NAME, MODE_PRIVATE);
        ParamedicID = sharedPreferences.getInt("paramedicid", 0);
        Log.d("test", "paramedicid Login = " + ParamedicID);
        ParamedicMaster entity = null;
        if(ParamedicID == 0){
            entity = BusinessLayer.getParamedicMasterList(this, "").get(0);
            ParamedicID = entity.ParamedicID;
        }
        else
            entity = BusinessLayer.getParamedicMaster(this, ParamedicID);

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
        txtTitle.setText(entity.ParamedicName);
        txtTitleMedicalNo.setText(entity.ParamedicCode);
        //setMessageCenterCounter();

        CircularImageView img = (CircularImageView) headerLayout.findViewById(R.id.imgNavHeaderPatientProfile);
        img.setImageBitmap(loadImageFromStorage(entity));

        return entity;
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

    protected Bitmap loadImageFromStorage(ParamedicMaster entity)
    {

        try {

            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            // path to /data/data/yourapp/app_data/imageDir
            File directory = cw.getDir("KiddielogicParamedic", Context.MODE_PRIVATE);
            File mypath = new File(directory, entity.UserName + ".jpg");
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

        //if (id == R.id.nav_appointment) {
        //    Intent i = new Intent(getBaseContext(), AppointmentActivity.class);
        //    i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        //    i.putExtra("mrn", MRN);
        //    startActivity(i);
        //} else
        if (id == R.id.nav_logout) {
            ParamedicMaster entity = BusinessLayer.getParamedicMaster(this, ParamedicID);
            BusinessLayer.deleteParamedicMaster(getBaseContext(), ParamedicID);
            FirebaseMessaging.getInstance().unsubscribeFromTopic(entity.UserName);
            //Delete Photo
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("KiddielogicParamedic", Context.MODE_PRIVATE);
            File mypath = new File(directory, entity.UserName + ".jpg");
            if(mypath.exists())
                mypath.delete();

            List<ParamedicMaster> lstEntity = BusinessLayer.getParamedicMasterList(getBaseContext(), "");
            if (lstEntity.size() == 1) {
                Intent i = new Intent(getBaseContext(), MainActivity.class);
                i.putExtra("paramedicid", lstEntity.get(0).ParamedicID);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(i);
            } else if (lstEntity.size() > 1) {
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
        } else if (id == R.id.nav_personal_data) {
            Intent i = new Intent(getBaseContext(), MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            //i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            i.putExtra("paramedicid", ParamedicID);
            startActivity(i);
        } else if (id == R.id.nav_patient) {
            Intent i = new Intent(getBaseContext(), PatientActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            //i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            i.putExtra("paramedicid", ParamedicID);
            startActivity(i);
        } else if (id == R.id.nav_manage_account) {
            Intent i = new Intent(getBaseContext(), ManageAccountActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(i);
            finish();

        } else if (id == R.id.nav_error_feedback) {
            Intent i = new Intent(getBaseContext(), ErrorFeedbackActivity.class);
            i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            i.putExtra("paramedicid", ParamedicID);
            startActivity(i);
        } else if (id == R.id.nav_user_help) {
            Helper.CopyReadAssets(this);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
