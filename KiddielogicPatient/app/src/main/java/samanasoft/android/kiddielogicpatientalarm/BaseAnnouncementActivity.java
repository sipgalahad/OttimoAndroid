package samanasoft.android.kiddielogicpatientalarm;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import samanasoft.android.framework.Constant;
import samanasoft.android.framework.DateTime;
import samanasoft.android.framework.Helper;
import samanasoft.android.framework.webservice.WebServiceResponse;
import samanasoft.android.ottimo.common.Methods;
import samanasoft.android.ottimo.dal.BusinessLayer;
import samanasoft.android.ottimo.dal.DataLayer.Announcement;
import samanasoft.android.ottimo.dal.DataLayer.Patient;

public class BaseAnnouncementActivity extends BaseMainActivity {
    private String GCAnnouncementType;
    private View mProgressView;
    private TextView tvLastSyncDate;
    private ListView lvwAnnouncement;

    protected void customOnCreate(Bundle savedInstanceState, String GCAnnouncementType1){
        this.GCAnnouncementType = GCAnnouncementType1;
        Patient entity = InitActivity(savedInstanceState, R.layout.activity_announcement);
        tvLastSyncDate = (TextView)findViewById(R.id.tvLastSyncDate);
        lvwAnnouncement = (ListView)findViewById(R.id.lvwAnnouncement);
        lvwAnnouncement.setDivider(null);
        lvwAnnouncement.setDividerHeight(0);

        mProgressView = findViewById(R.id.login_progress);
        //List<Announcement> lstAnnouncement = BusinessLayer.getAnnouncementList(this, String.format("MRN = '%1$s'", entity.MRN));
        List<Announcement> lstAnnouncement = BusinessLayer.getAnnouncementList(this, String.format("'%1$s' BETWEEN StartDate AND EndDate AND GCAnnouncementType = '%2$s' ORDER BY StartDate", DateTime.now().toString(Constant.FormatString.DATE_FORMAT_DB) + " 00:00:00", GCAnnouncementType));
        fillListAnnouncement(lstAnnouncement);


        SharedPreferences prefs = getSharedPreferences(samanasoft.android.ottimo.common.Constant.SharedPreference.NAME, MODE_PRIVATE);
        String lastSyncDate = prefs.getString(samanasoft.android.ottimo.common.Constant.SharedPreference.LAST_SYNC_ANNOUNCEMENT, "");
        if(!lastSyncDate.equals(""))
            tvLastSyncDate.setText("Last Sync : " + new DateTime(lastSyncDate).toString(Constant.FormatString.DATE_TIME_FORMAT));
    }

    private AnnouncementInformationAdapter adapter;
    private void fillListAnnouncement(List<Announcement> lstAnnouncement1){
        adapter = new AnnouncementInformationAdapter(getBaseContext(), lstAnnouncement1);
        lvwAnnouncement.setAdapter(adapter);
        lvwAnnouncement.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                Announcement entity = (Announcement) myAdapter.getItemAtPosition(myItemInt);
                Intent i = new Intent(getBaseContext(), AnnouncementDtActivity.class);
                i.putExtra("mrn", MRN);
                i.putExtra("announcementid", entity.AnnouncementID);
                startActivity(i);
            }
        });
    }
    //region Adapter
    private class AnnouncementInformationAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Context context;
        private List<Announcement> lstAnnouncement;

        public AnnouncementInformationAdapter(Context context, List<Announcement> lstAnnouncement1) {
            mInflater = LayoutInflater.from(context);
            this.context = context;
            lstAnnouncement = lstAnnouncement1;
        }
        public int getEntityID(int position){
            return lstAnnouncement.get(position).AnnouncementID;
        }
        public int getCount() {
            return lstAnnouncement.size();
        }
        public Object getItem(int position) {
            return lstAnnouncement.get(position);
        }
        public long getItemId(int position) {
            return position;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            final Announcement entity = lstAnnouncement.get(position);
            Log.d("position", position + "");
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.template_announcement_information, null);

                holder.txtAnnouncementDate = (TextView) convertView.findViewById(R.id.txtAnnouncementDate);
                holder.txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // Bind the data efficiently with the holder.
            holder.txtAnnouncementDate.setText(entity.StartDate.toString(Constant.FormatString.DATE_FORMAT) + " - " + entity.EndDate.toString(Constant.FormatString.DATE_FORMAT));
            holder.txtTitle.setText(entity.Title);
            return convertView;

        }

    }
    private static class ViewHolder {
        TextView txtAnnouncementDate;
        TextView txtTitle;
    }
    //endregion

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
            loadAnnouncementData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private LoadAnnouncementTask mAuthTask = null;
    private void loadAnnouncementData() {
        if (mAuthTask != null) {
            return;
        }

        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        showProgress(true);
        mAuthTask = new LoadAnnouncementTask(GCAnnouncementType);
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

            lvwAnnouncement.setVisibility(show ? View.GONE : View.VISIBLE);
            lvwAnnouncement.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    lvwAnnouncement.setVisibility(show ? View.GONE : View.VISIBLE);
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
            lvwAnnouncement.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class LoadAnnouncementTask extends AsyncTask<Void, Void, WebServiceResponse> {
        private String GCAnnouncementType;
        LoadAnnouncementTask(String GCAnnouncementType1) {
            this.GCAnnouncementType = GCAnnouncementType1;
        }

        @Override
        protected WebServiceResponse doInBackground(Void... params) {
            String filterExpression = String.format("'%1$s' BETWEEN StartDate AND EndDate", DateTime.now().toString(Constant.FormatString.DATE_FORMAT_DB));
            Log.d("filterExpression", filterExpression);
            try {
                WebServiceResponse result = BusinessLayer.getWebServiceListAnnouncement(getBaseContext(), filterExpression);
                return result;
            }
            catch (Exception ex) {
                Toast.makeText(getBaseContext(), "Get Announcement Failed", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final WebServiceResponse result) {
            mAuthTask = null;
            showProgress(false);

            if(result == null) {
                Toast.makeText(getBaseContext(), "Update Data Gagal. Silakan Cek Koneksi Internet Anda", Toast.LENGTH_SHORT).show();
            }
            else if(result.returnObj != null){
                List<Announcement> lstOldAnnouncement = BusinessLayer.getAnnouncementList(getBaseContext(), "");
                for (Announcement entity : lstOldAnnouncement) {
                    BusinessLayer.deleteAnnouncement(getBaseContext(), entity.AnnouncementID);
                }

                @SuppressWarnings("unchecked")
                List<Announcement> lstAnnouncement = (List<Announcement>) result.returnObj;
                for (Announcement entity : lstAnnouncement) {
                    BusinessLayer.insertAnnouncement(getBaseContext(), entity);
                }

                List<Announcement> lstAnnouncement1 = BusinessLayer.getAnnouncementList(getBaseContext(), String.format("'%1$s' BETWEEN StartDate AND EndDate AND GCAnnouncementType = '%2$s' ORDER BY StartDate", DateTime.now().toString(Constant.FormatString.DATE_FORMAT_DB) + " 00:00:00", GCAnnouncementType));
                fillListAnnouncement(lstAnnouncement1);

                SharedPreferences prefs = getSharedPreferences(samanasoft.android.ottimo.common.Constant.SharedPreference.NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(samanasoft.android.ottimo.common.Constant.SharedPreference.LAST_SYNC_ANNOUNCEMENT, result.timestamp.toString(Constant.FormatString.DATE_TIME_FORMAT_DB));
                editor.commit();
                setMessageCenterCounter();

                tvLastSyncDate.setText("Last Sync : " + result.timestamp.toString(Constant.FormatString.DATE_TIME_FORMAT));
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
