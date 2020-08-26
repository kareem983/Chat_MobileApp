package com.example.chatting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private Toolbar mToolBar;
    private TextInputEditText emailText;
    private TextInputEditText passwordText;
    private Button LoginBtn;
    private Button RBtn;
    private TextView ResetPassword;
    private FirebaseAuth mAuth;
    private ProgressDialog mDProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth=FirebaseAuth.getInstance();

        mToolBar= (Toolbar)findViewById(R.id.LoginToolBar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        emailText=(TextInputEditText)findViewById(R.id.LoginEmailEdit);
        passwordText=(TextInputEditText)findViewById(R.id.LoginPasswordEdit);
        LoginBtn=(Button)findViewById(R.id.LoginBtn);
        RBtn=(Button)findViewById(R.id.RBtn);
        ResetPassword= (TextView)findViewById(R.id.ResetPassword);


        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email_Value=emailText.getText().toString();
                String password_Value=passwordText.getText().toString();

                if(email_Value.isEmpty() || password_Value.isEmpty())Toast.makeText(LoginActivity.this,"Empty Cells",Toast.LENGTH_SHORT).show();
                else {
                    mDProgressDialog=new ProgressDialog(LoginActivity.this);
                    mDProgressDialog.setTitle("Logging in");
                    mDProgressDialog.setMessage("please wait while we check your account");
                    mDProgressDialog.setCanceledOnTouchOutside(false);
                    mDProgressDialog.show();
                    Login_User(email_Value, password_Value);
                }
            }
        });


        ResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(LoginActivity.this,ResetPasswordActivity.class);
                startActivity(intent);
            }
        });


        RBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }


    private void Login_User(final String email_value, String password_value){
        mAuth.signInWithEmailAndPassword(email_value,password_value).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    mDProgressDialog.dismiss();
                    Toast.makeText(LoginActivity.this,"Login Successfully",Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    WelcomeActivity.fa.finish();
                    finish();
                }
                else{
                    mDProgressDialog.hide();
                    Toast.makeText(LoginActivity.this,"Login failed",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

}