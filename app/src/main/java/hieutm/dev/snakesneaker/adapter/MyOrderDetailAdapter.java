package hieutm.dev.snakesneaker.adapter;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import hieutm.dev.snakesneaker.interfaces.OnClick;
import hieutm.dev.snakesneaker.item.MyOrderList;
import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.util.Method;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class MyOrderDetailAdapter extends RecyclerView.Adapter<MyOrderDetailAdapter.ViewHolder> {

    private Activity activity;
    private Method method;
    private String type;
    private List<MyOrderList> myOrderLists;

    public MyOrderDetailAdapter(Activity activity, String type, List<MyOrderList> myOrderLists, OnClick onClick) {
        this.activity = activity;
        this.type = type;
        this.myOrderLists = myOrderLists;
        method = new Method(activity, onClick);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.my_order_detail_adapter, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Glide.with(activity).load(myOrderLists.get(position).getProduct_image())
                .placeholder(R.drawable.placeholder_square)
                .into(holder.imageView);

        if (myOrderLists.get(position).getCurrent_order_status().equals("true")) {
            holder.view.setBackgroundColor(activity.getResources().getColor(R.color.green));
        } else {
            holder.view.setBackgroundColor(activity.getResources().getColor(R.color.red));
        }

        holder.textViewStatus.setText(myOrderLists.get(position).getOrder_status());
        holder.textViewTitle.setText(myOrderLists.get(position).getProduct_title());

        holder.linearLayout.setOnClickListener(v -> method.click(position, type, myOrderLists.get(position).getProduct_title(), myOrderLists.get(position).getOrder_unique_id(), myOrderLists.get(position).getProduct_id(), "", ""));

    }

    @Override
    public int getItemCount() {
        return myOrderLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private ImageView imageView;
        private LinearLayout linearLayout;
        private MaterialTextView textViewTitle, textViewStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            view = itemView.findViewById(R.id.view_my_orderDetail_adapter);
            imageView = itemView.findViewById(R.id.imageView_my_orderDetail_adapter);
            linearLayout = itemView.findViewById(R.id.ll_my_orderDetail_adapter);
            textViewTitle = itemView.findViewById(R.id.textView_title_my_orderDetail_adapter);
            textViewStatus = itemView.findViewById(R.id.textView_status_my_orderDetail_adapter);

        }
    }
}
