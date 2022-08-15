package hieutm.dev.snakesneaker.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import hieutm.dev.snakesneaker.activity.AddAddress;
import hieutm.dev.snakesneaker.activity.MainActivity;
import hieutm.dev.snakesneaker.adapter.MyAddressAdapter;
import hieutm.dev.snakesneaker.response.AddressListRP;
import hieutm.dev.snakesneaker.rest.ApiClient;
import hieutm.dev.snakesneaker.rest.ApiInterface;
import hieutm.dev.snakesneaker.util.API;
import hieutm.dev.snakesneaker.util.Constant;
import hieutm.dev.snakesneaker.util.Events;
import hieutm.dev.snakesneaker.util.GlobalBus;
import hieutm.dev.snakesneaker.util.Method;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyAddressFragment extends Fragment {

    private Method method;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private MyAddressAdapter myAddressAdapter;
    private MaterialTextView textViewTotal;
    private RelativeLayout relNoData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.my_adddress_fragment, container, false);

        GlobalBus.getBus().register(this);

        method = new Method(getActivity());

        if (MainActivity.toolbar != null) {
            if (method.isDarkMode()) {
                MainActivity.toolbar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getResources().getString(R.string.my_address) + "</font>"));
            } else {
                MainActivity.toolbar.setTitle(Html.fromHtml("<font color=\"#000000\">" + getResources().getString(R.string.my_address) + "</font>"));
            }
        }

        progressBar = view.findViewById(R.id.progressBar_myAdd_fragment);
        textViewTotal = view.findViewById(R.id.textView_total_myAdd_fragment);
        relNoData = view.findViewById(R.id.rel_noDataFound);
        MaterialCardView cardViewAddAddress = view.findViewById(R.id.cardView_myAdd_fragment);
        recyclerView = view.findViewById(R.id.recyclerView_myAdd_fragment);

        relNoData.setVisibility(View.GONE);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);

        cardViewAddAddress.setOnClickListener(v -> startActivity(new Intent(getActivity(), AddAddress.class)
                .putExtra("type", "add_my_address")));

        if (method.isNetworkAvailable()) {
            if (method.isLogin()) {
                addressList(method.userId());
            } else {
                progressBar.setVisibility(View.GONE);
                method.alertBox(getResources().getString(R.string.you_have_not_login));
            }
        } else {
            progressBar.setVisibility(View.GONE);
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

        return view;

    }

    @SuppressLint("SetTextI18n")
    @Subscribe
    public void getData(Events.EventMYAddress eventMYAddress) {
        if (!eventMYAddress.getType().equals("removeAddress")) {
            addressList(method.userId());
        } else {
            textViewTotal.setText(eventMYAddress.getAddressCount() + " " + getResources().getString(R.string.address));
        }
    }

    @Subscribe

    private void addressList(String userId) {

        if (getActivity() != null) {

            progressBar.setVisibility(View.VISIBLE);

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("user_id", userId);
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<AddressListRP> call = apiService.getAddressList(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<AddressListRP>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NotNull Call<AddressListRP> call, @NotNull Response<AddressListRP> response) {

                    if (getActivity() != null) {
                        try {

                            AddressListRP addressListRP = response.body();

                            assert addressListRP != null;
                            if (addressListRP.getStatus().equals("1")) {

                                textViewTotal.setText(addressListRP.getAddress_count() + " " + getResources().getString(R.string.address));

                                if (addressListRP.getAddressItemLists().size() != 0) {
                                    relNoData.setVisibility(View.GONE);
                                    myAddressAdapter = new MyAddressAdapter(getActivity(), addressListRP.getAddressItemLists());
                                    recyclerView.setAdapter(myAddressAdapter);
                                } else {
                                    relNoData.setVisibility(View.VISIBLE);
                                }

                            } else if (addressListRP.getStatus().equals("2")) {
                                method.suspend(addressListRP.getMessage());
                            } else {
                                relNoData.setVisibility(View.VISIBLE);
                                method.alertBox(addressListRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d(Constant.exceptionError, e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }
                    }

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(@NotNull Call<AddressListRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e(Constant.failApi, t.toString());
                    progressBar.setVisibility(View.GONE);
                    relNoData.setVisibility(View.VISIBLE);
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
