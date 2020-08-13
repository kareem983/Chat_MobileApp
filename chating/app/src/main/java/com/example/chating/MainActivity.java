package com.example.chating;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {
    private long backPressed;
    private Toolbar mToolBar;
    private FirebaseAuth mAuth;

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private SectionPagerAdapter mPagerAdapter;


    public static boolean isFinished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth= FirebaseAuth.getInstance();

        mToolBar= (Toolbar)findViewById(R.id.MainToolBar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Messageya");


        mViewPager=(ViewPager)findViewById(R.id.MainViewPager);
        mTabLayout=(TabLayout)findViewById(R.id.MainTabLayout);

        mPagerAdapter= new SectionPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);


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
            String CurrentUID = currentUser.getUid();
            FirebaseDatabase.getInstance().getReference().child("users").child(CurrentUID).child("Online").setValue("true");
            FirebaseDatabase.getInstance().getReference().child("users").child(CurrentUID).child("Seen").setValue("online");
        }

    }




    @Override
    protected void onStop() {
        super.onStop();

        if(isFinished) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
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

}