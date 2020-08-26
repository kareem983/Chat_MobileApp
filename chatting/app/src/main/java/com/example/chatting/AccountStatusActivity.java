package com.example.chatting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
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

public class AccountStatusActivity extends AppCompatActivity {

    private TextView StringCount;
    private TextWatcher textWatcher;
    private TextInputEditText StatusText;
    private Button SaveChangesBtn;
    private ProgressDialog mProgressBar;
    private Toolbar mToolBar;
    private final int STATUS_MAX_SIZE=38;
    private FirebaseAuth mAuth;
    private TextView AlertMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acount_status);
        mAuth=FirebaseAuth.getInstance();

        StatusText=(TextInputEditText) findViewById(R.id.StatusEdit);
        SaveChangesBtn=(Button)findViewById(R.id.changeStatusBtn);
        StringCount=(TextView)findViewById(R.id.StringCount);
        AlertMessage=(TextView)findViewById(R.id.AlertMessage);

        mToolBar=(Toolbar)findViewById(R.id.StatusToolBar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //refresh status
        RefreshStatus();


        //on status changes the number of characters will change
        textWatcher = new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (STATUS_MAX_SIZE - StatusText.getText().length() >= 0){
                    StringCount.setText(String.valueOf(STATUS_MAX_SIZE - StatusText.getText().length()));
                }
                else StringCount.setText("0");

                //show alert message
                if(STATUS_MAX_SIZE-StatusText.getText().length() <0){
                    AlertMessage.setVisibility(View.VISIBLE);
                }
                else AlertMessage.setVisibility(View.INVISIBLE);

            }
        };
        StatusText.addTextChangedListener(textWatcher);



        SaveChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(STATUS_MAX_SIZE-StatusText.getText().length() <0){
                    AlertMessage.setVisibility(View.VISIBLE);
                }
                else {
                    AlertMessage.setVisibility(View.INVISIBLE);
                    mProgressBar = new ProgressDialog(AccountStatusActivity.this);
                    mProgressBar.setTitle("Save Changes");
                    mProgressBar.setMessage("please wait while we change your Status");
                    mProgressBar.show();

                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    String UId = currentUser.getUid();
                    DatabaseReference x = FirebaseDatabase.getInstance().getReference().child("users").child(UId);
                    x.child("Status").setValue(StatusText.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                RefreshStatus();
                                mProgressBar.dismiss();
                                finish();
                            }
                        }
                    });

                }

            }
        });


    }


    private void RefreshStatus(){
        FirebaseUser currentUser= mAuth.getCurrentUser();
        String UId = currentUser.getUid();
        DatabaseReference x = FirebaseDatabase.getInstance().getReference().child("users").child(UId);

        x.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String Status = snapshot.child("Status").getValue().toString();
                StatusText.setText(Status);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });



    }
}