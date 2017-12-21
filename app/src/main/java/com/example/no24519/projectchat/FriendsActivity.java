package com.example.no24519.projectchat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView mFriendList;

    private DatabaseReference mFriendDatabase;
    private DatabaseReference mUsersDatabase;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mToolbar = (Toolbar) findViewById(R.id.friends_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("好友");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        mFriendDatabase.keepSynced(true);
        mUsersDatabase =FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);

        mFriendList = (RecyclerView)findViewById(R.id.friends_list);
        mFriendList.setHasFixedSize(true);
        mFriendList.setLayoutManager(new LinearLayoutManager(this));

    }
    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Friends,FriendsActivity.FriendsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friends, FriendsActivity.FriendsViewHolder>(

                Friends.class,
                R.layout.users_single_layout,
                FriendsActivity.FriendsViewHolder.class,
                mFriendDatabase
        ) {
            @Override
            protected void populateViewHolder(final FriendsActivity.FriendsViewHolder friendViewHolder, final Friends friends, int position) {

                friendViewHolder.setDate(friends.getDate());

                final String list_user_id =getRef(position).getKey();
                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();

                        if (dataSnapshot.hasChild("online")){

                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            friendViewHolder.setUserOnline(userOnline);

                        }

                        friendViewHolder.setName(userName);
                        friendViewHolder.setUserImage(userThumb,getApplicationContext());

                            friendViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    CharSequence options[] = new CharSequence[]{"查看好友資訊","好友聊天"};

                                    AlertDialog.Builder builder = new AlertDialog.Builder(FriendsActivity.this);

                                    builder.setTitle("選擇動作");
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            if (i == 0){

                                                Intent profileIntent = new Intent(FriendsActivity.this, ProfileActivity.class);
                                                profileIntent.putExtra("user_id",list_user_id);
                                                startActivity(profileIntent);

                                            }
                                            if (i == 1){

                                                Intent chatIntent = new Intent(FriendsActivity.this, ChatActivity.class);
                                                chatIntent.putExtra("user_id",list_user_id);
                                                chatIntent.putExtra("user_name",userName);
                                                startActivity(chatIntent);

                                            }
                                        }
                                    });
                                    builder.show();
                                }
                            });


                        }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        mFriendList.setAdapter(firebaseRecyclerAdapter);
    }


    public static class FriendsViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public FriendsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }
        public void setDate(String date){

            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_status);
            userNameView.setText(date);

        }
        public void setName(String name){
            TextView userStatusView = mView.findViewById(R.id.user_single_name);
            userStatusView.setText(name);
        }
        public void setUserImage(String thumb_image, Context ctx){
            CircleImageView userImageView = (CircleImageView)mView.findViewById(R.id.user_single_image);

            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.default_avatar).into(userImageView);
        }
        public void setUserOnline(String online_status){
            ImageView userOnlineView = mView.findViewById(R.id.user_single_onlone_icon);

            if(online_status.equals("true")){

                userOnlineView.setVisibility(View.VISIBLE);

            }else {

                userOnlineView.setVisibility(View.INVISIBLE);

            }

        }

    }
}
