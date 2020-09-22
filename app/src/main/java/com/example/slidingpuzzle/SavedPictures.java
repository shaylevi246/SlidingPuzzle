package com.example.slidingpuzzle;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class SavedPictures extends AppCompatActivity {
    private static final int LIBRARY_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private static final int WRITE_PERMISSION_REQUEST = 3;
    private static final int READ_PERMISSION_REQUEST = 4;
    private PicturesManager manager;
    private PicturesAdapter adapter;
    private Date date;
    private File file;
    private MediaPlayer mpBtnClick;
    private MediaPlayer mpSwap;
    private MediaPlayer mpPuzzle;
    private MediaPlayer mpOpen;
    private String soundEffects;
    private String background_music;
    private SharedPreferences sp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saved_pictures);

        getSupportActionBar().hide();

        LinearLayout linearLayout = findViewById(R.id.backColorSaved);
        AnimationDrawable animationDrawable = (AnimationDrawable) linearLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

        sp = getSharedPreferences("PuzzlesRecordsDetail", MODE_PRIVATE);
        background_music = sp.getString("music","true");
        soundEffects = sp.getString("soundEffects","true");
        if(background_music.equals("true")){
            stopMusicPlayer();
            startMusicPlayer("open");
        }

        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this , 4));
        manager = PicturesManager.getInstance(this);

        ArrayList<Picture> mPicturesList = manager.getPictures();
        if(mPicturesList.isEmpty()){
            manager.addPicture(new Picture("dolphin"));
            manager.addPicture(new Picture("lion"));
            manager.addPicture(new Picture("nature"));
            manager.addPicture(new Picture("pic4"));
            manager.addPicture(new Picture("pic5"));
            manager.addPicture(new Picture("pic6"));
            mPicturesList = manager.getPictures();
        }
        adapter = new PicturesAdapter(manager.getPictures());
        recyclerView.setAdapter(adapter);
        adapter.setListener(new PicturesAdapter.MyPicturesListener() {
            @Override
            public void OnPictureClicked(int position, View view) {
                if(soundEffects.equals("true")) {
                    stopPlayer();
                    startPlayer("click");
                }
                Intent intent = new Intent(SavedPictures.this,MainActivity.class);
                finish();
                intent.putExtra("chosen picture", manager.getPicture(position).getPhoto());
                startActivity(intent);

            }

            @Override
            public void OnPictureLongClicked(final int position, View view) {
                if(position>5){
                    AlertDialog.Builder builder = new AlertDialog.Builder(SavedPictures.this);
                    builder.setMessage(R.string.delete_confirmation);
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(soundEffects.equals("true")) {
                                stopPlayer();
                                startPlayer("click");
                            }
                            manager.removePicture(position);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(SavedPictures.this,R.string.picture_removed,Toast.LENGTH_LONG).show();
                        }
                    }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(soundEffects.equals("true")) {
                                stopPlayer();
                                startPlayer("click");
                            }
                            Toast.makeText(SavedPictures.this,R.string.delete_cancel,Toast.LENGTH_LONG).show();
                        }
                    }).create().show();

                }else{
                    Toast.makeText(SavedPictures.this,R.string.cannot_remove,Toast.LENGTH_LONG).show();
                }
            }

        });
        ImageView getPictureBtn = findViewById(R.id.btnAddPicture);
        getPictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if(soundEffects.equals("true")) {
                    stopPlayer();
                    startPlayer("click");
                }
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New photo");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From the camera");
                date = new Date();
                file = new File(Environment.getExternalStorageDirectory(),date.toString()+".jpg");
                Uri photoURI = FileProvider.getUriForFile(v.getContext(), "com.example.slidingpuzzle.provider", file);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, CAMERA_REQUEST);
            }
        });
        if(Build.VERSION.SDK_INT>= 23){
            //String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION_REQUEST);
            }
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION_REQUEST);
            }
        }
        Button back = findViewById(R.id.back_mainBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(soundEffects.equals("true")) {
                    stopPlayer();
                    startPlayer("click");
                }
                Intent intent = new Intent(SavedPictures.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == WRITE_PERMISSION_REQUEST){
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,R.string.permission_not_granted, Toast.LENGTH_LONG).show();
            }
        }
        if(background_music.equals("true")){
            stopMusicPlayer();
            startMusicPlayer("open");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK){
            manager.addPicture(new Picture(file.getAbsolutePath()));
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void startPlayer(String mp){
        if(mp.equals("swap")) {
            if (mpSwap == null) {
                mpSwap = MediaPlayer.create(SavedPictures.this, R.raw.swap_click);
            }
            mpSwap.start();
        }
        if(mp.equals("click")){
            if(mpBtnClick==null){
                mpBtnClick = MediaPlayer.create(SavedPictures.this, R.raw.button_click);
            }
            mpBtnClick.start();
        }
    }

    private void stopPlayer() {

        if (mpSwap != null) {
            mpSwap.release();
            mpSwap = null;
        }

        if (mpBtnClick != null) {
            mpBtnClick.release();
            mpBtnClick = null;
        }
    }

    private void startMusicPlayer(String mp){
        if(mp.equals("puzzle")) {
            if (mpPuzzle == null) {
                mpPuzzle = MediaPlayer.create(SavedPictures.this, R.raw.puzzle_music);
                mpPuzzle.setVolume(0.2f,0.2f);
                mpPuzzle.setLooping(true);
            }
            mpPuzzle.start();
        }
        if(mp.equals("open")){
            if(mpOpen==null){
                mpOpen = MediaPlayer.create(SavedPictures.this, R.raw.open_music);
                mpOpen.setVolume(0.2f,0.2f);
                mpOpen.setLooping(true);
            }
            mpOpen.start();
        }
    }

    private void stopMusicPlayer() {

        if (mpPuzzle != null) {
            mpPuzzle.release();
            mpPuzzle = null;
        }

        if (mpOpen != null) {
            mpOpen.release();
            mpOpen = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("soundEffects",soundEffects);
        editor.putString("music",background_music);
        editor.apply();
        stopMusicPlayer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(background_music.equals("true")){
            stopMusicPlayer();
            startMusicPlayer("open");
        }
    }
}