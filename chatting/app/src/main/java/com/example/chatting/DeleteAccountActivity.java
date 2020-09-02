package com.example.chatting;
 
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DeleteAccountActivity extends AppCompatActivity {
    private TextInputEditText Email_Edit;
    private Button DeleteBtn;
    private ProgressDialog mProgressDialog;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String CurrentUserId;
    private String CurrentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);
        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        CurrentUserId = currentUser.getUid();
        CurrentUserEmail = currentUser.getEmail();

        //define xml components
        Email_Edit = (TextInputEditText)findViewById(R.id.DeleteEdit);
        DeleteBtn = (Button)findViewById(R.id.DeleteBtn);


        DeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Email_value = Email_Edit.getText().toString();
                if(Email_value.isEmpty()) Toast.makeText(DeleteAccountActivity.this,"Empty cell",Toast.LENGTH_SHORT).show();
                else {
                    if(Email_value.equals(CurrentUserEmail)) AlertDialogMessage();
                    else Toast.makeText(DeleteAccountActivity.this,"Wrong email",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void AlertDialogMessage(){

        AlertDialog.Builder checkAlert = new AlertDialog.Builder(DeleteAccountActivity.this);
        checkAlert.setMessage("Are you sure to delete your Account?")
                .setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mProgressDialog=new ProgressDialog(DeleteAccountActivity.this);
                mProgressDialog.setTitle("Delete Account");
                mProgressDialog.setMessage("please wait while we Deleting your Account");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            DeleteUserAccountFromDatabase();
                            mProgressDialog.dismiss();
                            FirebaseAuth.getInstance().signOut();
                            MainActivity.fa.finish();
                            Toast.makeText(DeleteAccountActivity.this,"your account Deleted successfully",Toast.LENGTH_LONG).show();
                            Intent intent =new Intent(DeleteAccountActivity.this,WelcomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            mProgressDialog.hide();
                            Toast.makeText(DeleteAccountActivity.this,"Failed deleting this account",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alert = checkAlert.create();
        alert.setTitle("Delete Account");
        alert.show();

    }




    private void DeleteUserAccountFromDatabase(){
        //Delete user from users child
        FirebaseDatabase.getInstance().getReference().child("users").child(CurrentUserId).removeValue();
        FirebaseDatabase.getInstance().getReference().child("requests").child(CurrentUserId).removeValue();
        FirebaseDatabase.getInstance().getReference().child("friends").child(CurrentUserId).removeValue();
        FirebaseDatabase.getInstance().getReference().child("chats").child(CurrentUserId).removeValue();

        DeleteUserRequestsFromDatabase();
    }


    private void DeleteUserRequestsFromDatabase(){
        //Delete user from requests child
        DatabaseReference root=FirebaseDatabase.getInstance().getReference();
        DatabaseReference m=root.child("requests");
        ValueEventListener eventListener= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        snapshot.child(CurrentUserId).getRef().removeValue();
                    }
                    DeleteUserFriendsFromDatabase();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        m.addListenerForSingleValueEvent(eventListener);

    }


    private void DeleteUserFriendsFromDatabase(){
        //Delete user from requests child
        DatabaseReference root=FirebaseDatabase.getInstance().getReference();
        DatabaseReference m=root.child("friends");
        ValueEventListener eventListener= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        snapshot.child(CurrentUserId).getRef().removeValue();
                    }
                    DeleteUserChatsFromDatabase();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        m.addListenerForSingleValueEvent(eventListener);

    }

    private void DeleteUserChatsFromDatabase(){
        //Delete user from requests child
        DatabaseReference root=FirebaseDatabase.getInstance().getReference();
        DatabaseReference m=root.child("chats");
        ValueEventListener eventListener= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        snapshot.child(CurrentUserId).getRef().removeValue();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        m.addListenerForSingleValueEvent(eventListener);

    }


}