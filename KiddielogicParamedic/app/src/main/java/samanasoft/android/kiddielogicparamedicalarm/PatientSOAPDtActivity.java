package samanasoft.android.kiddielogicparamedicalarm;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import samanasoft.android.framework.Constant;
import samanasoft.android.framework.webservice.WebServiceResponse;
import samanasoft.android.ottimo.dal.BusinessLayer;
import samanasoft.android.ottimo.dal.DataLayer;

/**
 * Created by Ari on 2/2/2015.
 */
public class PatientSOAPDtActivity extends AppCompatActivity {
    Integer ParamedicID = null;
    Integer MRN = null;
    Integer VisitID = null;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patientsoapdt);

        Intent myIntent = getIntent();
        ParamedicID = myIntent.getIntExtra("paramedicid", 0);
        MRN = myIntent.getIntExtra("mrn", 0);
        VisitID = myIntent.getIntExtra("visitid", 0);



        DataLayer.ConsultVisit entity = BusinessLayer.getConsultVisit(this, VisitID);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle(entity.ServiceUnitName + " - " + entity.VisitDate.toString(Constant.FormatString.DATE_FORMAT));


        TextView tvSubjective = (TextView)findViewById(R.id.tvSubjective);
        TextView tvObjective = (TextView)findViewById(R.id.tvObjective);
        TextView tvAssessment = (TextView)findViewById(R.id.tvAssessment);
        TextView tvPlanning = (TextView)findViewById(R.id.tvPlanning);
        TextView tvInternalNotes = (TextView)findViewById(R.id.tvInternalNotes);

        tvSubjective.setText(entity.VisitNoteSubjective);
        tvInternalNotes.setText(entity.VisitNoteInternalNotes);

        //Objective
        {
            String noteText = entity.VisitNoteObjective;
            String value = "";
            if (!entity.VitalSign.equals(""))
                value += String.format("Tanda Vital :<br/>%1$s", entity.VitalSign.replace("/sp/", "").replace("/sp/", "").replace("/sp/", "").replace("/sp/", "").replace("\\sp\\", "").replace("\\sp\\", "").replace("\\sp\\", "").replace("\\sp\\", ""));
            if (!value.equals(""))
                value += "<br/>";
            value += noteText;
            tvObjective.setText(Html.fromHtml(value));
        }
        
        //Assessment
        {
            String value = entity.VisitNoteAssessment;
            if (!entity.DiagnosisText.equals(""))
            {
                if (!value.equals(""))
                    value += "<br class='brSeparator'/>";
                value += String.format("Diagnosa :<br/>%1$s", entity.DiagnosisText.replace("/Dx/", "<b>Dx</b>").replace("/br/", "<br/>").replace("/br/", "<br/>").replace("/br/", "<br/>").replace("/br/", "<br/>"));
            }
            if (!entity.ProcedureText.equals(""))
            {
                if (!value.equals(""))
                    value += "<br class='brSeparator'/>";
                value += String.format("Procedure :<br/>%1$s", entity.ProcedureText.replace("/Px/", "<b>Px</b>").replace("/br/", "<br/>").replace("/br/", "<br/>").replace("/br/", "<br/>").replace("/br/", "<br/>"));
            }
            tvAssessment.setText(Html.fromHtml(value));
        }

        //Planning
        {
            String value = entity.VisitNotePlanning;
            if (!entity.LabOrderText.equals(""))
            {
                if (!value.equals(""))
                    value += "<br class='brSeparator'/>";
                value += String.format("<span class='spanTitle'>Pemeriksaan Lab</span> :<br/>%1$s", entity.LabOrderText);
            }
            if (!entity.Prescription.equals(""))
            {
                if (!value.equals(""))
                    value += "<br class='brSeparator'/>";
                value += String.format("<label class='spanTitle lblLink spnCopyPrescriptionDt'>Resep</label> :<br/>");
                value += String.format("<span>%1$s</span>", entity.Prescription);
            }
            if (!entity.Vaccination.equals(""))
            {
                if (!value.equals(""))
                    value += "<br class='brSeparator'/>";
                value += String.format("<span class='spanTitle'>Vaksin</span> :<br/>");
                String lstVaccination = entity.Vaccination.replace("/b/", "<b style='color:Maroon'>")
                        .replace("/b/", "<b style='color:Maroon'>").replace("/b/", "<b style='color:Maroon'>").replace("/b/", "<b style='color:Maroon'>")
                        .replace("\\b\\", "</b>").replace("\\b\\", "</b>").replace("\\b\\", "</b>").replace("\\b\\", "</b>");

                value += String.format("<span>%1$s</span>", lstVaccination);
            }
            if (!entity.FollowUpVisit.equals(""))
            {
                if (!value.equals(""))
                    value += "<br class='brSeparator'/>";
                value += String.format("<label class='spanTitle lblLink spnCopyPrescriptionDt'>Kunjungan Berikutnya</label> :<br/>");
                value += String.format("<span>%1$s</span>", entity.FollowUpVisit);
            }
            tvPlanning.setText(Html.fromHtml(value));
        }
    }

}
