package samanasoft.android.kiddielogicparamedicalarm;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import android.content.Intent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ListView;
import android.widget.ScrollView;

import samanasoft.android.ottimo.dal.BusinessLayer;
import samanasoft.android.ottimo.dal.DataLayer;

/**
 * Created by Ari on 2/2/2015.
 */
public class PatientChatActivity extends AppCompatActivity {

    private Button btn_send_msg;
    private EditText input_msg;
    private String user_name ,room_name;
    private DatabaseReference root;
    private String temp_key;
    private ListView lvwChat;

    Integer ParamedicID = null;
    Integer MRN = null;
    private ChatAdapter adapter;
    List<Message> lstEntity = new ArrayList<Message>();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patientchat);

        Intent myIntent = getIntent();
        ParamedicID = myIntent.getIntExtra("paramedicid", 0);
        MRN = myIntent.getIntExtra("mrn", 0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        DataLayer.Patient entityPatient = BusinessLayer.getPatient(this, MRN);
        setTitle(entityPatient.FullName);

        DataLayer.ParamedicMaster entityParamedic = BusinessLayer.getParamedicMaster(this, ParamedicID);


        btn_send_msg = (Button)findViewById(R.id.button);
        input_msg = (EditText)findViewById(R.id.editText);
        user_name = entityParamedic.UserName;
        room_name = ParamedicID + "_" + MRN;


        root = FirebaseDatabase.getInstance().getReference().child(room_name).child("message");

        btn_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String, Object> map = new HashMap<String, Object>();
                temp_key = root.push().getKey();
                root.updateChildren(map);

                DatabaseReference message_root = root.child(temp_key);
                Map<String, Object> map2 = new HashMap<String, Object>();
                map2.put("name", user_name);
                map2.put("msg", input_msg.getText().toString());

                input_msg.setText("");
                message_root.updateChildren(map2);

                //lvwChat.setSelection(lstEntity.size() - 1);
            }
        });

        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                append_chat_conversatin(dataSnapshot);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                append_chat_conversatin(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //lvwChat.setSelection(lstEntity.size() - 1);
    }

    private String chat_msg, chat_user_name;

    private void append_chat_conversatin(DataSnapshot dataSnapshot) {
        Iterator i = dataSnapshot.getChildren().iterator();
        while (i.hasNext())
        {
            chat_msg = (String) ((DataSnapshot)i.next()).getValue();
            chat_user_name = (String) ((DataSnapshot)i.next()).getValue();

            Message entity = new Message();
            entity.userName = chat_user_name;
            entity.messageText = chat_msg;
            lstEntity.add(entity);
            //chat_conversation.append(chat_user_name + " : " + chat_msg + "\n");

        }
        lvwChat = (ListView)findViewById(R.id.lvwChat);
        adapter = new ChatAdapter(getBaseContext(), lstEntity);
        lvwChat.setAdapter(adapter);

        lvwChat.setSelection(lstEntity.size() - 1);
    }

    public class Message{
        public String userName;
        public String messageText;
    }

    private class ChatAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Context context;
        private List<Message> lstView;

        public ChatAdapter(Context context, List<Message> lstView1) {
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
            final Message entity = lstView.get(position);
            Log.d("position", position + "");
            if (convertView == null) {
                holder = new ViewHolder();
                if(entity.userName.equals(user_name))
                    convertView = mInflater.inflate(R.layout.template_chat_right, null);
                else
                    convertView = mInflater.inflate(R.layout.template_chat_left, null);

                holder.txtMessage = (TextView) convertView.findViewById(R.id.txtMessage);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // Bind the data efficiently with the holder.
            holder.txtMessage.setText(entity.messageText);

            return convertView;

        }

    }
    private static class ViewHolder {
        TextView txtMessage;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_patient_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_soap) {
            Intent i = new Intent(getBaseContext(), PatientSOAPActivity.class);

            i.putExtra("paramedicid", ParamedicID);
            i.putExtra("mrn", MRN);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
