package hieutm.dev.snakesneaker.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.adapter.ChangeAddressAdapter;
import hieutm.dev.snakesneaker.interfaces.OnClickSend;
import hieutm.dev.snakesneaker.item.AddressItemList;
import hieutm.dev.snakesneaker.response.AddressListRP;
import hieutm.dev.snakesneaker.response.ChangeAddressRP;
import hieutm.dev.snakesneaker.rest.ApiClient;
import hieutm.dev.snakesneaker.rest.ApiInterface;
import hieutm.dev.snakesneaker.util.API;
import hieutm.dev.snakesneaker.util.Constant;
import hieutm.dev.snakesneaker.util.Events;
import hieutm.dev.snakesneaker.util.GlobalBus;
import hieutm.dev.snakesneaker.util.Method;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
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

public class ChangeAddress extends AppCompatActivity {

    private Method method;
    private OnClickSend onClickSend;
    private String addressId;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;
    private RelativeLayout relMain, relNoData;
    private RecyclerView recyclerView;
    private ChangeAddressAdapter changeAddressList;
    private List<AddressItemList> addressItemLists;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_address);

        GlobalBus.getBus().register(this);

        method = new Method(ChangeAddress.this);
        method.forceRTLIfSupported();

        progressDialog = new ProgressDialog(ChangeAddress.this);

        onClickSend = (id, type, size, position) -> addressId = id;

        addressItemLists = new ArrayList<>();

        MaterialToolbar toolbar = findViewById(R.id.toolbar_change_address);
        toolbar.setTitle(getResources().getString(R.string.select_address));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        relMain = findViewById(R.id.rel_main_change_address);
        progressBar = findViewById(R.id.progressBar_change_address);
        relNoData = findViewById(R.id.rel_noDataFound);
        recyclerView = findViewById(R.id.recyclerView_change_address);
        MaterialCardView cardViewDeliver = findViewById(R.id.cardView_deliver_change_address);
        MaterialCardView cardViewAddAddress = findViewById(R.id.cardView_addAddress_change_address);

        relMain.setVisibility(View.GONE);
        relNoData.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        LinearLayout linearLayout = findViewById(R.id.linearLayout_change_address);
        method.bannerAd(linearLayout);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ChangeAddress.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);

        cardViewAddAddress.setOnClickListener(v -> startActivity(new Intent(ChangeAddress.this, AddAddress.class)
                .putExtra("type", "add_change_address")));

        cardViewDeliver.setOnClickListener(v -> {
            if (!addressId.equals("")) {
                changeAddress(addressId, method.userId());
            } else {
                method.alertBox(getResources().getString(R.string.no_address_found));
            }
        });

        if (method.isNetworkAvailable()) {
            if (method.isLogin()) {
                addressList(method.userId());
            } else {
                method.alertBox(getResources().getString(R.string.you_have_not_login));
                relNoData.setVisibility(View.VISIBLE);
            }
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

    }

    private void addressList(String userId) {

        addressItemLists.clear();
        progressBar.setVisibility(View.VISIBLE);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(ChangeAddress.this));
        jsObj.addProperty("user_id", userId);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<AddressListRP> call = apiService.getAddressList(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<AddressListRP>() {
            @Override
            public void onResponse(@NotNull Call<AddressListRP> call, @NotNull Response<AddressListRP> response) {

                try {

                    AddressListRP addressListRP = response.body();
                    assert addressListRP != null;
                    addressItemLists.addAll(addressListRP.getAddressItemLists());

                    if (addressListRP.getStatus().equals("1")) {

                        relMain.setVisibility(View.VISIBLE);

                        if (addressItemLists.size() != 0) {
                            changeAddressList = new ChangeAddressAdapter(ChangeAddress.this, onClickSend, addressItemLists);
                            recyclerView.setAdapter(changeAddressList);
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

                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(@NotNull Call<AddressListRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e(Constant.failApi, t.toString());
                relNoData.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });

    }

    private void changeAddress(String addressId, String userId) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(ChangeAddress.this));
        jsObj.addProperty("address_id", addressId);
        jsObj.addProperty("user_id", userId);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ChangeAddressRP> call = apiService.getChangeAddress(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<ChangeAddressRP>() {
            @Override
            public void onResponse(@NotNull Call<ChangeAddressRP> call, @NotNull Response<ChangeAddressRP> response) {

                try {

                    ChangeAddressRP changeAddressRP = response.body();

                    assert changeAddressRP != null;
                    if (changeAddressRP.getStatus().equals("1")) {

                        if (changeAddressRP.getSuccess().equals("1")) {

                            Events.UpdateDeliveryAddress updateDeliveryAddress = new Events.UpdateDeliveryAddress(addressId, changeAddressRP.getAddress(),
                                    changeAddressRP.getName(), changeAddressRP.getMobile_no(), changeAddressRP.getAddress_type());
                            GlobalBus.getBus().post(updateDeliveryAddress);
                            onBackPressed();
                        }

                        Toast.makeText(ChangeAddress.this, changeAddressRP.getMsg(), Toast.LENGTH_SHORT).show();

                    } else if (changeAddressRP.getStatus().equals("2")) {
                        method.suspend(changeAddressRP.getMessage());
                    } else {
                        method.alertBox(changeAddressRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d(Constant.exceptionError, e.toString());
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<ChangeAddressRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                progressDialog.dismiss();
                Log.e(Constant.failApi, t.toString());
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
