package hieutm.dev.snakesneaker.fragment;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.activity.Login;
import hieutm.dev.snakesneaker.activity.MainActivity;
import hieutm.dev.snakesneaker.adapter.MyOrderAdapter;
import hieutm.dev.snakesneaker.interfaces.OnClick;
import hieutm.dev.snakesneaker.item.MyOrderList;
import hieutm.dev.snakesneaker.response.MyOrderRP;
import hieutm.dev.snakesneaker.rest.ApiClient;
import hieutm.dev.snakesneaker.rest.ApiInterface;
import hieutm.dev.snakesneaker.util.API;
import hieutm.dev.snakesneaker.util.Constant;
import hieutm.dev.snakesneaker.util.EndlessRecyclerViewScrollListener;
import hieutm.dev.snakesneaker.util.Events;
import hieutm.dev.snakesneaker.util.GlobalBus;
import hieutm.dev.snakesneaker.util.Method;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyOrderFragment extends Fragment {

    private Method method;
    private OnClick onClick;
    private ProgressBar progressBar;
    private LinearLayout llEmpty;
    private MaterialButton button;
    private RecyclerView recyclerView;
    private List<MyOrderList> myOrderLists;
    private MyOrderAdapter myOrderAdapter;
    private ImageView imageView;
    private MaterialTextView textViewNoData;
    private Boolean isOver = false;
    private int paginationIndex = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.my_order_fragment, container, false);

        GlobalBus.getBus().register(this);

        method = new Method(getActivity(), onClick);
        myOrderLists = new ArrayList<>();
        if (MainActivity.toolbar != null) {
            if (method.isDarkMode()) {
                MainActivity.toolbar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getResources().getString(R.string.my_order) + "</font>"));
            } else {
                MainActivity.toolbar.setTitle(Html.fromHtml("<font color=\"#000000\">" + getResources().getString(R.string.my_order) + "</font>"));
            }
        }

        onClick = (position, type, title, id, sub_id, sub_sub_id, tag) -> {
            MyOrderDetailFragment myOrderDetailFragment = new MyOrderDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putString("order_unique_id", id);
            bundle.putString("product_id", sub_id);
            myOrderDetailFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main,
                    myOrderDetailFragment, getResources().getString(R.string.order_detail))
                    .addToBackStack(getResources().getString(R.string.order_detail)).commitAllowingStateLoss();
        };

        progressBar = view.findViewById(R.id.progressBar_my_order);
        imageView = view.findViewById(R.id.imageView_my_order);
        textViewNoData = view.findViewById(R.id.textView_noData_my_order);
        button = view.findViewById(R.id.button_my_order);
        llEmpty = view.findViewById(R.id.ll_empty_my_order);
        recyclerView = view.findViewById(R.id.recyclerView_my_order);

        llEmpty.setVisibility(View.GONE);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
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
                    myOrderAdapter.hideHeader();
                }
            }
        });

        callData();

        return view;
    }

    private void callData() {
        if (method.isNetworkAvailable()) {
            if (method.isLogin()) {
                myOrder(method.userId());
            } else {
                progressBar.setVisibility(View.GONE);
                changView(true);
            }
        } else {
            progressBar.setVisibility(View.GONE);
            method.alertBox(getResources().getString(R.string.internet_connection));
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void changView(boolean isValue) {
        llEmpty.setVisibility(View.VISIBLE);
        if (isValue) {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_login));
            textViewNoData.setText(getResources().getString(R.string.you_have_not_login));
            button.setText(getResources().getString(R.string.login));
        } else {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.my_order));
            textViewNoData.setText(getResources().getString(R.string.you_have_no_order));
            button.setText(getResources().getString(R.string.start_shopping));
        }
        button.setOnClickListener(v -> {
            if (isValue) {
                startActivity(new Intent(getActivity(), Login.class));
            } else {
                Events.GoToHome goToHome = new Events.GoToHome("");
                GlobalBus.getBus().post(goToHome);
                backStackRemove();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_main, new HomeFragment(),
                        getResources().getString(R.string.home)).commitAllowingStateLoss();
            }
        });
    }


    @Subscribe
    public void geString(Events.CancelOrder cancelOrder) {
        for (int i = 0; i < myOrderLists.size(); i++) {
            for (int j = 0; j < cancelOrder.getMyOrderLists().size(); j++) {
                if (myOrderLists.get(i).getProduct_id().equals(cancelOrder.getMyOrderLists().get(j).getProduct_id())
                        && myOrderLists.get(i).getOrder_id().equals(cancelOrder.getMyOrderLists().get(j).getOrder_id())) {
                    myOrderLists.get(i).setOrder_status(cancelOrder.getMyOrderLists().get(j).getOrder_status());
                    myOrderLists.get(i).setCurrent_order_status(cancelOrder.getMyOrderLists().get(j).getCurrent_order_status());
                }
            }
        }
        if (myOrderAdapter != null) {
            myOrderAdapter.notifyDataSetChanged();
        }
    }

    public void backStackRemove() {
        for (int i = 0; i < getActivity().getSupportFragmentManager().getBackStackEntryCount(); i++) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    private void myOrder(String userId) {

        if (getActivity() != null) {

            if (myOrderAdapter == null) {
                myOrderLists.clear();
                progressBar.setVisibility(View.VISIBLE);
            }

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("user_id", userId);
            jsObj.addProperty("page", paginationIndex);
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<MyOrderRP> call = apiService.getMyOderList(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<MyOrderRP>() {
                @Override
                public void onResponse(@NotNull Call<MyOrderRP> call, @NotNull Response<MyOrderRP> response) {

                    if (getActivity() != null) {

                        try {

                            MyOrderRP myOrderRP = response.body();

                            assert myOrderRP != null;
                            if (myOrderRP.getStatus().equals("1")) {

                                if (myOrderRP.getMyOrderLists().size() == 0) {
                                    if (myOrderAdapter != null) {
                                        myOrderAdapter.hideHeader();
                                        isOver = true;
                                    }
                                } else {
                                    myOrderLists.addAll(myOrderRP.getMyOrderLists());
                                }

                                if (myOrderAdapter == null) {
                                    if (myOrderLists.size() != 0) {
                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                            List<MyOrderList> usersDistinctByEmail = myOrderRP.getMyOrderLists().stream().collect(
                                                    collectingAndThen(toCollection(() ->
                                                            new TreeSet<>(comparing(MyOrderList::getOrder_unique_id))), ArrayList::new));
                                            myOrderAdapter = new MyOrderAdapter(getActivity(), "my_order", usersDistinctByEmail, onClick);
                                            recyclerView.setAdapter(myOrderAdapter);
                                        }
                                    } else {
                                        changView(false);
                                    }
                                } else {
                                    myOrderAdapter.notifyDataSetChanged();
                                }

                            } else if (myOrderRP.getStatus().equals("2")) {
                                method.suspend(myOrderRP.getMessage());
                            } else {
                                method.alertBox(myOrderRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d(Constant.exceptionError, e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }
                    }

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(@NotNull Call<MyOrderRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e(Constant.failApi, t.toString());
                    progressBar.setVisibility(View.GONE);
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Unregister the registered event.
        GlobalBus.getBus().unregister(this);
    }

}
