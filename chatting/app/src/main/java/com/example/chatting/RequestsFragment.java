package com.example.chatting;
 
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class RequestsFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private View mMainView;
    private String mParam1;
    private String mParam2;

    //my variables
    private ArrayList <String>UsersId;
    private ArrayList <Users>UsersArrayList;
    private ListView UserRequestListView;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String CurrentUId;


    public RequestsFragment() {
        // Required empty public constructor
    }
    public static RequestsFragment newInstance(String param1, String param2) {
        RequestsFragment fragment = new RequestsFragment();
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
        mMainView = inflater.inflate(R.layout.fragment_requests, container, false);

        //define xml component
        UserRequestListView=(ListView)mMainView.findViewById(R.id.UserRequest_ListView_id);

        SomeProcess();

        return mMainView;
    }


   private void SomeProcess(){

       //firebase
       mAuth=FirebaseAuth.getInstance();
       currentUser=mAuth.getCurrentUser();
       CurrentUId=currentUser.getUid();

       //define array lists
       UsersId=new ArrayList<>();
       UsersArrayList=new ArrayList<>();


       //if the user click to any user contact
       UserRequestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               Users User= UsersArrayList.get(i);
               Intent intent = new Intent(getActivity(),RequestProfileActivity.class);
               intent.putExtra("User Id",User.getUserId());
               intent.putExtra("User Name",User.getUserName());
               intent.putExtra("User Status",User.getUserStatus());
               intent.putExtra("User Image",User.getUserImage());
               startActivity(intent);

               UsersId=new ArrayList<>();
              //UsersArrayList=new ArrayList<>();

           }
       });


       //check if the current user have requests or not
       DatabaseReference root= FirebaseDatabase.getInstance().getReference();
       DatabaseReference m=root.child("requests").child(CurrentUId);
       ValueEventListener eventListener= new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(dataSnapshot.exists()){
                   UsersId.clear();
                   UsersArrayList.clear();
                   final UsersAdapter adapter=new UsersAdapter(getActivity(),UsersArrayList);
                   UserRequestListView.setAdapter(adapter);
                   checkTheRequestState();
               }
               else{
                   //to not display any friend request because the user doesn't have any friend request
                   UsersId.clear();
                   UsersArrayList.clear();
                   final UsersAdapter adapter=new UsersAdapter(getActivity(),UsersArrayList);
                   UserRequestListView.setAdapter(adapter);
               }
           }
           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {}
       };
       m.addListenerForSingleValueEvent(eventListener);


   }


    private void checkTheRequestState(){
        UsersId.clear();
        mDatabaseReference= FirebaseDatabase.getInstance().getReference().child("requests").child(CurrentUId);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for( DataSnapshot Snapshot: snapshot.getChildren()){
                    if(Snapshot.child("requestState").getValue().equals("received")){
                        UsersId.add(Snapshot.getKey().toString());
                    }
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
        UserRequestListView.setAdapter(adapter);

    }

}