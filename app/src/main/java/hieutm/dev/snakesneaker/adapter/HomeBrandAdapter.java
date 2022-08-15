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
import hieutm.dev.snakesneaker.item.BrandList;
import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.util.Method;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class HomeBrandAdapter extends RecyclerView.Adapter<HomeBrandAdapter.ViewHolder> {

    private Activity activity;
    private Method method;
    private String type;
    private List<BrandList> brandLists;

    public HomeBrandAdapter(Activity activity, List<BrandList> brandLists, String type, OnClick onClick) {
        this.activity = activity;
        this.type = type;
        this.brandLists = brandLists;
        method = new Method(activity, onClick);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.home_category_adapter, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.textView.setText(brandLists.get(position).getBrand_name());
        Glide.with(activity).load(brandLists.get(position).getBrand_image_thumb())
                .placeholder(R.drawable.placeholder_cat)
                .into(holder.imageView);

        holder.materialCardView.setOnClickListener(view -> method.click(position, type, brandLists.get(position).getBrand_name(), brandLists.get(position).getId(),"","",""));

    }

    @Override
    public int getItemCount() {
        return brandLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private MaterialTextView textView;
        private ImageView imageView;
        private MaterialCardView materialCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.textView_name_home_cat_adapter);
            imageView = itemView.findViewById(R.id.imageView_home_cat_adapter);
            materialCardView = itemView.findViewById(R.id.cardView_home_cat_adapter);

        }
    }
}
