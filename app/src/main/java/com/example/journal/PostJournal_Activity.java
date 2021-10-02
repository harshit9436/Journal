package com.example.journal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.Objects;

import Util.Journal;
import Util.JournalApi;

public class PostJournal_Activity extends AppCompatActivity {
    private static final int GALLERY_CODE = 1;
    private TextView name_textview, date_textview;
    private EditText post_title_et , post_description_et;
    private Button savePost_button , delete_button;
    private ImageView post_Imageview , changeImage_imageview;
    private ProgressBar savePost_progressBar;

    private String username;
    private String userId;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
//    private FirebaseStorage firebaseStorage;
    private StorageReference storageRef ;

    private CollectionReference collectionReference = db.collection("Journal");
    private Uri imageURI;
    private static String documentId = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_journal);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        name_textview = findViewById(R.id.post_username_textview);
        date_textview = findViewById(R.id.post_date_textview);
        post_description_et = findViewById(R.id.post_thought_editText);
        post_title_et = findViewById(R.id.post_title_editText);
        savePost_button = findViewById(R.id.post_save_button);
        delete_button = findViewById(R.id.delete_post_button);
        savePost_progressBar = findViewById(R.id.post_progressBar);
        post_Imageview = findViewById(R.id.post_Image_view);
        changeImage_imageview = findViewById(R.id.post_add_ImageView);

        firebaseAuth = FirebaseAuth.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        JournalApi journalApi = JournalApi.getInstance();
        if(journalApi!=null) {
            username = journalApi.getUsername();
            userId = journalApi.getUserID();
            name_textview.setText(username);

        }

        changeImage_imageview.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent , GALLERY_CODE);
        });


        savePost_button.setOnClickListener(v -> {
            savePost_progressBar.setVisibility(View.VISIBLE);
             String title = post_title_et.getText().toString().trim();
             String thought = post_description_et.getText().toString().trim();

            StorageReference filePath = storageRef.child("journal_images")
                    .child("my_image_"+ Timestamp.now().getSeconds());


            if(getIntent().hasExtra("userID")){
               assert journalApi != null;
//                Toast.makeText(PostJournal_Activity.this , "id" + journalApi.getUsername() ,Toast.LENGTH_SHORT ).show();
//                Toast.makeText(PostJournal_Activity.this , "id" + journalApi.getDocumentID() ,Toast.LENGTH_SHORT ).show();

                if(imageURI!=null && !TextUtils.isEmpty(title) && !TextUtils.isEmpty(thought)){
                    filePath.putFile(imageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageURL = uri.toString();
                                    Journal journal = new Journal();
                                    journal.setTitle(title);
                                    journal.setThought(thought);
                                    journal.setUserId(userId);
                                    journal.setUserName(username);
                                    journal.setImageUrl(imageURL);
                                    journal.setTimeAdded( new Timestamp(new Date()));
                                    journal.setDocumentID(getIntent().getStringExtra("documentID"));

                                    collectionReference.document( getIntent().getStringExtra("documentID")).set(journal).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            savePost_progressBar.setVisibility(View.INVISIBLE);
                                            startActivity(new Intent(PostJournal_Activity.this, JournalListActivity.class));
                                            Toast.makeText(PostJournal_Activity.this , "UPDATED" , Toast.LENGTH_LONG).show();
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            savePost_progressBar.setVisibility(View.INVISIBLE);
                                            Log.d("OnPostSaving", "onFailure: " + e.toString());                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    savePost_progressBar.setVisibility(View.INVISIBLE);
                                    Log.d("OnPostSaving", "onFailure: " + e.toString());
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            savePost_progressBar.setVisibility(View.INVISIBLE);
                            Log.d("OnPostSaving", "onFailure: " + e.toString());

                        }
                    });
                }else{
                    savePost_progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(PostJournal_Activity.this , "add all fields" ,Toast.LENGTH_SHORT ).show();

                }
            }else{

                if(imageURI!=null && !TextUtils.isEmpty(title) && !TextUtils.isEmpty(thought)) {
                    filePath.putFile(imageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageURL = uri.toString();
                                    Journal journal = new Journal();
                                    journal.setTitle(title);
                                    journal.setThought(thought);
                                    journal.setUserId(userId);
                                    journal.setUserName(username);
                                    journal.setImageUrl(imageURL);
                                    journal.setTimeAdded( new Timestamp(new Date()));

                                    collectionReference.add(journal).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            documentId = documentReference.getId();
                                            journal.setDocumentID(documentId);
                                            collectionReference.document(documentId).set(journal);
                                            journal.setDocumentID(documentId);
                                            assert journalApi != null;
                                            journalApi.setDocumentID(documentId);
                                            savePost_progressBar.setVisibility(View.INVISIBLE);
                                            startActivity(new Intent(PostJournal_Activity.this, JournalListActivity.class));
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            savePost_progressBar.setVisibility(View.INVISIBLE);
                                            Log.d("OnPostSaving", "onFailure: " + e.toString());
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    savePost_progressBar.setVisibility(View.INVISIBLE);
                                    Log.d("OnPostSaving", "onFailure: " + e.toString());
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            savePost_progressBar.setVisibility(View.INVISIBLE);
                            Log.d("OnPostSaving", "onFailure: " + e.toString());
                        }
                    });
                }else{
                    savePost_progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(PostJournal_Activity.this , "Empty Fields not allowed" , Toast.LENGTH_LONG).show();
                }

            }


        });

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectionReference.document(getIntent().getStringExtra("documentID"))
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(PostJournal_Activity.this , "DELETED" , Toast.LENGTH_LONG).show();
                        startActivity(new Intent(PostJournal_Activity.this, JournalListActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("OnDeleting", "onFailure: " + e.toString());
                    }
                });
            }
        });

        if(getIntent().hasExtra("userID")){
            post_title_et.setText(getIntent().getStringExtra("title"));
            post_description_et.setText(getIntent().getStringExtra("thought"));
            savePost_button.setText(R.string.update);
            delete_button.setVisibility(View.VISIBLE);
            delete_button.setVisibility(View.VISIBLE);

        }




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLERY_CODE && resultCode == RESULT_OK){
            if(data!=null){
                imageURI = data.getData();
                post_Imageview.setImageURI(imageURI);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }


}
