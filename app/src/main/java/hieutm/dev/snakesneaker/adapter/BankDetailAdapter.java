package hieutm.dev.snakesneaker.adapter;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.fragment.AddBankDetailFragment;
import hieutm.dev.snakesneaker.interfaces.OnClickSend;
import hieutm.dev.snakesneaker.item.BankDetailList;

public class BankDetailAdapter extends RecyclerView.Adapter<BankDetailAdapter.ViewHolder> {

    private Activity activity;
    private OnClickSend onClickSend;
    private int lastSelectedPosition = 0;
    private List<BankDetailList> bankDetailLists;

    public BankDetailAdapter(Activity activity, List<BankDetailList> bankDetailLists, OnClickSend onClickSend) {
        this.activity = activity;
        this.onClickSend = onClickSend;
        this.bankDetailLists = bankDetailLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.bank_detail_adapter, parent, false);

        return new BankDetailAdapter.ViewHolder(view);
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

        holder.buttonEdit.setOnClickListener(v -> {
            AddBankDetailFragment addBankDetailFragment = new AddBankDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putString("type", "edit_bank");
            bundle.putString("bank_id", bankDetailLists.get(position).getId());
            addBankDetailFragment.setArguments(bundle);
            ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main, addBankDetailFragment,
                    activity.getResources().getString(R.string.edit_bank_detail)).addToBackStack(activity.getResources().getString(R.string.edit_bank_detail))
                    .commitAllowingStateLoss();
        });

        //since only one radio button is allowed to be selected,
        // this condition un-checks previous selections
        holder.radioButton.setChecked(lastSelectedPosition == position);
        if (lastSelectedPosition == position) {
            holder.buttonEdit.setVisibility(View.VISIBLE);
            onClickSend.onClick(bankDetailLists.get(position).getId(), "", "", position);
        } else {
            holder.buttonEdit.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return bankDetailLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private MaterialButton buttonEdit;
        private RadioButton radioButton;
        private LinearLayout linearLayout;
        private MaterialTextView textViewBankName, textViewAccNo, textViewName, textViewIfsc, textViewMobile, textViewAccType, textViewEmail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.ll_bankDetail_adapter);
            buttonEdit = itemView.findViewById(R.id.button_edit_bankDetail_adapter);
            radioButton = itemView.findViewById(R.id.radioButton_bankDetail_adapter);
            textViewBankName = itemView.findViewById(R.id.textView_bankName_bankDetail_adapter);
            textViewAccNo = itemView.findViewById(R.id.textView_accNo_bankDetail_adapter);
            textViewName = itemView.findViewById(R.id.textView_name_bankDetail_adapter);
            textViewIfsc = itemView.findViewById(R.id.textView_ifsc_bankDetail_adapter);
            textViewMobile = itemView.findViewById(R.id.textView_mob_bankDetail_adapter);
            textViewAccType = itemView.findViewById(R.id.textView_accType_bankDetail_adapter);
            textViewEmail = itemView.findViewById(R.id.textView_email_bankDetail_adapter);

            linearLayout.setOnClickListener(v -> {
                lastSelectedPosition = getAdapterPosition();
                notifyDataSetChanged();
            });

            radioButton.setOnClickListener(v -> {
                lastSelectedPosition = getAdapterPosition();
                notifyDataSetChanged();
            });

        }
    }
}
