package com.sortscript.Book_Store;

import static com.google.android.material.color.MaterialColors.getColor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sortscript.Book_Store.databinding.FragmentBooksBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BooksFragment extends Fragment implements getCategoryClick {


    private List<The_Slide_Items_Model_Class> listItems;
    FragmentBooksBinding binding;
    ArrayList<AddBookModelClass> novellist;
    ProgressDialog progressDialog;
    AdapterShowMyBooks adapterClass;

    String Category;
    int currentPage = 0;
    Timer timer;
    final long DELAY_MS = 500;
    final long PERIOD_MS = 3000;
    DatabaseReference databaseReference;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentBooksBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();


        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Content Loader");
        progressDialog.setProgress(10);
        progressDialog.setMax(3);
        progressDialog.setMessage("Loading...");
        new MyTask().execute();


        binding.Searching.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchBook.class);
                startActivity(intent);
            }
        });

        binding.txtSeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SeeAllBooks.class);
                intent.putExtra("Category", Category);
                startActivity(intent);
            }
        });


        String[] category = {"Novel",
                "Romance", "Self-Help",
                "Biography", "Political",
                "Adventure", "Mystery",
                "Poetry", "Cooking",
                "Arts", "History"};

        int color_text = getActivity().getColor(R.color.light_bg);
        int color_bg = getActivity().getColor(R.color.btn_color);


        binding.recycyleViewCategory.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.recycyleViewCategory.setAdapter(new AdapterBooksCategory(category,
                color_text,
                color_bg,
                getActivity(),
                this::catValue));


        listItems = new ArrayList<>();
        listItems.add(new The_Slide_Items_Model_Class(R.drawable.novel_img));
        listItems.add(new The_Slide_Items_Model_Class(R.drawable.romantic_img));
        listItems.add(new The_Slide_Items_Model_Class(R.drawable.selfhelp_img));
        listItems.add(new The_Slide_Items_Model_Class(R.drawable.biography_img));
        listItems.add(new The_Slide_Items_Model_Class(R.drawable.political_img));
        listItems.add(new The_Slide_Items_Model_Class(R.drawable.adventure_img));
        listItems.add(new The_Slide_Items_Model_Class(R.drawable.mystery));
        listItems.add(new The_Slide_Items_Model_Class(R.drawable.poetry_img));
        listItems.add(new The_Slide_Items_Model_Class(R.drawable.cooking_img));
        listItems.add(new The_Slide_Items_Model_Class(R.drawable.aets_img));
        listItems.add(new The_Slide_Items_Model_Class(R.drawable.history_img));


        The_Slide_items_Pager_Adapter itemsPager_adapter = new The_Slide_items_Pager_Adapter(getContext(), listItems);
        binding.myPager.setAdapter(itemsPager_adapter);
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == 12 - 1) {
                    currentPage = 0;
                }
                binding.myPager.setCurrentItem(currentPage++, true);
            }
        };
        timer = new Timer(); // This will create a new Thread
        timer.schedule(new TimerTask() { // task to be scheduled
            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);


        databaseReference = FirebaseDatabase.getInstance().getReference().child("BookStore").child("Books");
        novellist = new ArrayList<>();
        databaseReference.orderByChild("category").equalTo("Novel").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                novellist.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    AddBookModelClass addBookModelClass = dataSnapshot.getValue(AddBookModelClass.class);
                    novellist.add(addBookModelClass);
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
        binding.recycyleViewBooks.setHasFixedSize(true);
        binding.recycyleViewBooks.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        adapterClass = new AdapterShowMyBooks(novellist, getActivity());
        binding.recycyleViewBooks.setAdapter(adapterClass);

    }

    @Override
    public void catValue(String Cat) {
        Category = Cat;

        if (Category != null) {
            databaseReference.orderByChild("category").equalTo(Category).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    novellist.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        AddBookModelClass addBookModelClass = dataSnapshot.getValue(AddBookModelClass.class);
                        novellist.add(addBookModelClass);
                    }
                    setAdapterValue();
                    progressDialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


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


