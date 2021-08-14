package com.ducanh.welcome;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.ducanh.update.MainActivity2;
import com.ducanh.update.R;
import com.github.appintro.AppIntro2;
import com.github.appintro.AppIntroCustomLayoutFragment;

import org.jetbrains.annotations.Nullable;

public class hello extends AppIntro2 {
    Intent getMain;
    boolean b = false;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        sharedPreferences = getSharedPreferences (getString (R.string.file_name), MODE_PRIVATE);
        editor = sharedPreferences.edit ();
        getMain = new Intent (hello.this, MainActivity2.class);
        if (sharedPreferences.getBoolean (getString (R.string.rules), false)) {
            startActivity (getMain);
            finishAffinity ();
        }
        addSlide (AppIntroCustomLayoutFragment.newInstance (R.layout.slide_1));
        addSlide (AppIntroCustomLayoutFragment.newInstance (R.layout.slide_2));
        addSlide (AppIntroCustomLayoutFragment.newInstance (R.layout.slide_3));
        addSlide (AppIntroCustomLayoutFragment.newInstance (R.layout.slide_4));
        addSlide (AppIntroCustomLayoutFragment.newInstance (R.layout.slide_5));
        addSlide (AppIntroCustomLayoutFragment.newInstance (R.layout.request));
        addSlide (AppIntroCustomLayoutFragment.newInstance (R.layout.dieukhoan));
        setSystemBackButtonLocked (true);
        showStatusBar (true);
        setSkipButtonEnabled (false);
        setIndicatorColor (Color.RED, Color.BLACK);
        askForPermissions (new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 6, true);
        new CountDownTimer (1000, 1000) {

            public void onTick(final long millisUntilFinished) {
                time = millisUntilFinished / 1000;
            }

            public void onFinish() {
                b = true;
            }
        }.start ();
    }

    @Override
    protected void onSkipPressed(@Nullable Fragment currentFragment) {
        super.onSkipPressed (currentFragment);

    }

    @Override
    protected void onDonePressed(@Nullable Fragment currentFragment) {
        super.onDonePressed (currentFragment);
        CheckBox checkBox = currentFragment.getActivity ().findViewById (R.id.oknhoa);
        if (checkBox.isChecked ()) {
            if (b) {
                editor.putBoolean (getString (R.string.rules), b);
                editor.commit ();
                Toast.makeText (getApplicationContext (), getString (R.string.good_letgo), Toast.LENGTH_SHORT).show ();
                startActivity (getMain);
                finish ();
            } else {
                Toast.makeText (getApplicationContext (), getString (R.string.wait) + time + getString (R.string.wait1), Toast.LENGTH_SHORT).show ();
            }
        } else {
            toast_time (getString (R.string.agree));
        }
    }

    private void toast_time(String string) {
        final Toast toast = Toast.makeText (getApplicationContext (), string, Toast.LENGTH_SHORT);
        toast.show ();
        Handler handler = new Handler ();
        handler.postDelayed (new Runnable () {
            @Override
            public void run() {
                toast.cancel ();
            }
        }, 1000);
    }
}
