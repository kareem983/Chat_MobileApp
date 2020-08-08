package com.example.chating;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class UserProfileActivity extends AppCompatActivity {

    private Toolbar mToolBar;
    private ImageView UserImageView;
    private TextView UserNameView;
    private TextView UserStatusView;
    private TextView UserTotalFriendsView;
    private Button requestBtn;

    private String UserId;
    private String UserName;
    private String UserStatus;
    private String UserImage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);


        mToolBar= (Toolbar)findViewById(R.id.UserProfile_ToolBar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        UserImageView=(ImageView)findViewById(R.id.UserProfileImage);
        UserNameView=(TextView)findViewById(R.id.UserProfileName);
        UserStatusView=(TextView)findViewById(R.id.UserProfileStatus);
        UserTotalFriendsView=(TextView)findViewById(R.id.UserProfileTotalFriend);
        requestBtn = (Button)findViewById(R.id.UserProfileRequestBtn);

        //retrieve User data from the previous Activity
        UserId=getIntent().getStringExtra("User Id");
        UserName=getIntent().getStringExtra("User Name");
        UserStatus=getIntent().getStringExtra("User Status");
        UserImage=getIntent().getStringExtra("User Image");

        //display user data
        UserNameView.setText(UserName);
        UserStatusView.setText(UserStatus);
        Picasso.get().load(UserImage).placeholder(R.drawable.userr).into(UserImageView);



        //request button state

        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(UserProfileActivity.this,"You sent Friend Request",Toast.LENGTH_LONG).show();
            }
        });



    }


}