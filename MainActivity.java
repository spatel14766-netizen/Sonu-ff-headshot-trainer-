package com.sonu.fftrainer;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.*;
import android.view.*;
import android.widget.*;
import android.content.Context;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends Activity {
    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(new TrainerView(this));
    }

    static class TrainerView extends View {
        Paint p = new Paint(3);
        Random random = new Random();
        float tx, ty, radius;
        int hits = 0, misses = 0;
        long shownAt, totalReaction = 0;
        boolean started = false;
        String message = "Tap START to begin";
        float downX, downY;
        boolean dragging = false;

        TrainerView(Context c) { super(c); setLayerType(View.LAYER_TYPE_SOFTWARE, null); }

        void nextTarget() {
            float w = getWidth(), h = getHeight();
            radius = Math.max(34, Math.min(w, h) * 0.055f);
            tx = radius + 18 + random.nextFloat() * Math.max(1, w - 2 * radius - 36);
            ty = h * 0.48f + random.nextFloat() * h * 0.28f;
            shownAt = System.currentTimeMillis();
            invalidate();
        }

        @Override protected void onDraw(Canvas c) {
            super.onDraw(c);
            float w=getWidth(), h=getHeight();
            c.drawColor(Color.rgb(13,17,28));
            p.setColor(Color.rgb(25,33,50)); p.setStyle(Paint.Style.FILL);
            c.drawRoundRect(22,25,w-22, h-25,28,28,p);

            p.setColor(Color.WHITE); p.setTextSize(30); p.setTypeface(Typeface.create("sans-serif-black",1));
            c.drawText("SONU FF", 42, 76, p);
            p.setColor(Color.rgb(255,179,0)); p.setTextSize(15);
            c.drawText("HEADSHOT TRAINER", 44, 101, p);

            p.setColor(Color.LTGRAY); p.setTextSize(16); p.setTypeface(Typeface.DEFAULT);
            c.drawText("HITS  " + hits + "      MISSES  " + misses, 42, 145, p);
            int total=hits+misses;
            float acc=total==0?0:(hits*100f/total);
            c.drawText(String.format(Locale.US,"ACCURACY  %.1f%%",acc),42,174,p);
            long avg=hits==0?0:totalReaction/hits;
            c.drawText("AVG REACTION  "+avg+" ms",42,201,p);

            p.setColor(Color.rgb(55,68,91)); p.setTextSize(13);
            c.drawText("Tap the moving target or drag across it.",42,h*0.34f,p);

            if(started) {
                p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(5); p.setColor(Color.rgb(255,179,0));
                c.drawCircle(tx,ty,radius+9,p);
                p.setStyle(Paint.Style.FILL); p.setColor(Color.rgb(255,179,0));
                c.drawCircle(tx,ty,radius,p);
                p.setColor(Color.rgb(30,35,48)); c.drawCircle(tx,ty,radius*0.62f,p);
                p.setColor(Color.WHITE); c.drawCircle(tx,ty,radius*0.23f,p);
            } else {
                p.setColor(Color.rgb(255,179,0)); p.setTextSize(19);
                c.drawText(message,42,h*0.56f,p);
            }

            float bw=w-84, by=h-130;
            p.setColor(Color.rgb(255,179,0)); p.setStyle(Paint.Style.FILL);
            c.drawRoundRect(42,by,w-42,by+58,18,18,p);
            p.setColor(Color.rgb(18,22,31)); p.setTextSize(18);
            p.setTypeface(Typeface.create("sans-serif-black",1));
            String btn=started?"RESET SESSION":"START PRACTICE";
            c.drawText(btn,w/2-p.measureText(btn)/2,by+37,p);
            p.setColor(Color.GRAY); p.setTextSize(11); p.setTypeface(Typeface.DEFAULT);
            c.drawText("Practice simulator • Not connected to Free Fire",42,h-48,p);
        }

        void countHit() {
            hits++; totalReaction += System.currentTimeMillis()-shownAt;
            message="Nice shot!";
            nextTarget();
        }

        @Override public boolean onTouchEvent(android.view.MotionEvent e) {
            float x=e.getX(), y=e.getY(), h=getHeight(), w=getWidth();
            if(e.getAction()==MotionEvent.ACTION_DOWN) {
                downX=x; downY=y; dragging=true; return true;
            }
            if(e.getAction()==MotionEvent.ACTION_UP && dragging) {
                dragging=false;
                if(y>h-155 && x>25 && x<w-25) {
                    if(started) { started=false; hits=misses=0; totalReaction=0; message="Tap START to begin"; }
                    else { started=true; nextTarget(); }
                } else if(started) {
                    float dx=x-tx, dy=y-ty;
                    if(dx*dx+dy*dy <= (radius+22)*(radius+22)) countHit();
                    else { misses++; message="Miss"; invalidate(); }
                }
                invalidate(); return true;
            }
            return true;
        }
    }
}
