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
import java.util.Date;

public class UserProfileActivity extends AppCompatActivity {
    private Toolbar mToolBar;
    private ProgressDialog mProgressDialog;

    private ImageView UserImageView;
    private TextView UserNameView;
    private TextView UserStatusView;
    private TextView UserTotalFriendsView;
    private ImageView UserProfileStar;
    private Button SendRequestBtn;
    private Button CancelRequestBtn;
    private Button ConfirmRequestBtn;
    private Button RejectRequestBtn;
    private Button UnFriendPerson;

    private String UserId;
    private String UserName;
    private String UserStatus;
    private String UserImage;
    private int numOfUsers;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String CurrentUId;
    private DatabaseReference mDatabaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        CurrentUId=currentUser.getUid();

        //retrieve User data from the previous Activity
        UserId=getIntent().getStringExtra("User Id");
        UserName=getIntent().getStringExtra("User Name");
        UserStatus=getIntent().getStringExtra("User Status");
        UserImage=getIntent().getStringExtra("User Image");


        //tool bar
        mToolBar= (Toolbar)findViewById(R.id.UserProfile_ToolBar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        numOfUsers=0;

        //define xml views
        UserImageView=(ImageView)findViewById(R.id.UserProfileImage);
        UserNameView=(TextView)findViewById(R.id.UserProfileName);
        UserStatusView=(TextView)findViewById(R.id.UserProfileStatus);
        UserTotalFriendsView=(TextView)findViewById(R.id.UserProfileTotalFriend);
        UserProfileStar=(ImageView) findViewById(R.id.UserProfileStar);
        SendRequestBtn = (Button)findViewById(R.id.UserProfileSendRequestBtn);
        CancelRequestBtn = (Button)findViewById(R.id.UserProfileCancelRequestBtn);
        ConfirmRequestBtn = (Button)findViewById(R.id.UserProfileConfirmRequestBtn);
        RejectRequestBtn = (Button)findViewById(R.id.UserProfileRejectRequestBtn);
        UnFriendPerson=(Button)findViewById(R.id.UserProfileUnFriendRequestBtn);


        //display user data
        UserNameView.setText(UserName);
        UserStatusView.setText(UserStatus);
        Picasso.get().load(UserImage).placeholder(R.drawable.userr).into(UserImageView);

        FirebaseDatabase.getInstance().getReference().child("requests").child("1").setValue("1");
        FirebaseDatabase.getInstance().getReference().child("friends").child("1").setValue("1");
        FirebaseDatabase.getInstance().getReference().child("chats").child("1").setValue("1");



        //buttons state
        SendRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendRequestBtn.setVisibility(View.GONE);
                CancelRequestBtn.setVisibility(View.VISIBLE);
                ConfirmRequestBtn.setVisibility(View.GONE);
                RejectRequestBtn.setVisibility(View.GONE);
                UserProfileStar.setVisibility(View.GONE);
                UnFriendPerson.setVisibility(View.GONE);

                FirebaseDatabase.getInstance().getReference().child("requests").child(UserId).child(CurrentUId).child("requestState").setValue("received");
                FirebaseDatabase.getInstance().getReference().child("requests").child(CurrentUId).child(UserId).child("requestState").setValue("sent");

                Toast.makeText(UserProfileActivity.this,"You sent Friend Request",Toast.LENGTH_SHORT).show();
            }
        });

        CancelRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendRequestBtn.setVisibility(View.VISIBLE);
                CancelRequestBtn.setVisibility(View.GONE);
                ConfirmRequestBtn.setVisibility(View.GONE);
                RejectRequestBtn.setVisibility(View.GONE);
                UserProfileStar.setVisibility(View.GONE);
                UnFriendPerson.setVisibility(View.GONE);

                //delete the request
                FirebaseDatabase.getInstance().getReference().child("requests").child(CurrentUId).child(UserId).removeValue();
                FirebaseDatabase.getInstance().getReference().child("requests").child(UserId).child(CurrentUId).removeValue();

                Toast.makeText(UserProfileActivity.this,"You canceled Friend Request",Toast.LENGTH_SHORT).show();

            }
        });




        ConfirmRequestBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                SendRequestBtn.setVisibility(View.GONE);
                CancelRequestBtn.setVisibility(View.GONE);
                ConfirmRequestBtn.setVisibility(View.GONE);
                RejectRequestBtn.setVisibility(View.GONE);
                UserProfileStar.setVisibility(View.VISIBLE);
                UnFriendPerson.setVisibility(View.VISIBLE);

                //display progress dialog
                mProgressDialog=new ProgressDialog(UserProfileActivity.this);
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
                                mProgressDialog.dismiss();
                                Toast.makeText(UserProfileActivity.this,"You became Friends",Toast.LENGTH_SHORT).show();
                                UpdateNumOfFriends();
                            }
                        });
                    }
                });

                //add friends
                FirebaseDatabase.getInstance().getReference().child("friends").child(CurrentUId).child(UserId).setValue(new SimpleDateFormat("dd MMM yyyy HH:mm a").format(Calendar.getInstance().getTime()));
                FirebaseDatabase.getInstance().getReference().child("friends").child(UserId).child(CurrentUId).setValue(new SimpleDateFormat("dd MMM yyyy HH:mm a").format(Calendar.getInstance().getTime()));

                //add chats child
                FirebaseDatabase.getInstance().getReference().child("chats").child(CurrentUId).child(UserId).setValue("");
                FirebaseDatabase.getInstance().getReference().child("chats").child(UserId).child(CurrentUId).setValue("");




            }
        });


        RejectRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendRequestBtn.setVisibility(View.VISIBLE);
                CancelRequestBtn.setVisibility(View.GONE);
                ConfirmRequestBtn.setVisibility(View.GONE);
                RejectRequestBtn.setVisibility(View.GONE);
                UserProfileStar.setVisibility(View.GONE);
                UnFriendPerson.setVisibility(View.GONE);

                //display progress dialog
                mProgressDialog=new ProgressDialog(UserProfileActivity.this);
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
                                Toast.makeText(UserProfileActivity.this,"You rejected Friend Request",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });


            }
        });


        UnFriendPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendRequestBtn.setVisibility(View.VISIBLE);
                CancelRequestBtn.setVisibility(View.GONE);
                ConfirmRequestBtn.setVisibility(View.GONE);
                RejectRequestBtn.setVisibility(View.GONE);
                UserProfileStar.setVisibility(View.GONE);
                UnFriendPerson.setVisibility(View.GONE);

                //display progress dialog
                mProgressDialog=new ProgressDialog(UserProfileActivity.this);
                mProgressDialog.setTitle("Deleting Friendship");
                mProgressDialog.setMessage("Please wait while we delete the friendship");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                //delete the two requests
                FirebaseDatabase.getInstance().getReference().child("requests").child(CurrentUId).child(UserId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FirebaseDatabase.getInstance().getReference().child("requests").child(UserId).child(CurrentUId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //delete the two friends
                                FirebaseDatabase.getInstance().getReference().child("friends").child(CurrentUId).child(UserId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        FirebaseDatabase.getInstance().getReference().child("friends").child(UserId).child(CurrentUId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mProgressDialog.dismiss();
                                                Toast.makeText(UserProfileActivity.this,"You UnFriend this person",Toast.LENGTH_SHORT).show();
                                                UpdateNumOfFriends();
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });

                //delete chats child
                FirebaseDatabase.getInstance().getReference().child("chats").child(CurrentUId).child(UserId).removeValue();
                FirebaseDatabase.getInstance().getReference().child("chats").child(UserId).child(CurrentUId).removeValue();

            }
        });

    }



    @Override
    protected void onStart() {
        super.onStart();

        //******************************************************************************************
        //check if the user have the request before or not
        DatabaseReference root=FirebaseDatabase.getInstance().getReference();
        DatabaseReference m=root.child("requests").child(CurrentUId).child(UserId);
        ValueEventListener eventListener= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    checkTheUserState();
                }
                else{
                    SendRequestBtn.setVisibility(View.VISIBLE);
                    CancelRequestBtn.setVisibility(View.GONE);
                    ConfirmRequestBtn.setVisibility(View.GONE);
                    RejectRequestBtn.setVisibility(View.GONE);
                    UserProfileStar.setVisibility(View.GONE);
                    UnFriendPerson.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        m.addListenerForSingleValueEvent(eventListener);
        //******************************************************************************************


        UpdateNumOfFriends();

    }




    private void checkTheUserState(){
        DatabaseReference root=FirebaseDatabase.getInstance().getReference();
        DatabaseReference m=root.child("requests").child(CurrentUId).child(UserId).child("requestState");
        ValueEventListener eventListener= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String UserState= dataSnapshot.getValue().toString();
                    if(UserState.equals("sent")){
                        SendRequestBtn.setVisibility(View.GONE);
                        CancelRequestBtn.setVisibility(View.VISIBLE);
                        ConfirmRequestBtn.setVisibility(View.GONE);
                        RejectRequestBtn.setVisibility(View.GONE);
                        UserProfileStar.setVisibility(View.GONE);
                        UnFriendPerson.setVisibility(View.GONE);
                    }
                    else if(UserState.equals("received")){
                        SendRequestBtn.setVisibility(View.GONE);
                        CancelRequestBtn.setVisibility(View.GONE);
                        ConfirmRequestBtn.setVisibility(View.VISIBLE);
                        RejectRequestBtn.setVisibility(View.VISIBLE);
                        UserProfileStar.setVisibility(View.GONE);
                        UnFriendPerson.setVisibility(View.GONE);
                    }
                    else if(UserState.equals("friends")){
                        SendRequestBtn.setVisibility(View.GONE);
                        CancelRequestBtn.setVisibility(View.GONE);
                        ConfirmRequestBtn.setVisibility(View.GONE);
                        RejectRequestBtn.setVisibility(View.GONE);
                        UserProfileStar.setVisibility(View.VISIBLE);
                        UnFriendPerson.setVisibility(View.VISIBLE);
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        m.addListenerForSingleValueEvent(eventListener);

    }



    private void UpdateNumOfFriends(){
        //number of friends
        DatabaseReference Root=FirebaseDatabase.getInstance().getReference();
        DatabaseReference x=Root.child("friends").child(UserId);
        ValueEventListener EventListener= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot Snapshot: dataSnapshot.getChildren()){numOfUsers++;}

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