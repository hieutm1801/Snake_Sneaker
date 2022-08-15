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
import hieutm.dev.snakesneaker.adapter.SizeSelectionAdapter;
import hieutm.dev.snakesneaker.response.SizeFilterRP;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SizeSelectionFragment extends Fragment {

    private Method method;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private SizeSelectionAdapter sizeSelectionAdapter;
    private RelativeLayout relMain;
    private TextInputEditText editTextSearch;
    private MaterialTextView textViewNoData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.size_select_fragment, container, false);

        method = new Method(getActivity());

        assert getArguments() != null;
        String filterType = getArguments().getString("filter_type");
        String search = getArguments().getString("search");
        String id = getArguments().getString("id");

        progressBar = view.findViewById(R.id.progressBar_size_select);
        textViewNoData = view.findViewById(R.id.textView_noData_size_select);
        relMain = view.findViewById(R.id.rel_main_size_select);
        editTextSearch = view.findViewById(R.id.editText_search_size_select);
        recyclerView = view.findViewById(R.id.recyclerView_size_select);

        textViewNoData.setVisibility(View.GONE);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);

        relMain.setVisibility(View.GONE);

        if (method.isNetworkAvailable()) {
            sizeFilter(filterType, search, id);
        } else {
            progressBar.setVisibility(View.GONE);
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

        return view;

    }

    private void sizeFilter(String type, String search, String id) {

        if (getActivity() != null) {

            progressBar.setVisibility(View.VISIBLE);

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("type", type);
            jsObj.addProperty("id", id);
            jsObj.addProperty("sizes", Constant.sizes_temp);
            jsObj.addProperty("brand_ids", Constant.brand_ids_temp);
            if (type.equals("recent_viewed_products")) {
                jsObj.addProperty("user_id", method.userId());
            }
            if (type.equals("search")) {
                jsObj.addProperty("keyword", search);
            }
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<SizeFilterRP> call = apiService.getSizeFilter(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<SizeFilterRP>() {
                @Override
                public void onResponse(@NotNull Call<SizeFilterRP> call, @NotNull Response<SizeFilterRP> response) {

                    if (getActivity() != null) {
                        try {

                            SizeFilterRP sizeFilterRP = response.body();

                            assert sizeFilterRP != null;
                            if (sizeFilterRP.getStatus().equals("1")) {

                                if (sizeFilterRP.getSizeFilterLists().size() != 0) {

                                    relMain.setVisibility(View.VISIBLE);

                                    sizeSelectionAdapter = new SizeSelectionAdapter(getActivity(), sizeFilterRP.getSizeFilterLists());
                                    recyclerView.setAdapter(sizeSelectionAdapter);

                                    editTextSearch.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                            Log.d("data_app", "");
                                        }

                                        @Override
                                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                                            sizeSelectionAdapter.getFilter().filter(s.toString());
                                        }

                                        @Override
                                        public void afterTextChanged(Editable s) {
                                            Log.d("data_app", "");
                                        }
                                    });

                                } else {
                                    textViewNoData.setVisibility(View.VISIBLE);
                                }

                            } else if (sizeFilterRP.getStatus().equals("2")) {
                                method.suspend(sizeFilterRP.getMessage());
                            } else {
                                textViewNoData.setVisibility(View.VISIBLE);
                                method.alertBox(sizeFilterRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d(Constant.exceptionError, e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }
                    }

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(@NotNull Call<SizeFilterRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e(Constant.failApi, t.toString());
                    textViewNoData.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }

    }


}
