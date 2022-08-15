package hieutm.dev.snakesneaker.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.interfaces.OnClick;
import hieutm.dev.snakesneaker.item.MyOrderList;
import hieutm.dev.snakesneaker.util.Method;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class ProMyOrder extends RecyclerView.Adapter<ProMyOrder.ViewHolder> {

    private Activity activity;
    private Method method;
    private String type;
    private List<MyOrderList> myOrderLists;

    public ProMyOrder(Activity activity, List<MyOrderList> myOrderLists, String type, OnClick onClick) {
        this.activity = activity;
        this.type = type;
        this.myOrderLists = myOrderLists;
        method = new Method(activity, onClick);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.pro_my_order_adapter, parent, false);
        return new ProMyOrder.ViewHolder(view);
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

        holder.relativeLayout.setOnClickListener(v -> method.click(position, type, myOrderLists.get(position).getProduct_title(), myOrderLists.get(position).getOrder_unique_id(),
                myOrderLists.get(position).getProduct_id(), myOrderLists.get(position).getOrder_id(), ""));

    }

    @Override
    public int getItemCount() {
        return myOrderLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private ImageView imageView;
        private RelativeLayout relativeLayout;
        private MaterialTextView textViewTitle, textViewStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            relativeLayout = itemView.findViewById(R.id.rel_pro_adapter);
            imageView = itemView.findViewById(R.id.imageView_pro_adapter);
            view = itemView.findViewById(R.id.view_pro_adapter);
            textViewTitle = itemView.findViewById(R.id.textView_title_pro_adapter);
            textViewStatus = itemView.findViewById(R.id.textView_status_pro_adapter);

        }
    }
}
