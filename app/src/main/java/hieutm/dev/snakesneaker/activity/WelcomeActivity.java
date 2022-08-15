package hieutm.dev.snakesneaker.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.viewpager.widget.ViewPager;

import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.adapter.WelcomeAdapter;
import hieutm.dev.snakesneaker.util.Method;
import com.google.android.material.button.MaterialButton;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class WelcomeActivity extends AppCompatActivity {

    private Method method;
    private ViewPager viewPager;
    private int[] layouts;
    private MaterialButton btnSkip, btnNext;
    private WelcomeAdapter welcomeAdapter;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        method = new Method(WelcomeActivity.this);
        method.forceRTLIfSupported();
        switch (method.themMode()) {
            case "system":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                break;
        }

        // Checking for first time launch - before calling setContentView()
        if (!method.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }

        // Making notification bar transparent
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        // making notification bar transparent
        method.changeStatusBarColor();

        setContentView(R.layout.activity_welcome);

        viewPager = findViewById(R.id.view_pager);
        btnSkip = findViewById(R.id.btn_skip);
        btnNext = findViewById(R.id.btn_next);

        btnNext.setText(getResources().getString(R.string.next));

        // layouts of all welcome sliders
        // add few more layouts if you want
        layouts = new int[]{
                R.layout.welcome_slide_one,
                R.layout.welcome_slide_two,
                R.layout.welcome_slide_three};

        welcomeAdapter = new WelcomeAdapter(WelcomeActivity.this, layouts);
        viewPager.setAdapter(welcomeAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btnSkip.setOnClickListener(v -> launchHomeScreen());

        btnNext.setOnClickListener(v -> {
            // checking for last page
            // if last page home screen will be launched
            int current = getItem(+1);
            if (current < layouts.length) {
                // move to next screen
                viewPager.setCurrentItem(current);
            } else {
                launchHomeScreen();
            }
        });
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        method.setFirstTimeLaunch(false);
        startActivity(new Intent(WelcomeActivity.this, SplashScreen.class));
        finish();
    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
                btnNext.setText(getResources().getString(R.string.got_it));
                btnSkip.setVisibility(View.GONE);
            } else {
                // still pages are left
                btnNext.setText(getResources().getString(R.string.next));
                btnSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            Log.d("data_app", "");
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            Log.d("data_app", "");
        }
    };

}