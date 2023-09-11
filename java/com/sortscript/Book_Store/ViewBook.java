package com.sortscript.Book_Store;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sortscript.Book_Store.databinding.ActivityViewBookBinding;

public class ViewBook extends AppCompatActivity {

    ActivityViewBookBinding binding;
    String passimg;
    String uid, id;
    DatabaseReference reference;
    String Value =" ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewBookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.etBookname.setEnabled(false);
        binding.etBookauthor.setEnabled(false);
        binding.etBookcondition.setEnabled(false);
        binding.etDescription.setEnabled(false);
        binding.etPrice.setEnabled(false);




        uid = FirebaseAuth.getInstance().getUid();
        id = getIntent().getExtras().getString("id");
        reference = FirebaseDatabase.getInstance().getReference().child("BookStore").child("Marked").child(uid);

        passimg = getIntent().getExtras().getString("bookimg");
        binding.etBookname.setText(getIntent().getExtras().getString("title"));
        binding.etBookauthor.setText(getIntent().getExtras().getString("auth"));
        binding.etDescription.setText(getIntent().getExtras().getString("des"));
        binding.txtBookcategorys.setText(getIntent().getExtras().getString("cat"));
        binding.etBookcondition.setText(getIntent().getExtras().getString("condition"));
        binding.etPrice.setText(getIntent().getExtras().getString("price"));
        Glide.with(this).load(passimg).into(binding.imgView);


        reference.orderByChild("id").equalTo(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.exists()) {
                    //    Toast.makeText(ViewBook.this, "Exists", Toast.LENGTH_SHORT).show();
                        binding.markedicon.setImageResource(R.drawable.ic_baseline_bookmark_24);
                        Value = "Yes";

                    } else {
                        Toast.makeText(ViewBook.this, "Not Exists", Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.imgBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.layoutBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String image = getIntent().getExtras().getString("bookimg");
                String title = getIntent().getExtras().getString("title");
                String author = getIntent().getExtras().getString("auth");
                String category = getIntent().getExtras().getString("cat");
                String description = getIntent().getExtras().getString("des");
                String sellprice = getIntent().getExtras().getString("price");
                String condition = getIntent().getExtras().getString("condition");


                AddBookModelClass addBookModelClass = new AddBookModelClass(uid, id, image, title, author, category, description, sellprice, condition);

                if (Value.equals("Yes")) {
                    reference.child(id).removeValue();
              //      Toast.makeText(ViewBook.this, "UnMarked", Toast.LENGTH_SHORT).show();
                    binding.markedicon.setImageResource(R.drawable.ic_baseline_bookmark_border_24);
                    Value = " ";

                } else {
                    reference.child(id).setValue(addBookModelClass).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                 //           Toast.makeText(ViewBook.this, "Marked Done", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ViewBook.this, "Marked Fail", Toast.LENGTH_SHORT).show();
                        }
                    });

                }


            }


        });



    }
}