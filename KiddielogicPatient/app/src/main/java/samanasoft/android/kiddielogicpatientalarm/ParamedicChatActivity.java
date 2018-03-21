package samanasoft.android.kiddielogicpatientalarm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import samanasoft.android.framework.Constant;
import samanasoft.android.framework.DateTime;
import samanasoft.android.ottimo.dal.BusinessLayer;
import samanasoft.android.ottimo.dal.DataLayer;

/**
 * Created by Ari on 2/2/2015.
 */
public class ParamedicChatActivity extends AppCompatActivity {

    private Button btn_send_msg;
    private EditText input_msg;
    private String user_name ,room_name;
    private DatabaseReference root;
    private String temp_key;
    private ListView lvwChat;

    Integer ParamedicID = null;
    Integer MRN = null;
    private ChatAdapter adapter;
    private DateTime dtNow;
    List<Message> lstEntity = new ArrayList<Message>();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paramedicchat);

        Intent myIntent = getIntent();
        ParamedicID = myIntent.getIntExtra("paramedicid", 0);
        MRN = myIntent.getIntExtra("mrn", 0);

        dtNow = DateTime.now();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        DataLayer.Patient entityPatient = BusinessLayer.getPatient(this, MRN);

        DataLayer.ParamedicMaster entityParamedic = BusinessLayer.getParamedicMaster(this, ParamedicID);
        setTitle(entityParamedic.ParamedicName);


        btn_send_msg = (Button)findViewById(R.id.button);
        input_msg = (EditText)findViewById(R.id.editText);
        user_name = MRN.toString();
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
                map2.put("dt", DateTime.now().toString(Constant.FormatString.DATE_TIME_FORMAT_DB));
                map2.put("st", "0");

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

    private String chat_msg, chat_user_name, chat_date_time, chat_status;

    private void append_chat_conversatin(DataSnapshot dataSnapshot) {
        Iterator i = dataSnapshot.getChildren().iterator();
        while (i.hasNext())
        {
            chat_date_time = (String) ((DataSnapshot)i.next()).getValue();
            chat_msg = (String) ((DataSnapshot)i.next()).getValue();
            chat_user_name = (String) ((DataSnapshot)i.next()).getValue();
            chat_status = (String) ((DataSnapshot)i.next()).getValue();

            Message entity = new Message();
            entity.dateTime = chat_date_time;
            entity.messageText = chat_msg;
            entity.userName = chat_user_name;
            entity.status = chat_status;
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
        public String dateTime;
        public String status;
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
            holder = new ViewHolder();
            if (entity.userName.equals(user_name))
                convertView = mInflater.inflate(R.layout.template_chat_right, null);
            else
                convertView = mInflater.inflate(R.layout.template_chat_left, null);

            holder.txtMessage = (TextView) convertView.findViewById(R.id.txtMessage);
            holder.txtTime = (TextView) convertView.findViewById(R.id.txtTime);
            convertView.setTag(holder);

            // Bind the data efficiently with the holder.
            holder.txtMessage.setText(entity.messageText);
            DateTime dt = new DateTime(entity.dateTime);
            if(dt.toString(Constant.FormatString.DATE_FORMAT_DB).equals(dtNow.toString(Constant.FormatString.DATE_FORMAT_DB)))
                holder.txtTime.setText(dt.toString("HH:mm"));
            else
                holder.txtTime.setText(dt.toString("dd-MMM-yyyy HH:mm"));

            return convertView;

        }

    }
    private static class ViewHolder {
        TextView txtMessage;
        TextView txtTime;
    }
}
