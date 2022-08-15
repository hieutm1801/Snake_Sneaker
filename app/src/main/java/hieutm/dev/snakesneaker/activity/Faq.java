package hieutm.dev.snakesneaker.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.adapter.FaqAdapter;
import hieutm.dev.snakesneaker.response.FaqRp;
import hieutm.dev.snakesneaker.rest.ApiClient;
import hieutm.dev.snakesneaker.rest.ApiInterface;
import hieutm.dev.snakesneaker.util.API;
import hieutm.dev.snakesneaker.util.Constant;
import hieutm.dev.snakesneaker.util.Method;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Faq extends AppCompatActivity {

    private Method method;
    private String type;
    private RelativeLayout relNoData;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private FaqAdapter faqAdapter;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        method = new Method(Faq.this);
        method.forceRTLIfSupported();

        type = getIntent().getStringExtra("type");

        MaterialToolbar toolbar = findViewById(R.id.toolbar_faq);

        if (type.equals("faq")) {
            toolbar.setTitle(getResources().getString(R.string.faq));
        } else {
            toolbar.setTitle(getResources().getString(R.string.payment));
        }
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        relNoData = findViewById(R.id.rel_noDataFound);
        progressBar = findViewById(R.id.progressBar_faq);
        recyclerView = findViewById(R.id.recyclerView_faq);

        relNoData.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        LinearLayout linearLayout = findViewById(R.id.linearLayout_faq);
        method.bannerAd(linearLayout);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Faq.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);

        if (method.isNetworkAvailable()) {
            faq(type);
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

    }

    public void faq(String type) {

        progressBar.setVisibility(View.VISIBLE);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(Faq.this));
        jsObj.addProperty("type", type);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<FaqRp> call = apiService.getFaq(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<FaqRp>() {
            @Override
            public void onResponse(@NotNull Call<FaqRp> call, @NotNull Response<FaqRp> response) {

                try {

                    FaqRp faqRp = response.body();

                    assert faqRp != null;
                    if (faqRp.getStatus().equals("1")) {

                        if (faqRp.getFaqLists().size() != 0) {
                            faqAdapter = new FaqAdapter(Faq.this, faqRp.getFaqLists());
                            recyclerView.setAdapter(faqAdapter);
                        } else {
                            relNoData.setVisibility(View.VISIBLE);
                        }

                    } else {
                        relNoData.setVisibility(View.VISIBLE);
                        method.alertBox(faqRp.getMessage());
                    }

                } catch (Exception e) {
                    Log.d(Constant.exceptionError, e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(@NotNull Call<FaqRp> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e(Constant.failApi, t.toString());
                relNoData.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
