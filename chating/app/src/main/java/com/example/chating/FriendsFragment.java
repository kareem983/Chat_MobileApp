package com.example.chating;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FriendsFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private View mMainView;
    private String mParam1;
    private String mParam2;

    //my variables
    private ArrayList<String> UsersId;
    private ArrayList <Users>UsersArrayList;
    private ArrayList <String>FriendsDates;

    private ListView UserFriendsListView;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String CurrentUId;



    public FriendsFragment() {
        // Required empty public constructor
    }
    public static FriendsFragment newInstance(String param1, String param2) {
        FriendsFragment fragment = new FriendsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView= inflater.inflate(R.layout.fragment_friends, container, false);

        //define list view
        UserFriendsListView=(ListView)mMainView.findViewById(R.id.UserFriends_ListView_id);



        return mMainView;
    }


    @Override
    public void onStart() {
        super.onStart();

        //firebase
        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        CurrentUId=currentUser.getUid();

        //define array lists
        UsersId=new ArrayList<>();
        UsersArrayList=new ArrayList<>();
        FriendsDates=new ArrayList<>();

        //check if the current user have requests or not
        DatabaseReference root= FirebaseDatabase.getInstance().getReference();
        DatabaseReference m=root.child("friends").child(CurrentUId);
        ValueEventListener eventListener= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    checkTheFriends();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        m.addListenerForSingleValueEvent(eventListener);
    }


    private void checkTheFriends(){
        UsersId.clear();
        FriendsDates.clear();
        mDatabaseReference= FirebaseDatabase.getInstance().getReference().child("friends").child(CurrentUId);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for( DataSnapshot Snapshot: snapshot.getChildren()){
                        UsersId.add(Snapshot.getKey().toString());
                        FriendsDates.add(Snapshot.getValue().toString());
                }
                sentUserDataToArrayAdapter();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }


    private void sentUserDataToArrayAdapter(){
        UsersArrayList.clear();
        final UsersAdapter adapter=new UsersAdapter(getActivity(),UsersArrayList);

        for(int i=0;i<UsersId.size();i++){
            final String FriendshipDate = FriendsDates.get(i);
            mDatabaseReference= FirebaseDatabase.getInstance().getReference().child("users").child(UsersId.get(i));
            mDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String name = snapshot.child("Name").getValue().toString();
                    String image = snapshot.child("Image").getValue().toString();

                    UsersArrayList.add(new Users(name,FriendshipDate,image,snapshot.getKey()));
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });

        }
        UserFriendsListView.setAdapter(adapter);

    }



}