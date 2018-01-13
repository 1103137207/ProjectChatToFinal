package com.example.no24519.projectchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {

    private FirebaseUser mCurrentUser;
    private DatabaseReference mUserDataBase;

    private CircleImageView mDisplayImage;
    private TextView mName;

    private TextView mIntroductionStatus;
    private TextView mEmotionalStatus;
    private TextView mInterestStatus;
    private TextView mCityStatus;
    private TextView mPersonStatus;

    private Button mStatusBtn;
    private Button mImageBtn;

    private static final int GALLERY_PICK = 1;

    private StorageReference mImageStorage;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mDisplayImage = (CircleImageView)findViewById(R.id.settings_image);
        mName = (TextView)findViewById(R.id.settings_name);
        mPersonStatus = (TextView) findViewById(R.id.settings_person_status);
        mCityStatus = (TextView) findViewById(R.id.profile_city_status);
        mInterestStatus = (TextView) findViewById(R.id.profile_interest_status);
        mEmotionalStatus = (TextView) findViewById(R.id.profile_emotional_status);
        mIntroductionStatus = (TextView) findViewById(R.id.profile_introduction_status);


        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        String current_uid = mCurrentUser.getUid();

        mUserDataBase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mUserDataBase.keepSynced(true);

        mImageStorage = FirebaseStorage.getInstance().getReference();

        mUserDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();
                String city = dataSnapshot.child("city").getValue().toString();
                String interest = dataSnapshot.child("interest").getValue().toString();
                String emotional = dataSnapshot.child("emotional").getValue().toString();
                String introduction = dataSnapshot.child("introduction").getValue().toString();

                mName.setText(name);
                mPersonStatus.setText(status);
                mCityStatus.setText("居住地："+city);
                mInterestStatus.setText("興趣／專長："+interest);
                mEmotionalStatus.setText("感情狀況："+emotional);
                mIntroductionStatus.setText("自我介紹："+introduction);


                if (!image.equals("default")){

                    //Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.default_avatar).into(mDisplayImage);
                    Picasso.with(SettingsActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.default_avatar).into(mDisplayImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            //如果沒有上傳照片則顯示預設
                        }

                        @Override
                        public void onError() {

                            Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.default_avatar).into(mDisplayImage);

                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //預設
            }
        });

        mStatusBtn = (Button) findViewById(R.id.setting_status_btn);
        mStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String status_value = mPersonStatus.getText().toString();
                String city_value = mCityStatus.getText().toString();
                String interest_value = mInterestStatus.getText().toString();
                String emotional_value = mEmotionalStatus.getText().toString();
                String introduction_value = mIntroductionStatus.getText().toString();

                Intent statusIntent = new Intent(SettingsActivity.this,StatusActivity.class);
                statusIntent.putExtra("status_value",status_value);
                statusIntent.putExtra("city_value",city_value);
                statusIntent.putExtra("interest_value",interest_value);
                statusIntent.putExtra("emotional_value",emotional_value);
                statusIntent.putExtra("introduction_value",introduction_value);
                startActivity(statusIntent);
            }
        });

        mImageBtn = (Button) findViewById(R.id.settings_image_btn);
        mImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleyIntent = new Intent();
                galleyIntent.setType("image/*");
                galleyIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleyIntent,"選擇照片"),GALLERY_PICK);

                /*
                        CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingsActivity.this);
                        */

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK){
            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setTitle("上傳中");
                mProgressDialog.setMessage("上傳中   請稍後...");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                Uri resultUri = result.getUri();

                File thumb_filePath = new File(resultUri.getPath());

                String current_uid = mCurrentUser.getUid();

                Bitmap thumb_bitmap = new Compressor(SettingsActivity.this)
                        .setMaxWidth(400)
                        .setMaxHeight(400)
                        .setQuality(150)
                        .compressToBitmap(thumb_filePath);


                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();

                StorageReference filepath = mImageStorage.child("profile_images").child(current_uid+".jpg");
                final StorageReference thumb_filepath =mImageStorage.child("profile_images").child("thumb").child(current_uid+".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()){

                            @SuppressWarnings("VisibleForTests") final String download_url = task.getResult().getDownloadUrl().toString();

                            UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumbtask) {

                                    @SuppressWarnings("VisibleForTests")String thumb_downloadUrl =thumbtask.getResult().getDownloadUrl().toString();

                                    if (thumbtask.isSuccessful()){

                                        Map update_hasMap = new HashMap();
                                        update_hasMap.put("image",download_url);
                                        update_hasMap.put("thumb_image",thumb_downloadUrl);

                                        mUserDataBase.updateChildren(update_hasMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){

                                                    mProgressDialog.dismiss();
                                                    Toast.makeText(SettingsActivity.this,"上傳成功",Toast.LENGTH_LONG).show();

                                                }
                                            }
                                        });
                                    }
                                    else {
                                        Toast.makeText(SettingsActivity.this,"上傳失敗",Toast.LENGTH_LONG).show();
                                        mProgressDialog.dismiss();
                                    }

                                }
                            });



                        }
                        else {
                            Toast.makeText(SettingsActivity.this,"上傳失敗",Toast.LENGTH_LONG).show();
                            mProgressDialog.dismiss();
                        }

                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }
    }

    public static String random(){
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength =generator.nextInt(10);
        char tempChar;
        for (int i =0 ;i<randomLength;i++){
            tempChar =(char) (generator.nextInt(96)+32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

}
