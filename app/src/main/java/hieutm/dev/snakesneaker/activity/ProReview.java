package hieutm.dev.snakesneaker.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.adapter.ProReviewAdapter;
import hieutm.dev.snakesneaker.item.ProReviewList;
import hieutm.dev.snakesneaker.response.ProReviewRP;
import hieutm.dev.snakesneaker.response.ProReviewTypeRP;
import hieutm.dev.snakesneaker.rest.ApiClient;
import hieutm.dev.snakesneaker.rest.ApiInterface;
import hieutm.dev.snakesneaker.util.API;
import hieutm.dev.snakesneaker.util.Constant;
import hieutm.dev.snakesneaker.util.EndlessRecyclerViewScrollListener;
import hieutm.dev.snakesneaker.util.Method;
import com.github.ornolfr.ratingview.RatingView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProReview extends AppCompatActivity {

    private Method method;
    private MaterialToolbar toolbar;
    private String productId, filterType;
    private ProgressBar progressBar;
    private RatingView ratingView;
    private MaterialTextView textViewUserReview;
    private AppCompatSpinner spinner;
    private RecyclerView recyclerView;
    private List<ProReviewList> proReviewLists;
    private ProReviewAdapter proReviewAdapter;
    private RelativeLayout relMain;
    private LinearLayout linearLayout;
    private RelativeLayout relNoData;
    private Boolean isOver = false, isSpinner = false;
    private int paginationIndex = 1;
    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pro_review);

        method = new Method(ProReview.this);
        method.forceRTLIfSupported();

        proReviewLists = new ArrayList<>();

        productId = getIntent().getStringExtra("product_id");

        toolbar = findViewById(R.id.toolbar_pro_review);
        toolbar.setTitle(getResources().getString(R.string.product_review));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        relMain = findViewById(R.id.rel_main_pro_review);
        relNoData = findViewById(R.id.rel_noDataFound);
        progressBar = findViewById(R.id.progressBar_pro_review);
        ratingView = findViewById(R.id.ratingBar_pro_review);
        textViewUserReview = findViewById(R.id.textView_rating_pro_review);
        spinner = findViewById(R.id.spinner_contact_us);
        recyclerView = findViewById(R.id.recyclerView_pro_review);
        linearLayout = findViewById(R.id.linearLayout_pro_review);
        method.bannerAd(linearLayout);

        relMain.setVisibility(View.GONE);
        relNoData.setVisibility(View.GONE);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ProReview.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setFocusable(false);

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (!isOver) {
                    new Handler().postDelayed(() -> {
                        paginationIndex++;
                        callData();
                    }, 1000);
                } else {
                    proReviewAdapter.hideHeader();
                }
            }
        };
        recyclerView.addOnScrollListener(scrollListener);

        if (method.isNetworkAvailable()) {
            proReviewType();
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

    }

    private void callData() {
        if (method.isNetworkAvailable()) {
            proReview(productId, filterType);
        } else {
            progressBar.setVisibility(View.GONE);
            method.alertBox(getResources().getString(R.string.internet_connection));
        }
    }

    private void proReviewType() {

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(ProReview.this));
        jsObj.addProperty("product_id", productId);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ProReviewTypeRP> call = apiService.getReviewType(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<ProReviewTypeRP>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call<ProReviewTypeRP> call, @NotNull Response<ProReviewTypeRP> response) {

                try {

                    ProReviewTypeRP proReviewTypeRP = response.body();

                    assert proReviewTypeRP != null;
                    if (proReviewTypeRP.getStatus().equals("1")) {

                        ratingView.setRating(Float.parseFloat(proReviewTypeRP.getRate_avg()));
                        textViewUserReview.setText(proReviewTypeRP.getTotal_rate());

                        List<String> strings = new ArrayList<String>();
                        for (int i = 0; i < proReviewTypeRP.getProReviewTypeLists().size(); i++) {
                            strings.add(proReviewTypeRP.getProReviewTypeLists().get(i).getTitle());
                        }
                        filterType = proReviewTypeRP.getProReviewTypeLists().get(0).getValue();

                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ProReview.this, android.R.layout.simple_spinner_item, strings);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(dataAdapter);

                        callData();

                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (isSpinner) {

                                    if (position == 0) {
                                        ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.textView_proReview));
                                    } else {
                                        ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.textView_app_color));
                                    }
                                    filterType = proReviewTypeRP.getProReviewTypeLists().get(position).getValue();

                                    scrollListener.resetState();
                                    isOver = false;
                                    paginationIndex = 1;
                                    proReviewLists.clear();
                                    proReviewAdapter = null;
                                    callData();

                                }
                                isSpinner = true;

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                Log.d("data_app", "");
                            }
                        });

                    } else {
                        method.alertBox(proReviewTypeRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d(Constant.exceptionError, e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(@NotNull Call<ProReviewTypeRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e(Constant.failApi, t.toString());
                progressBar.setVisibility(View.GONE);
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });

    }

    public void proReview(String product_id, String filter_type) {

        if (proReviewAdapter == null) {
            proReviewLists.clear();
            progressBar.setVisibility(View.VISIBLE);
        }

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(ProReview.this));
        jsObj.addProperty("product_id", product_id);
        jsObj.addProperty("filter_type", filter_type);
        jsObj.addProperty("page", paginationIndex);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ProReviewRP> call = apiService.getProReviewDetail(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<ProReviewRP>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call<ProReviewRP> call, @NotNull Response<ProReviewRP> response) {

                try {

                    ProReviewRP proReviewRP = response.body();

                    assert proReviewRP != null;
                    if (proReviewRP.getStatus().equals("1")) {

                        relMain.setVisibility(View.VISIBLE);

                        if (proReviewRP.getProReviewLists().size() == 0) {
                            if (proReviewAdapter != null) {
                                proReviewAdapter.hideHeader();
                                isOver = true;
                            }
                        } else {
                            proReviewLists.addAll(proReviewRP.getProReviewLists());
                        }

                        if (proReviewAdapter == null) {
                            if (proReviewLists.size() != 0) {
                                proReviewAdapter = new ProReviewAdapter(ProReview.this, proReviewLists);
                                recyclerView.setAdapter(proReviewAdapter);
                            } else {
                                relNoData.setVisibility(View.VISIBLE);
                            }
                        } else {
                            proReviewAdapter.notifyDataSetChanged();
                        }

                    } else {
                        method.alertBox(proReviewRP.getMessage());
                        relNoData.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    Log.d(Constant.exceptionError, e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(@NotNull Call<ProReviewRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e(Constant.failApi, t.toString());
                progressBar.setVisibility(View.GONE);
                relNoData.setVisibility(View.VISIBLE);
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
