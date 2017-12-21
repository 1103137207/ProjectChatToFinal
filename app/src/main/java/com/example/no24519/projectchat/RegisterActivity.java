package com.example.no24519.projectchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout mDisplayName,mEmail,mPassword;
    private Button mCreateBtn;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Toolbar mToolbar;

    private ProgressDialog mRefProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mToolbar = (Toolbar)findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("註冊");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRefProgress = new ProgressDialog(this);


        mDisplayName = (TextInputLayout) findViewById(R.id.reg_display_name);
        mEmail = (TextInputLayout) findViewById(R.id.reg_email);
        mPassword = (TextInputLayout) findViewById(R.id.reg_password);

        mCreateBtn = (Button)findViewById(R.id.reg_create_btn);

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String display_name = mDisplayName.getEditText().getText().toString();
                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();

                if (!TextUtils.isEmpty(display_name)||!TextUtils.isEmpty(email)||!TextUtils.isEmpty(password)){
                    mRefProgress.setTitle("註冊使用者");
                    mRefProgress.setMessage("註冊中  請稍後...");
                    mRefProgress.setCanceledOnTouchOutside(false);
                    mRefProgress.show();

                    register_user(display_name,email,password);

                }

            }
        });
    }

    private void register_user(final String display_name, String email, String password) {

      mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task) {
              if (task.isSuccessful()){

                  FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                  String uid = current_user.getUid();

                  mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                  String device_token = FirebaseInstanceId.getInstance().getToken();

                  HashMap <String,String> userMap = new HashMap<String, String>();
                  userMap.put("name",display_name);
                  userMap.put("status","測試狀態");
                  userMap.put("image","default");
                  userMap.put("thumb_image","default");
                  userMap.put("device_token",device_token);
                  userMap.put("city","default");
                  userMap.put("interest","default");
                  userMap.put("emotional","default");
                  userMap.put("introduction","default");

                  mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                      @Override
                      public void onComplete(@NonNull Task<Void> task) {
                          if (task.isSuccessful()){

                              mRefProgress.dismiss();

                              Intent mainIntent = new Intent(RegisterActivity.this,GameDescription.class);
                              startActivity(mainIntent);
                              finish();
                          }
                      }
                  });
              }
              else {
                  mRefProgress.hide();
                  Toast.makeText(RegisterActivity.this,"註冊錯誤",Toast.LENGTH_LONG).show();
              }
          }
      });
    }
}
