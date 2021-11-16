package com.practice.chat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.practice.chat.Adapter.UsersAdapter;
import com.practice.chat.Auth.RegisterUser;
import com.practice.chat.Model.User;
import com.practice.chat.R;
import com.practice.chat.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    FirebaseDatabase database;
    ArrayList<User> users;
    UsersAdapter usersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (FirebaseAuth.getInstance().getUid() == null) {
            startActivity(new Intent(this, RegisterUser.class));
            finish();
        }

        users = new ArrayList<>();
        database = FirebaseDatabase.getInstance("https://chat-app-c921b-default-rtdb.asia-southeast1.firebasedatabase.app/");

        usersAdapter = new UsersAdapter(this, users);
        binding.recyclerView.setAdapter(usersAdapter);

        database.getReference().child("Users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        users.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            User user1 = snapshot1.getValue(User.class);
                            if (!user1.uid.equals(FirebaseAuth.getInstance().getUid()))
                                users.add(user1);
                        }
                        usersAdapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, "updated", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logOut) {
            FirebaseAuth.getInstance().signOut();
            finishAffinity();
            startActivity(getIntent());
            return true;
        } else if (item.getItemId() == R.id.group) {
            startActivity(new Intent(MainActivity.this, GroupChatActivity.class));
        }
        return false;
    }
}