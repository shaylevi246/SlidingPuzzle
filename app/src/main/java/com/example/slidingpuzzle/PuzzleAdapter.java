package com.example.slidingpuzzle;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

public class PuzzleAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<Bitmap> pieces;

    public PuzzleAdapter(Context applicationContext, ArrayList<Bitmap> pieces) {
        this.context = applicationContext;
        this.pieces = pieces;
    }

    @Override
    public int getCount() {
        return pieces.size();
    }

    @Override
    public Object getItem(int position) {
        return pieces.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ImageView imageView = new ImageView(context);
        imageView.setImageBitmap(pieces.get(position));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setPaddingRelative(2 , 2, 2,2);
        return imageView;
    }
}
