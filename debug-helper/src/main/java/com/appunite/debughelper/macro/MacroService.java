package com.appunite.debughelper.macro;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.appunite.debughelper.R;

import javax.annotation.Nonnull;

public class MacroService extends Service {

    private static Activity activity;
    private WindowManager windowManager;
    private ImageView chatHead;
    private DeployMacroListener listener;

    @Override
    public IBinder onBind(Intent intent) {
        listener = (DeployMacroListener) intent;
        return null;
    }

    public static Intent newInstance(@Nonnull final Activity activity) {
        MacroService.activity = activity;
        return new Intent(activity, MacroService.class);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        listener = (DeployMacroListener) activity;
        chatHead = new ImageView(this);
        chatHead.setImageResource(R.drawable.macro_button);
        chatHead.setBackgroundResource(R.drawable.circle_background);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;

        windowManager.addView(chatHead, params);

        chatHead.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            private boolean shouldClick = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        shouldClick = true;
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (shouldClick) {
                            listener.deployMacro();
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(chatHead, params);
                        shouldClick = false;
                        return true;
                }
                return false;
            }
        });

    }

    @Override
    public boolean stopService(final Intent name) {
        return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatHead != null) windowManager.removeView(chatHead);
    }

    public interface DeployMacroListener {

        void deployMacro();
    }

}