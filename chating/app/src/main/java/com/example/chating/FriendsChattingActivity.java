package com.example.chating;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsChattingActivity extends AppCompatActivity {

    private Toolbar mToolBar;

    private String UserId;
    private String UserName;
    private String UserImage;
    private String UserOnLine;

    //custom action bar items
    private TextView custom_UserName;
    private TextView custom_UserOnline;
    private CircleImageView custom_UserImage;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String CurrentUID;

    private ListView chatting_listView;;
    private ImageButton SendBtn;
    private EditText MessageEdit;
    private ArrayList<Message> chatting_arraylist;
    private int UserSeenNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_chatting);
        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        CurrentUID= currentUser.getUid();

        MainActivity.isFinished=false;
        FirebaseDatabase.getInstance().getReference().child("users").child(CurrentUID).child("Online").setValue("true");
        FirebaseDatabase.getInstance().getReference().child("users").child(CurrentUID).child("Seen").setValue("seen");


        //retrieve User data from the previous Activity
        UserId=getIntent().getStringExtra("User Id");
        UserName=getIntent().getStringExtra("User Name");
        UserImage=getIntent().getStringExtra("User Image");



        //define and show Actionbar
        show_ActionBar();
        //display user data in action bar
        displayUserDataInActionBar();


        chatting_listView=(ListView)findViewById(R.id.Chatting_ListView);
        SendBtn = (ImageButton)findViewById(R.id.sendBtn);
        MessageEdit = (EditText)findViewById(R.id.messageEdit);

        chatting_arraylist =new ArrayList<>();
        final MessageAdapter adapter=new MessageAdapter(this,chatting_arraylist);
        chatting_listView.setAdapter(adapter);





        SendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message_value=MessageEdit.getText().toString();
                if(message_value.isEmpty()) Toast.makeText(FriendsChattingActivity.this,"The message is empty",Toast.LENGTH_SHORT).show();
                else{

                    FirebaseDatabase.getInstance().getReference().child("chats").child(CurrentUID).child(UserId).push().setValue("S"+message_value);
                    FirebaseDatabase.getInstance().getReference().child("chats").child(UserId).child(CurrentUID).push().setValue("R"+message_value);

                    MessageEdit.setText("");
                }
            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseDatabase.getInstance().getReference().child("users").child(UserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String stat = snapshot.child("Seen").getValue().toString();
                if(stat.equals("offline")) UserSeenNum=1;
                else if(stat.equals("online")) UserSeenNum=2;
                else if(stat.equals("seen")) UserSeenNum=3;
                SaveInAdapter();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

    }

    private void SaveInAdapter(){
        final MessageAdapter adapter=new MessageAdapter(this,chatting_arraylist);
        DatabaseReference mFirebase= FirebaseDatabase.getInstance().getReference().child("chats").child(CurrentUID).child(UserId);
        mFirebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatting_arraylist.clear();
                for(DataSnapshot Snapshot : dataSnapshot.getChildren()){
                    if (Snapshot.getValue().toString().substring(0,1).equals("S")) {
                        chatting_arraylist.add(new Message(Snapshot.getValue().toString().substring(1, Snapshot.getValue().toString().length()), " ","S",UserSeenNum));
                    }
                    else {
                        chatting_arraylist.add(new Message(" ", Snapshot.getValue().toString().substring(1,Snapshot.getValue().toString().length()),"R",UserSeenNum));
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        chatting_listView.setAdapter(adapter);


    }

    private void show_ActionBar(){
        //tool & action bar
        mToolBar= (Toolbar)findViewById(R.id.FriendsChatting_TooBar);
        setSupportActionBar(mToolBar);
        ActionBar actionBar= getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view= inflater.inflate(R.layout.custom_action_bar,null);
        actionBar.setCustomView(view);

        //************custom action items xml**********************
        custom_UserName=(TextView)findViewById(R.id.custom_UserName);
        custom_UserOnline=(TextView)findViewById(R.id.custom_UserOnline);
        custom_UserImage=(CircleImageView)findViewById(R.id.custom_UserImage);

    }


    private void displayUserDataInActionBar(){

        FirebaseDatabase.getInstance().getReference().child("users").child(UserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserOnLine = snapshot.child("Online").getValue().toString();

                //display UserData
                custom_UserName.setText(UserName);
                // online state
                if(UserOnLine.equals("true")) custom_UserOnline.setText("Online now");
                else {
                    Long time = Long.valueOf(UserOnLine);
                    custom_UserOnline.setText(GetTimeAgo.getTimeAgo(time,FriendsChattingActivity.this).toString());
                }

                Picasso.get().load(UserImage).placeholder(R.drawable.user).into(custom_UserImage);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

    }

}