package com.sortscript.Book_Store;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sortscript.Book_Store.databinding.FragmentBookmarkBinding;
import com.sortscript.Book_Store.databinding.FragmentBooksBinding;

import java.util.ArrayList;

public class BookmarkFragment extends Fragment {
    FragmentBookmarkBinding binding;
    ArrayList<AddBookModelClass> list;
    ProgressDialog progressDialog;
    AdapterShowMyBooks adapterClass;
    DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentBookmarkBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Content Loader");
        progressDialog.setProgress(10);
        progressDialog.setMax(3);
        progressDialog.setMessage("Loading...");
        new MyTask().execute();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("BookStore").child("Marked").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        list = new ArrayList<>();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    AddBookModelClass addBookModelClass = dataSnapshot.getValue(AddBookModelClass.class);
                    list.add(addBookModelClass);
                }
                setAdapterValue();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return view;
    }
    private void setAdapterValue() {
        binding.recycyleBookmarks.setHasFixedSize(true);
        binding.recycyleBookmarks.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        adapterClass = new AdapterShowMyBooks(list, getActivity());
        binding.recycyleBookmarks.setAdapter(adapterClass);

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