package com.example.journal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import Util.*;



public class JournalListActivity extends AppCompatActivity implements JournalRecyclerViewAdapter.onJournalClickListener {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private StorageReference storageRef ;
    CollectionReference collectionReference = db.collection("Journal");
    private List<Journal> journalList = new ArrayList<>();
    private RecyclerView recyclerView;
    private JournalRecyclerViewAdapter recyclerViewAdapter;
    private TextView noItem_textview;
    private Button share_info;
    private JournalRecyclerViewAdapter.onJournalClickListener onJournalClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_list);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noItem_textview = findViewById(R.id.noItem_textview);
       // share_info = findViewById(R.id.share_row_button);


        firebaseAuth = FirebaseAuth.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();


//        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
//            @Override
//            public boolean onMove(@NonNull RecyclerView recyclerView
//                    , @NonNull RecyclerView.ViewHolder viewHolder
//                    , @NonNull RecyclerView.ViewHolder target) {
//
//                return false;
//            }
//
//            @Override
//            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//                    int position  = viewHolder.getAdapterPosition();
//                    Journal journal = journalList.get(position);
//                    String documentID = journal.getDocumentID();
//                    collectionReference.document(documentID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void unused) {
//                            journalList.remove(position);
//                            //recyclerViewAdapter.notifyDataSetChanged();
//                            Toast.makeText(JournalListActivity.this , "DELETED" , Toast.LENGTH_SHORT).show();
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.d("onSwiping", "onFailure: "+ e.toString());
//                        }
//                    });
//            }
//        }).attachToRecyclerView(recyclerView);

      //      Toast.makeText(JournalListActivity.this , "SWIPE RIGHT TO DELETE", Toast.LENGTH_SHORT).show();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu , menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_journal:

                startActivity(new Intent(JournalListActivity.this , PostJournal_Activity.class));
                finish();
                break;

            case R.id.sign_out:

                currentUser = firebaseAuth.getCurrentUser();
                if(currentUser!=null && firebaseAuth!=null) {
                    firebaseAuth.signOut();
                    startActivity(new Intent(JournalListActivity.this, MainActivity.class));
                    finish();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {
        super.onStart();
        collectionReference.whereEqualTo("userId" , JournalApi.getInstance().getUserID()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                assert value != null;
                if(!value.isEmpty()) {
                    for (QueryDocumentSnapshot snapshot : value) {
                        Journal journal = snapshot.toObject(Journal.class);
                        journalList.add(journal);
                    }
                    recyclerViewAdapter = new JournalRecyclerViewAdapter( journalList , JournalListActivity.this );
                    recyclerView.setAdapter(recyclerViewAdapter);
                    recyclerViewAdapter.notifyDataSetChanged();
                }
                else{
                    noItem_textview.setVisibility(View.VISIBLE);
                }

            }
        });
//
//        if(journalList.size()==1){
//            Toast.makeText(JournalListActivity.this , "SWIPE RIGHT TO DELETE", Toast.LENGTH_SHORT).show();
//        }
        journalList.removeAll(journalList);
    }

    @Override
    public void onJournalClick(Journal journal) {
        Intent intent = new Intent(JournalListActivity.this , PostJournal_Activity.class);
        intent.putExtra("title" , journal.getTitle());
        intent.putExtra("thought" , journal.getThought());
        intent.putExtra("userID" , journal.getUserId());
        intent.putExtra("imageURL" , journal.getImageUrl());
        intent.putExtra("username", journal.getUserName());
        intent.putExtra("documentID" , journal.getDocumentID());
        startActivity(intent);
//        finish();
    }

    @Override
    public void onShareButtonClick(Journal journal) {
        Toast.makeText(JournalListActivity.this , "it can share data" , Toast.LENGTH_SHORT).show();

    }
}