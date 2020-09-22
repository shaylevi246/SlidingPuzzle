package com.example.slidingpuzzle;

import android.content.Context;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class PicturesManager {
    private static PicturesManager instance=null;
    private final Context context;
    private ArrayList<Picture> pictures = new ArrayList<>();

    private static final String FILE_NAME = "SavedPic.dat";

    private PicturesManager(Context context) {
        this.context = context;

        try {
            FileInputStream fis = context.openFileInput(FILE_NAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            pictures = (ArrayList<Picture>) ois.readObject();
            ois.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static PicturesManager getInstance(Context context) {
        if(instance == null)
        {
            instance = new PicturesManager(context);
        }
        return instance;
    }

    public Picture getPicture(int position) {
        if (position < pictures.size()) {
            return pictures.get(position);
        }
        return null;
    }

    public void addPicture(Picture picture) {
        pictures.add(pictures.size(), picture);
        savePictures();
    }

    public void removePicture(int position) {

        if (position < pictures.size()) {
            pictures.remove(position);
        }
        savePictures();
    }

    private void savePictures() {
        try {
            FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(pictures);
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Picture> getPictures()
    {
        return pictures;
    }
}
