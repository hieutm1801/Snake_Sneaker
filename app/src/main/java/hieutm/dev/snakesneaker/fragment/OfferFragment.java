package hieutm.dev.snakesneaker.fragment;

import android.os.Bundle;
import android.os.Handler;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.activity.MainActivity;
import hieutm.dev.snakesneaker.adapter.OfferAdapter;
import hieutm.dev.snakesneaker.interfaces.OnClick;
import hieutm.dev.snakesneaker.item.OfferList;
import hieutm.dev.snakesneaker.response.OfferRP;
import hieutm.dev.snakesneaker.rest.ApiClient;
import hieutm.dev.snakesneaker.rest.ApiInterface;
import hieutm.dev.snakesneaker.util.API;
import hieutm.dev.snakesneaker.util.Constant;
import hieutm.dev.snakesneaker.util.EndlessRecyclerViewScrollListener;
import hieutm.dev.snakesneaker.util.Method;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OfferFragment extends Fragment {

    private Method method;
    private OnClick onClick;
    private ProgressBar progressBar;
    private List<OfferList> offerLists;
    private RecyclerView recyclerView;
    private OfferAdapter offerAdapter;
    private RelativeLayout relNoData;
    private Boolean isOver = false;
    private int paginationIndex = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.category_fragment, container, false);

        offerLists = new ArrayList<>();

        onClick = (position, type, title, id, sub_id, sub_sub_id, tag) -> {
            ProductFragment productFragment_slider = new ProductFragment();
            Bundle bundle = new Bundle();
            bundle.putString("type", type);
            bundle.putString("title", title);
            bundle.putString("cat_id", id);
            bundle.putString("sub_cat_id", "0");
            bundle.putString("search", "");
            productFragment_slider.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.frameLayout_main, productFragment_slider, title).addToBackStack(title)
                    .commitAllowingStateLoss();

        };
        method = new Method(getActivity(), onClick);
        if (MainActivity.toolbar != null) {
            if (method.isDarkMode()) {
                MainActivity.toolbar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getResources().getString(R.string.offers) + "</font>"));
            } else {
                MainActivity.toolbar.setTitle(Html.fromHtml("<font color=\"#000000\">" + getResources().getString(R.string.offers) + "</font>"));
            }
        }

        progressBar = view.findViewById(R.id.progressBar_category);
        relNoData = view.findViewById(R.id.rel_noDataFound);
        recyclerView = view.findViewById(R.id.recyclerView_category);
        relNoData.setVisibility(View.GONE);

        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (offerAdapter.getItemViewType(position) == 0) {
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
                    offerAdapter.hideHeader();
                }
            }
        });

        callData();

        return view;

    }

    private void callData() {
        if (method.isNetworkAvailable()) {
            offer();
        } else {
            progressBar.setVisibility(View.GONE);
            method.alertBox(getResources().getString(R.string.internet_connection));
        }
    }

    private void offer() {

        if (getActivity() != null) {

            if (offerAdapter == null) {
                offerLists.clear();
                progressBar.setVisibility(View.VISIBLE);
            }

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("page", paginationIndex);
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<OfferRP> call = apiService.getOffer(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<OfferRP>() {
                @Override
                public void onResponse(@NotNull Call<OfferRP> call, @NotNull Response<OfferRP> response) {

                    if (getActivity() != null) {
                        try {

                            OfferRP offerRP = response.body();

                            assert offerRP != null;

                            if (offerRP.getStatus().equals("1")) {

                                if (offerRP.getOfferLists().size() == 0) {
                                    if (offerAdapter != null) {
                                        offerAdapter.hideHeader();
                                        isOver = true;
                                    }
                                } else {
                                    offerLists.addAll(offerRP.getOfferLists());
                                }

                                if (offerAdapter == null) {
                                    if (offerLists.size() != 0) {
                                        offerAdapter = new OfferAdapter(getActivity(), offerLists, "offer", onClick);
                                        recyclerView.setAdapter(offerAdapter);
                                    } else {
                                        relNoData.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    offerAdapter.notifyDataSetChanged();
                                }

                            } else {
                                relNoData.setVisibility(View.VISIBLE);
                                method.alertBox(offerRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d(Constant.exceptionError, e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }
                    }

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(@NotNull Call<OfferRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e(Constant.failApi, t.toString());
                    progressBar.setVisibility(View.GONE);
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }

    }

}
