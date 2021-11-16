package com.practice.chat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.practice.chat.Adapter.GroupMessagesAdapter;
import com.practice.chat.Model.Message;
import com.practice.chat.R;
import com.practice.chat.databinding.ActivityGroupChatBinding;

import java.util.ArrayList;
import java.util.Date;

public class GroupChatActivity extends AppCompatActivity {

    ActivityGroupChatBinding binding;
    GroupMessagesAdapter adapter;
    ArrayList<Message> messages;

    FirebaseDatabase database;
    FirebaseStorage storage;

    ProgressDialog dialog;

    String senderUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setTitle("Group Chat");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        senderUid = FirebaseAuth.getInstance().getUid();
        database = FirebaseDatabase.getInstance("https://chat-app-c921b-default-rtdb.asia-southeast1.firebasedatabase.app/");
        storage = FirebaseStorage.getInstance();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading image...");
        dialog.setCancelable(false);

        messages = new ArrayList<>();
        adapter = new GroupMessagesAdapter(this, messages);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        database.getReference().child("public")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messages.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            Message message = snapshot1.getValue(Message.class);
                            message.setMessageId(snapshot1.getKey());
                            messages.add(message);
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageTxt = binding.messageBox.getText().toString();

                Date date = new Date();
                Message message = new Message(messageTxt, senderUid, date.getTime());
                binding.messageBox.setText("");

                database.getReference().child("public")
                        .push()
                        .setValue(message);
            }
        });


    }
}