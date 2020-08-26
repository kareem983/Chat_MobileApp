package com.example.chatting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private TextInputEditText Email_Edit;
    private Button SendBtn;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        Email_Edit=(TextInputEditText)findViewById(R.id.ResetEdit);
        SendBtn = (Button)findViewById(R.id.ResetBtn);


        SendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             String Email_value = Email_Edit.getText().toString();

                if(Email_value.isEmpty()) Toast.makeText(ResetPasswordActivity.this,"Empty cell",Toast.LENGTH_SHORT).show();
                else {
                    mProgressDialog=new ProgressDialog(ResetPasswordActivity.this);
                    mProgressDialog.setTitle("Sending Email");
                    mProgressDialog.setMessage("please wait while we will send Reset Email");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();
                    FirebaseAuth.getInstance().sendPasswordResetEmail(Email_value).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mProgressDialog.dismiss();
                                Toast.makeText(ResetPasswordActivity.this,"Reset Email sent successfully",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else {
                                mProgressDialog.hide();
                                Toast.makeText(ResetPasswordActivity.this,"Wrong Email",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }
}