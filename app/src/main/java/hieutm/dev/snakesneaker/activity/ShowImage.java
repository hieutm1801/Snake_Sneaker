package hieutm.dev.snakesneaker.activity;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.util.Method;
import hieutm.dev.snakesneaker.util.TouchImageView;
import com.google.android.material.appbar.MaterialToolbar;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class ShowImage extends AppCompatActivity {

    private Method method;
    private String path;
    private MaterialToolbar toolbar;
    private TouchImageView imageView;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        path = getIntent().getStringExtra("path");

        method = new Method(ShowImage.this);
        method.forceRTLIfSupported();

        toolbar = findViewById(R.id.toolbar_show_image);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        imageView = findViewById(R.id.imageView_show_image);

        Glide.with(ShowImage.this).load(path)
                .placeholder(R.drawable.placeholder_rectangle).into(imageView);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}