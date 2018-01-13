package com.example.no24519.projectchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mToolbar;

    private DatabaseReference mUserRef;

    private Button mUserProfileBtn;
    private Button mUserRandomChatBtn;
    private Button mUserFriendsBtn;

    private String online = "online";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mToolbar = (Toolbar) findViewById(R.id.friends_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("媒朋友");

        if (mAuth.getCurrentUser() != null) {


            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        }

        mUserProfileBtn = (Button) findViewById(R.id.main_user_profile_btn);
        mUserFriendsBtn = (Button) findViewById(R.id.main_user_to_friends_btn);
        mUserRandomChatBtn = (Button) findViewById(R.id.main_random_chat_btn);

        mUserProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mUserRef.child(online).setValue("true");
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);

            }
        });
        mUserFriendsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mUserRef.child("online").setValue("true");
                Intent settingsIntent = new Intent(MainActivity.this, FriendsActivity.class);
                startActivity(settingsIntent);

            }
        });
        mUserRandomChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUserRef.child(online).setValue("true");
                Intent settingsIntent = new Intent(MainActivity.this, RandomChatActivity.class);
                startActivity(settingsIntent);
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {

            Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
            startActivity(startIntent);
            finish();

        }else{
            mUserRef.child(online).setValue("true");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.main_logout_btn) {
            mUserRef.child(online).setValue(ServerValue.TIMESTAMP);
            //logging out the user
            mAuth.signOut();
            //closing activity
            finish();
            //starting login activity
            startActivity(new Intent(this, StartActivity.class));
        }
        if (item.getItemId() == R.id.main_game_description_btn){

            finish();
            startActivity(new Intent(this, GameDescription.class));
        }
        return true;
    }
}