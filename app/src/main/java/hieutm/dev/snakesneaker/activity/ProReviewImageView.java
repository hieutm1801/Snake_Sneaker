package hieutm.dev.snakesneaker.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.adapter.ProReviewImageViewAdapter;
import hieutm.dev.snakesneaker.util.Constant;
import hieutm.dev.snakesneaker.util.Method;
import com.google.android.material.appbar.MaterialToolbar;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class ProReviewImageView extends AppCompatActivity {

    private Method method;
    private MaterialToolbar toolbar;
    private ViewPager viewPager;
    private int selectedPosition = 0;
    private LinearLayout linearLayout;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pro_review_image_view);

        method = new Method(ProReviewImageView.this);
        method.forceRTLIfSupported();

        Intent in = getIntent();
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        selectedPosition = in.getIntExtra("position", 0);

        toolbar = findViewById(R.id.toolbar_pro_review_image_view);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        linearLayout = findViewById(R.id.ll_pro_review_image_view);
        method.bannerAd(linearLayout);

        viewPager = findViewById(R.id.viewpager_pro_review_image_view);

        ProReviewImageViewAdapter proReviewImageViewAdapter = new ProReviewImageViewAdapter(ProReviewImageView.this, Constant.reviewProImageLists);
        viewPager.setAdapter(proReviewImageViewAdapter);

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
