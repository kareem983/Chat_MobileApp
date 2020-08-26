package com.example.chatting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AllUsersActivity extends AppCompatActivity {
    private ArrayList <String>UsersId;
    private ArrayList <Users>UsersArrayList;
    private ListView usersListView;
    private Toolbar mToolBar;
    private DatabaseReference mDatabaseReference;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        final String UId=currentUser.getUid();

        //Tool bar
        mToolBar=(Toolbar)findViewById(R.id.AllUsers_Toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //listView
        usersListView= (ListView)findViewById(R.id.AllUsers_ListView_id);

        UsersId=new ArrayList<>();
        UsersArrayList=new ArrayList<>();

        final UsersAdapter adapter=new UsersAdapter(AllUsersActivity.this,UsersArrayList);
        usersListView.setAdapter(adapter);


        //save users IDs in Users ArrayList to enable me to take the user's data to display them
        DatabaseReference root=FirebaseDatabase.getInstance().getReference();
        DatabaseReference m=root.child("users");
        ValueEventListener eventListener= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    UsersId.clear();
                    for( DataSnapshot Snapshot: dataSnapshot.getChildren()){
                        //to not add my account to all users
                        if(!Snapshot.getKey().equals(UId)) UsersId.add(Snapshot.getKey().toString());
                    }
                    sentUserDataToArrayAdapter();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        m.addListenerForSingleValueEvent(eventListener);


        //if the user click to any user contact
        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Users User= UsersArrayList.get(i);
                Intent intent = new Intent(AllUsersActivity.this,UserProfileActivity.class);
                intent.putExtra("User Id",User.getUserId());
                intent.putExtra("User Name",User.getUserName());
                intent.putExtra("User Status",User.getUserStatus());
                intent.putExtra("User Image",User.getUserImage());
                startActivity(intent);
            }
        });

    }


    private void sentUserDataToArrayAdapter(){
        UsersArrayList.clear();
        final UsersAdapter adapter=new UsersAdapter(AllUsersActivity.this,UsersArrayList);

        for(int i=0;i<UsersId.size();i++){
            DatabaseReference root=FirebaseDatabase.getInstance().getReference();
            DatabaseReference m=root.child("users").child(UsersId.get(i));
            ValueEventListener eventListener= new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        String name = dataSnapshot.child("Name").getValue().toString();
                        String status = dataSnapshot.child("Status").getValue().toString();
                        String image = dataSnapshot.child("Image").getValue().toString();

                        UsersArrayList.add(new Users(name,status,image,dataSnapshot.getKey()));
                        adapter.notifyDataSetChanged();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            };
            m.addListenerForSingleValueEvent(eventListener);

        }
        usersListView.setAdapter(adapter);
    }


}