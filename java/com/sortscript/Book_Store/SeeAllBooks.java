package com.sortscript.Book_Store;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sortscript.Book_Store.databinding.ActivityAddBookBinding;
import com.sortscript.Book_Store.databinding.ActivitySeeAllBooksBinding;

import java.util.ArrayList;

public class SeeAllBooks extends AppCompatActivity {
    ActivitySeeAllBooksBinding binding;
    ArrayList<AddBookModelClass> list;
    DatabaseReference databaseReference;
    AdapterShowMyBooks adapterClass;

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySeeAllBooksBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        Intent intent = getIntent();
        String str = intent.getStringExtra("Category");
        binding.txtViewCategory.setText(str);


        binding.imgBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference().child("BookStore").child("Books");
        list = new ArrayList<>();


        if(str==null)
        {
            binding.txtViewCategory.setText("Novel");
            binding.imgCatergory.setImageResource(R.drawable.novel_img);
            binding.linearlayoutAllBooks.setBackgroundResource(R.drawable.novel_img);
            databaseReference.orderByChild("category").equalTo("Novel").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        AddBookModelClass addBookModelClass = dataSnapshot.getValue(AddBookModelClass.class);
                        list.add(addBookModelClass);
                    }
                    setAdapterValue();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

    else {

            databaseReference.orderByChild("category").equalTo(str).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        AddBookModelClass addBookModelClass = dataSnapshot.getValue(AddBookModelClass.class);
                        list.add(addBookModelClass);
                    }
                    setAdapterValue();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            if(str.equals("Novel"))
            {
                binding.linearlayoutAllBooks.setBackgroundResource(R.drawable.novel_img);
                binding.imgCatergory.setImageResource(R.drawable.novel_img);
            }
            else if(str.equals("Romance"))
            {
                binding.linearlayoutAllBooks.setBackgroundResource(R.drawable.romantic_img);
                binding.imgCatergory.setImageResource(R.drawable.romantic_img);
            }
            else if(str.equals("Self-Help"))
            {
                binding.linearlayoutAllBooks.setBackgroundResource(R.drawable.selfhelp_img);
                binding.imgCatergory.setImageResource(R.drawable.selfhelp_img);
            }
            else if(str.equals("Biography"))
            {
                binding.linearlayoutAllBooks.setBackgroundResource(R.drawable.biography_img);
                binding.imgCatergory.setImageResource(R.drawable.biography_img);
            }
            else if(str.equals("Political"))
            {
                binding.linearlayoutAllBooks.setBackgroundResource(R.drawable.political_img);
                binding.imgCatergory.setImageResource(R.drawable.political_img);
            }
            else if(str.equals("Adventure"))
            {
                binding.linearlayoutAllBooks.setBackgroundResource(R.color.adventure_bg);
                binding.imgCatergory.setImageResource(R.drawable.adventure_img);
            }
            else if(str.equals("Mystery"))
            {
                binding.linearlayoutAllBooks.setBackgroundResource(R.color.black);
                binding.imgCatergory.setImageResource(R.drawable.mystery);
            }
            else if(str.equals("Poetry"))
            {
                binding.linearlayoutAllBooks.setBackgroundResource(R.drawable.poetry_img);
                binding.imgCatergory.setImageResource(R.drawable.poetry_img);
            }
            else if(str.equals("Cooking"))
            {
                binding.linearlayoutAllBooks.setBackgroundResource(R.color.cooking_bg);
                binding.imgCatergory.setImageResource(R.drawable.cooking_img);
            }
            else if(str.equals("Arts"))
            {
                binding.linearlayoutAllBooks.setBackgroundResource(R.drawable.aets_img);
                binding.imgCatergory.setImageResource(R.drawable.aets_img);
            }
            else if(str.equals("History"))
            {
                binding.linearlayoutAllBooks.setBackgroundResource(R.color.black);
                binding.imgCatergory.setImageResource(R.drawable.history_img);
            }

        }

    }




    private void setAdapterValue() {
        binding.recycyleViewCategory.setHasFixedSize(true);
        binding.recycyleViewCategory.setLayoutManager(new GridLayoutManager(SeeAllBooks.this, 1));
        adapterClass = new AdapterShowMyBooks(list, SeeAllBooks.this);
        binding.recycyleViewCategory.setAdapter(adapterClass);

    }

}