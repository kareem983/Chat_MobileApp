package com.example.chating;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class RequestProfileActivity extends AppCompatActivity {
    private Toolbar mToolBar;
    private ProgressDialog mProgressDialog;

    private ImageView UserImageView;
    private TextView UserNameView;
    private TextView UserStatusView;
    private TextView UserTotalFriendsView;
    private Button ConfirmRequestBtn;
    private Button RejectRequestBtn;


    private String UserId;
    private String UserName;
    private String UserStatus;
    private String UserImage;
    private int numOfUsers;


    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String CurrentUId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_profile);
        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        CurrentUId=currentUser.getUid();

        //retrieve User data from the previous Activity
        UserId=getIntent().getStringExtra("User Id");
        UserName=getIntent().getStringExtra("User Name");
        UserStatus=getIntent().getStringExtra("User Status");
        UserImage=getIntent().getStringExtra("User Image");


        //tool bar
        mToolBar= (Toolbar)findViewById(R.id.RequestProfile_ToolBar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        numOfUsers=0;

        //define xml views
        UserImageView=(ImageView)findViewById(R.id.RequestProfileImage);
        UserNameView=(TextView)findViewById(R.id.RequestProfileName);
        UserStatusView=(TextView)findViewById(R.id.RequestProfileStatus);
        UserTotalFriendsView=(TextView)findViewById(R.id.RequestProfileTotalFriend);
        ConfirmRequestBtn = (Button)findViewById(R.id.RequestProfileConfirmRequestBtn);
        RejectRequestBtn = (Button)findViewById(R.id.RequestProfileRejectRequestBtn);

        //display user data
        UserNameView.setText(UserName);
        UserStatusView.setText(UserStatus);
        Picasso.get().load(UserImage).placeholder(R.drawable.userr).into(UserImageView);
        UpdateNumOfFriends();

        FirebaseDatabase.getInstance().getReference().child("requests").child("1").setValue("1");
        FirebaseDatabase.getInstance().getReference().child("friends").child("1").setValue("1");
        FirebaseDatabase.getInstance().getReference().child("chats").child("1").setValue("1");



        //buttons clicks
        ConfirmRequestBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                //display progress dialog
                mProgressDialog=new ProgressDialog(RequestProfileActivity.this);
                mProgressDialog.setTitle("Confirmation process");
                mProgressDialog.setMessage("Please wait while we add your new friend");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                //confirm the request
                FirebaseDatabase.getInstance().getReference().child("requests").child(CurrentUId).child(UserId).child("requestState").setValue("friends").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FirebaseDatabase.getInstance().getReference().child("requests").child(UserId).child(CurrentUId).child("requestState").setValue("friends").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(RequestProfileActivity.this, "You became Friends", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                });

                //add friends
                FirebaseDatabase.getInstance().getReference().child("friends").child(CurrentUId).child(UserId).setValue(new SimpleDateFormat("dd MMM yyyy  HH:mm a").format(Calendar.getInstance().getTime()));
                FirebaseDatabase.getInstance().getReference().child("friends").child(UserId).child(CurrentUId).setValue(new SimpleDateFormat("dd MMM yyyy  HH:mm a").format(Calendar.getInstance().getTime()));

                //add chats child
                //add chats child
                FirebaseDatabase.getInstance().getReference().child("chats").child(CurrentUId).child(UserId).setValue("");
                FirebaseDatabase.getInstance().getReference().child("chats").child(UserId).child(CurrentUId).setValue("");


            }
        });



        RejectRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //display progress dialog
                mProgressDialog = new ProgressDialog(RequestProfileActivity.this);
                mProgressDialog.setTitle("Rejection process");
                mProgressDialog.setMessage("Please wait while we reject the friend request");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                //delete the request
                FirebaseDatabase.getInstance().getReference().child("requests").child(CurrentUId).child(UserId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FirebaseDatabase.getInstance().getReference().child("requests").child(UserId).child(CurrentUId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mProgressDialog.dismiss();
                                Toast.makeText(RequestProfileActivity.this, "You rejected Friend Request", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                });
            }
        });



    }



    private void UpdateNumOfFriends(){
        //number of friends
        DatabaseReference Root=FirebaseDatabase.getInstance().getReference();
        DatabaseReference x=Root.child("friends").child(UserId);
        ValueEventListener EventListener= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot Snapshot: dataSnapshot.getChildren()){
                        numOfUsers++;}
                    if(numOfUsers==1)UserTotalFriendsView.setText(" "+String.valueOf(numOfUsers)+" friend");
                    else UserTotalFriendsView.setText(" "+String.valueOf(numOfUsers)+" friends");
                }
                else{
                    UserTotalFriendsView.setText(" 0 friends");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        x.addListenerForSingleValueEvent(EventListener);

    }

}