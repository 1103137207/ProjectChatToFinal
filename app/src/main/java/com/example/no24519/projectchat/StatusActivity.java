package com.example.no24519.projectchat;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class StatusActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextInputLayout mIntroductionStatus;
    private TextInputLayout mEmotionalStatus;
    private TextInputLayout mInterestStatus;
    private TextInputLayout mCityStatus;
    private TextInputLayout mPersonStatus;
    private Button mSavebtn;

    private DatabaseReference mStatusDataBase;
    private FirebaseUser mCurrentUser;

    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mProgress = new ProgressDialog(this);

        mPersonStatus = (TextInputLayout) findViewById(R.id.status_person_input);
        mCityStatus = (TextInputLayout) findViewById(R.id.status_city_input);
        mInterestStatus = (TextInputLayout) findViewById(R.id.status_interest_input);
        mEmotionalStatus = (TextInputLayout) findViewById(R.id.status_emotional_state_input);
        mIntroductionStatus = (TextInputLayout) findViewById(R.id.status_introduction_input);
        mSavebtn = (Button) findViewById(R.id.status_sava_btn);

        String status_value = getIntent().getStringExtra("status_value");
        String city_value = getIntent().getStringExtra("city_value");
        String interest_value = getIntent().getStringExtra("interest_value");
        String emotional_value = getIntent().getStringExtra("emotional_value");
        String introduction_value = getIntent().getStringExtra("introduction_value");


        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();

        mPersonStatus.getEditText().setText(status_value);
        mCityStatus.getEditText().setText(city_value);
        mInterestStatus.getEditText().setText(interest_value);
        mEmotionalStatus.getEditText().setText(emotional_value);
        mIntroductionStatus.getEditText().setText(introduction_value);

        mStatusDataBase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        mToolbar = (Toolbar)findViewById(R.id.status_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("設定帳號資訊");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSavebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProgress.setTitle("儲存");
                mProgress.setMessage("儲存中  請稍後...");
                mProgress.show();

                String person_status = mPersonStatus.getEditText().getText().toString();
                String city_status = mCityStatus.getEditText().getText().toString();
                String interest_status = mInterestStatus.getEditText().getText().toString();
                String emotional_status = mEmotionalStatus.getEditText().getText().toString();
                String introduction_status = mIntroductionStatus.getEditText().getText().toString();



                Map userMap = new HashMap();
                userMap.put("status",person_status);
                userMap.put("city",city_status);
                userMap.put("interest",interest_status);
                userMap.put("emotional",emotional_status);
                userMap.put("introduction",introduction_status);

                mStatusDataBase.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            mProgress.dismiss();
                            Toast.makeText(StatusActivity.this,"修改成功",Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(StatusActivity.this,"狀態更改有誤",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
}
