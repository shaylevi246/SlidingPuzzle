package com.example.slidingpuzzle;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Button btnPlay;
    private String selected_size;
    private String path;
    private int show;
    private TextView levelTv;
    private MediaPlayer mpBtnClick;
    private MediaPlayer mpSwap;
    private MediaPlayer mpPuzzle;
    private MediaPlayer mpOpen;
    private String soundEffects;
    private String background_music;
    private SharedPreferences sp;
    private ImageView arrow2;
    private ImageView arrow3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RelativeLayout relativeLayout = findViewById(R.id.backColorMain);
        AnimationDrawable animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

        /*DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);*/

        sp = getSharedPreferences("PuzzlesRecordsDetail", MODE_PRIVATE);
        background_music = sp.getString("music","true");
        soundEffects = sp.getString("soundEffects","true");
        show = sp.getInt("show",1);

        if(background_music.equals("true")){
            stopMusicPlayer();
            startMusicPlayer("open");
        }

        levelTv = findViewById(R.id.sizeTv);
        selected_size = "3x3";
        levelTv.setText(selected_size);

        // flipping title
        startTitleAnimations();

        ImageView imageView = findViewById(R.id.chosenImage);
        Intent intent = getIntent();
        path = intent.getStringExtra("chosen picture");
        if(path!=null){
            switch (path){
                case "dolphin": imageView.setImageResource(R.drawable.dolphin);
                    break;
                case "lion": imageView.setImageResource(R.drawable.lion);
                    break;
                case "nature": imageView.setImageResource(R.drawable.nature);
                    break;
                case "pic4": imageView.setImageResource(R.drawable.pic4);
                    break;
                case "pic5": imageView.setImageResource(R.drawable.pic5);
                    break;
                case "pic6": imageView.setImageResource(R.drawable.pic6);
                    break;
                default:
                    imageView.setImageBitmap(BitmapFactory.decodeFile(path));
                    break;
            }
        }

        Button btnChoosePic = findViewById(R.id.choosePuzzle);
        btnChoosePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(soundEffects.equals("true")) {
                    stopPlayer();
                    startPlayer("click");
                }
                Intent intent = new Intent(MainActivity.this,SavedPictures.class);
                finish();
                startActivity(intent);

            }
        });
        btnPlay = findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x,y;
                if(soundEffects.equals("true")) {
                    stopPlayer();
                    startPlayer("click");
                }

                switch (selected_size) {
                    case "3x3":
                        x = y = 3;
                        break;
                    case "4x4":
                        x = y = 4;
                        break;
                    case "5x5":
                        x = y = 5;
                        break;
                    case "6x6":
                        x = y = 6;
                        break;
                    case "7x7":
                        x = y = 7;
                        break;
                    default:
                        x = y = 3;
                }
                if(path != null) {
                    stopMusicPlayer();
                    Intent intent1 = new Intent(MainActivity.this, SolvePuzzle.class);
                    intent1.putExtra("row", x);
                    intent1.putExtra("col", y);
                    intent1.putExtra("path", path);
                    startActivity(intent1);
                }else{
                    Toast.makeText(MainActivity.this, R.string.cannot_continue,Toast.LENGTH_LONG).show();
                }

            }
        });

        //arrows for instruction start
        ImageView arrow1 = findViewById(R.id.arrow_1);
        arrow2 = findViewById(R.id.arrow_2);
        arrow3 = findViewById(R.id.arrow_3);
        LinearLayout linearLayout = findViewById(R.id.secondLin);
        //linearLayout2 = findViewById(R.id.thirdLin);
        if(show == 1) {
            arrow1.animate().alpha(1).start();
            ObjectAnimator move = ObjectAnimator.ofFloat(arrow1, "translationX", -20).setDuration(1000);
            move.setRepeatCount(100);
            move.setRepeatMode(ValueAnimator.REVERSE);
            move.start();
            btnPlay.animate().alpha(0).start();
            linearLayout.animate().alpha(0).start();
            show++;
        }else if(show == 2){
            arrow2.animate().alpha(1).withEndAction(new Runnable() {
                @Override
                public void run() {
                    arrow2.animate().alpha(0).setStartDelay(3000);
                }
            }).start();
            ObjectAnimator move = ObjectAnimator.ofFloat(arrow2, "translationX", -20).setDuration(1000);
            move.setRepeatCount(100);
            move.setRepeatMode(ValueAnimator.REVERSE);
            move.start();
            btnPlay.animate().alpha(0).withEndAction(new Runnable() {
                @Override
                public void run() {
                    btnPlay.animate().setStartDelay(3500).alpha(1);
                }
            }).start();

            show++;
            arrow3.animate().alpha(1).setStartDelay(3700).withEndAction(new Runnable() {
                @Override
                public void run() {
                    arrow3.animate().alpha(0).setStartDelay(5000);
                }
            }).start();
            ObjectAnimator move3 = ObjectAnimator.ofFloat(arrow3, "translationX", -20).setDuration(1000);
            move3.setRepeatCount(100);
            move3.setRepeatMode(ValueAnimator.REVERSE);
            move3.start();
            show++;

        } // arrows for instructions end
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogLevel();
            }
        });
    }

    private void startTitleAnimations()
    {
        TextView mySlidingTextView = findViewById(R.id.mySlidingText);
        ImageView p = findViewById(R.id.letP);
        ImageView u = findViewById(R.id.letU);
        ImageView z = findViewById(R.id.letZ);
        ImageView z2 = findViewById(R.id.letZ2);
        ImageView l = findViewById(R.id.letL);
        ImageView e = findViewById(R.id.letE);
        ImageView s = findViewById(R.id.letS);
        ObjectAnimator animatorMySlidingX = ObjectAnimator.ofFloat(mySlidingTextView, "scaleX", 1.1f).setDuration(1000);
        ObjectAnimator animatorMySlidingY = ObjectAnimator.ofFloat(mySlidingTextView, "scaleY", 1.1f).setDuration(1000);
        AnimatorSet set1 = new AnimatorSet();
        set1.playTogether(animatorMySlidingX, animatorMySlidingY);
        ObjectAnimator animatorMySlidingXtoSmall = ObjectAnimator.ofFloat(mySlidingTextView, "scaleX", 1).setDuration(1000);
        ObjectAnimator animatorMySlidingYtoSmall = ObjectAnimator.ofFloat(mySlidingTextView, "scaleY", 1).setDuration(1000);
        AnimatorSet set2 = new AnimatorSet();
        set2.playTogether(animatorMySlidingXtoSmall, animatorMySlidingYtoSmall);

        ObjectAnimator animatorP = ObjectAnimator.ofFloat(p, "rotation", 360).setDuration(500);
        ObjectAnimator animatorU = ObjectAnimator.ofFloat(u, "rotation", 360).setDuration(500);
        ObjectAnimator animatorZ = ObjectAnimator.ofFloat(z, "rotation", 360).setDuration(500);
        ObjectAnimator animatorZ2 = ObjectAnimator.ofFloat(z2, "rotation", 360).setDuration(500);
        ObjectAnimator animatorL = ObjectAnimator.ofFloat(l, "rotation", 360).setDuration(500);
        ObjectAnimator animatorE = ObjectAnimator.ofFloat(e, "rotation", 360).setDuration(500);
        ObjectAnimator animatorS = ObjectAnimator.ofFloat(s, "rotation", 360).setDuration(500);
        AnimatorSet set = new AnimatorSet();
        set.playSequentially(set1, set2, animatorP, animatorU, animatorZ, animatorZ2, animatorL, animatorE, animatorS);
        set.addListener(new AnimatorListenerAdapter() {

            private boolean mCanceled;

            @Override
            public void onAnimationStart(Animator animation) {
                mCanceled = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCanceled = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!mCanceled) {
                    animation.setStartDelay(3000);
                    animation.start();
                }
            }

        });
        set.start();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        sp = getSharedPreferences("PuzzlesRecordsDetail", MODE_PRIVATE);
        soundEffects = sp.getString("soundEffects","true");
        background_music = sp.getString("music","true");
        show = sp.getInt("show", 1);
        if(background_music.equals("true")){
            stopMusicPlayer();
            startMusicPlayer("open");
        }
        invalidateOptionsMenu();
        setIntent(intent);

    }

    private void showDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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

    private void showDialogLevel() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final View dialogView = getLayoutInflater().inflate(R.layout.difficulty_dialog, null);
        Button lvl3 = dialogView.findViewById(R.id.lvl_3x3);
        Button lvl4 = dialogView.findViewById(R.id.lvl_4x4);
        Button lvl5 = dialogView.findViewById(R.id.lvl_5x5);
        Button lvl6 = dialogView.findViewById(R.id.lvl_6x6);
        Button lvl7 = dialogView.findViewById(R.id.lvl_7x7);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.show();
        lvl3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (soundEffects.equals("true")) {
                    stopPlayer();
                    startPlayer("click");
                }
                selected_size = "3x3";
                levelTv.setText(selected_size);
                dialog.dismiss();
            }
        });
        lvl4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (soundEffects.equals("true")) {
                    stopPlayer();
                    startPlayer("click");
                }
                selected_size = "4x4";
                levelTv.setText(selected_size);
                dialog.dismiss();
            }
        });
        lvl5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (soundEffects.equals("true")) {
                    stopPlayer();
                    startPlayer("click");
                }
                selected_size = "5x5";
                levelTv.setText(selected_size);
                dialog.dismiss();
            }
        });
        lvl6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (soundEffects.equals("true")) {
                    stopPlayer();
                    startPlayer("click");
                }
                selected_size = "6x6";
                levelTv.setText(selected_size);
                dialog.dismiss();
            }
        });
        lvl7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (soundEffects.equals("true")) {
                    stopPlayer();
                    startPlayer("click");
                }
                selected_size = "7x7";
                levelTv.setText(selected_size);
                dialog.dismiss();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(soundEffects.equals("true") && background_music.equals("true")) {
            getMenuInflater().inflate(R.menu.main_on_on, menu);
        }
        if(soundEffects.equals("false") && background_music.equals("true")) {
            getMenuInflater().inflate(R.menu.main_off_on, menu);
        }
        if(soundEffects.equals("true") && background_music.equals("false")) {
            getMenuInflater().inflate(R.menu.main_on_off, menu);
        }
        if(soundEffects.equals("false") && background_music.equals("false")) {
            getMenuInflater().inflate(R.menu.main_off_off, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
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
                invalidateOptionsMenu();
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
                    startMusicPlayer("open");
                    background_music = "true";
                }
                invalidateOptionsMenu();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startPlayer(String mp){
        if(mp.equals("click")){
            if(mpBtnClick==null){
                mpBtnClick = MediaPlayer.create(MainActivity.this, R.raw.button_click);
            }
            mpBtnClick.start();
        }
        if(mp.equals("swap")) {
            if (mpSwap == null) {
                mpSwap = MediaPlayer.create(MainActivity.this, R.raw.swap_click);
            }
            mpSwap.start();
        }
    }

    private void stopPlayer() {
        if (mpBtnClick != null) {
            mpBtnClick.release();
            mpBtnClick = null;
        }
        if (mpSwap != null) {
            mpSwap.release();
            mpSwap = null;
        }
    }

    private void startMusicPlayer(String mp){
        if(mp.equals("puzzle")) {
            if (mpPuzzle == null) {
                mpPuzzle = MediaPlayer.create(MainActivity.this, R.raw.puzzle_music);
                mpPuzzle.setVolume(0.2f,0.2f);
                mpPuzzle.setLooping(true);
            }
            mpPuzzle.start();
        }
        if(mp.equals("open")){
            if(mpOpen==null){
                mpOpen = MediaPlayer.create(MainActivity.this, R.raw.open_music);
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
        editor.putInt("show",show);
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