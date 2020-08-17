package com.example.chating;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private long backPressed;
    private Toolbar mToolBar;
    private FirebaseAuth mAuth;

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private SectionPagerAdapter mPagerAdapter;
    public static Activity fa;

    private ArrayList<String> Users;


    public static boolean isFinished;
    private String UserId;
    private String CurrentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fa=this;
        mAuth= FirebaseAuth.getInstance();

        mToolBar= (Toolbar)findViewById(R.id.MainToolBar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Messageya");


        mTabLayout=(TabLayout)findViewById(R.id.MainTabLayout);
        mViewPager=(ViewPager)findViewById(R.id.MainViewPager);

        mPagerAdapter= new SectionPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        Users=new ArrayList<>();

    }



    @Override
    public void onStart() {
        super.onStart();

        isFinished=true;
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser==null){
            Intent intent = new Intent(MainActivity.this,WelcomeActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            if(isFinished) {
                AllUsersChats();
            }
            String CurrentUID = currentUser.getUid();
            FirebaseDatabase.getInstance().getReference().child("users").child(CurrentUID).child("Online").setValue("true");
            FirebaseDatabase.getInstance().getReference().child("users").child(CurrentUID).child("Seen").setValue("online");
        }

    }


    @Override
    protected void onStop() {
        super.onStop();

        if(isFinished) {
            FirebaseAuth Auth= FirebaseAuth.getInstance();
            FirebaseUser currentUser = Auth.getCurrentUser();
            if (currentUser != null) {
                String CurrentUID = currentUser.getUid();
                FirebaseDatabase.getInstance().getReference().child("users").child(CurrentUID).child("Online").setValue(ServerValue.TIMESTAMP);
                FirebaseDatabase.getInstance().getReference().child("users").child(CurrentUID).child("Seen").setValue("offline");
            }
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_options,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();

        if(id == R.id.AccountSetting_id){
            Intent intent = new Intent(MainActivity.this,AccountSettingActivity.class);
            startActivity(intent);
        }

        else if(id == R.id.AllUsers_id){
            Intent intent = new Intent(MainActivity.this,AllUsersActivity.class);
            startActivity(intent);
        }

        else if(id == R.id.DeleteAccount_id){
            Intent intent = new Intent(MainActivity.this,DeleteAccountActivity.class);
            startActivity(intent);
        }

        else if(id == R.id.Logout_id){
            CheckIfLogOutOrNot();
        }

        return super.onOptionsItemSelected(item);
    }




    private void CheckIfLogOutOrNot(){
        //create the AlertDialog then check the user choose yes or no
        AlertDialog.Builder checkAlert = new AlertDialog.Builder(MainActivity.this);
        checkAlert.setMessage("Do you want to Log out?")
                .setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                String CurrentUID = currentUser.getUid();
                FirebaseDatabase.getInstance().getReference().child("users").child(CurrentUID).child("Online").setValue(ServerValue.TIMESTAMP);
                FirebaseDatabase.getInstance().getReference().child("users").child(CurrentUID).child("Seen").setValue("offline");

                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this,WelcomeActivity.class);
                startActivity(intent);
                finish();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alert = checkAlert.create();
        alert.setTitle("Log Out");
        alert.show();

    }


    @Override
    public void onBackPressed() {
        if(backPressed+2000 > System.currentTimeMillis()){
            super.onBackPressed();
            return;
        }
        else{
            Toast.makeText(MainActivity.this,"Next Tap To Exit",Toast.LENGTH_SHORT).show();
        }
        backPressed=System.currentTimeMillis();
    }






    private void AllUsersChats(){
        Users.clear();
        DatabaseReference root= FirebaseDatabase.getInstance().getReference();
        DatabaseReference m=root.child("chats");
        ValueEventListener eventListener= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot Snapshot : dataSnapshot.getChildren()){
                        Users.add(Snapshot.getKey());
                    }
                    markMessagesOnlineState();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        m.addListenerForSingleValueEvent(eventListener);


    }


    private void markMessagesOnlineState(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        CurrentUserId= currentUser.getUid();


        //mark all the sent friends' messages online because me online now
        for(int i=0 ; i<Users.size();i++) {
            UserId = Users.get(i);
            DatabaseReference root = FirebaseDatabase.getInstance().getReference();
            DatabaseReference m = root.child("chats").child(UserId).child(CurrentUserId);
            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot Snapshot : dataSnapshot.getChildren()) {
                            if (Snapshot.child("Message").getValue().toString().substring(0, 1).equals("S") && !Snapshot.child("Message State").getValue().toString().equals("3")) {
                                FirebaseDatabase.getInstance().getReference().child("chats").child(UserId).child(CurrentUserId).child(Snapshot.getKey().toString()).child("Message State").setValue("2");
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
    }


}