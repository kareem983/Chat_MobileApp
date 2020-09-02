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

public class ChatsFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private View mMainView;
    private String mParam1;
    private String mParam2;

    //my variables
    private ArrayList<String> UsersId;
    private ArrayList <Friends>UsersArrayList;
    private ListView UserChatsListView;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String CurrentUId;
    private String FinalMessage="";



    public ChatsFragment() {
    }
    public static ChatsFragment newInstance(String param1, String param2) {
        ChatsFragment fragment = new ChatsFragment();
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
        mMainView =  inflater.inflate(R.layout.fragment_chats, container, false);

        //define xml component
        UserChatsListView=(ListView)mMainView.findViewById(R.id.UserChats_ListView_id);

        someProcess();

        return  mMainView;
    }


    private void someProcess(){

        //firebase
        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        CurrentUId=currentUser.getUid();


        //define array lists
        UsersId=new ArrayList<>();
        UsersArrayList=new ArrayList<>();

        //if the user click to any friend contact
        UserChatsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Friends friend= UsersArrayList.get(i);
                Intent intent = new Intent(getActivity(),FriendsChattingActivity.class);
                intent.putExtra("User Id",friend.getFriendId());
                intent.putExtra("User Name",friend.getFriendName());
                intent.putExtra("User Image",friend.getFriendImage());
                startActivity(intent);
            }
        });


        //check if the current user have friends or not
        DatabaseReference root= FirebaseDatabase.getInstance().getReference();
        DatabaseReference m=root.child("friends").child(CurrentUId);
        ValueEventListener eventListener= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    UsersId.clear();
                    UsersArrayList.clear();
                    final FriendsAdapter adapter=new FriendsAdapter(getActivity(),UsersArrayList);
                    UserChatsListView.setAdapter(adapter);
                    checkTheFriends();
                }
                else{
                    //to not display any friend because the user doesn't have any friend
                    UsersId.clear();
                    UsersArrayList.clear();
                    final FriendsAdapter adapter=new FriendsAdapter(getActivity(),UsersArrayList);
                    UserChatsListView.setAdapter(adapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        m.addListenerForSingleValueEvent(eventListener);

    }


    private void checkTheFriends(){
        UsersId.clear();

        DatabaseReference root=FirebaseDatabase.getInstance().getReference();
        DatabaseReference m=root.child("friends").child(CurrentUId);
        ValueEventListener eventListener= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for( DataSnapshot Snapshot: dataSnapshot.getChildren()){
                        UsersId.add(Snapshot.getKey().toString());
                    }
                    sentUserDataToArrayAdapter();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        m.addListenerForSingleValueEvent(eventListener);
    }


    private void sentUserDataToArrayAdapter(){
        UsersArrayList.clear();
        final FriendsAdapter adapter=new FriendsAdapter(getActivity(),UsersArrayList);

        for(int i=0;i<UsersId.size();i++){
            getFinalMessage(UsersId.get(i));
            DatabaseReference root=FirebaseDatabase.getInstance().getReference();
            DatabaseReference m=root.child("users").child(UsersId.get(i));
            ValueEventListener eventListener= new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        String name = dataSnapshot.child("Name").getValue().toString();
                        String image = dataSnapshot.child("Image").getValue().toString();
                        String OnLine = dataSnapshot.child("Online").getValue().toString();
                        if(OnLine.equals("true"))UsersArrayList.add(new Friends(name,FinalMessage,image,dataSnapshot.getKey(),true));
                        else UsersArrayList.add(new Friends(name,FinalMessage,image,dataSnapshot.getKey(),false));
                        FinalMessage="";
                        adapter.notifyDataSetChanged();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            };
            m.addListenerForSingleValueEvent(eventListener);

        }
        UserChatsListView.setAdapter(adapter);

    }


    private void getFinalMessage(String UserId){
        FinalMessage="";
        DatabaseReference root= FirebaseDatabase.getInstance().getReference();
        DatabaseReference m=root.child("chats").child(CurrentUId).child(UserId);
        ValueEventListener eventListener= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot Snapshot : dataSnapshot.getChildren()){
                        if(Snapshot.child("Message Type").getValue().equals("Image")) FinalMessage="Image";
                        else if(Snapshot.child("Message Type").getValue().equals("Record")) FinalMessage="Audio";
                        else FinalMessage=Snapshot.child("Message").getValue().toString().substring(1, Snapshot.child("Message").getValue().toString().length());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        m.addListenerForSingleValueEvent(eventListener);

    }

}