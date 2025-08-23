package com.gd.codefury;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private final ArrayList<Image> imageList;
    private final Context context;

    public ImageAdapter(ArrayList<Image> imageList, Context context) {
        this.imageList = imageList;
        this.context = context;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_adapter_view, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Image item = imageList.get(position);

        // Load just the image thumbnail
        Glide.with(context).load(item.getImageUrl()).into(holder.imageView);

        // When clicked → open ImageDetailActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ImageDetailActivity.class);
            intent.putExtra("imageUrl", item.getImageUrl());
            intent.putExtra("title", item.getTitle());
            intent.putExtra("description", item.getDescription());
            intent.putExtra("link", item.getLink());
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return imageList.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageButton backButton;
        ImageView imageView;
        TextView title, description, link;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            backButton = itemView.findViewById(R.id.backbutton);
            imageView = itemView.findViewById(R.id.image_item_view);
            title = itemView.findViewById(R.id.title_ia);
            description = itemView.findViewById(R.id.description_ia);
            link = itemView.findViewById(R.id.link_ia);
        }
    }
}
