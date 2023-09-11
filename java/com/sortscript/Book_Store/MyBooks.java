package com.sortscript.Book_Store;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sortscript.Book_Store.databinding.ActivityAddBookBinding;
import com.sortscript.Book_Store.databinding.ActivityMyBooksBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyBooks extends AppCompatActivity {
    ActivityMyBooksBinding binding;
    ProgressDialog progressDialog;
    ArrayList<AddBookModelClass> list;
    AdapterMyBooks adapterClass;
    String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyBooksBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(MyBooks.this);
        progressDialog.setTitle("Content Loader");
        progressDialog.setProgress(10);
        progressDialog.setMax(3);
        progressDialog.setMessage("Loading...");
        new MyTask().execute();


        list = new ArrayList<>();
        UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("BookStore").child("Books");
        databaseReference.orderByChild("uid").equalTo(UID)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    AddBookModelClass addBookModelClass = dataSnapshot.getValue(AddBookModelClass.class);
                    list.add(addBookModelClass);
                }
                setNovelAdapter();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        binding.btnAddbooks.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                startActivity(new Intent(MyBooks.this, AddBook.class));
                finish();
            }
        });

    }


    private void setNovelAdapter() {

        binding.recycyleViewMybooks.setHasFixedSize(true);
        binding.recycyleViewMybooks.setLayoutManager(new GridLayoutManager(getApplicationContext(),1));
        adapterClass = new AdapterMyBooks(list,this);
        binding.recycyleViewMybooks.setAdapter(adapterClass);

    }


    public class MyTask extends AsyncTask<Void, Void, Void> {
        public void onPreExecute() {
            progressDialog.show();
        }
        public Void doInBackground(Void... unused) {
            return null;
        }
    }
}