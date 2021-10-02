package com.example.journal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import Util.JournalApi;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private StorageReference storageReference;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser;
    private CollectionReference collectionReference = db.collection("users");
    private ProgressBar progressBar;
    private AutoCompleteTextView email_EditTextView;
    private EditText pass_editTextview;
    private Button login_Button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        firebaseAuth = FirebaseAuth.getInstance();
        progressBar  =findViewById(R.id.progressBar_login);
        email_EditTextView = findViewById(R.id.email_textview_loginAcc);
        pass_editTextview = findViewById(R.id.password_textview_loginAcc);
        login_Button = findViewById(R.id.login_acc_button);

        login_Button.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            String email = email_EditTextView.getText().toString().trim();
            String password = pass_editTextview.getText().toString().trim();
            if(!TextUtils.isEmpty(email ) && !TextUtils.isEmpty(password)){
                loginEmailAndPassword(email , password);
            }else{
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(LoginActivity.this , "Enter all fields" , Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loginEmailAndPassword(String email, String password) {

        firebaseAuth.signInWithEmailAndPassword(email , password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                currentUser = firebaseAuth.getCurrentUser();
                assert currentUser != null;
                String userID = currentUser.getUid();
                collectionReference.whereEqualTo("userId" , userID).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        assert value != null;
                        if(!value.isEmpty()){
                            for(QueryDocumentSnapshot snapshot : value){
                                progressBar.setVisibility(View.INVISIBLE);
                                JournalApi journalApi = JournalApi.getInstance();
                                journalApi.setUsername(snapshot.getString("username").toString().trim());
                                journalApi.setUserID(userID);
                                startActivity(new Intent(LoginActivity.this , PostJournal_Activity.class));
                                finish();
                            }

                        }else{
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this , "Wrong email or password" , Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }


    public void create_Account(View view) {
        startActivity(new Intent(LoginActivity.this , CreateAcc_Activity.class));
        finish();
    }


}