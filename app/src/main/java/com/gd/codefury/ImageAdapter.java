package com.gd.codefury;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

 //converter image to bitmap
        String base64Image = item.getImage();
        if (base64Image != null && !base64Image.isEmpty()) {
            byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            holder.imageView.setImageBitmap(bitmap);
        }

        // Set other fields
        holder.title.setText(item.getTitle());
        holder.description.setText(item.getDescription());
        holder.link.setText(item.getLink());

        // When clicked → open ImageDetailActivity
        // When clicked → open ImageDetailActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ImageDetailActivity.class);
            intent.putExtra("imageBase64", item.getImage()); // pass Base64
            intent.putExtra("title", item.getTitle());
            intent.putExtra("description", item.getDescription());
            intent.putExtra("link", item.getLink());
            v.getContext().startActivity(intent);  // ✅ correct context
        });

    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title, description, link;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_item_view);
            title = itemView.findViewById(R.id.title_ia);
            description = itemView.findViewById(R.id.description_ia);
            link = itemView.findViewById(R.id.link_ia);
        }
    }
}
