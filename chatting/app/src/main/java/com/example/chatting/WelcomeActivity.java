package com.example.chatting;
 
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends AppCompatActivity {
    private Button GoToRegister_Btn;
    private Button GoToLogin_Btn;
    public static Activity fa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        fa = this;

        GoToRegister_Btn=(Button)findViewById(R.id.GoToRegisterBtn);
        GoToLogin_Btn=(Button)findViewById(R.id.GoToLoginBtn);

        GoToRegister_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent RegisterIntent =new Intent(WelcomeActivity.this,RegisterActivity.class);
                startActivity(RegisterIntent);
            }
        });

        GoToLogin_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent LoginIntent =new Intent(WelcomeActivity.this,LoginActivity.class);
                startActivity(LoginIntent);
            }
        });

    }

}
