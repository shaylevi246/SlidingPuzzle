package com.example.slidingpuzzle;

import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PicturesAdapter extends RecyclerView.Adapter<PicturesAdapter.PicturesViewHolder> {
    private MyPicturesListener listener;
    private final List<Picture> pictures;

    interface MyPicturesListener{
        void OnPictureClicked(int position, View view);
        void OnPictureLongClicked(int position,View view);
    }

    public void setListener(MyPicturesListener listener){
        this.listener = listener;
    }

    public PicturesAdapter(List<Picture> pictures)
    {
        this.pictures = pictures;
    }

    public class PicturesViewHolder extends RecyclerView.ViewHolder{
        private final ImageView imageView;

        public PicturesViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.small_pic);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        listener.OnPictureClicked(getAdapterPosition(),v);
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (listener != null) {
                        listener.OnPictureLongClicked(getAdapterPosition(), v);
                    }
                    return false;
                }
            });
        }
    }

    @NonNull
    @Override
    public PicturesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cards_view, parent, false);
        return new PicturesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PicturesViewHolder holder, int position) {
        Picture picture = pictures.get(position);
        switch(position){
            case 0: holder.imageView.setImageResource(R.drawable.dolphin);
                break;
            case 1: holder.imageView.setImageResource(R.drawable.lion);
                break;
            case 2: holder.imageView.setImageResource(R.drawable.nature);
                break;
            case 3: holder.imageView.setImageResource(R.drawable.pic4);
                break;
            case 4: holder.imageView.setImageResource(R.drawable.pic5);
                break;
            case 5: holder.imageView.setImageResource(R.drawable.pic6);
                break;
            default:
                holder.imageView.setImageBitmap(BitmapFactory.decodeFile(picture.getPhoto()));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return pictures.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}