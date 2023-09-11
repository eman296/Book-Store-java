package com.sortscript.Book_Store;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;



public class AdapterShowMyBooks extends RecyclerView.Adapter<AdapterShowMyBooks.ViewHolder> {
    ArrayList<AddBookModelClass> list;
    Context context;
    DatabaseReference databaseReference;
    public AdapterShowMyBooks(ArrayList<AddBookModelClass> list, Context context) {
        this.list = list;
        this.context = context;
    }
    public  void  setFilteredList(List<AddBookModelClass> filteredList){
        this.list = (ArrayList<AddBookModelClass>) filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdapterShowMyBooks.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_items,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterShowMyBooks.ViewHolder holder,@SuppressLint("RecyclerView") int position) {

        final  AddBookModelClass data_position = list.get(position);

        holder.title.setText(list.get(position).getTitle());
        holder.auther.setText(list.get(position).getAuthor());
        holder.price.setText(list.get(position).getSellprice());
        holder.bookcategory.setText(list.get(position).getCategory());
        holder.condition.setText(list.get(position).getCondition());
        Glide.with(context).load(list.get(position).getImage()).into(holder.image);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = list.get(position).getCategory().toString();


                Intent intent = new Intent(v.getContext(),ViewBook.class);
                intent.putExtra("bookimg",data_position.getImage());
                intent.putExtra("title",data_position.getTitle());
                intent.putExtra("auth",data_position.getAuthor());
                intent.putExtra("cat",data_position.getCategory());
                intent.putExtra("des",data_position.getDescription());
                intent.putExtra("price",data_position.getSellprice());
                intent.putExtra("condition",data_position.getCondition());
                intent.putExtra("id",data_position.getId());
                v.getContext().startActivity(intent);



            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setMessage("Are you Sure to Delete.");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                databaseReference= FirebaseDatabase.getInstance().getReference().child("BookStore").child("Books");
                                databaseReference.child(list.get(position).getId()).removeValue().addOnCompleteListener(task -> {

                                    if (task.isSuccessful()){
                                        Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                dialog.cancel();
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image,delete;
        public TextView title,auther,price,bookcategory,condition;
        public LinearLayout cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.img_view);
            title = itemView.findViewById(R.id.txt_bookname);
            auther = itemView.findViewById(R.id.txt_bookauthor);
            delete = itemView.findViewById(R.id.delete);
            price = itemView.findViewById(R.id.text_price);
            bookcategory = itemView.findViewById(R.id.txt_bookcategory);
            condition = itemView.findViewById(R.id.txt_bookcondition);
            cardView = itemView.findViewById(R.id.card_MyBook);

        }
    }
}

