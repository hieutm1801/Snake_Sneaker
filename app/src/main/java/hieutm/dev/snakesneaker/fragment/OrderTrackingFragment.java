package hieutm.dev.snakesneaker.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.activity.MainActivity;
import hieutm.dev.snakesneaker.adapter.OrderTrackingAdapter;
import hieutm.dev.snakesneaker.response.OrderTrackingRP;
import hieutm.dev.snakesneaker.rest.ApiClient;
import hieutm.dev.snakesneaker.rest.ApiInterface;
import hieutm.dev.snakesneaker.util.API;
import hieutm.dev.snakesneaker.util.Constant;
import hieutm.dev.snakesneaker.util.Method;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderTrackingFragment extends Fragment {

    private Method method;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private RelativeLayout relNoData;
    private OrderTrackingAdapter orderTrackingAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.order_tracking_fragment, container, false);

        method = new Method(getActivity());
        if (MainActivity.toolbar != null) {
            if (method.isDarkMode()) {
                MainActivity.toolbar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getResources().getString(R.string.order_tracking) + "</font>"));
            } else {
                MainActivity.toolbar.setTitle(Html.fromHtml("<font color=\"#000000\">" + getResources().getString(R.string.order_tracking) + "</font>"));
            }
        }

        method.forceRTLIfSupported();

        assert getArguments() != null;
        String order_id = getArguments().getString("order_id");
        String product_id = getArguments().getString("product_id");

        recyclerView = view.findViewById(R.id.recyclerView_order_tracking);
        relNoData = view.findViewById(R.id.rel_noDataFound);
        progressBar = view.findViewById(R.id.progressBar_order_tracking);

        relNoData.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);

        if (method.isNetworkAvailable()) {
            if (method.isLogin()) {
                orderTracking(order_id, method.userId(), product_id);
            } else {
                relNoData.setVisibility(View.VISIBLE);
                method.alertBox(getResources().getString(R.string.you_have_not_login));
            }
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

        return view;

    }

    private void orderTracking(String orderId, String userId, String productId) {

        if (getActivity() != null) {

            progressBar.setVisibility(View.VISIBLE);

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("order_id", orderId);
            jsObj.addProperty("user_id", userId);
            jsObj.addProperty("product_id", productId);
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<OrderTrackingRP> call = apiService.getOrderTrackingList(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<OrderTrackingRP>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NotNull Call<OrderTrackingRP> call, @NotNull Response<OrderTrackingRP> response) {

                    if (getActivity() != null) {

                        try {

                            OrderTrackingRP orderTrackingRP = response.body();

                            assert orderTrackingRP != null;
                            if (orderTrackingRP.getStatus().equals("1")) {

                                if (orderTrackingRP.getOrderStatusLists().size() != 0) {
                                    orderTrackingAdapter = new OrderTrackingAdapter(getActivity(), orderTrackingRP.getOrderStatusLists());
                                    recyclerView.setAdapter(orderTrackingAdapter);
                                } else {
                                    relNoData.setVisibility(View.VISIBLE);
                                }

                            } else if (orderTrackingRP.getStatus().equals("2")) {
                                method.suspend(orderTrackingRP.getMessage());
                            } else {
                                relNoData.setVisibility(View.VISIBLE);
                                method.alertBox(orderTrackingRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d(Constant.exceptionError, e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(@NotNull Call<OrderTrackingRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e(Constant.failApi, t.toString());
                    relNoData.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }
    }

}
