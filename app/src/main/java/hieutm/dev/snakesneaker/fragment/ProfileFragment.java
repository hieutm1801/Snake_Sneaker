package hieutm.dev.snakesneaker.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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

import com.bumptech.glide.Glide;
import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.activity.Login;
import hieutm.dev.snakesneaker.activity.MainActivity;
import hieutm.dev.snakesneaker.activity.ShowImage;
import hieutm.dev.snakesneaker.adapter.ProMyOrder;
import hieutm.dev.snakesneaker.interfaces.OnClick;
import hieutm.dev.snakesneaker.item.MyOrderList;
import hieutm.dev.snakesneaker.response.UserProfileRP;
import hieutm.dev.snakesneaker.rest.ApiClient;
import hieutm.dev.snakesneaker.rest.ApiInterface;
import hieutm.dev.snakesneaker.util.API;
import hieutm.dev.snakesneaker.util.Constant;
import hieutm.dev.snakesneaker.util.Events;
import hieutm.dev.snakesneaker.util.GlobalBus;
import hieutm.dev.snakesneaker.util.Method;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileFragment extends Fragment {

    private Method method;
    private OnClick onClick;
    private ProgressBar progressBar;
    private LinearLayout llMain;
    private ProMyOrder proMyOrder;
    private List<MyOrderList> myOrderLists;
    private RecyclerView recyclerViewMyOrder;
    private CircleImageView circleImageView;
    private LinearLayout llEmpty;
    private MaterialButton button;
    private ImageView imageViewLoginType, imageViewEdit, imageView;
    private MaterialCardView cardViewAddress, cardViewBank, cardViewChangePassword;
    private MaterialTextView textViewNoData, textViewNoDataMyOrder, textViewName, textViewEmail, textViewMyOrder, textViewAdd, textViewAddCount,
            textViewBank, textViewBankCount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.profile_fragment, container, false);

        GlobalBus.getBus().register(this);

        myOrderLists = new ArrayList<>();
        method = new Method(getActivity());

        onClick = (position, type, title, id, subId, subSubId, tag) -> {
            MyOrderDetailFragment myOrderDetailFragment = new MyOrderDetailFragment();
            Bundle bundle_myOrder = new Bundle();
            bundle_myOrder.putString("order_unique_id", id);
            bundle_myOrder.putString("product_id", subId);
            myOrderDetailFragment.setArguments(bundle_myOrder);
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main,
                    myOrderDetailFragment, getResources().getString(R.string.order_detail))
                    .addToBackStack(getResources().getString(R.string.order_detail)).commitAllowingStateLoss();
        };

        if (MainActivity.toolbar != null) {
            if (method.isDarkMode()) {
                MainActivity.toolbar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getResources().getString(R.string.profile) + "</font>"));
            } else {
                MainActivity.toolbar.setTitle(Html.fromHtml("<font color=\"#000000\">" + getResources().getString(R.string.profile) + "</font>"));
            }
        }

        llMain = view.findViewById(R.id.ll_main_profile);
        llEmpty = view.findViewById(R.id.ll_empty_profile);
        progressBar = view.findViewById(R.id.progressbar_profile);
        imageView = view.findViewById(R.id.imageView_profile);
        button = view.findViewById(R.id.button_profile);
        circleImageView = view.findViewById(R.id.imageView_user_profile);
        imageViewLoginType = view.findViewById(R.id.imageView_loginType_profile);
        imageViewEdit = view.findViewById(R.id.imageView_edit_profile);
        textViewNoData = view.findViewById(R.id.textView_noData_profile);
        textViewName = view.findViewById(R.id.textView_name_profile);
        textViewEmail = view.findViewById(R.id.textView_email_profile);
        textViewMyOrder = view.findViewById(R.id.textView_myOrderViewAll_profile);
        textViewAdd = view.findViewById(R.id.textView_add_profile);
        textViewAddCount = view.findViewById(R.id.textView_addCount_profile);
        textViewBank = view.findViewById(R.id.textView_saveBank_profile);
        textViewBankCount = view.findViewById(R.id.textView_saveBankCount_profile);
        textViewNoDataMyOrder = view.findViewById(R.id.textView_noDataMyOrder_profile);
        recyclerViewMyOrder = view.findViewById(R.id.recyclerView_profile);
        cardViewAddress = view.findViewById(R.id.cardView_address_profile);
        cardViewBank = view.findViewById(R.id.cardView_saveBank_profile);
        cardViewChangePassword = view.findViewById(R.id.cardView_changePassword_profile);

        recyclerViewMyOrder.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager_myOrder = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewMyOrder.setLayoutManager(layoutManager_myOrder);
        recyclerViewMyOrder.setFocusable(false);
        recyclerViewMyOrder.setNestedScrollingEnabled(false);

        llMain.setVisibility(View.GONE);
        llEmpty.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        if (method.isNetworkAvailable()) {
            if (method.isLogin()) {
                profile(method.userId());
            } else {
                changView(true);
            }
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

        return view;

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void changView(boolean isValue) {
        llEmpty.setVisibility(View.VISIBLE);
        if (isValue) {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_login));
            textViewNoData.setText(getResources().getString(R.string.you_have_not_login));
            button.setVisibility(View.VISIBLE);
            button.setText(getResources().getString(R.string.login));
        } else {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.no_data));
            textViewNoData.setText(getResources().getString(R.string.no_data));
            button.setVisibility(View.GONE);
        }
        button.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), Login.class));
        });
    }

    @Subscribe
    public void getData(Events.ProfileUpdate profileUpdate) {
        if (MainActivity.toolbar != null) {
            if (method.isDarkMode()) {
                MainActivity.toolbar.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getResources().getString(R.string.profile) + "</font>"));
            } else {
                MainActivity.toolbar.setTitle(Html.fromHtml("<font color=\"#000000\">" + getResources().getString(R.string.profile) + "</font>"));
            }
        }
        llMain.setVisibility(View.GONE);
        profile(method.userId());
    }

    @Subscribe
    public void getData(Events.CancelOrder cancelOrder) {
        for (int i = 0; i < myOrderLists.size(); i++) {
            for (int j = 0; j < cancelOrder.getMyOrderLists().size(); j++) {
                if (myOrderLists.get(i).getProduct_id().equals(cancelOrder.getMyOrderLists().get(j).getProduct_id())
                        && myOrderLists.get(i).getOrder_id().equals(cancelOrder.getMyOrderLists().get(j).getOrder_id())) {
                    myOrderLists.get(i).setOrder_status(cancelOrder.getMyOrderLists().get(j).getOrder_status());
                    myOrderLists.get(i).setCurrent_order_status(cancelOrder.getMyOrderLists().get(j).getCurrent_order_status());
                }
            }
        }
        if (proMyOrder != null) {
            proMyOrder.notifyDataSetChanged();
        }
    }

    @Subscribe
    public void getData(Events.EventBankDetail eventBankDetail) {
        String stringBank = getResources().getString(R.string.view_all_bank) + " " + "(" + eventBankDetail.getBankCount() + ")";
        textViewBankCount.setText(stringBank);
        if (eventBankDetail.getBankCount().equals("0")) {
            textViewBank.setVisibility(View.GONE);
        } else {
            textViewBank.setVisibility(View.VISIBLE);
            textViewBank.setText(eventBankDetail.getBankDetails());
        }
    }

    @Subscribe
    public void getData(Events.EventMYAddress eventMYAddress) {
        String stringAddress = getResources().getString(R.string.view_all_address) + " " + "(" + eventMYAddress.getAddressCount() + ")";
        textViewAddCount.setText(stringAddress);
        if (eventMYAddress.getAddressCount().equals("0")) {
            textViewAdd.setVisibility(View.GONE);
        } else {
            textViewAdd.setVisibility(View.VISIBLE);
            textViewAdd.setText(eventMYAddress.getAddress());
        }

    }

    private void profile(String userId) {

        if (getActivity() != null) {

            progressBar.setVisibility(View.VISIBLE);

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("user_id", userId);
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<UserProfileRP> call = apiService.getProfile(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<UserProfileRP>() {
                @SuppressLint("UseCompatLoadingForDrawables")
                @Override
                public void onResponse(@NotNull Call<UserProfileRP> call, @NotNull Response<UserProfileRP> response) {

                    if (getActivity() != null) {

                        try {

                            UserProfileRP userProfileRP = response.body();

                            assert userProfileRP != null;
                            if (userProfileRP.getStatus().equals("1")) {

                                if (userProfileRP.getSuccess().equals("1")) {

                                    String loginType = method.getLoginType();
                                    if (loginType.equals("google") || loginType.equals("facebook")) {
                                        cardViewChangePassword.setVisibility(View.GONE);
                                    }

                                    if (loginType.equals("google")) {
                                        imageViewLoginType.setImageDrawable(getResources().getDrawable(R.drawable.google_user_pro));
                                    } else if (loginType.equals("facebook")) {
                                        imageViewLoginType.setImageDrawable(getResources().getDrawable(R.drawable.fb_user_pro));
                                    } else {
                                        imageViewLoginType.setVisibility(View.GONE);
                                    }

                                    Glide.with(getActivity().getApplicationContext()).load(userProfileRP.getUser_image())
                                            .placeholder(R.drawable.user_profile).into(circleImageView);

                                    circleImageView.setOnClickListener(v -> startActivity(new Intent(getActivity(), ShowImage.class)
                                            .putExtra("path", userProfileRP.getUser_image())));

                                    textViewName.setText(userProfileRP.getUser_name());
                                    if (userProfileRP.getUser_email().equals("")) {
                                        textViewEmail.setVisibility(View.GONE);
                                    } else {
                                        textViewEmail.setVisibility(View.VISIBLE);
                                        textViewEmail.setText(userProfileRP.getUser_email());
                                    }

                                    String stringAddress = getResources().getString(R.string.view_all_address) + " " + "(" + userProfileRP.getAddress_count() + ")";
                                    textViewAddCount.setText(stringAddress);
                                    if (userProfileRP.getAddress_count().equals("0")) {
                                        textViewAdd.setVisibility(View.GONE);
                                    } else {
                                        textViewAdd.setText(userProfileRP.getAddress());
                                    }

                                    String stringBank = getResources().getString(R.string.view_all_bank) + " " + "(" + userProfileRP.getBank_count() + ")";
                                    textViewBankCount.setText(stringBank);
                                    if (userProfileRP.getBank_count().equals("0")) {
                                        textViewBank.setVisibility(View.GONE);
                                    } else {
                                        textViewBank.setText(userProfileRP.getBank_details());
                                    }

                                    myOrderLists.addAll(userProfileRP.getMyOrderLists());
                                    if (myOrderLists.size() != 0) {

                                        textViewNoDataMyOrder.setVisibility(View.GONE);
                                        textViewMyOrder.setVisibility(View.VISIBLE);
                                        recyclerViewMyOrder.setVisibility(View.VISIBLE);

                                        proMyOrder = new ProMyOrder(getActivity(), myOrderLists, "pro_my_order", onClick);
                                        recyclerViewMyOrder.setAdapter(proMyOrder);

                                        textViewMyOrder.setOnClickListener(v -> getActivity().getSupportFragmentManager().beginTransaction()
                                                .add(R.id.frameLayout_main, new MyOrderFragment(), getResources().getString(R.string.my_order))
                                                .addToBackStack(getResources().getString(R.string.my_order))
                                                .commitAllowingStateLoss());

                                    } else {
                                        recyclerViewMyOrder.setVisibility(View.GONE);
                                        textViewMyOrder.setVisibility(View.GONE);
                                        textViewNoDataMyOrder.setVisibility(View.VISIBLE);
                                    }

                                    llMain.setVisibility(View.VISIBLE);

                                    imageViewEdit.setOnClickListener(v -> {
                                        EditProfileFragment editProfileFragment = new EditProfileFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("profileId", userProfileRP.getId());
                                        editProfileFragment.setArguments(bundle);
                                        getActivity().getSupportFragmentManager().beginTransaction()
                                                .add(R.id.frameLayout_main, editProfileFragment, getResources().getString(R.string.edit_profile))
                                                .addToBackStack(getResources().getString(R.string.edit_profile))
                                                .commitAllowingStateLoss();
                                    });

                                    cardViewAddress.setOnClickListener(v -> getActivity().getSupportFragmentManager().beginTransaction()
                                            .add(R.id.frameLayout_main, new MyAddressFragment(), getResources().getString(R.string.my_address))
                                            .addToBackStack(getResources().getString(R.string.my_address))
                                            .commitAllowingStateLoss());

                                    cardViewBank.setOnClickListener(v -> getActivity().getSupportFragmentManager().beginTransaction()
                                            .add(R.id.frameLayout_main, new MyBankDetailFragment(), getResources().getString(R.string.my_bank_detail))
                                            .addToBackStack(getResources().getString(R.string.my_bank_detail))
                                            .commitAllowingStateLoss());

                                    cardViewChangePassword.setOnClickListener(v -> getActivity().getSupportFragmentManager().beginTransaction()
                                            .add(R.id.frameLayout_main, new ChangePasswordFragment(), getResources().getString(R.string.change_password))
                                            .addToBackStack(getResources().getString(R.string.change_password))
                                            .commitAllowingStateLoss());

                                } else {
                                    changView(false);
                                    method.alertBox(userProfileRP.getMsg());
                                }

                            } else if (userProfileRP.getStatus().equals("2")) {
                                method.suspend(userProfileRP.getMessage());
                            } else {
                                changView(false);
                                method.alertBox(userProfileRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d(Constant.exceptionError, e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(@NotNull Call<UserProfileRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e(Constant.failApi, t.toString());
                    changView(false);
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
