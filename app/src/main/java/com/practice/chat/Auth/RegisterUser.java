package com.practice.chat.Auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.practice.chat.Activities.MainActivity;
import com.practice.chat.Model.User;
import com.practice.chat.R;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterUser extends AppCompatActivity {

    EditText username, email, password;
    Button btn_register, goToLogin;
    CircleImageView imageView;

    FirebaseAuth auth;
    FirebaseStorage storage;
    Uri selectedImage;
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Updating profile...");
        dialog.setCancelable(false);


        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btn_register = findViewById(R.id.btn_register);
        goToLogin = findViewById(R.id.btn_goToLogin);
        imageView = findViewById(R.id.imageView);
        username = findViewById(R.id.name);

        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();

        imageView.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 45);
        });

        btn_register.setOnClickListener(view -> {

            String txt_username = username.getText().toString();
            String txt_email = email.getText().toString();
            String txt_password = password.getText().toString();

            if (TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)) {
                Toast.makeText(RegisterUser.this, "All fields are required", Toast.LENGTH_SHORT).show();
            } else if (txt_password.length() < 6) {
                Toast.makeText(RegisterUser.this, "password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            } else {
                register(txt_username, txt_email, txt_password);
            }
        });


        goToLogin.setOnClickListener(view -> {
            startActivity(new Intent(this, Login.class));
            finish();
        });
    }

    private void register(final String username, String email, String password) {

        dialog.show();

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (selectedImage != null) {
                                StorageReference reference = storage.getReference().child("Profiles").child(auth.getCurrentUser().getUid());
                                reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String imageUrl = uri.toString();

                                                    FirebaseUser firebaseUser = auth.getCurrentUser();
                                                    assert firebaseUser != null;
                                                    String userid = firebaseUser.getUid();
                                                    Toast.makeText(RegisterUser.this, userid, Toast.LENGTH_SHORT).show();

                                                    User user = new User();
                                                    user.name = username;
                                                    user.uid = userid;
                                                    user.email = email;
                                                    user.profileImage = imageUrl;

                                                    FirebaseDatabase.getInstance("https://chat-app-c921b-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users")
                                                            .child(userid)
                                                            .setValue(user).addOnCompleteListener(task1 -> {
                                                        if (task1.isSuccessful()) {
                                                            Toast.makeText(RegisterUser.this, "success", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(RegisterUser.this, MainActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        } else {
                                                            Toast.makeText(RegisterUser.this, "error " + task1.getException(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            });
                                        } else {
                                            Toast.makeText(RegisterUser.this, "Error" + task.getException(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                            else {
                                FirebaseUser firebaseUser = auth.getCurrentUser();
                                assert firebaseUser != null;
                                String userid = firebaseUser.getUid();
                                Toast.makeText(RegisterUser.this, userid, Toast.LENGTH_SHORT).show();

                                User user = new User();
                                user.name = username;
                                user.uid = userid;
                                user.email = email;
                                user.profileImage = "No Image";

                                FirebaseDatabase.getInstance("https://chat-app-c921b-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users")
                                        .child(userid)
                                        .setValue(user).addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(RegisterUser.this, "success", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(RegisterUser.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(RegisterUser.this, "error " + task1.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null) {
            if(data.getData() != null) {
  /* //                Uri uri = data.getData(); // filepath
             FirebaseStorage storage = FirebaseStorage.getInstance();
//                long time = new Date().getTime();
                StorageReference reference = storage.getReference().child("Profiles").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                reference.putFile(uri).addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri1) {
                                String filePath = uri1.toString();
                                HashMap<String, Object> obj = new HashMap<>();
                                obj.put("image", filePath);
                                database.getReference().child("users")
                                        .child(FirebaseAuth.getInstance().getUid())
                                        .updateChildren(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                });
                            }
                        });
                    }
                });*/
                imageView.setImageURI(data.getData());
                selectedImage = data.getData();
            }
        }
    }
}
