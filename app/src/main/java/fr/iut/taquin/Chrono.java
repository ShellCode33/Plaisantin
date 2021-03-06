package fr.iut.taquin;

import android.app.Activity;
import android.graphics.Color;
import android.util.TypedValue;
import android.widget.TextView;

/**
 * Created by shellcode on 3/7/17.
 */

public class Chrono extends TextView {

    private long begin;
    private long end;
    private boolean running = false;
    private Activity activity;

    public Chrono(Activity activity) {
        super(activity.getApplicationContext());
        this.activity = activity;
        setText("00:00:00");
        setTextColor(Color.WHITE);
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
    }

    public void start() {
        begin = System.currentTimeMillis();
        running = true;
        loop();
    }

    public void stop() {
        running = false;
    }

    public long getDuration() {
        return end - begin;
    }

    private void loop() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(running) {
                    end = System.currentTimeMillis();
                    long elaspedTime = end - begin;

                    long milliseconds = elaspedTime % 100;
                    long seconds = elaspedTime / 1000 % 60;
                    long minutes = elaspedTime / (60 * 1000) % 60;

                    final StringBuilder stringBuilder = new StringBuilder();

                    if(minutes < 10)
                        stringBuilder.append("0");

                    stringBuilder.append(minutes);
                    stringBuilder.append(" : ");

                    if(seconds < 10)
                        stringBuilder.append("0");

                    stringBuilder.append(seconds);
                    stringBuilder.append(" : ");

                    if(milliseconds < 10)
                        stringBuilder.append("0");

                    stringBuilder.append(milliseconds);

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setText(stringBuilder.toString());
                        }
                    });

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
