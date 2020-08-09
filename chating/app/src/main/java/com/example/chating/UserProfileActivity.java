package com.example.chating;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

public class UserProfileActivity extends AppCompatActivity {
    private Toolbar mToolBar;
    private ImageView UserImageView;
    private TextView UserNameView;
    private TextView UserStatusView;
    private TextView UserTotalFriendsView;
    private ImageView UserProfileYouAreFriend;
    private ImageView UserProfileStar;
    private Button SendRequestBtn;
    private Button CancelRequestBtn;
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
    private DatabaseReference mDatabaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();

        CurrentUId=currentUser.getUid();

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
        UserProfileYouAreFriend=(ImageView) findViewById(R.id.UserProfileYouAreFriend);
        UserProfileStar=(ImageView) findViewById(R.id.UserProfileStar);
        SendRequestBtn = (Button)findViewById(R.id.UserProfileSendRequestBtn);
        CancelRequestBtn = (Button)findViewById(R.id.UserProfileCancelRequestBtn);
        ConfirmRequestBtn = (Button)findViewById(R.id.UserProfileConfirmRequestBtn);
        RejectRequestBtn = (Button)findViewById(R.id.UserProfileRejectRequestBtn);


        //retrieve User data from the previous Activity
        UserId=getIntent().getStringExtra("User Id");
        UserName=getIntent().getStringExtra("User Name");
        UserStatus=getIntent().getStringExtra("User Status");
        UserImage=getIntent().getStringExtra("User Image");

        //display user data
        UserNameView.setText(UserName);
        UserStatusView.setText(UserStatus);
        Picasso.get().load(UserImage).placeholder(R.drawable.userr).into(UserImageView);





        //buttons state
        SendRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendRequestBtn.setVisibility(View.INVISIBLE);
                CancelRequestBtn.setVisibility(View.VISIBLE);
                ConfirmRequestBtn.setVisibility(View.INVISIBLE);
                RejectRequestBtn.setVisibility(View.INVISIBLE);
                UserProfileYouAreFriend.setVisibility(View.INVISIBLE);
                UserProfileStar.setVisibility(View.INVISIBLE);

                mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("requests").child(UserId).child(CurrentUId);
                mDatabaseReference.child("requestState").setValue("received");
                mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("requests").child(CurrentUId).child(UserId);
                mDatabaseReference.child("requestState").setValue("sent");


                Toast.makeText(UserProfileActivity.this,"You sent Friend Request",Toast.LENGTH_SHORT).show();
            }
        });

        CancelRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendRequestBtn.setVisibility(View.VISIBLE);
                CancelRequestBtn.setVisibility(View.INVISIBLE);
                ConfirmRequestBtn.setVisibility(View.INVISIBLE);
                RejectRequestBtn.setVisibility(View.INVISIBLE);
                UserProfileYouAreFriend.setVisibility(View.INVISIBLE);
                UserProfileStar.setVisibility(View.INVISIBLE);

                //delete the request
                FirebaseDatabase.getInstance().getReference().child("requests").child(CurrentUId).child(UserId).removeValue();
                FirebaseDatabase.getInstance().getReference().child("requests").child(UserId).child(CurrentUId).removeValue();



                Toast.makeText(UserProfileActivity.this,"You canceled Friend Request",Toast.LENGTH_SHORT).show();

            }
        });


        ConfirmRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendRequestBtn.setVisibility(View.INVISIBLE);
                CancelRequestBtn.setVisibility(View.INVISIBLE);
                ConfirmRequestBtn.setVisibility(View.INVISIBLE);
                RejectRequestBtn.setVisibility(View.INVISIBLE);
                UserProfileYouAreFriend.setVisibility(View.VISIBLE);
                UserProfileStar.setVisibility(View.VISIBLE);

                mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("requests").child(CurrentUId).child(UserId);
                mDatabaseReference.child("requestState").setValue("friends");
                mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("requests").child(UserId).child(CurrentUId);
                mDatabaseReference.child("requestState").setValue("friends");

                Toast.makeText(UserProfileActivity.this,"You became Friends",Toast.LENGTH_SHORT).show();


                //add the two users to friends child
                mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("friends").child(CurrentUId).child(UserId);
                mDatabaseReference.setValue("Friends");
                mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("friends").child(UserId).child(CurrentUId);
                mDatabaseReference.setValue("Friends");
                onStart();
            }
        });


        RejectRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendRequestBtn.setVisibility(View.VISIBLE);
                CancelRequestBtn.setVisibility(View.INVISIBLE);
                ConfirmRequestBtn.setVisibility(View.INVISIBLE);
                RejectRequestBtn.setVisibility(View.INVISIBLE);
                UserProfileYouAreFriend.setVisibility(View.INVISIBLE);
                UserProfileStar.setVisibility(View.INVISIBLE);

                //delete the request
                FirebaseDatabase.getInstance().getReference().child("requests").child(CurrentUId).child(UserId).removeValue();
                FirebaseDatabase.getInstance().getReference().child("requests").child(UserId).child(CurrentUId).removeValue();

                Toast.makeText(UserProfileActivity.this,"You rejected Friend Request",Toast.LENGTH_SHORT).show();

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
                    CancelRequestBtn.setVisibility(View.INVISIBLE);
                    ConfirmRequestBtn.setVisibility(View.INVISIBLE);
                    RejectRequestBtn.setVisibility(View.INVISIBLE);
                    UserProfileYouAreFriend.setVisibility(View.INVISIBLE);
                    UserProfileStar.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        m.addListenerForSingleValueEvent(eventListener);
        //******************************************************************************************


        //******************************************************************************************
        //number of friends
        DatabaseReference Root=FirebaseDatabase.getInstance().getReference();
        DatabaseReference x=Root.child("friends").child(UserId);
        ValueEventListener EventListener= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                  for(DataSnapshot Snapshot: dataSnapshot.getChildren()){
                      numOfUsers++;}
                  if(numOfUsers==1)UserTotalFriendsView.setText("Total friends: "+String.valueOf(numOfUsers)+" friend");
                   else UserTotalFriendsView.setText("Total friends: "+String.valueOf(numOfUsers)+" friends");
                }
                else{
                   UserTotalFriendsView.setText("Total friends: "+String.valueOf(numOfUsers)+" friends");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        x.addListenerForSingleValueEvent(EventListener);
        //******************************************************************************************

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
                        SendRequestBtn.setVisibility(View.INVISIBLE);
                        CancelRequestBtn.setVisibility(View.VISIBLE);
                        ConfirmRequestBtn.setVisibility(View.INVISIBLE);
                        RejectRequestBtn.setVisibility(View.INVISIBLE);
                        UserProfileYouAreFriend.setVisibility(View.INVISIBLE);
                        UserProfileStar.setVisibility(View.INVISIBLE);
                    }
                    else if(UserState.equals("received")){
                        SendRequestBtn.setVisibility(View.INVISIBLE);
                        CancelRequestBtn.setVisibility(View.INVISIBLE);
                        ConfirmRequestBtn.setVisibility(View.VISIBLE);
                        RejectRequestBtn.setVisibility(View.VISIBLE);
                        UserProfileYouAreFriend.setVisibility(View.INVISIBLE);
                        UserProfileStar.setVisibility(View.INVISIBLE);
                    }
                    else if(UserState.equals("friends")){
                        SendRequestBtn.setVisibility(View.INVISIBLE);
                        CancelRequestBtn.setVisibility(View.INVISIBLE);
                        ConfirmRequestBtn.setVisibility(View.INVISIBLE);
                        RejectRequestBtn.setVisibility(View.INVISIBLE);
                        UserProfileYouAreFriend.setVisibility(View.VISIBLE);
                        UserProfileStar.setVisibility(View.VISIBLE);
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        m.addListenerForSingleValueEvent(eventListener);

    }


}