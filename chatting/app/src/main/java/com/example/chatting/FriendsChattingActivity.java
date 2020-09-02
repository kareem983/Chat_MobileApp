package com.example.chatting;
 
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;
import de.hdodenhof.circleimageview.CircleImageView;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class FriendsChattingActivity extends AppCompatActivity {
    private Toolbar mToolBar;
    //xml
    private ListView chatting_listView;
    private ImageButton SendImageBtn;
    private ImageButton AddRecordBtn;
    private EditText MessageEdit;
    private ImageButton SendMessageBtn;
    private LinearLayout RecordContainer;
    private TextView CancelRecord;
    private Chronometer RecordTimer;
    private ImageButton SendRecordBtn;
    //custom action bar items
    private TextView custom_UserName;
    private TextView custom_UserOnline;
    private CircleImageView custom_UserImage;
    //user variables
    private String UserId;
    private String UserName;
    private String UserImage;
    private String UserOnLine;
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String CurrentUID;
    //record variables
    private MediaRecorder recorder;
    private String mFileName=null;
    private String mFilePath=null;
    private ImageView PlayOrPauseRecordBtn;
    private MediaPlayer mediaPlayer;
    private Runnable runnable;
    private Handler handler;
    private LinearLayout RecordLin;
    private SeekBar SeekBar;
    private ImageView playRecord;
    private ImageView closeRecord;
    private boolean isRecordSending;
    private ArrayList<Message> chatting_arraylist;
    private static final int GALARY_PICK=1;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_chatting);
        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        CurrentUID= currentUser.getUid();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        //retrieve User data from the previous Activity
        UserId=getIntent().getStringExtra("User Id");
        UserName=getIntent().getStringExtra("User Name");
        UserImage=getIntent().getStringExtra("User Image");


        MainActivity.isFinished=false;
        FirebaseDatabase.getInstance().getReference().child("users").child(CurrentUID).child("Online").setValue("true");
        FirebaseDatabase.getInstance().getReference().child("users").child(CurrentUID).child("Seen").setValue("seen");


        //mark all the friend messages as seen mark because am seen now
        markAllFriendMessagesAsSeen();

        //define and show Actionbar
        show_ActionBar();

        //display user data in action bar
        displayUserDataInActionBar();

        //define xml components
        chatting_listView=(ListView)findViewById(R.id.Chatting_ListView);
        SendImageBtn=(ImageButton)findViewById(R.id.SendImageBtn);
        AddRecordBtn = (ImageButton)findViewById(R.id.AddRecordBtn);
        MessageEdit = (EditText)findViewById(R.id.messageEdit);
        SendMessageBtn = (ImageButton)findViewById(R.id.sendMessageBtn);
        RecordContainer = (LinearLayout)findViewById(R.id.RecordContainer);
        CancelRecord =(TextView)findViewById(R.id.CancelRecord);
        RecordTimer =(Chronometer)findViewById(R.id.RecordTimer);
        SendRecordBtn =(ImageButton)findViewById(R.id.sendRecordBtn);
        RecordLin = (LinearLayout)findViewById(R.id.RecordContainerInList);
        SeekBar =(SeekBar)findViewById(R.id.SeekBar);
        playRecord = (ImageView)findViewById(R.id.PlayOrPauseBtn);
        closeRecord = (ImageView)findViewById(R.id.closeRecord);
        handler=new Handler();
        mediaPlayer = new MediaPlayer();


        chatting_arraylist =new ArrayList<>();
        final MessageAdapter adapter=new MessageAdapter(this,chatting_arraylist);
        chatting_listView.setAdapter(adapter);

        //if the user wanted to delete message
        DeleteMessage();

        // if any one send message to other
        ChangeInChatting();

        //if the user play any record
        playRecord();


        //on click to send Image button
        SendImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                RecordLin.setVisibility(View.GONE);

                Intent intent =new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"SELECT IMAGE"),GALARY_PICK);

            }
        });


        //on click to Add record button
        AddRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                RecordLin.setVisibility(View.GONE);

                SendImageBtn.setEnabled(false);
                AddRecordBtn.setEnabled(false);
                MessageEdit.setEnabled(false);
                SendMessageBtn.setEnabled(false);
                RecordContainer.setVisibility(View.VISIBLE);
                RecordTimer.setBase(SystemClock.elapsedRealtime());
                RecordTimer.start();

                if(checkPermission()) startRecording();
            }
        });


        //on click to cancel record Text view
        CancelRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                RecordLin.setVisibility(View.GONE);

                SendImageBtn.setEnabled(true);
                AddRecordBtn.setEnabled(true);
                MessageEdit.setEnabled(true);
                SendMessageBtn.setEnabled(true);
                RecordContainer.setVisibility(View.GONE);
                RecordTimer.stop();

            }
        });


        //on click to send record button
        SendRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                RecordLin.setVisibility(View.GONE);

                SendImageBtn.setEnabled(true);
                AddRecordBtn.setEnabled(true);
                MessageEdit.setEnabled(true);
                SendMessageBtn.setEnabled(true);
                RecordContainer.setVisibility(View.GONE);
                RecordTimer.stop();

                //stop recording and save in storage database
                stopRecording();
            }
        });


        //on click to send message button
        SendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                RecordLin.setVisibility(View.GONE);

                final String message_value=MessageEdit.getText().toString();
                if(message_value.isEmpty()) Toast.makeText(FriendsChattingActivity.this,"The message is empty",Toast.LENGTH_SHORT).show();
                else{
                    sendMessage(message_value);
                }
            }
        });



    }


    @Override
    protected void onStop() {
        super.onStop();
        RecordLin.setVisibility(View.GONE);
        mediaPlayer.stop();
    }



    private void playRecord(){
        SeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b)mediaPlayer.seekTo(i);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });


        chatting_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Message message =chatting_arraylist.get(i);
                if(message.getSenderMessage().equals(" ")) {
                    isRecordSending=false;
                    PlayOrPauseRecordBtn = (ImageView)view.findViewById(R.id.ReceiverPlayOrPauseBtn);
                }
                else{
                    isRecordSending =true;
                    PlayOrPauseRecordBtn = (ImageView)view.findViewById(R.id.SenderPlayOrPauseBtn);
                }


                PlayOrPauseRecordBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RecordLin.setVisibility(View.VISIBLE);
                        PlayOrPauseRecordBtn.setEnabled(false);

                        try {
                            handler=new Handler();
                            mediaPlayer = new MediaPlayer();

                            if(isRecordSending)mediaPlayer.setDataSource(message.getSenderMessage());
                            else mediaPlayer.setDataSource(message.getReceiverMessage());
                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mediaPlayer) {
                                    SeekBar.setMax(mediaPlayer.getDuration());
                                    mediaPlayer.start();
                                    playRecord.setImageResource(R.drawable.ic_baseline_pause_24);
                                    ChangeSeekBar();
                                }
                            });
                            mediaPlayer.prepare();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                RecordLin.setVisibility(View.GONE);
                                PlayOrPauseRecordBtn.setEnabled(true);
                            }
                        });


                    }
                });

            }
        });

        playRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    playRecord.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                }
                else{
                    mediaPlayer.start();
                    playRecord.setImageResource(R.drawable.ic_baseline_pause_24);
                    ChangeSeekBar();
                }

            }
        });

        closeRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                RecordLin.setVisibility(View.GONE);
                PlayOrPauseRecordBtn.setEnabled(true);
            }
        });

    }



    private void ChangeSeekBar() {
        SeekBar.setProgress(mediaPlayer.getCurrentPosition());
        if(mediaPlayer.isPlaying()){
            runnable = new Runnable() {
                @Override
                public void run() {
                    ChangeSeekBar();
                }
            };
            handler.postDelayed(runnable,1000);
        }
    }

    //**********************************************************************************************
    private void startRecording() {
        mFilePath = this.getExternalFilesDir("/").getAbsolutePath();
        mFileName = random()+".3gp";

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(mFilePath+"/"+mFileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e("Record_Log","prepare() failed");
        }

        recorder.start();
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;

        uploadRecord();
    }


    private void uploadRecord(){
       final  StorageReference FilePath = mStorageRef.child("message_records").child(random()+".3gp");
       Uri uri = Uri.fromFile(new File(mFilePath+"/"+mFileName));

        FilePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                FilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //Toast.makeText(FriendsChattingActivity.this,"save",Toast.LENGTH_SHORT).show();
                        sendImageOrRecord(uri,"Record");
                    }
                });
            }
        });

    }


    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }


//**********************************************************************************************



    private void markAllFriendMessagesAsSeen(){
        //mark all the sent friend's messages seen because me seen now
        DatabaseReference Root= FirebaseDatabase.getInstance().getReference();
        DatabaseReference a=Root.child("chats").child(UserId).child(CurrentUID);
        ValueEventListener EventListener= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot Snapshot : dataSnapshot.getChildren()){
                        if(Snapshot.child("Message").getValue().toString().substring(0,1).equals("S")) {
                            FirebaseDatabase.getInstance().getReference().child("chats").child(UserId).child(CurrentUID).child(Snapshot.getKey().toString()).child("Message State").setValue("3");
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        a.addListenerForSingleValueEvent(EventListener);

    }


    private void sendMessage(final String message_value){
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        DatabaseReference m = root.child("users").child(UserId);
        ValueEventListener eventListener = new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int x=0;
                    String stat = dataSnapshot.child("Seen").getValue().toString();
                    if(stat.equals("offline")) x=1;
                    else if(stat.equals("online")) x=2;
                    else if(stat.equals("seen")) x=3;

                    //send message
                    HashMap <String,String> SendHashMap= new HashMap<>();
                    SendHashMap.put("Message State",String.valueOf(x));
                    SendHashMap.put("Message","S"+message_value);
                    SendHashMap.put("Message Type","Message");
                    SendHashMap.put("Message Time",new SimpleDateFormat("hh:mm a").format(Calendar.getInstance().getTime()));


                    //receive message
                    HashMap <String,String> ReceiveHashMap= new HashMap<>();
                    ReceiveHashMap.put("Message State","null");
                    ReceiveHashMap.put("Message","R"+message_value);
                    ReceiveHashMap.put("Message Type","Message");
                    ReceiveHashMap.put("Message Time",new SimpleDateFormat("hh:mm a").format(Calendar.getInstance().getTime()));


                    FirebaseDatabase.getInstance().getReference().child("chats").child(CurrentUID).child(UserId).push().setValue(SendHashMap);
                    FirebaseDatabase.getInstance().getReference().child("chats").child(UserId).child(CurrentUID).push().setValue(ReceiveHashMap);

                    MessageEdit.setText("");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        m.addListenerForSingleValueEvent(eventListener);

    }



    private void ChangeInChatting(){

        DatabaseReference mFirebase= FirebaseDatabase.getInstance().getReference().child("chats").child(CurrentUID).child(UserId);
        mFirebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //check the state of the friend (offline or online or seen) to mark the message
                update_MessagesSeenStateInDatabase(dataSnapshot);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

    }


    private void update_MessagesSeenStateInDatabase(final DataSnapshot data){
        //mark all my sent messages (seen or online) applying to Friend state

        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        DatabaseReference m = root.child("users").child(UserId);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int x=0;
                    String stat = dataSnapshot.child("Seen").getValue().toString();
                    if(stat.equals("offline")) x=1;
                    else if(stat.equals("online")) x=2;
                    else if(stat.equals("seen")) x=3;

                    if(x==3){  // so the friend seen now
                        DatabaseReference root= FirebaseDatabase.getInstance().getReference();
                        DatabaseReference m=root.child("chats").child(CurrentUID).child(UserId);
                        ValueEventListener eventListener= new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    for(DataSnapshot Snapshot : dataSnapshot.getChildren()){
                                        if(Snapshot.child("Message").getValue().toString().substring(0,1).equals("S")) {
                                            FirebaseDatabase.getInstance().getReference().child("chats").child(CurrentUID).child(UserId).child(Snapshot.getKey().toString()).child("Message State").setValue("3");
                                        }
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {}
                        };
                        m.addListenerForSingleValueEvent(eventListener);
                    }

                    else if(x==2){  // so the friend online and not seen
                        DatabaseReference root= FirebaseDatabase.getInstance().getReference();
                        DatabaseReference m=root.child("chats").child(CurrentUID).child(UserId);
                        ValueEventListener eventListener= new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    for(DataSnapshot Snapshot : dataSnapshot.getChildren()){
                                        if(Snapshot.child("Message").getValue().toString().substring(0,1).equals("S") && !Snapshot.child("Message State").getValue().toString().equals("3")) {
                                            FirebaseDatabase.getInstance().getReference().child("chats").child(CurrentUID).child(UserId).child(Snapshot.getKey().toString()).child("Message State").setValue("2");
                                        }
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {}
                        };
                        m.addListenerForSingleValueEvent(eventListener);
                    }

                    SaveInAdapterAndDisplay(data);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        m.addListenerForSingleValueEvent(eventListener);


    }




    private void SaveInAdapterAndDisplay(DataSnapshot data){
        final MessageAdapter adapter=new MessageAdapter(this,chatting_arraylist);

        chatting_arraylist.clear();

        for(DataSnapshot Snapshot : data.getChildren()){

            if (Snapshot.child("Message").getValue().toString().substring(0,1).equals("S")) {
                if(Snapshot.child("Message Type").getValue().equals("Image")){
                    chatting_arraylist.add(new Message(Snapshot.child("Message").getValue().toString().substring(1, Snapshot.child("Message").getValue().toString().length()), " ", true,
                            Integer.valueOf(Snapshot.child("Message State").getValue().toString()),"Image",Snapshot.child("Message Time").getValue().toString()));
                }
                else if(Snapshot.child("Message Type").getValue().equals("Message")){
                    chatting_arraylist.add(new Message(Snapshot.child("Message").getValue().toString().substring(1, Snapshot.child("Message").getValue().toString().length()), " ", true,
                            Integer.valueOf(Snapshot.child("Message State").getValue().toString()),"Message",Snapshot.child("Message Time").getValue().toString()));
                }
                else if(Snapshot.child("Message Type").getValue().equals("Record")){
                    chatting_arraylist.add(new Message(Snapshot.child("Message").getValue().toString().substring(1, Snapshot.child("Message").getValue().toString().length()), " ", true,
                            Integer.valueOf(Snapshot.child("Message State").getValue().toString()),"Record",Snapshot.child("Message Time").getValue().toString()));
                }

            }

            else {
                if(Snapshot.child("Message Type").getValue().equals("Image")){
                    chatting_arraylist.add(new Message(" ", Snapshot.child("Message").getValue().toString().substring(1, Snapshot.child("Message").getValue().toString().length()), false
                            , 0,"Image",Snapshot.child("Message Time").getValue().toString()));
                }
                else if(Snapshot.child("Message Type").getValue().equals("Message")){
                    chatting_arraylist.add(new Message(" ", Snapshot.child("Message").getValue().toString().substring(1, Snapshot.child("Message").getValue().toString().length()), false
                            , 0,"Message",Snapshot.child("Message Time").getValue().toString()));
                }
                else if(Snapshot.child("Message Type").getValue().equals("Record")){
                    chatting_arraylist.add(new Message(" ", Snapshot.child("Message").getValue().toString().substring(1, Snapshot.child("Message").getValue().toString().length()), false
                            , 0,"Record",Snapshot.child("Message Time").getValue().toString()));
                }
            }
        }

        adapter.notifyDataSetChanged();

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

        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        DatabaseReference m = root.child("users").child(UserId);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UserOnLine = dataSnapshot.child("Online").getValue().toString();

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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        m.addListenerForSingleValueEvent(eventListener);

    }




    private void DeleteMessage(){
        //long click in any message
        chatting_listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Message message =chatting_arraylist.get(i);

                AlertDialog.Builder checkAlert = new AlertDialog.Builder(FriendsChattingActivity.this);
                checkAlert.setMessage("Delete this message for")
                        .setCancelable(true).setPositiveButton("Me", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //call delete For me method
                        DeleteForMe(message);
                    }
                }).setNegativeButton("Everyone", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(message.getSeenStateNum() == 0 ){
                            Toast.makeText(FriendsChattingActivity.this,"you can't delete this message for Everyone\nthat your friend sent it before",Toast.LENGTH_LONG).show();
                            dialog.cancel();
                        }
                        else if(message.getSeenStateNum() == 3 ){
                            Toast.makeText(FriendsChattingActivity.this,"you can't delete this message for Everyone\nyour friend seen this message",Toast.LENGTH_LONG).show();
                            dialog.cancel();
                        }
                        else {
                            //call delete For everyone method
                            DeleteForEveryOne(message);
                        }
                    }
                });
                AlertDialog alert = checkAlert.create();
                alert.setTitle("Delete message?");
                alert.show();
                return false;
            }
        });

    }


    private void DeleteForMe(final Message message){

        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        DatabaseReference m = root.child("chats").child(CurrentUID).child(UserId);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for( DataSnapshot Snapshot: dataSnapshot.getChildren()){
                        if(Snapshot.child("Message").getValue().equals("R"+message.getReceiverMessage()) || Snapshot.child("Message").getValue().equals("S"+message.getSenderMessage())){
                            //delete message
                            FirebaseDatabase.getInstance().getReference().child("chats").child(CurrentUID).child(UserId).child(Snapshot.getKey()).removeValue();
                            Toast.makeText(FriendsChattingActivity.this,"The message deleted just for you Successfully",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        m.addListenerForSingleValueEvent(eventListener);

    }


    private void DeleteForEveryOne(final Message message){

        DatabaseReference Root = FirebaseDatabase.getInstance().getReference();
        DatabaseReference x = Root.child("chats").child(UserId).child(CurrentUID);
        ValueEventListener EventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for( DataSnapshot Snapshot: dataSnapshot.getChildren()){
                        if(Snapshot.child("Message").getValue().equals("R"+message.getReceiverMessage()) ||
                                Snapshot.child("Message").getValue().equals("S"+message.getSenderMessage())
                                ||Snapshot.child("Message").getValue().equals("S"+message.getReceiverMessage()) ||
                                Snapshot.child("Message").getValue().equals("R"+message.getSenderMessage())){
                            //delete message
                            FirebaseDatabase.getInstance().getReference().child("chats").child(UserId).child(CurrentUID).child(Snapshot.getKey()).removeValue();
                            Toast.makeText(FriendsChattingActivity.this,"The message deleted for Every one Successfully",Toast.LENGTH_SHORT).show();
                            HelpDelete(message);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        x.addListenerForSingleValueEvent(EventListener);


    }


    private void HelpDelete(final Message message){
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        DatabaseReference m = root.child("chats").child(CurrentUID).child(UserId);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for( DataSnapshot Snapshot: dataSnapshot.getChildren()){
                        if(Snapshot.child("Message").getValue().equals("R"+message.getReceiverMessage()) ||
                                Snapshot.child("Message").getValue().equals("S"+message.getSenderMessage())
                                ||Snapshot.child("Message").getValue().equals("S"+message.getReceiverMessage()) ||
                                Snapshot.child("Message").getValue().equals("R"+message.getSenderMessage())){
                            //delete message
                            FirebaseDatabase.getInstance().getReference().child("chats").child(CurrentUID).child(UserId).child(Snapshot.getKey()).removeValue();

                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        m.addListenerForSingleValueEvent(eventListener);

    }



    //crop and send image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //to crop image
        if(requestCode== GALARY_PICK && resultCode==RESULT_OK){
            Uri IamgeUri=data.getData();
            CropImage.activity(IamgeUri)
                    .setAspectRatio(1,1)
                    .start(this);
        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                UploadImageInStorageDataBase(resultUri);
            }

            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }



    private void UploadImageInStorageDataBase(Uri resultUri){
        //upload image in storage database
        final StorageReference FilePath = mStorageRef.child("message_images").child(random()+"jpg");
        FilePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                FilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //save download url to image child
                        sendImageOrRecord(uri,"Image");
                    }
                });

            }
        });

    }


    private void sendImageOrRecord(final Uri uri,final String type){
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        DatabaseReference m = root.child("users").child(UserId);
        ValueEventListener eventListener = new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int x=0;
                    String stat = dataSnapshot.child("Seen").getValue().toString();
                    if(stat.equals("offline")) x=1;
                    else if(stat.equals("online")) x=2;
                    else if(stat.equals("seen")) x=3;

                    //save download url to image child
                    HashMap <String,String> SendImageHashMap= new HashMap<>();
                    SendImageHashMap.put("Message State",String.valueOf(x));
                    SendImageHashMap.put("Message","S"+uri.toString());
                    SendImageHashMap.put("Message Type",type);
                    SendImageHashMap.put("Message Time",new SimpleDateFormat("hh:mm a").format(Calendar.getInstance().getTime()));


                    HashMap <String,String> ReceiveImageHashMap= new HashMap<>();
                    ReceiveImageHashMap.put("Message State","null");
                    ReceiveImageHashMap.put("Message","R"+uri.toString());
                    ReceiveImageHashMap.put("Message Type",type);
                    ReceiveImageHashMap.put("Message Time",new SimpleDateFormat("hh:mm a").format(Calendar.getInstance().getTime()));

                    FirebaseDatabase.getInstance().getReference().child("chats").child(CurrentUID).child(UserId).push().setValue(SendImageHashMap);
                    FirebaseDatabase.getInstance().getReference().child("chats").child(UserId).child(CurrentUID).push().setValue(ReceiveImageHashMap);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        m.addListenerForSingleValueEvent(eventListener);

    }




    //return random string to sending image name in storage database
    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(10);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }


}