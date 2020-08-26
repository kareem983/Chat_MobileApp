package com.example.chatting;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountSettingActivity extends AppCompatActivity {

    private CircleImageView UserImage;
    private TextView UserName;
    private TextView UserStatus;
    private Button StatusBtn;
    private Button ImageBtn;
    private Toolbar mToolBar;
    private ProgressDialog mDProgressialog;

    private static final int GALARY_PICK=1;
    private String CureentUserId;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;
    private StorageReference mStorageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);
        mAuth=FirebaseAuth.getInstance();
        mStorageRef= FirebaseStorage.getInstance().getReference();

        //tool bar
        mToolBar=(Toolbar)findViewById(R.id.AccountSetting_ToolBar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Account Setting");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //define xml views
        UserImage=(CircleImageView)findViewById(R.id.UserImage);
        UserName=(TextView)findViewById(R.id.UserName);
        UserStatus=(TextView)findViewById(R.id.UserStatus);
        StatusBtn=(Button)findViewById(R.id.changeStatusBtn);
        ImageBtn=(Button)findViewById(R.id.changeImageBtn);

        //firebase
        FirebaseUser currentUser= mAuth.getCurrentUser();
        CureentUserId = currentUser.getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(CureentUserId);

        //display name and status and image of the user
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String Name = snapshot.child("Name").getValue().toString();
                String Image = snapshot.child("Image").getValue().toString();
                String Status = snapshot.child("Status").getValue().toString();

                UserName.setText(Name);
                UserStatus.setText(Status);
                if(!Image.equals("default")) Picasso.get().load(Image).placeholder(R.drawable.user).into(UserImage);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });


        StatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountSettingActivity.this,AccountStatusActivity.class);
                startActivity(intent);
            }
        });



        ImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"SELECT IMAGE"),GALARY_PICK);
            }
        });


        UserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountSettingActivity.this,DisplayUserImageActivity.class);
                startActivity(intent);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //to crop image
        if(requestCode== GALARY_PICK && resultCode==RESULT_OK){
            Uri IamgeUri=data.getData();
            CropImage.activity(IamgeUri)
                    .setAspectRatio(1,1)
                    .start(this);
        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                //Display Dialog progress
                mDProgressialog=new ProgressDialog(AccountSettingActivity.this);
                mDProgressialog.setTitle("Uploading Image");
                mDProgressialog.setMessage("please wait while we Uploading your Image");
                mDProgressialog.setCanceledOnTouchOutside(false);
                mDProgressialog.show();

                Uri resultUri = result.getUri();

                UploadImageInStorageDataBase(resultUri);

            }

            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    private void UploadImageInStorageDataBase(Uri resultUri){
        //upload image in storage database
        final StorageReference FilePath = mStorageRef.child("profile_images").child(CureentUserId+"jpg");
        FilePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                FilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //save download url to image child
                        mUserDatabase.child("Image").setValue(uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    mDProgressialog.dismiss();
                                    Toast.makeText(AccountSettingActivity.this,"your image uploaded successfully",Toast.LENGTH_SHORT).show(); }
                                else{
                                    Toast.makeText(AccountSettingActivity.this,"Error in uploading",Toast.LENGTH_SHORT).show();
                                    mDProgressialog.hide();}
                            }
                        });

                    }
                });

            }
        });

    }

}