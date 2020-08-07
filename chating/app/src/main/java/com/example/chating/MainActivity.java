package com.example.chating;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private long backPressed;
    private Toolbar mToolBar;
    private FirebaseAuth mAuth;

    private ViewPager mViewPager;
    private SectionPagerAdapter mPagerAdapter;
    private TabLayout mTabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth= FirebaseAuth.getInstance();

        mToolBar= (Toolbar)findViewById(R.id.MainToolBar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Messageya");


        mViewPager=(ViewPager)findViewById(R.id.MainViewPager);

        mPagerAdapter= new SectionPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);

        mTabLayout=(TabLayout)findViewById(R.id.MainTabLayout);
        mTabLayout.setupWithViewPager(mViewPager);

    }



    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser==null){
            Intent intent = new Intent(MainActivity.this,WelcomeActivity.class);
            startActivity(intent);
            finish();
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

        if(id== R.id.contacts_AccountSetting_id){
            Intent intent = new Intent(MainActivity.this,AccountSettingActivity.class);
            startActivity(intent);
        }

        else if(id==R.id.contacts_Logout_id){
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this,WelcomeActivity.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }





    //************************************Refresh function *****************************************
    private void Refresh(){


    }
    //**********************************************************************************************

    @Override
    public void onBackPressed() {
        if(backPressed+2000 > System.currentTimeMillis()){
            super.onBackPressed();
            return;
        }
        else{
            Toast.makeText(MainActivity.this,"Next Tap To Exit",Toast.LENGTH_SHORT).show();
            Refresh();
        }
        backPressed=System.currentTimeMillis();
    }

}