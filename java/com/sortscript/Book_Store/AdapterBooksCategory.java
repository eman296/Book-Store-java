package com.sortscript.Book_Store;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterBooksCategory extends RecyclerView.Adapter<AdapterBooksCategory.ViewHolder> {
    String[] text1;
    Context context;
    int color_text;
    int color_bg;
    private int pos;
    getCategoryClick getCategory;

    public AdapterBooksCategory(String[] text1, int color_text, int color_bg, Context context, getCategoryClick getcat) {
        this.text1 = text1;
        this.color_bg = color_bg;
        this.color_text = color_text;
        this.context = context;
        this.getCategory = getcat;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_of_books, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.category.setText(text1[position]);
        holder.cardid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCategory.catValue(holder.category.getText().toString());
                pos = position;
                notifyDataSetChanged();
            }
        });
        if (pos == position) {
            holder.cardid.setCardBackgroundColor(color_bg);
            holder.category.setTextColor(Color.parseColor("#ffffff"));
        }
        else {
            holder.cardid.setCardBackgroundColor(color_text);
            holder.category.setTextColor(Color.parseColor("#0fa4dc"));
        }


    }

    @Override
    public int getItemCount() {
        return text1.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView category,seeAll,txtcategoey,delete;
        CardView cardid;

        public ViewHolder(View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.txt_category);
            txtcategoey=itemView.findViewById(R.id.txt_view_category);
            seeAll = itemView.findViewById(R.id.txt_seeAll);
            cardid = itemView.findViewById(R.id.cardid);


        }
    }
}


