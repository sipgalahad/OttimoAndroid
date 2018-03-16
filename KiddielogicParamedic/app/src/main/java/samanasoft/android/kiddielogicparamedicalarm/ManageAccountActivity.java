package samanasoft.android.kiddielogicparamedicalarm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import samanasoft.android.ottimo.dal.BusinessLayer;
import samanasoft.android.ottimo.dal.DataLayer.ParamedicMaster;

public class ManageAccountActivity extends AppCompatActivity {

    List<ParamedicMaster> lstParamedic = null;
    ParamedicMaster paramedic = null;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_account);
        fillListView();
    }

    private void fillListView(){
        lstParamedic = BusinessLayer.getParamedicMasterList(this, "");
        List<String> lstEntity = new ArrayList<String>();
        for(ParamedicMaster paramedic : lstParamedic){
            lstEntity.add(paramedic.ParamedicName + " (" + paramedic.ParamedicCode + ")");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1, lstEntity);
        ListView listView = (ListView)findViewById(R.id.lvwPatient);
        //listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                paramedic = lstParamedic.get(myItemInt);
                Intent i = new Intent(getBaseContext(), MainActivity.class);
                SharedPreferences sharedPreferences = getSharedPreferences(samanasoft.android.ottimo.common.Constant.SharedPreference.NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("paramedicid", paramedic.ParamedicID);
                editor.commit();
                i.putExtra("paramedicid", paramedic.ParamedicID);
                startActivity(i);
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

        return super.onOptionsItemSelected(item);
    }
}

