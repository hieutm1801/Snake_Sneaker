package hieutm.dev.snakesneaker.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import hieutm.dev.snakesneaker.interfaces.OnClick;
import hieutm.dev.snakesneaker.item.OfferList;
import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.util.Method;

import java.util.List;

public class HomeOfferAdapter extends RecyclerView.Adapter<HomeOfferAdapter.ViewHolder> {

    private Method method;
    private String type;
    private Activity activity;
    private List<OfferList> offerLists;

    public HomeOfferAdapter(Activity activity, List<OfferList> offerLists, String type, OnClick onClick) {
        this.activity = activity;
        this.type = type;
        this.offerLists = offerLists;
        method = new Method(activity, onClick);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.home_coupons_adapter, parent, false);

        return new HomeOfferAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Glide.with(activity).load(offerLists.get(position).getOffer_image_thumb())
                .placeholder(R.drawable.placeholder_rectangle)
                .into(holder.imageView);

        holder.imageView.setOnClickListener(v -> method.click(position, type, offerLists.get(position).getOffer_title(), offerLists.get(position).getId(), "", "",""));

    }

    @Override
    public int getItemCount() {
        return offerLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView_home_coupons_adapter);

        }
    }
}
