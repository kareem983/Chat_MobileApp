package com.example.chating;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

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
    private ImageButton AddImageBtn;
    private EditText MessageEdit;
    private ArrayList<Message> chatting_arraylist;
    private int UserSeenNum;
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
        AddImageBtn=(ImageButton)findViewById(R.id.AddImageBtn);
        MessageEdit = (EditText)findViewById(R.id.messageEdit);

        chatting_arraylist =new ArrayList<>();
        final MessageAdapter adapter=new MessageAdapter(this,chatting_arraylist);
        chatting_listView.setAdapter(adapter);

        //if the user wanted to delete message
        DeleteMessage();

        //check if the user online or not
        checkUserState();



        AddImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"SELECT IMAGE"),GALARY_PICK);

            }
        });


        //on click to send button
        SendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message_value=MessageEdit.getText().toString();
                if(message_value.isEmpty()) Toast.makeText(FriendsChattingActivity.this,"The message is empty",Toast.LENGTH_SHORT).show();
                else{
                    //send message
                    HashMap <String,String> SendHashMap= new HashMap<>();
                    SendHashMap.put("Message State",String.valueOf(UserSeenNum));
                    SendHashMap.put("Message","S"+message_value);
                    SendHashMap.put("Message Type","Message");


                    //receive message
                    HashMap <String,String> ReceiveHashMap= new HashMap<>();
                    ReceiveHashMap.put("Message State","null");
                    ReceiveHashMap.put("Message","R"+message_value);
                    ReceiveHashMap.put("Message Type","Message");

                    FirebaseDatabase.getInstance().getReference().child("chats").child(CurrentUID).child(UserId).push().setValue(SendHashMap);
                    FirebaseDatabase.getInstance().getReference().child("chats").child(UserId).child(CurrentUID).push().setValue(ReceiveHashMap);

                    MessageEdit.setText("");


                }
            }
        });



    }


    @Override
    protected void onStart() {
        super.onStart();



    }

    private void checkUserState(){
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        DatabaseReference m = root.child("users").child(UserId);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String stat = dataSnapshot.child("Seen").getValue().toString();
                    if(stat.equals("offline")) UserSeenNum=1;
                    else if(stat.equals("online")) UserSeenNum=2;
                    else if(stat.equals("seen")) UserSeenNum=3;
                    SaveInAdapter();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        m.addListenerForSingleValueEvent(eventListener);

    }



    private void SaveInAdapter(){
        final MessageAdapter adapter=new MessageAdapter(this,chatting_arraylist);

        DatabaseReference mFirebase= FirebaseDatabase.getInstance().getReference().child("chats").child(CurrentUID).child(UserId);
        mFirebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                update_MessagesSeenStateInDatabase();
                chatting_arraylist.clear();
                for(DataSnapshot Snapshot : dataSnapshot.getChildren()){

                    if (Snapshot.child("Message").getValue().toString().substring(0,1).equals("S")) {
                        if(Snapshot.child("Message Type").getValue().equals("Image")){
                            chatting_arraylist.add(new Message(Snapshot.child("Message").getValue().toString().substring(1, Snapshot.child("Message").getValue().toString().length()), " ", "S",
                                    Integer.valueOf(Snapshot.child("Message State").getValue().toString()),true));
                        }
                        else {
                            chatting_arraylist.add(new Message(Snapshot.child("Message").getValue().toString().substring(1, Snapshot.child("Message").getValue().toString().length()), " ", "S",
                                    Integer.valueOf(Snapshot.child("Message State").getValue().toString()),false));
                        }
                    }

                    else {
                        if(Snapshot.child("Message Type").getValue().equals("Image")){
                            chatting_arraylist.add(new Message(" ", Snapshot.child("Message").getValue().toString().substring(1, Snapshot.child("Message").getValue().toString().length()), "R", 0,true));
                        }
                        else {
                            chatting_arraylist.add(new Message(" ", Snapshot.child("Message").getValue().toString().substring(1, Snapshot.child("Message").getValue().toString().length()), "R", 0,false));
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        chatting_listView.setAdapter(adapter);


    }



    private void update_MessagesSeenStateInDatabase(){
        //mark all my sent messages (seen or online) applying to Friend state

        if(UserSeenNum==3){  // so the friend seen now
            DatabaseReference root= FirebaseDatabase.getInstance().getReference();
            DatabaseReference m=root.child("chats").child(CurrentUID).child(UserId);
            ValueEventListener eventListener= new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot Snapshot : dataSnapshot.getChildren()){
                            if(Snapshot.child("Message").getValue().toString().substring(0,1).equals("S")) {
                                //Toast.makeText(FriendsChattingActivity.this,Snapshot.getKey().toString(),Toast.LENGTH_SHORT).show();
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

        else if(UserSeenNum==2){  // so the friend online and not seen
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


        //mark all the sent friend's messages seen because me seen now
        DatabaseReference root= FirebaseDatabase.getInstance().getReference();
        DatabaseReference m=root.child("chats").child(UserId).child(CurrentUID);
        ValueEventListener eventListener= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot Snapshot : dataSnapshot.getChildren()){
                        if(Snapshot.child("Message").getValue().toString().substring(0,1).equals("S")) {
                            //Toast.makeText(FriendsChattingActivity.this,Snapshot.getKey().toString(),Toast.LENGTH_SHORT).show();
                            FirebaseDatabase.getInstance().getReference().child("chats").child(UserId).child(CurrentUID).child(Snapshot.getKey().toString()).child("Message State").setValue("3");
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        m.addListenerForSingleValueEvent(eventListener);



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
                        if(UserSeenNum==3){
                            Toast.makeText(FriendsChattingActivity.this,"you can't delete this message\nyour friend seen this message",Toast.LENGTH_LONG).show();
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
                            Toast.makeText(FriendsChattingActivity.this,"The message deleted for Every one Successfully",Toast.LENGTH_SHORT).show();
                            HelpDelete(message);
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


    private void HelpDelete(final Message message){
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
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        x.addListenerForSingleValueEvent(EventListener);



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
                        HashMap <String,String> SendImageHashMap= new HashMap<>();
                        SendImageHashMap.put("Message State",String.valueOf(UserSeenNum));
                        SendImageHashMap.put("Message","S"+uri.toString());
                        SendImageHashMap.put("Message Type","Image");

                        HashMap <String,String> ReceiveImageHashMap= new HashMap<>();
                        ReceiveImageHashMap.put("Message State","null");
                        ReceiveImageHashMap.put("Message","R"+uri.toString());
                        ReceiveImageHashMap.put("Message Type","Image");

                        FirebaseDatabase.getInstance().getReference().child("chats").child(CurrentUID).child(UserId).push().setValue(SendImageHashMap);
                        FirebaseDatabase.getInstance().getReference().child("chats").child(UserId).child(CurrentUID).push().setValue(ReceiveImageHashMap);

                    }
                });

            }
        });

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