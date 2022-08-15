package hieutm.dev.snakesneaker.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.adapter.CouponsAdapter;
import hieutm.dev.snakesneaker.interfaces.OnClick;
import hieutm.dev.snakesneaker.item.CouponsList;
import hieutm.dev.snakesneaker.response.CouponsRP;
import hieutm.dev.snakesneaker.rest.ApiClient;
import hieutm.dev.snakesneaker.rest.ApiInterface;
import hieutm.dev.snakesneaker.util.API;
import hieutm.dev.snakesneaker.util.Constant;
import hieutm.dev.snakesneaker.util.EndlessRecyclerViewScrollListener;
import hieutm.dev.snakesneaker.util.Events;
import hieutm.dev.snakesneaker.util.GlobalBus;
import hieutm.dev.snakesneaker.util.Method;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Coupons extends AppCompatActivity {

    private Method method;
    private OnClick onClick;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private List<CouponsList> couponsLists;
    private CouponsAdapter couponsAdapter;
    private RelativeLayout relNoData;
    private Boolean isOver = false;
    private int paginationIndex = 1;
    private String cartIds, type;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupons);

        GlobalBus.getBus().register(this);

        couponsLists = new ArrayList<>();

        cartIds = getIntent().getStringExtra("cart_ids");
        type = getIntent().getStringExtra("type");

        MaterialToolbar toolbar = findViewById(R.id.toolbar_coupons);
        toolbar.setTitle(getResources().getString(R.string.coupons));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        onClick = (position, send_type, title, id, sub_id, sub_sub_id, tag) ->
                startActivity(new Intent(Coupons.this, CouponsDetail.class)
                        .putExtra("id", id)
                        .putExtra("cart_ids", cartIds)
                        .putExtra("type", type));
        method = new Method(Coupons.this, onClick);
        method.forceRTLIfSupported();

        progressBar = findViewById(R.id.progressBar_coupons);
        relNoData = findViewById(R.id.rel_noDataFound);
        recyclerView = findViewById(R.id.recyclerView_coupons);
        relNoData.setVisibility(View.GONE);

        LinearLayout linearLayout = findViewById(R.id.linearLayout_coupons);
        method.bannerAd(linearLayout);

        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(Coupons.this, 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (couponsAdapter.getItemViewType(position) == 0) {
                    return 2;
                }
                return 1;
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setFocusable(false);

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (!isOver) {
                    new Handler().postDelayed(() -> {
                        paginationIndex++;
                        callData();
                    }, 1000);
                } else {
                    couponsAdapter.hideHeader();
                }
            }
        });

        callData();

    }

    private void callData() {
        if (method.isNetworkAvailable()) {
            coupons(method.userId());
        } else {
            progressBar.setVisibility(View.GONE);
            method.alertBox(getResources().getString(R.string.internet_connection));
        }
    }

    private void coupons(String user_id) {

        if (couponsAdapter == null) {
            couponsLists.clear();
            progressBar.setVisibility(View.VISIBLE);
        }
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(Coupons.this));
        jsObj.addProperty("user_id", user_id);
        jsObj.addProperty("cart_ids", cartIds);
        if (type.equals("by_now")) {
            jsObj.addProperty("cart_type", "temp_cart");
        } else {
            jsObj.addProperty("cart_type", "main_cart");
        }
        jsObj.addProperty("page", paginationIndex);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<CouponsRP> call = apiService.getCoupons(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<CouponsRP>() {
            @Override
            public void onResponse(@NotNull Call<CouponsRP> call, @NotNull Response<CouponsRP> response) {

                try {

                    CouponsRP couponsRP = response.body();

                    assert couponsRP != null;
                    if (couponsRP.getStatus().equals("1")) {

                        if (couponsRP.getCouponsLists().size() == 0) {
                            if (couponsAdapter != null) {
                                couponsAdapter.hideHeader();
                                isOver = true;
                            }
                        } else {
                            couponsLists.addAll(couponsRP.getCouponsLists());
                        }

                        if (couponsAdapter == null) {
                            if (couponsLists.size() != 0) {
                                couponsAdapter = new CouponsAdapter(Coupons.this, couponsLists, "coupons", onClick);
                                recyclerView.setAdapter(couponsAdapter);
                            } else {
                                relNoData.setVisibility(View.VISIBLE);
                            }
                        } else {
                            couponsAdapter.notifyDataSetChanged();
                        }

                    } else if (couponsRP.getStatus().equals("2")) {
                        method.suspend(couponsRP.getMessage());
                    } else {
                        relNoData.setVisibility(View.VISIBLE);
                        method.alertBox(couponsRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d(Constant.exceptionError, e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(@NotNull Call<CouponsRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e(Constant.failApi, t.toString());
                progressBar.setVisibility(View.GONE);
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });

    }


    @Subscribe
    public void getData(Events.OnBackData onBackData) {
        onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        GlobalBus.getBus().unregister(this);
        super.onDestroy();
    }

}


