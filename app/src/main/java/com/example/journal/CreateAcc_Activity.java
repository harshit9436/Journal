package com.example.journal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import Util.JournalApi;

public class CreateAcc_Activity extends AppCompatActivity {
    private EditText username_textview , email_textview, password_textview ;
    private Button createAcc_button;
    private ProgressBar progressBar;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private CollectionReference collectionReference = db.collection("users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_acc);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        username_textview = findViewById(R.id.username_textview);
        email_textview = findViewById(R.id.email_textview_createAcc);
        password_textview = findViewById(R.id.password_textview_createAcc);
        progressBar = findViewById(R.id.progressBar_createAcc);
        createAcc_button = findViewById(R.id.create_new_acc_button);

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull  FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
                if(currentUser != null){
                    //user logged in
                }else{
                    //no user yet
                }
            }
        };

        createAcc_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = email_textview.getText().toString().trim();
                String username = username_textview.getText().toString().trim();
                String password = password_textview.getText().toString().trim();

                createUserAccount(email , password , username);
            }
        });

    }

    private void createUserAccount(String email, String password, String username) {
            if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(username)){
                progressBar.setVisibility(View.VISIBLE);
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {

                            if(task.isSuccessful()) {
                                currentUser = firebaseAuth.getCurrentUser();
                                assert currentUser != null;
                                String currentUserId = currentUser.getUid();
                                Map<String, String> userObj = new HashMap<>();
                                userObj.put("username", username);
                                userObj.put("userId", currentUserId);

                                collectionReference.add(userObj)
                                        .addOnSuccessListener(documentReference ->
                                                documentReference.get()
                                                        .addOnCompleteListener(task1 -> {

                                                            if (Objects.requireNonNull(task1.getResult()).exists()) {
                                                                progressBar.setVisibility(View.GONE);
                                                                String name = task1.getResult().getString("username");
                                                                JournalApi journalApi = JournalApi.getInstance();
                                                                journalApi.setUserID(currentUserId);
                                                                journalApi.setUsername(name);

                                                                Intent intent = new Intent(CreateAcc_Activity.this, PostJournal_Activity.class);
//                                                                intent.putExtra("username", name);
//                                                                intent.putExtra("userID", currentUserId);
                                                                startActivity(intent);
                                                                finish();
                                                            } else {
                                                                progressBar.setVisibility(View.GONE);
                                                            }
                                                        })
                                                        .addOnFailureListener(e -> Log.d("TAG", "onFailure: " + e.toString())))
                                        .addOnFailureListener(e -> Log.d("TAG", "onFailure: " + e.toString()));
                            } })
                        .addOnFailureListener(e -> {
                            Toast.makeText(CreateAcc_Activity.this, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        });
            }else{
                Toast.makeText(CreateAcc_Activity.this , "Empty Field(s) are not allowed" , Toast.LENGTH_SHORT).show();

            }
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
         firebaseAuth.addAuthStateListener(authStateListener);
    }
}