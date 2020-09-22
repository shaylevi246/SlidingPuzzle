package com.example.slidingpuzzle;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;

public class SolvePuzzle extends AppCompatActivity {
    private static final int SHUFFLE_NUMBER=150;
    private SharedPreferences sp;
    private ArrayList<Bitmap> splitView;
    private ArrayList<Bitmap> mixedPuzzle;
    private ArrayList<Bitmap> complete;
    private ArrayList<Integer> neighbors;
    private GridView gv;
    private PuzzleAdapter puzzleAdapter;
    private int empty_space;
    private Bitmap emptyTile;
    private ImageView victory;
    private String path;
    private boolean checkShufflePressed;
    private boolean checkRefreshPressed;
    private ImageView btnRefresh;
    private Button shuffleBtn;
    private int col;
    private int num_steps = 0;
    private int record = 1000;
    private TextView steps;
    private TextView bestRecord;
    private MediaPlayer mpSwap;
    private MediaPlayer mpBtnClick;
    private MediaPlayer mpPuzzle;
    private MediaPlayer mpOpen;
    private MediaPlayer mpCheering;
    private String soundEffects;
    private String background_music;
    private PopupMenu popupMenu;
    private int show;
    private ImageView arrow4;
    private boolean isGameEnd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_puzzle);

        getSupportActionBar().hide();

        // indicate if the game status
        isGameEnd = false;

        // init the neighbors ArrayList
        neighbors = new ArrayList<>();

        sp = getSharedPreferences("PuzzlesRecordsDetail", MODE_PRIVATE);

        RelativeLayout relativeLayout = findViewById(R.id.backColorShow);
        AnimationDrawable animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

        final Intent intent = getIntent();
        int row = intent.getIntExtra("row", 0);
        col = intent.getIntExtra("col", 0);
        gv = findViewById(R.id.IvPuzzle);
        gv.setNumColumns(col);

        bestRecord = findViewById(R.id.bestRecordTV);
        background_music = sp.getString("music","true");
        soundEffects = sp.getString("soundEffects","true");
        record = sp.getInt(String.valueOf(col), 1000);
        show = sp.getInt("show",1);
        if(record == 1000){
            bestRecord.setText("--");
        }else{
            bestRecord.setText(String.valueOf(record));
        }
        if(background_music.equals("true")){
            stopMusicPlayer();
            startMusicPlayer("puzzle");
        }else {
            stopMusicPlayer();
        }
        arrow4 = findViewById(R.id.arrow_4);
        if(show == 4){
            arrow4.animate().alpha(1).start();
            ObjectAnimator move = ObjectAnimator.ofFloat(arrow4, "translationY", -20).setDuration(1000);
            move.setRepeatCount(20);
            move.setRepeatMode(ValueAnimator.REVERSE);
            move.start();
            show++;
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        checkShufflePressed = false;
        checkRefreshPressed = true;
        steps = findViewById(R.id.stepCounterTV);
        steps.setText("00");


        btnRefresh = findViewById(R.id.btnRefresh);
        btnRefresh.animate().alpha((float)0.2);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkShufflePressed == true) {
                    if(soundEffects.equals("true")) {
                        stopPlayer();
                        startPlayer("click");
                    }
                    if (mixedPuzzle.equals(complete) && complete.get(col -1).equals( puzzleAdapter.getItem(col -1))) {
                        checkShufflePressed = false;
                        checkRefreshPressed = true;
                        Intent intent2 = new Intent(SolvePuzzle.this, SolvePuzzle.class);
                        finish();
                        intent2.putExtra("row", intent.getIntExtra("row", 0));
                        intent2.putExtra("col", intent.getIntExtra("col", 0));
                        intent2.putExtra("path", path);
                        startActivity(intent2);

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SolvePuzzle.this);
                        builder.setMessage(R.string.restart);
                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                checkShufflePressed = false;
                                checkRefreshPressed = true;
                                if(soundEffects.equals("true")) {
                                    stopPlayer();
                                    startPlayer("click");
                                }

                                Intent intent2 = new Intent(SolvePuzzle.this, SolvePuzzle.class);
                                finish();
                                intent2.putExtra("row", intent.getIntExtra("row", 0));
                                intent2.putExtra("col", intent.getIntExtra("col", 0));
                                intent2.putExtra("path", path);
                                startActivity(intent2);
                            }
                        }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(soundEffects.equals("true")) {
                                    stopPlayer();
                                    startPlayer("click");
                                }

                                Toast.makeText(SolvePuzzle.this, R.string.continue_state, Toast.LENGTH_LONG).show();
                            }
                        }).create().show();
                    }
                }
            }
        });

        Bitmap bitmap = null;
        path = intent.getStringExtra("path");
        if(path!=null){
            switch (path){
                case "dolphin": bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dolphin);
                    bitmap = Bitmap.createScaledBitmap(bitmap, width, (int)(height*0.75), false);
                    break;
                case "lion": bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lion);
                    bitmap = Bitmap.createScaledBitmap(bitmap, width, (int)(height*0.75), false);
                    break;
                case "nature": bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.nature);
                    bitmap = Bitmap.createScaledBitmap(bitmap, width, (int)(height*0.75), false);
                    break;
                case "pic4": bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pic4);
                    bitmap = Bitmap.createScaledBitmap(bitmap, width, (int)(height*0.75), false);
                    break;
                case "pic5": bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pic5);
                    bitmap = Bitmap.createScaledBitmap(bitmap, width, (int)(height*0.75), false);
                    break;
                case "pic6": bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pic6);
                    bitmap = Bitmap.createScaledBitmap(bitmap, width, (int)(height*0.75), false);
                    break;
                default:
                    bitmap = BitmapFactory.decodeFile(path);
                    bitmap = Bitmap.createScaledBitmap(bitmap, width, (int)(height*0.75), false);
                    break;
            }
            //the array of small Bitmaps which created based on the chosen picture
            splitView = splitBitmap(bitmap, col, row);

            puzzleAdapter = new PuzzleAdapter(getApplicationContext(),splitView);
            gv.setAdapter(puzzleAdapter);
        }
        shuffleBtn = findViewById(R.id.btnShuffle);
        shuffleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrow4.animate().alpha(0).start();
                if (checkRefreshPressed == true) {
                    checkShufflePressed = true;
                    checkRefreshPressed = false;
                    shuffleBtn.animate().alpha((float)0.2);
                    if(soundEffects.equals("true")) {
                        stopPlayer();
                        startPlayer("click");
                    }
                    btnRefresh.animate().alpha(1);
                    steps.setText("00");
                    num_steps = 0;
                    mixedPuzzle = new ArrayList<>(splitView);
                    emptyTile = BitmapFactory.decodeResource(getResources(), R.drawable.zemptytile);
                    int position = mixedPuzzle.size() - 1;
                    empty_space = position;
                    //change the size of the blank tile to be the same size of the tile that we replace
                    emptyTile = Bitmap.createScaledBitmap(emptyTile, splitView.get(position).getWidth(), splitView.get(position).getHeight(), true);
                    //remove the last bitmap in the array
                    mixedPuzzle.remove(position);
                    //add the blank tile to the array
                    mixedPuzzle.add(emptyTile);
                    //a new array of bitmaps to compare the outcome
                    complete = new ArrayList<>(mixedPuzzle);
                    shuffle();
                    empty_space = mixedPuzzle.indexOf(emptyTile);
                    //call the adapter with our context and the new array and set the grid with the new array
                    puzzleAdapter = new PuzzleAdapter(getApplicationContext(), mixedPuzzle);
                    gv.setAdapter(puzzleAdapter);
                    gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if (checkEmptyUp(position) || checkEmptyDown(position) || checkEmptyRight(position) || checkEmptyLeft(position)) {
                                Collections.swap(mixedPuzzle, position, empty_space);


                                if(soundEffects.equals("true")) {
                                    stopPlayer();
                                    startPlayer("swap");
                                }


                                num_steps++;
                                if (num_steps < 10) {
                                    steps.setText('0' + String.valueOf(num_steps));
                                } else {
                                    steps.setText(String.valueOf(num_steps));
                                }

                                empty_space = position;
                                puzzleAdapter.notifyDataSetChanged();
                                gv.invalidateViews();
                                // check if puzzle has been solved
                                if (mixedPuzzle.equals(complete) && complete.get(col - 1).equals(puzzleAdapter.getItem(col - 1))) {
                                    puzzleAdapter = new PuzzleAdapter(getApplicationContext(), splitView);
                                    gv.setAdapter(puzzleAdapter);
                                    shuffleBtn.animate().alpha((float) 0.2);

                                    //sets the record
                                    if (num_steps < record) {
                                        record = num_steps;
                                        bestRecord.setText(String.valueOf(record));
                                    }
                                    stopMusicPlayer();
                                    startMusicPlayer("cheer");
                                    victory = findViewById(R.id.victoryIV);
                                    victory.animate().alpha(1).setDuration(100);
                                    victory.animate().scaleX(2).scaleY(2).setDuration(1000).withEndAction(new Runnable() {
                                        @Override
                                        public void run() {
                                            victory.animate().scaleX(0.5f).scaleY(0.5f).setDuration(1000).withEndAction(new Runnable() {
                                                @Override
                                                public void run() {
                                                    victory.animate().scaleX(2).scaleY(2).setDuration(1000);
                                                }
                                            });
                                        }
                                    }).start();


                                }
                            } else {
                                Toast.makeText(SolvePuzzle.this, R.string.wrong_move, Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
        });
        Button optionsBtn = findViewById(R.id.btnOptions);
        optionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                if(soundEffects.equals("true")) {
                    stopPlayer();
                    startPlayer("click");
                }


                popupMenu = new PopupMenu(SolvePuzzle.this,v);
                if(soundEffects.equals("true") && background_music.equals("true")) {
                    getMenuInflater().inflate(R.menu.back_and_instructions, popupMenu.getMenu());
                }
                if(soundEffects.equals("false") && background_music.equals("true")) {
                    getMenuInflater().inflate(R.menu.back_and_instructions_2, popupMenu.getMenu());
                }
                if(soundEffects.equals("true") && background_music.equals("false")) {
                    getMenuInflater().inflate(R.menu.back_and_instructions_3, popupMenu.getMenu());
                }
                if(soundEffects.equals("false") && background_music.equals("false")) {
                    getMenuInflater().inflate(R.menu.back_and_instructions_4, popupMenu.getMenu());
                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.return_main:
                                onBackPressed();
                                return true;
                            case R.id.instructions_menu:
                                if(soundEffects.equals("true")) {
                                    stopPlayer();
                                    startPlayer("click");
                                }
                                showDialog();
                                return true;
                            case R.id.sound_effects:
                                if(soundEffects.equals("true")){
                                    stopPlayer();
                                    soundEffects ="false";

                                }else {
                                    stopPlayer();
                                    startPlayer("click");
                                    soundEffects = "true";
                                }
                                return true;
                            case R.id.music:
                                if(soundEffects.equals("true")) {
                                    stopPlayer();
                                    startPlayer("click");
                                }
                                if(background_music.equals("true")){
                                    stopMusicPlayer();
                                    background_music ="false";

                                }else {
                                    stopMusicPlayer();
                                    startMusicPlayer("puzzle");
                                    background_music = "true";
                                }
                                return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    private ArrayList<Bitmap> splitBitmap(Bitmap CompleteImage, int columnsCount, int rowsCount)
    {
        // Allocate array of bitmaps to hold the splitted image pieces
        ArrayList<Bitmap> splittedImages = new ArrayList<>();
        int width, height;

        width = CompleteImage.getWidth() / columnsCount; // Divide the original bitmap width by the desired vertical columns count
        height = CompleteImage.getHeight() / rowsCount;  // Divide the original bitmap height by the desired horizontal rows count

        // Loop the array and create bitmaps for each coordinate
        for (int y = 0; y < columnsCount; ++y)
        {
            for (int x = 0; x < rowsCount; ++x)
            {
                splittedImages.add(Bitmap.createBitmap(CompleteImage, x * width, y * height, width, height)); // Create the sliced bitmap
            }
        }
        // Return the splitted images bitmaps array
        return splittedImages;
    }

    private Boolean checkEmptyUp(int position){
        if(position - gv.getNumColumns() >= 0 && puzzleAdapter.getItem(position- gv.getNumColumns())  ==emptyTile){
            return true;
        }else{
            return false;
        }
    }

    private Boolean checkEmptyDown(int position){
        if(position + gv.getNumColumns() <= mixedPuzzle.size()-1 && puzzleAdapter.getItem(position+ gv.getNumColumns())  ==emptyTile){
            return true;
        }else{
            return false;
        }
    }

    private Boolean checkEmptyRight(int position){
        int row_size = gv.getNumColumns();
        //checks if we jumped one row up AND if the right position is outside the Grid AND if the right position is empty tile
        if( (position + 1)% row_size != 0 && position + 1 <= mixedPuzzle.size()-1 && puzzleAdapter.getItem(position+1)  == emptyTile){
            return true;
        }else{
            return false;
        }
    }

    private Boolean checkEmptyLeft(int position){
        int row_size = gv.getNumColumns();
        if((position-1)% row_size != row_size-1 && position - 1 >= 0 && puzzleAdapter.getItem(position-1)  ==emptyTile){
            return true;
        }else{
            return false;
        }

    }

    private void shuffle() //shuffle the puzzle pieces
    {
        //get the last index
        int currentTilePosition = mixedPuzzle.size()-1;

        //loop for shuffling
        for(int i = 0; i < SHUFFLE_NUMBER; i++)
        {
            //send to function to get the neighbors around the empty tile
            getNeighbor(currentTilePosition);
            //shuffle the array of neighbors positions
            Collections.shuffle(neighbors);
            //get the first neighbor index
            int randTile = neighbors.get(0);
            //swapping between the neighbor and the empty tile
            Collections.swap(mixedPuzzle, currentTilePosition, randTile);
            //the new position of the empty tile
            currentTilePosition = randTile;
        }
    }

    private void getNeighbor(int position) {
        int row_size = gv.getNumColumns();
        neighbors.clear();

        //check if the neighbor to the right of the empty tile is valid
        if ((position + 1)% row_size != 0 && position+1 < mixedPuzzle.size()){
            neighbors.add(position+1);
        }
        //check if the neighbor to the left of the empty tile is valid
        if((position-1)% row_size != row_size-1 && position-1 >0){
            neighbors.add(position-1);
        }
        //check if the neighbor above the empty tile is valid
        if(position-row_size > 0){
            neighbors.add(position-row_size);
        }
        //check if the neighbor below the empty tile is valid
        if(position+row_size < mixedPuzzle.size()){
            neighbors.add(position+row_size);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveCurrentGamePreferences();
    }

    private void saveCurrentGamePreferences() {
        SharedPreferences.Editor editor = sp.edit();

        switch (col){
            case 3: editor.putInt("3",record);
                break;
            case 4: editor.putInt("4",record);
                break;
            case 5: editor.putInt("5",record);
                break;
            case 6: editor.putInt("6",record);
                break;
            case 7: editor.putInt("7",record);
                break;
            default:
                break;
        }
        editor.putString("soundEffects",soundEffects);
        editor.putString("music",background_music);
        editor.putInt("show",show);
        editor.apply();
        stopMusicPlayer();
    }

    private void showDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(SolvePuzzle.this);
        final View dialogView = getLayoutInflater().inflate(R.layout.instruction_dialog,null);
        Button backBtn = dialogView.findViewById(R.id.back_from_dialog);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.show();
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(soundEffects.equals("true")) {
                    stopPlayer();
                    startPlayer("click");
                }
                dialog.dismiss();
            }
        });
    }

    private void startPlayer(String mp){
        if(mp.equals("swap")) {
            if (mpSwap == null) {
                mpSwap = MediaPlayer.create(SolvePuzzle.this, R.raw.swap_click);
            }
            mpSwap.start();
        }
        if(mp.equals("click")){
            if(mpBtnClick==null){
                mpBtnClick = MediaPlayer.create(SolvePuzzle.this, R.raw.button_click);
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
                mpPuzzle = MediaPlayer.create(SolvePuzzle.this, R.raw.puzzle_music);
                mpPuzzle.setVolume(0.2f,0.2f);
                mpPuzzle.setLooping(true);
                mpPuzzle.start();
            }
        }
        if(mp.equals("open")){
            if(mpOpen==null){
                mpOpen = MediaPlayer.create(SolvePuzzle.this, R.raw.open_music);
                mpOpen.setVolume(0.2f,0.2f);
                mpOpen.setLooping(true);
                mpOpen.start();
            }
        }
        if(mp.equals("cheer")){
            if(mpCheering==null){
                mpCheering = MediaPlayer.create(SolvePuzzle.this, R.raw.cheering_and_clapping);
                mpCheering.setVolume(0.2f,0.2f);
                mpCheering.setLooping(true);
                mpCheering.start();

                // game ends
                isGameEnd = true;
            }
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
        if (mpCheering != null) {
            mpCheering.release();
            mpCheering = null;
        }
    }

    @Override
    public void onBackPressed() {
        if(!isGameEnd)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(SolvePuzzle.this);
            builder.setMessage(R.string.restart);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) { backToMainActivity();
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) { backToGame(); }
            });

            builder.create();
            builder.show();
        }
        else
        {
            backToMainActivity();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(background_music.equals("true")){
            stopMusicPlayer();
            startMusicPlayer("puzzle");
        }
    }

    private void backToMainActivity() {
        if(soundEffects.equals("true")) {
            stopPlayer();
            startPlayer("click");
        }
        Intent intent = new Intent(SolvePuzzle.this, MainActivity.class);
        intent.putExtra("path",path);
        startActivity(intent);
    }

    private void backToGame() {
        if(soundEffects.equals("true")) {
            stopPlayer();
            startPlayer("click");
        }

        //Toast.makeText(SolvePuzzle.this, R.string.continue_state, Toast.LENGTH_LONG).show();
    }
}