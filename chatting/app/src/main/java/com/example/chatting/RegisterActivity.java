package com.example.chatting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private Toolbar mToolBar;
    private TextInputEditText nameText;
    private TextInputEditText emailText;
    private TextInputEditText passwordText;
    private TextInputEditText confirmPasswordText;
    private Button RegisterBtn;
    private Button LBtn;
    private boolean PasswordIsIdentical;
    private FirebaseAuth mAuth;
    private ProgressDialog mDProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth=FirebaseAuth.getInstance();

        //toolbar
        mToolBar= (Toolbar)findViewById(R.id.registerToolBar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //define xml components
        nameText=(TextInputEditText)findViewById(R.id.RegisterNameEdit);
        emailText=(TextInputEditText)findViewById(R.id.RegisterEmailEdit);
        passwordText=(TextInputEditText)findViewById(R.id.RegisterPasswordEdit);
        confirmPasswordText=(TextInputEditText)findViewById(R.id.RegisterConfirmPasswordEdit);
        RegisterBtn=(Button)findViewById(R.id.RegisterBtn);
        LBtn=(Button)findViewById(R.id.LBtn);


        RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PasswordIsIdentical=true;
                String name_Value=nameText.getText().toString();
                String email_Value=emailText.getText().toString();
                String password_Value=passwordText.getText().toString();
                String confirmPass_Value=confirmPasswordText.getText().toString();

                if(name_Value.isEmpty() || confirmPass_Value.isEmpty() || email_Value.isEmpty() || password_Value.isEmpty())Toast.makeText(RegisterActivity.this,"Empty Cells",Toast.LENGTH_SHORT).show();
                else {
                    //check if the confirmation password is identical or not
                    checkConfirmPassword(password_Value,confirmPass_Value);

                    if(PasswordIsIdentical){
                        //show progress dialog
                        mDProgressDialog=new ProgressDialog(RegisterActivity.this);
                        mDProgressDialog.setTitle("Registering User");
                        mDProgressDialog.setMessage("Please wait while we create your account");
                        mDProgressDialog.setCanceledOnTouchOutside(false);
                        mDProgressDialog.show();
                        Register_User(name_Value,email_Value, password_Value);
                    }
                    else {
                        Toast.makeText(RegisterActivity.this, "the confirmation password is wrong", Toast.LENGTH_LONG).show();
                        confirmPasswordText.setText("");
                    }
                }
            }
        });

        LBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }


    private void Register_User(final String name_value, String email_value, String password_value){
        mAuth.createUserWithEmailAndPassword(email_value,password_value).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    mDProgressDialog.dismiss();

                    HashMap <String,String> hashMap=new HashMap<>();
                    hashMap.put("Name",name_value);
                    hashMap.put("Status","Hi there Im using Messageya chat App");
                    hashMap.put("Image","default");
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    String uId= currentUser.getUid();
                    DatabaseReference z= FirebaseDatabase.getInstance().getReference().child("users");
                    z.child(uId).setValue(hashMap);
                    Toast.makeText(RegisterActivity.this,"Registered Successfully",Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    WelcomeActivity.fa.finish();
                    finish();
                 }
                else{
                    mDProgressDialog.hide();
                    Toast.makeText(RegisterActivity.this,"registration failed",Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    private void checkConfirmPassword(final String password_Value,final String confirnPass_Value){
        if(!password_Value.equals(confirnPass_Value)) PasswordIsIdentical=false;
    }

}