package hieutm.dev.snakesneaker.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.adapter.CountryAdapter;
import hieutm.dev.snakesneaker.interfaces.CountryIF;
import hieutm.dev.snakesneaker.item.CountryList;
import hieutm.dev.snakesneaker.response.AddEditRemoveAddressRP;
import hieutm.dev.snakesneaker.response.CountryRP;
import hieutm.dev.snakesneaker.response.GetAddressRP;
import hieutm.dev.snakesneaker.rest.ApiClient;
import hieutm.dev.snakesneaker.rest.ApiInterface;
import hieutm.dev.snakesneaker.util.API;
import hieutm.dev.snakesneaker.util.Constant;
import hieutm.dev.snakesneaker.util.Events;
import hieutm.dev.snakesneaker.util.GlobalBus;
import hieutm.dev.snakesneaker.util.Method;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
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

public class AddAddress extends AppCompatActivity {

    private Method method;
    private MaterialToolbar toolbar;
    private MaterialButton button;
    private ProgressDialog progressDialog;
    private RadioGroup radioGroup;
    private List<CountryList> countryLists;
    private InputMethodManager imm;
    private MaterialTextView textViewCountry;
    private String selectCountry, addressId;
    private String addressType, type, typeOrder, productId, productSize;
    private TextInputEditText editTextName, editTextPinCode, editTextHouseName, editTextRoadName, editTextCity,
            editTextState, editTextDistrict, editTextLandmark, editTextPhoneNo, editTextAlterNatePhoneNo, editTextEmail;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        method = new Method(AddAddress.this);
        method.forceRTLIfSupported();

        countryLists = new ArrayList<>();
        selectCountry = getResources().getString(R.string.select_country);

        progressDialog = new ProgressDialog(AddAddress.this);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        toolbar = findViewById(R.id.toolbar_address);

        Intent intent = getIntent();
        type = intent.getStringExtra("type");

        assert type != null;
        if (type.equals("edit_my_add") || type.equals("edit_change_add")) {
            addressId = intent.getStringExtra("address_id");
            toolbar.setTitle(getResources().getString(R.string.edit_address));
        } else {
            toolbar.setTitle(getResources().getString(R.string.add_address));
        }

        if (type.equals("check_address")) {
            typeOrder = intent.getStringExtra("type_order");
            productId = intent.getStringExtra("product_id");
            productSize = intent.getStringExtra("product_size");
        }

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        LinearLayout linearLayout = findViewById(R.id.linearLayout_address);
        method.bannerAd(linearLayout);

        editTextName = findViewById(R.id.editText_name_address);
        editTextPinCode = findViewById(R.id.editText_pinCode_address);
        editTextHouseName = findViewById(R.id.editText_houseName_address);
        editTextRoadName = findViewById(R.id.editText_roadName_address);
        editTextCity = findViewById(R.id.editText_city_address);
        editTextState = findViewById(R.id.editText_state_address);
        editTextDistrict = findViewById(R.id.editText_district_address);
        editTextLandmark = findViewById(R.id.editText_landmark_address);
        editTextPhoneNo = findViewById(R.id.editText_phoneNo_address);
        editTextAlterNatePhoneNo = findViewById(R.id.editText_alterNate_phoneNo_address);
        editTextEmail = findViewById(R.id.editText_email_address);
        textViewCountry = findViewById(R.id.textView_country_address);
        radioGroup = findViewById(R.id.radioGroup_address);
        button = findViewById(R.id.button_ok_address);

        if (method.isNetworkAvailable()) {
            if (method.isLogin()) {
                getCountry(method.userId());
            } else {
                getCountry("0");
            }
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

    }

    private void submit() {

        String pinCode = editTextPinCode.getText().toString();
        String houseName = editTextHouseName.getText().toString();
        String roadName = editTextRoadName.getText().toString();
        String city = editTextCity.getText().toString();
        String state = editTextState.getText().toString();
        String district = editTextDistrict.getText().toString();
        String landmark = editTextLandmark.getText().toString();
        String name = editTextName.getText().toString();
        String email = editTextEmail.getText().toString();
        String phoneNo = editTextPhoneNo.getText().toString();
        String alterNatePhoneNo = editTextAlterNatePhoneNo.getText().toString();

        if (pinCode.equals("") || pinCode.isEmpty()) {
            editTextPinCode.requestFocus();
            editTextPinCode.setError(getResources().getString(R.string.please_enter_pinCode));
        } else if (houseName.equals("") || houseName.isEmpty()) {
            editTextHouseName.requestFocus();
            editTextHouseName.setError(getResources().getString(R.string.please_enter_houseName));
        } else if (roadName.equals("") || roadName.isEmpty()) {
            editTextRoadName.requestFocus();
            editTextRoadName.setError(getResources().getString(R.string.please_enter_roadName));
        } else if (city.equals("") || city.isEmpty()) {
            editTextCity.requestFocus();
            editTextCity.setError(getResources().getString(R.string.please_enter_city));
        } else if (name.equals("") || name.isEmpty()) {
            editTextName.requestFocus();
            editTextName.setError(getResources().getString(R.string.please_enter_name));
        } else if (!isValidMail(email) || email.isEmpty()) {
            editTextEmail.requestFocus();
            editTextEmail.setError(getResources().getString(R.string.please_enter_email));
        } else if (phoneNo.equals("") || phoneNo.isEmpty()) {
            editTextPhoneNo.requestFocus();
            editTextPhoneNo.setError(getResources().getString(R.string.please_enter_phone));
        } else if (selectCountry == null || selectCountry.equals("") || selectCountry.equals(getResources().getString(R.string.select_country))) {
            method.alertBox(getResources().getString(R.string.please_select_address_type));
        } else if (addressType == null || addressType.equals("") || addressType.isEmpty()) {
            method.alertBox(getResources().getString(R.string.please_select_address_type));
        } else {

            editTextPinCode.clearFocus();
            editTextHouseName.clearFocus();
            editTextRoadName.clearFocus();
            editTextCity.clearFocus();
            editTextState.clearFocus();
            editTextDistrict.clearFocus();
            editTextLandmark.clearFocus();
            editTextName.clearFocus();
            editTextEmail.clearFocus();
            editTextPhoneNo.clearFocus();
            editTextAlterNatePhoneNo.clearFocus();
            imm.hideSoftInputFromWindow(editTextPinCode.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editTextHouseName.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editTextRoadName.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editTextCity.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editTextState.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editTextDistrict.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editTextLandmark.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editTextName.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editTextEmail.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editTextPhoneNo.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editTextAlterNatePhoneNo.getWindowToken(), 0);

            if (method.isNetworkAvailable()) {
                if (method.isLogin()) {
                    String typeAddSend = null;
                    if (type.equals("add_change_address") || type.equals("add_my_address") || type.equals("check_address")) {
                        typeAddSend = "add";
                    } else {
                        typeAddSend = "edit";
                    }
                    addAddress(name, pinCode, houseName, roadName, city, state, district, landmark, phoneNo, alterNatePhoneNo, email, typeAddSend);
                } else {
                    method.alertBox(getResources().getString(R.string.you_have_not_login));
                }
            } else {
                method.alertBox(getResources().getString(R.string.internet_connection));
            }
        }

    }

    private boolean isValidMail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void getCountry(String userId) {

        countryLists.clear();

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(AddAddress.this));
        jsObj.addProperty("user_id", userId);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<CountryRP> call = apiService.getCountry(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<CountryRP>() {
            @Override
            public void onResponse(@NotNull Call<CountryRP> call, @NotNull Response<CountryRP> response) {

                try {
                    CountryRP countryRP = response.body();
                    assert countryRP != null;

                    if (countryRP.getStatus().equals("1")) {

                        countryLists.addAll(countryRP.getCountryLists());

                        editTextName.setText(countryRP.getUser_name());
                        editTextEmail.setText(countryRP.getUser_email());
                        editTextPhoneNo.setText(countryRP.getUser_phone());

                        RadioButton rbHome = (RadioButton) radioGroup.getChildAt(0);
                        RadioButton rbOffice = (RadioButton) radioGroup.getChildAt(1);
                        rbHome.setText(countryRP.getHome_address_lbl());
                        rbOffice.setText(countryRP.getOffice_address_lbl());

                        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @SuppressLint("ResourceType")
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                RadioButton rb = group.findViewById(checkedId);
                                if (null != rb && checkedId > -1) {
                                    if (rb.getText().equals(countryRP.getHome_address_lbl())) {
                                        addressType = "1";
                                    } else {
                                        addressType = "2";
                                    }
                                }
                            }
                        });

                        if (type.equals("edit_my_add") || type.equals("edit_change_add")) {
                            getAddress(addressId);
                        }

                        textViewCountry.setOnClickListener(v -> countryDialog());

                        button.setOnClickListener(v -> submit());

                    }

                } catch (Exception e) {
                    Log.d(Constant.exceptionError, e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<CountryRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e(Constant.failApi, t.toString());
                progressDialog.dismiss();
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });

    }

    @SuppressLint("ResourceType")
    private void getAddress(String address_id) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(AddAddress.this));
        jsObj.addProperty("address_id", address_id);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<GetAddressRP> call = apiService.getAddress(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<GetAddressRP>() {
            @Override
            public void onResponse(@NotNull Call<GetAddressRP> call, @NotNull Response<GetAddressRP> response) {

                try {

                    GetAddressRP getAddressRP = response.body();

                    assert getAddressRP != null;

                    if (getAddressRP.getStatus().equals("1")) {

                        editTextPinCode.setText(getAddressRP.getPincode());
                        editTextHouseName.setText(getAddressRP.getBuilding_name());
                        editTextRoadName.setText(getAddressRP.getRoad_area_colony());
                        editTextCity.setText(getAddressRP.getCity());
                        editTextDistrict.setText(getAddressRP.getDistrict());
                        editTextState.setText(getAddressRP.getState());
                        editTextLandmark.setText(getAddressRP.getLandmark());
                        editTextName.setText(getAddressRP.getName());
                        editTextEmail.setText(getAddressRP.getEmail());
                        editTextPhoneNo.setText(getAddressRP.getMobile_no());
                        editTextAlterNatePhoneNo.setText(getAddressRP.getAlter_mobile_no());

                        selectCountry = getAddressRP.getCountry();
                        textViewCountry.setText(getAddressRP.getCountry());

                        addressType = getAddressRP.getAddress_type();
                        if (addressType.equals("1")) {
                            radioGroup.check(radioGroup.getChildAt(0).getId());
                        } else {
                            radioGroup.check(radioGroup.getChildAt(1).getId());
                        }

                    } else {
                        method.alertBox(getAddressRP.getMessage());
                    }


                } catch (Exception e) {
                    Log.d(Constant.exceptionError, e.toString());
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<GetAddressRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                progressDialog.dismiss();
                Log.e(Constant.failApi, t.toString());
            }
        });

    }

    private void addAddress(String name, String pinCode, String houseName, String roadName, String city,
                            String state, String district, String landmark, String phoneNo, String alterNatePhoneNo,
                            String email, String typeSend) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(AddAddress.this));
        jsObj.addProperty("user_id", method.userId());
        jsObj.addProperty("pincode", pinCode);
        jsObj.addProperty("building_name", houseName);
        jsObj.addProperty("road_area_colony", roadName);
        jsObj.addProperty("city", city);
        jsObj.addProperty("district", district);
        jsObj.addProperty("state", state);
        jsObj.addProperty("landmark", landmark);
        jsObj.addProperty("name", name);
        jsObj.addProperty("mobile_no", phoneNo);
        jsObj.addProperty("alter_mobile_no", alterNatePhoneNo);
        jsObj.addProperty("email", email);
        jsObj.addProperty("country", selectCountry);
        jsObj.addProperty("address_type", addressType);
        jsObj.addProperty("type", typeSend);
        if (typeSend.equals("edit")) {
            jsObj.addProperty("id", addressId);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<AddEditRemoveAddressRP> call = apiService.addEditAddress(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<AddEditRemoveAddressRP>() {
            @Override
            public void onResponse(@NotNull Call<AddEditRemoveAddressRP> call, @NotNull Response<AddEditRemoveAddressRP> response) {

                try {

                    AddEditRemoveAddressRP addEditRemoveAddressRp = response.body();

                    assert addEditRemoveAddressRp != null;

                    if (addEditRemoveAddressRp.getStatus().equals("1")) {

                        if (addEditRemoveAddressRp.getSuccess().equals("1")) {

                            radioGroup.clearCheck();
                            addressType = "";
                            selectCountry = getResources().getString(R.string.select_country);

                            /**
                             * - User choose cart option in profile section and add or edit address then update address MyAddressFragment and profileFragment
                             * - Add or edit address using My Address Fragment then update data
                             */
                            Events.EventMYAddress eventMYAddress = new Events.EventMYAddress(addEditRemoveAddressRp.getAddress(), addEditRemoveAddressRp.getAddress_count(), "addAddress");
                            GlobalBus.getBus().post(eventMYAddress);

                            //check_address this tag used to the check address available or not (detail fragment and cart activity)
                            if (type.equals("check_address")) {

                                startActivity(new Intent(AddAddress.this, OrderSummary.class)
                                        .putExtra("type", typeOrder)
                                        .putExtra("product_id", productId)
                                        .putExtra("product_size", productSize)
                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                finish();

                            } else {

                                // add_my_address -> MyAddressFragment
                                // edit_my_add -> change MyAddressAdapter
                                if (type.equals("add_my_address") || type.equals("edit_my_add")) {
                                    onBackPressed();
                                }

                                //add_change_address -> change address activity add address
                                //edit_change_add -> change change address adapter
                                if (type.equals("add_change_address") || type.equals("edit_change_add")) {
                                    if (addEditRemoveAddressRp.getSuccess().equals("1")) {

                                        Events.UpdateDeliveryAddress updateDeliveryAddress = new Events.UpdateDeliveryAddress(addressId, addEditRemoveAddressRp.getAddress(),
                                                addEditRemoveAddressRp.getName(), addEditRemoveAddressRp.getMobile_no(), addEditRemoveAddressRp.getAddress_type());
                                        GlobalBus.getBus().post(updateDeliveryAddress);

                                        onBackPressed();

                                        Events.OnBackData onBackData = new Events.OnBackData("");
                                        GlobalBus.getBus().post(onBackData);

                                    }

                                    Toast.makeText(AddAddress.this, addEditRemoveAddressRp.getMsg(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            method.alertBox(addEditRemoveAddressRp.getMsg());
                        }

                    } else if (addEditRemoveAddressRp.getStatus().equals("2")) {
                        method.suspend(addEditRemoveAddressRp.getMessage());
                    } else {
                        method.alertBox(addEditRemoveAddressRp.getMessage());
                    }

                } catch (Exception e) {
                    Log.d(Constant.exceptionError, e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<AddEditRemoveAddressRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e(Constant.failApi, t.toString());
                progressDialog.dismiss();
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });


    }

    private void countryDialog() {

        int selectPosition = 0;

        Dialog dialog = new Dialog(AddAddress.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_country);
        dialog.setCancelable(false);
        if (method.isRtl()) {
            dialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        TextInputEditText editTextSearch = dialog.findViewById(R.id.editText_dialog_country);
        ImageView imageViewClose = dialog.findViewById(R.id.imageView_close_dialog_country);
        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerView_dialog_country);

        imageViewClose.setOnClickListener(v -> dialog.dismiss());

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(AddAddress.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setFocusable(false);

        if (type.equals("edit_my_add") || type.equals("edit_change_add") && !selectCountry.equals("")) {
            for (int i = 0; i < countryLists.size(); i++) {
                if (selectCountry.equals(countryLists.get(i).getCountry_name())) {
                    selectPosition = i;
                    recyclerView.getLayoutManager().scrollToPosition(i);
                }
            }
        } else {
            if (!selectCountry.equals("")) {
                for (int i = 0; i < countryLists.size(); i++) {
                    if (selectCountry.equals(countryLists.get(i).getCountry_name())) {
                        selectPosition = i;
                        recyclerView.getLayoutManager().scrollToPosition(i);
                    }
                }
            }
        }

        CountryIF countryIF = countryData -> {
            selectCountry = countryData;
            if (textViewCountry != null) {
                textViewCountry.setText(countryData);
            }
            dialog.dismiss();
        };
        CountryAdapter countryAdapter = new CountryAdapter(AddAddress.this, countryIF, countryLists, selectPosition);
        recyclerView.setAdapter(countryAdapter);

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("data_app", s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                countryAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("data_app", s.toString());
            }
        });


        dialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        super.onBackPressed();
    }

}
