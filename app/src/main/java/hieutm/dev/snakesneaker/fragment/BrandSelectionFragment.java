package hieutm.dev.snakesneaker.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import hieutm.dev.snakesneaker.adapter.BrandSelectionAdapter;
import hieutm.dev.snakesneaker.item.BrandFilterList;
import hieutm.dev.snakesneaker.response.BrandFilterRP;
import hieutm.dev.snakesneaker.rest.ApiClient;
import hieutm.dev.snakesneaker.rest.ApiInterface;
import hieutm.dev.snakesneaker.util.API;
import hieutm.dev.snakesneaker.util.Constant;
import hieutm.dev.snakesneaker.util.Method;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BrandSelectionFragment extends Fragment {

    private Method method;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private List<BrandFilterList> brandFilterLists;
    private BrandSelectionAdapter brandSelectionAdapter;
    private RelativeLayout relMain;
    private TextInputEditText editTextSearch;
    private MaterialTextView textViewNoData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.brand_select_fragment, container, false);

        method = new Method(getActivity());

        brandFilterLists = new ArrayList<>();

        assert getArguments() != null;
        String filter_type = getArguments().getString("filter_type");
        String search = getArguments().getString("search");
        String id = getArguments().getString("id");

        progressBar = view.findViewById(R.id.progressBar_brand_select);
        textViewNoData = view.findViewById(R.id.textView_noData_brand_select);
        relMain = view.findViewById(R.id.rel_main_brand_select);
        editTextSearch = view.findViewById(R.id.editText_search_brand_select);
        recyclerView = view.findViewById(R.id.recyclerView_brand_select);
        textViewNoData.setVisibility(View.GONE);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);

        relMain.setVisibility(View.GONE);

        if (method.isNetworkAvailable()) {
            brandFilter(filter_type, search, id);
        } else {
            progressBar.setVisibility(View.GONE);
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

        return view;
    }

    private void brandFilter(String type, String search, String id) {

        if (getActivity() != null) {

            progressBar.setVisibility(View.VISIBLE);

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("type", type);
            jsObj.addProperty("id", id);
            jsObj.addProperty("brand_ids", Constant.brand_ids_temp);
            jsObj.addProperty("sizes", Constant.sizes_temp);
            if (type.equals("recent_viewed_products")) {
                jsObj.addProperty("user_id", method.userId());
            }
            if (type.equals("search")) {
                jsObj.addProperty("keyword", search);
            }
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<BrandFilterRP> call = apiService.getBrandFilter(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<BrandFilterRP>() {
                @Override
                public void onResponse(@NotNull Call<BrandFilterRP> call, @NotNull Response<BrandFilterRP> response) {

                    if (getActivity() != null) {

                        try {

                            BrandFilterRP brandFilterRP = response.body();

                            assert brandFilterRP != null;
                            if (brandFilterRP.getStatus().equals("1")) {

                                if (brandFilterRP.getBrandFilterLists().size() != 0) {

                                    brandFilterLists.addAll(brandFilterRP.getBrandFilterLists());

                                    if (brandFilterLists.size() != 0) {
                                        relMain.setVisibility(View.VISIBLE);
                                        brandSelectionAdapter = new BrandSelectionAdapter(getActivity(), brandFilterLists);
                                        recyclerView.setAdapter(brandSelectionAdapter);
                                    } else {
                                        textViewNoData.setVisibility(View.VISIBLE);
                                    }

                                    editTextSearch.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                            Log.d("data_app", "");
                                        }

                                        @Override
                                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                                            brandSelectionAdapter.getFilter().filter(s.toString());
                                        }

                                        @Override
                                        public void afterTextChanged(Editable s) {
                                            Log.d("data_app", "");
                                        }
                                    });

                                }

                            } else if (brandFilterRP.getStatus().equals("2")) {
                                method.suspend(brandFilterRP.getMessage());
                            } else {
                                textViewNoData.setVisibility(View.VISIBLE);
                                method.alertBox(brandFilterRP.getMessage());
                            }


                        } catch (Exception e) {
                            Log.d(Constant.exceptionError, e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }
                    }

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(@NotNull Call<BrandFilterRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e(Constant.failApi, t.toString());
                    progressBar.setVisibility(View.GONE);
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }

    }

}
