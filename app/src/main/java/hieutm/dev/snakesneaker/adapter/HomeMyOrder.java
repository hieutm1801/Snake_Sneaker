package hieutm.dev.snakesneaker.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.interfaces.OnClick;
import hieutm.dev.snakesneaker.item.MyOrderList;
import hieutm.dev.snakesneaker.util.Method;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class HomeMyOrder extends RecyclerView.Adapter<HomeMyOrder.ViewHolder> {

    private Activity activity;
    private String type;
    private Method method;
    private List<MyOrderList> myOrderLists;

    public HomeMyOrder(Activity activity, List<MyOrderList> myOrderLists, String type, OnClick onClick) {
        this.activity = activity;
        this.type = type;
        this.myOrderLists = myOrderLists;
        method = new Method(activity, onClick);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.home_my_order_adapter, parent, false);
        return new HomeMyOrder.ViewHolder(view);
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

        holder.cardView.setOnClickListener(v -> method.click(position, type, myOrderLists.get(position).getProduct_title(), myOrderLists.get(position).getOrder_unique_id(),
                myOrderLists.get(position).getProduct_id(), myOrderLists.get(position).getOrder_id(), ""));
    }

    @Override
    public int getItemCount() {
        return myOrderLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private ImageView imageView;
        private MaterialCardView cardView;
        private MaterialTextView textViewTitle, textViewStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView_home_myOrder_adapter);
            imageView = itemView.findViewById(R.id.imageView_home_myOrder_adapter);
            view = itemView.findViewById(R.id.view_home_myOrder_adapter);
            textViewTitle = itemView.findViewById(R.id.textView_title_home_myOrder_adapter);
            textViewStatus = itemView.findViewById(R.id.textView_status_home_myOrder_adapter);

        }
    }
}
