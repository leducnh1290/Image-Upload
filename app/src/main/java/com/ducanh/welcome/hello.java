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
import com.github.appintro.AppIntroPageTransformerType;

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
        setSkipButtonEnabled (false);
       setWizardMode (true);
       showStatusBar (true);
       setIndicatorEnabled (true);
        setIndicatorColor (Color.RED, Color.BLACK);
        askForPermissions (new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 6, true);
        new CountDownTimer (30000, 1000) {

            public void onTick(final long millisUntilFinished) {
                time = millisUntilFinished / 1000;
            }

            public void onFinish() {
                b = true;
            }
        }.start ();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed ();
    }

    @Override
    protected void onDonePressed(@Nullable Fragment currentFragment) {
        super.onDonePressed (currentFragment);
            if (b) {
                editor.putBoolean (getString (R.string.rules), b);
                editor.commit ();
                Toast.makeText (getApplicationContext (), getString (R.string.good_letgo), Toast.LENGTH_SHORT).show ();
                startActivity (getMain);
                finishAffinity ();
            } else {
                Toast.makeText (getApplicationContext (), getString (R.string.wait) + time + getString (R.string.wait1), Toast.LENGTH_SHORT).show ();
            }
    }
}
