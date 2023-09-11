package com.sortscript.Book_Store;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import com.sortscript.Book_Store.databinding.ActivityHomeMenuBinding;

public class HomeMenu extends AppCompatActivity {

    ActivityHomeMenuBinding binding;

    @RequiresApi(api = Build.VERSION_CODES.FROYO)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.your_placeholder, new BooksFragment());
        ft.commit();


        binding.Searching.setOnClickListener(view -> {
            FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
            ft1.replace(R.id.your_placeholder, new BooksFragment());
            ft1.commit();

            binding.SearchIcon.setColorFilter(Color.parseColor("#ffffff"));
            binding.SearchIcon.setBackgroundResource(R.drawable.selectedwork);
            binding.SearchText.setTextColor(Color.parseColor("#0fa4dc"));


            binding.BookIcon.setColorFilter(Color.parseColor("#757575"));
            binding.BookIcon.setBackgroundResource(android.R.color.transparent);
            binding.BookText.setTextColor(Color.parseColor("#757575"));

            binding.ProfileIcon.setColorFilter(Color.parseColor("#757575"));
            binding.ProfileIcon.setBackgroundResource(android.R.color.transparent);
            binding.ProfileText.setTextColor(Color.parseColor("#757575"));

        });


        binding.Bookmark.setOnClickListener(view -> {
            FragmentTransaction ft12 = getSupportFragmentManager().beginTransaction();
            ft12.replace(R.id.your_placeholder, new BookmarkFragment());
            ft12.commit();

            binding.BookIcon.setColorFilter(Color.parseColor("#ffffff"));
            binding.BookIcon.setBackgroundResource(R.drawable.selectedwork);
            binding.BookText.setTextColor(Color.parseColor("#0fa4dc"));



            binding.SearchIcon.setColorFilter(Color.parseColor("#757575"));
            binding.SearchIcon.setBackgroundResource(android.R.color.transparent);
            binding.SearchText.setTextColor(Color.parseColor("#757575"));


            binding.ProfileIcon.setColorFilter(Color.parseColor("#757575"));
            binding.ProfileIcon.setBackgroundResource(android.R.color.transparent);
            binding.ProfileText.setTextColor(Color.parseColor("#757575"));

        });

        binding.Profile.setOnClickListener(view -> {
            FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
            ft1.replace(R.id.your_placeholder, new SettingFragment());
            ft1.commit();

            binding.ProfileIcon.setColorFilter(Color.parseColor("#ffffff"));
            binding.ProfileIcon.setBackgroundResource(R.drawable.selectedwork);
            binding.ProfileText.setTextColor(Color.parseColor("#0fa4dc"));

            binding.SearchIcon.setColorFilter(Color.parseColor("#757575"));
            binding.SearchIcon.setBackgroundResource(android.R.color.transparent);
            binding.SearchText.setTextColor(Color.parseColor("#757575"));

            binding.BookIcon.setColorFilter(Color.parseColor("#757575"));
            binding.BookIcon.setBackgroundResource(android.R.color.transparent);
            binding.BookText.setTextColor(Color.parseColor("#757575"));




        });

    }


}