package com.webgi.ali.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
// برای کاور اهنگ
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    ImageView music_cover;
    Button playmusicBtn;
    SeekBar music_seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaPlayer = new MediaPlayer();
        music_cover = findViewById(R.id.music_cover);
        playmusicBtn = findViewById(R.id.playBtn);
        music_seekBar = findViewById(R.id.seekBar);

        try {
            mediaPlayer.setDataSource(getResources().openRawResourceFd(R.raw.mymusic));
            mediaPlayer.prepare(); //فایل صوتی رو برای پخش آماده می‌کنه
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // فهمیدن طول اهنگ برای ست کردن حداکثر حرکت سیکبار
        int musictime = mediaPlayer.getDuration() / 1000;
        music_seekBar.setMax(musictime);

        // استخراج کاور آهنگ
        setAlbumArt(R.raw.mymusic);

        playmusicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    playmusicBtn.setText("Play");
                } else {
                    mediaPlayer.start();
                    playmusicBtn.setText("Pause");
                }
            }
        });



        music_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {  //  وقتی کاربر تغییر داده باشه
                    mediaPlayer.seekTo(progress * 1000);  // موزیک رو به اندازه ای که سیکبار پر شده جلو ببر
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        //برای اینکه سیکبار بطور دائم موقع پخش حرکت کنه
        Handler handler = new Handler();
        Runnable updateSeekBar = new Runnable() {
            @Override
            public void run() {
                int currentPosition = mediaPlayer.getCurrentPosition() / 1000;  // زمان جاری موزیک به ثانیه
                music_seekBar.setProgress(currentPosition);  // بروزرسانی SeekBar
                handler.postDelayed(this, 1000);  // بعد 1 ثانیه دوباره تابع خودمون رو صدا میزنیم
            }
        };
        // اینم برای اینه در ابتدا باید تابع رو صدا بزنیم تا کار کنه
        handler.postDelayed(updateSeekBar, 0);


    }

    // تابع برای استخراج و نمایش کاور آلبوم
    private void setAlbumArt(int resourceId) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            // دریافت مسیر فایل از منابع raw
            String filePath = "android.resource://" + getPackageName() + "/" + resourceId;
            retriever.setDataSource(this, android.net.Uri.parse(filePath));

            byte[] art = retriever.getEmbeddedPicture();  // دریافت کاور به صورت byte array
            if (art != null) {
                Bitmap albumArt = BitmapFactory.decodeByteArray(art, 0, art.length);
                music_cover.setImageBitmap(albumArt);  // نمایش کاور در ImageView
            } else {
                music_cover.setImageResource(R.drawable.default_cover);  // در صورت نبود کاور، عکس پیش‌فرض نمایش داده می‌شود
            }
        } catch (Exception e) {
            e.printStackTrace();
            music_cover.setImageResource(R.drawable.default_cover);
        }
    }




    // برای اینکه بعد بستن برنامه منابع سیستم ازاد بشه حتما باید نوشت این کد رو
    //super.onDestroy() باعث میشه که کارهای پیش‌فرض کلاس پدر (مثل پاک کردن منابع) انجام بشه قبل از اینکه کدهای خودم اجرا بشه

    @Override //به کامپایلر می‌گه که متد شما قصد داره یک متد از کلاس پدر یا اینترفیس رو بازنویسی کنه
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();// آزادسازی منابع
            mediaPlayer = null; // خالی کردن متغیر حهت اطمینان از نشت حافظه
        }
    }
//    end
}
