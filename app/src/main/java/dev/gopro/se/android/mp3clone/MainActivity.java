package dev.gopro.se.android.mp3clone;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {

    public final int PLAY_IMG = R.mipmap.play;
    public final int PAUSE_IMG = R.mipmap.pause;

    private ImageButton prev, play, next;
    private SeekBar seekBar;
    private MediaPlayer mediaPlayer;
    private TextView songTitle, currentTimeView, totalTimeView;
    private Handler seekBarUpdateHandler;
    private Runnable updateSeekBar;
    private MediaPlayer.OnCompletionListener completionListener;

    public static List<Song> listSong;
    public static int index = 0;
    public static int totalTime = 0;

    private List<Song> getListSong() {
        List<Song> listSong = new ArrayList<Song>();
        // TODO: get songs in storage ?
        Song song1 = new Song("Co The La Tai Sao (Vu.)", R.raw.cothelataisao);
        Song song2 = new Song("You And Me Alone (Hoaprox x Minh)", R.raw.ngauhung);
        Song song3 = new Song("Dancing On My Own (Pixie Lott ft. GD and TOP", R.raw.dancing_on_my_own);
        Song song4 = new Song("Chan Ai (Orange x Superbrothers x Chau Dang Khoa)", R.raw.chan_ai);
        Song song5 = new Song("Viet Mix 2018 (Tam Dolce)", R.raw.viet_mix_2018);

        listSong.add(song1);
        listSong.add(song2);
        listSong.add(song3);
        listSong.add(song4);
        listSong.add(song5);

        return listSong;
    }

    private void init() {
        this.play = (ImageButton) findViewById(R.id.play_pause_btn);
        this.prev = (ImageButton) findViewById(R.id.prev_btn);
        this.next = (ImageButton) findViewById(R.id.next_btn);
        this.seekBar = (SeekBar) findViewById(R.id.seek_bar);
        this.songTitle = (TextView) findViewById(R.id.song_title);
        this.mediaPlayer = MediaPlayer.create(this, listSong.get(index).getResourceId());
        this.currentTimeView = (TextView) findViewById(R.id.current_time);
        this.totalTimeView = (TextView) findViewById(R.id.total_time);

        this.seekBarUpdateHandler = new Handler();
        this.updateSeekBar = new Runnable() {
            @Override
            public void run() {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                currentTimeView.setText(TimeHandler.miliSecondsToTimer(mediaPlayer.getCurrentPosition()));
                seekBarUpdateHandler.postDelayed(this, 1000);
            }
        };

        this.completionListener = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                switchMediaNext(true);
            }
        };

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                    currentTimeView.setText(TimeHandler.miliSecondsToTimer(mediaPlayer.getCurrentPosition()));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        totalTime = this.mediaPlayer.getDuration();

        songTitle.setText(listSong.get(index).getTitle());
        seekBar.setMax(totalTime);
        currentTimeView.setText(TimeHandler.miliSecondsToTimer(0));
        totalTimeView.setText("/   " + TimeHandler.miliSecondsToTimer(totalTime));
        mediaPlayer.setOnCompletionListener(completionListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listSong = getListSong();
        init();
    }

    private void resetSeekBar(MediaPlayer media) {
        this.seekBar.setMax(media.getDuration());
        this.seekBar.setProgress(0);
        this.totalTimeView.setText("/   " + TimeHandler.miliSecondsToTimer(media.getDuration()));
        this.currentTimeView.setText(TimeHandler.miliSecondsToTimer(0));
    }

    private void resetPlayer() {
        if (this.mediaPlayer.isPlaying()) {
            this.mediaPlayer.stop();
        }
    }

    private void createPlayer(int songIndex) {
        this.songTitle.setText(listSong.get(songIndex).getTitle());
        this.mediaPlayer = MediaPlayer.create(this, listSong.get(songIndex).getResourceId());
        this.mediaPlayer.setOnCompletionListener(this.completionListener);
    }

    private void switchMediaNext(boolean isNext) {
        resetPlayer();
        if (isNext) {
            index = index + 1 >= listSong.size() ? 0 : index + 1;
        } else {
            index = index - 1 < 0 ? listSong.size() - 1 : index - 1;
        }
        createPlayer(index);
        resetSeekBar(this.mediaPlayer);
        this.mediaPlayer.start();
        this.play.setImageResource(PAUSE_IMG);
        this.seekBarUpdateHandler.postDelayed(this.updateSeekBar, 1000);
    }

    public void play(View view) {
        if (this.mediaPlayer.isPlaying()) {
            this.mediaPlayer.pause();
            this.seekBarUpdateHandler.removeCallbacks(this.updateSeekBar);
            this.play.setImageResource(PLAY_IMG);
        } else {
            this.mediaPlayer.start();
            this.seekBarUpdateHandler.postDelayed(this.updateSeekBar, 1000);
            this.play.setImageResource(PAUSE_IMG);
        }
    }

    public void next(View view) {
        switchMediaNext(true);
    }

    public void previous(View view) {
        switchMediaNext(false);
    }
}
