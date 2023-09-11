package com.sortscript.Book_Store;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sortscript.Book_Store.databinding.ActivityAddBookBinding;
import com.sortscript.Book_Store.databinding.ActivitySearchBookBinding;

import java.util.ArrayList;
import java.util.List;

public class SearchBook extends AppCompatActivity {

    ActivitySearchBookBinding binding;
    ArrayList<AddBookModelClass> list;
    DatabaseReference reference;
    ProgressDialog progressDialog;
    AdapterShowMyBooks adapterClass;

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        progressDialog = new ProgressDialog(SearchBook.this);
        progressDialog.setTitle("Content Loader");
        progressDialog.setProgress(10);
        progressDialog.setMax(3);
        progressDialog.setMessage("Loading...");
        new MyTask().execute();


        binding.searchView.clearFocus();
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filterList(s);
                return true;
            }
        });



        reference = FirebaseDatabase.getInstance().getReference().child("BookStore").child("Books");
        list = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
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

    }


    public class MyTask extends AsyncTask<Void, Void, Void> {
        public void onPreExecute() {
            progressDialog.show();
        }

        public Void doInBackground(Void... unused) {
            return null;
        }
    }

    private void filterList(String text) {

        List<AddBookModelClass> filteredList = new ArrayList<>();

        for (AddBookModelClass postBookModelClass : list) {
            if (postBookModelClass.getTitle().toLowerCase().contains(text.toLowerCase())
                    || postBookModelClass.getAuthor().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(postBookModelClass);
            }
        }
        if (filteredList.isEmpty()) {
//            Toast.makeText(SearchBook.this, "No Data Found", Toast.LENGTH_SHORT).show();
        } else {
            adapterClass.setFilteredList(filteredList);
        }
    }


    private void setAdapterValue() {
        binding.recycyleview.setHasFixedSize(true);
        binding.recycyleview.setLayoutManager(new GridLayoutManager(SearchBook.this, 1));
        adapterClass = new AdapterShowMyBooks(list, SearchBook.this);
        binding.recycyleview.setAdapter(adapterClass);

    }
}