package com.example.chatting;
 
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;
import android.widget.ImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class DisplayUserImageActivity extends AppCompatActivity {
    private Toolbar mToolBar;
    private ImageView UserImage;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_user_image);
        mAuth= FirebaseAuth.getInstance();

        //toolbar
        mToolBar=(Toolbar)findViewById(R.id.UserImageToolBar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Profile photo");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //define xml component
        UserImage=(ImageView)findViewById(R.id.DisUserImage);

        FirebaseUser currentUser= mAuth.getCurrentUser();
        String CurrentUserId = currentUser.getUid();

        DatabaseReference mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(CurrentUserId);

        //display name and status and image of the user
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String Image = snapshot.child("Image").getValue().toString();
                if(!Image.equals("default")) Picasso.get().load(Image).placeholder(R.drawable.userr).into(UserImage);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

    }
}