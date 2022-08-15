package hieutm.dev.snakesneaker.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.adapter.ProViewDetailAdapter;
import hieutm.dev.snakesneaker.util.Constant;
import hieutm.dev.snakesneaker.util.Method;
import com.google.android.material.appbar.MaterialToolbar;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class ProImageView extends AppCompatActivity {

    private ViewPager viewPager;
    private int selectedPosition = 0;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pro_image_view);

        Method method = new Method(ProImageView.this);
        method.forceRTLIfSupported();

        Intent in = getIntent();
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        selectedPosition = in.getIntExtra("position", 0);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_pro_imageView);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        LinearLayout linearLayout = findViewById(R.id.ll_pro_imageView);
        method.bannerAd(linearLayout);

        viewPager = findViewById(R.id.viewpager_pro_imageView);

        ProViewDetailAdapter proViewAdapter = new ProViewDetailAdapter(ProImageView.this, Constant.proImageLists);
        viewPager.setAdapter(proViewAdapter);

        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        setCurrentItem(selectedPosition);

    }

    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
    }

    //	page change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            selectedPosition = position;
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}