package com.example.no24519.projectchat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;


import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RandomChatActivity extends AppCompatActivity {


    private Toolbar mToolbar;

    private RecyclerView mMessageList;

    private final List<Messages> messageList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private MessageAdapter mAdapter;





    private ProgressDialog mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_chat);

        mProgress = new ProgressDialog(this);
        mProgress.setTitle("配對中");
        mProgress.setMessage("開始配對中！請稍後...");
        
        mProgress.show();


        mToolbar = (Toolbar) findViewById(R.id.chat_app_bar);

        setSupportActionBar(mToolbar);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.random_chat_bar,null);

        actionBar.setCustomView(action_bar_view);



        mAdapter = new MessageAdapter(messageList);

        mMessageList = (RecyclerView) findViewById(R.id.message_list);
        mLinearLayout = new LinearLayoutManager(this);

        mMessageList.setHasFixedSize(true);
        mMessageList.setLayoutManager(mLinearLayout);

        mMessageList.setAdapter(mAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        mMessageList.setLayoutManager(layoutManager);



            mProgress.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(RandomChatActivity.this);
            builder.setTitle("系統訊息");
            builder.setMessage("你是小白熊"+"\n"+"你與拉拉熊相遇了");
            builder.setPositiveButton("確認", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //尚未決定呈現方式
                }
            });
            builder.show();



    }


}

