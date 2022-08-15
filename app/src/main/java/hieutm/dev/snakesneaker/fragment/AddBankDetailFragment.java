package hieutm.dev.snakesneaker.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;

import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.activity.MainActivity;
import hieutm.dev.snakesneaker.response.AddEditRemoveBankRP;
import hieutm.dev.snakesneaker.response.GetBankDetailRP;
import hieutm.dev.snakesneaker.rest.ApiClient;
import hieutm.dev.snakesneaker.rest.ApiInterface;
import hieutm.dev.snakesneaker.util.API;
import hieutm.dev.snakesneaker.util.Constant;
import hieutm.dev.snakesneaker.util.Events;
import hieutm.dev.snakesneaker.util.GlobalBus;
import hieutm.dev.snakesneaker.util.Method;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBankDetailFragment extends Fragment {

    private Method method;
    private String accountType, type, bankId;
    private InputMethodManager imm;
    private AppCompatSpinner spinner;
    private MaterialCheckBox checkBox;
    private ProgressDialog progressDialog;
    private TextInputEditText editTextBankName, editTextAccNo, editTextName, editTextIfsc, editTextMobile, editTextEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.add_bank_detail_fragment, container, false);

        assert getArguments() != null;
        type = getArguments().getString("type");
        assert type != null;
        if (type.equals("edit_bank")) {
            bankId = getArguments().getString("bank_id");
            MainActivity.toolbar.setTitle(getResources().getString(R.string.edit_bank_detail));
        } else {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.add_bank_detail));
        }

        method = new Method(getActivity());

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        progressDialog = new ProgressDialog(getActivity());

        editTextBankName = view.findViewById(R.id.editText_bankName_add_bank_detail);
        editTextAccNo = view.findViewById(R.id.editText_accountNO_add_bank_detail);
        editTextIfsc = view.findViewById(R.id.editText_ifsc_add_bank_detail);
        editTextName = view.findViewById(R.id.editText_bankHolderName_add_bank_detail);
        editTextMobile = view.findViewById(R.id.editText_bankHolderMobile_add_bank_detail);
        editTextEmail = view.findViewById(R.id.editText_bankHolderEmail_add_bank_detail);
        spinner = view.findViewById(R.id.spinner_add_bank_detail);
        checkBox = view.findViewById(R.id.checkBox_add_bank_detail);
        MaterialButton button = view.findViewById(R.id.button_ok_add_bank_detail);

        List<String> string_accType = new ArrayList<String>();
        string_accType.add(getResources().getString(R.string.saving));
        string_accType.add(getResources().getString(R.string.current));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, string_accType);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.textView_app_color));
                if (position == 0) {
                    accountType = "saving";
                } else {
                    accountType = "current";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("data_app", parent.toString());
            }
        });

        button.setOnClickListener(v -> form());

        if (type.equals("edit_bank")) {
            getBankDetail(method.userId(), bankId);
        }

        return view;

    }

    private void form() {

        String bankName = editTextBankName.getText().toString();
        String accNo = editTextAccNo.getText().toString();
        String ifsc = editTextIfsc.getText().toString();
        String name = editTextName.getText().toString();
        String mobile = editTextMobile.getText().toString();
        String email = editTextEmail.getText().toString();

        editTextBankName.setError(null);
        editTextAccNo.setError(null);
        editTextIfsc.setError(null);
        editTextName.setError(null);
        editTextMobile.setError(null);
        editTextEmail.setError(null);

        if (bankName.equals("") || bankName.isEmpty()) {
            editTextBankName.requestFocus();
            editTextBankName.setError(getResources().getString(R.string.please_enter_bank_name));
        } else if (accNo.equals("") || accNo.isEmpty()) {
            editTextAccNo.requestFocus();
            editTextAccNo.setError(getResources().getString(R.string.please_enter_bank_acc_no));
        } else if (ifsc.equals("") || ifsc.isEmpty()) {
            editTextIfsc.requestFocus();
            editTextIfsc.setError(getResources().getString(R.string.please_enter_ifsc));
        } else if (name.equals("") || name.isEmpty()) {
            editTextName.requestFocus();
            editTextName.setError(getResources().getString(R.string.please_enter_name));
        } else if (mobile.equals("") || mobile.isEmpty()) {
            editTextMobile.requestFocus();
            editTextMobile.setError(getResources().getString(R.string.please_enter_mobile));
        } else if (email.equals("") || email.isEmpty()) {
            editTextEmail.requestFocus();
            editTextEmail.setError(getResources().getString(R.string.please_enter_email));
        } else {

            editTextBankName.clearFocus();
            editTextAccNo.clearFocus();
            editTextIfsc.clearFocus();
            editTextName.clearFocus();
            editTextMobile.clearFocus();
            editTextEmail.clearFocus();
            imm.hideSoftInputFromWindow(editTextBankName.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editTextAccNo.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editTextIfsc.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editTextName.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editTextMobile.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editTextEmail.getWindowToken(), 0);

            String typeSend = null;
            if (type.equals("edit_bank")) {
                typeSend = "edit";
            } else {
                typeSend = "add";
            }

            if (method.isNetworkAvailable()) {
                submitBankDetail(bankName, accNo, ifsc, name, mobile, email, typeSend);
            } else {
                method.alertBox(getResources().getString(R.string.internet_connection));
            }
        }

    }

    public void getBankDetail(String userId, String bankId) {

        if (getActivity() != null) {

            progressDialog.show();
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setCancelable(false);

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("user_id", userId);
            jsObj.addProperty("bank_id", bankId);
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<GetBankDetailRP> call = apiService.getBankDetail(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<GetBankDetailRP>() {
                @Override
                public void onResponse(@NotNull Call<GetBankDetailRP> call, @NotNull Response<GetBankDetailRP> response) {

                    if (getActivity() != null) {

                        try {
                            GetBankDetailRP getBankDetailRP = response.body();
                            assert getBankDetailRP != null;

                            if (getBankDetailRP.getStatus().equals("1")) {

                                editTextBankName.setText(getBankDetailRP.getBank_name());
                                editTextAccNo.setText(getBankDetailRP.getAccount_no());
                                editTextIfsc.setText(getBankDetailRP.getBank_ifsc());
                                editTextName.setText(getBankDetailRP.getBank_holder_name());
                                editTextMobile.setText(getBankDetailRP.getBank_holder_phone());
                                editTextEmail.setText(getBankDetailRP.getBank_holder_email());

                                if (getBankDetailRP.getAccount_type().equals("saving")) {
                                    spinner.setSelection(0);
                                } else {
                                    spinner.setSelection(1);
                                }

                                checkBox.setChecked(getBankDetailRP.getIs_default().equals("true"));

                            } else if (getBankDetailRP.getStatus().equals("2")) {
                                method.suspend(getBankDetailRP.getMessage());
                            } else {
                                method.alertBox(getBankDetailRP.getMessage());
                            }

                            progressDialog.dismiss();

                        } catch (Exception e) {
                            Log.d(Constant.exceptionError, e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }

                    progressDialog.dismiss();

                }

                @Override
                public void onFailure(@NotNull Call<GetBankDetailRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e(Constant.failApi, t.toString());
                    progressDialog.dismiss();
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }

    }

    private void submitBankDetail(String bankName, String accountNo, String bankIfsc, String name, String phone, String email, String typeSend) {

        if (getActivity() != null) {

            progressDialog.show();
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setCancelable(false);

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("user_id", method.userId());
            jsObj.addProperty("bank_name", bankName);
            jsObj.addProperty("account_no", accountNo);
            jsObj.addProperty("bank_ifsc", bankIfsc);
            jsObj.addProperty("account_type", accountType);
            jsObj.addProperty("name", name);
            jsObj.addProperty("phone", phone);
            jsObj.addProperty("email", email);
            jsObj.addProperty("is_default", checkBox.isChecked());
            jsObj.addProperty("type", typeSend);
            if (typeSend.equals("edit")) {
                jsObj.addProperty("bank_id", bankId);
            }
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<AddEditRemoveBankRP> call = apiService.submitBankDetail(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<AddEditRemoveBankRP>() {
                @Override
                public void onResponse(@NotNull Call<AddEditRemoveBankRP> call, @NotNull Response<AddEditRemoveBankRP> response) {

                    if (getActivity() != null) {

                        try {
                            AddEditRemoveBankRP addEditRemoveBankRP = response.body();
                            assert addEditRemoveBankRP != null;

                            if (addEditRemoveBankRP.getStatus().equals("1")) {

                                if (addEditRemoveBankRP.getSuccess().equals("1")) {
                                    Toast.makeText(getActivity(), addEditRemoveBankRP.getMsg(), Toast.LENGTH_SHORT).show();

                                    if (type.equals("edit_bank") || type.equals("add_bank_my") || type.equals("add_bank_cancel")) {
                                        getActivity().getSupportFragmentManager().popBackStack();
                                    }

                                    Events.EventBankDetail eventBankDetail = new Events.EventBankDetail(addEditRemoveBankRP.getBank_count(), addEditRemoveBankRP.getBank_details(),"addEditBank");
                                    GlobalBus.getBus().post(eventBankDetail);

                                }

                            } else if (addEditRemoveBankRP.getStatus().equals("2")) {
                                method.suspend(addEditRemoveBankRP.getMessage());
                            } else {
                                method.alertBox(addEditRemoveBankRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d(Constant.exceptionError, e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }

                    progressDialog.dismiss();

                }

                @Override
                public void onFailure(@NotNull Call<AddEditRemoveBankRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e(Constant.failApi, t.toString());
                    progressDialog.dismiss();
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }

    }


}
