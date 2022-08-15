package hieutm.dev.snakesneaker.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.fragment.AddBankDetailFragment;
import hieutm.dev.snakesneaker.item.BankDetailList;
import hieutm.dev.snakesneaker.response.AddEditRemoveBankRP;
import hieutm.dev.snakesneaker.rest.ApiClient;
import hieutm.dev.snakesneaker.rest.ApiInterface;
import hieutm.dev.snakesneaker.util.API;
import hieutm.dev.snakesneaker.util.Constant;
import hieutm.dev.snakesneaker.util.Events;
import hieutm.dev.snakesneaker.util.GlobalBus;
import hieutm.dev.snakesneaker.util.Method;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyBankDetailAdapter extends RecyclerView.Adapter<MyBankDetailAdapter.ViewHolder> {

    private Activity activity;
    private Method method;
    private List<BankDetailList> bankDetailLists;

    public MyBankDetailAdapter(Activity activity, List<BankDetailList> bankDetailLists) {
        this.activity = activity;
        this.bankDetailLists = bankDetailLists;
        method = new Method(activity);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.my_bank_detail_adapter, parent, false);

        return new MyBankDetailAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.textViewBankName.setText(bankDetailLists.get(position).getBank_name());
        holder.textViewAccNo.setText(bankDetailLists.get(position).getAccount_no());
        holder.textViewName.setText(bankDetailLists.get(position).getBank_holder_name());
        holder.textViewIfsc.setText(bankDetailLists.get(position).getBank_ifsc());
        holder.textViewMobile.setText(bankDetailLists.get(position).getBank_holder_phone());
        holder.textViewAccType.setText(bankDetailLists.get(position).getAccount_type());
        holder.textViewEmail.setText(bankDetailLists.get(position).getBank_holder_email());

        holder.textViewEdit.setOnClickListener(v -> {
            AddBankDetailFragment addBankDetailFragment = new AddBankDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putString("type", "edit_bank");
            bundle.putString("bank_id", bankDetailLists.get(position).getId());
            addBankDetailFragment.setArguments(bundle);
            ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, addBankDetailFragment,
                    activity.getResources().getString(R.string.edit_bank_detail)).addToBackStack(activity.getResources().getString(R.string.edit_bank_detail))
                    .commitAllowingStateLoss();
        });

        holder.textViewRemove.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity, R.style.DialogTitleTextStyle);
            builder.setMessage(activity.getResources().getString(R.string.delete_msg));
            builder.setCancelable(false);
            builder.setPositiveButton(activity.getResources().getString(R.string.yes),
                    (arg0, arg1) -> deleteBankDetail(bankDetailLists.get(position).getId(), method.userId(), position));
            builder.setNegativeButton(activity.getResources().getString(R.string.no),
                    (dialog, which) -> dialog.dismiss());

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });

    }

    @Override
    public int getItemCount() {
        return bankDetailLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private MaterialTextView textViewBankName, textViewAccNo, textViewName, textViewIfsc, textViewMobile,
                textViewAccType, textViewEmail, textViewEdit, textViewRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewBankName = itemView.findViewById(R.id.textView_bankName_myBankDetail_adapter);
            textViewAccNo = itemView.findViewById(R.id.textView_accNo_myBankDetail_adapter);
            textViewName = itemView.findViewById(R.id.textView_name_myBankDetail_adapter);
            textViewIfsc = itemView.findViewById(R.id.textView_ifsc_myBankDetail_adapter);
            textViewMobile = itemView.findViewById(R.id.textView_mob_myBankDetail_adapter);
            textViewAccType = itemView.findViewById(R.id.textView_accType_myBankDetail_adapter);
            textViewEmail = itemView.findViewById(R.id.textView_email_myBankDetail_adapter);
            textViewEdit = itemView.findViewById(R.id.textView_edit_myBankDetail_adapter);
            textViewRemove = itemView.findViewById(R.id.textView_remove_myBankDetail_adapter);

        }
    }

    private void deleteBankDetail(String bankId, String userId, int position) {

        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.show();
        progressDialog.setMessage(activity.getResources().getString(R.string.processing));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(activity));
        jsObj.addProperty("bank_id", bankId);
        jsObj.addProperty("user_id", userId);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<AddEditRemoveBankRP> call = apiService.deleteBank(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<AddEditRemoveBankRP>() {
            @Override
            public void onResponse(@NotNull Call<AddEditRemoveBankRP> call, @NotNull Response<AddEditRemoveBankRP> response) {

                try {

                    AddEditRemoveBankRP addEditRemoveBankRP = response.body();

                    assert addEditRemoveBankRP != null;
                    if (addEditRemoveBankRP.getStatus().equals("1")) {
                        if (addEditRemoveBankRP.getSuccess().equals("1")) {
                            Toast.makeText(activity, addEditRemoveBankRP.getMsg(), Toast.LENGTH_SHORT).show();
                            Events.EventBankDetail eventBankDetail = new Events.EventBankDetail(addEditRemoveBankRP.getBank_count(), addEditRemoveBankRP.getBank_details(), "removeBank");
                            GlobalBus.getBus().post(eventBankDetail);
                            bankDetailLists.remove(position);
                            notifyDataSetChanged();
                        } else {
                            method.alertBox(addEditRemoveBankRP.getMsg());
                        }

                    } else if (addEditRemoveBankRP.getStatus().equals("2")) {
                        method.suspend(addEditRemoveBankRP.getMessage());
                    } else {
                        method.alertBox(addEditRemoveBankRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d(Constant.exceptionError, e.toString());
                    method.alertBox(activity.getResources().getString(R.string.failed_try_again));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<AddEditRemoveBankRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e(Constant.failApi, t.toString());
                progressDialog.dismiss();
                method.alertBox(activity.getResources().getString(R.string.failed_try_again));
            }

        });

    }

}
