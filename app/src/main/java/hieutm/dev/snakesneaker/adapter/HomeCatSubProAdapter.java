package hieutm.dev.snakesneaker.adapter;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.fragment.ProductFragment;
import hieutm.dev.snakesneaker.interfaces.OnClick;
import hieutm.dev.snakesneaker.item.HomeCatSubProList;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class HomeCatSubProAdapter extends RecyclerView.Adapter<HomeCatSubProAdapter.ViewHolder> {

    private Activity activity;
    private String type;
    private OnClick onClick;
    private List<HomeCatSubProList> homeCatSubProLists;

    public HomeCatSubProAdapter(Activity activity, List<HomeCatSubProList> homeCatSubProLists, String type, OnClick onClick) {
        this.activity = activity;
        this.homeCatSubProLists = homeCatSubProLists;
        this.type = type;
        this.onClick = onClick;
    }

    @NonNull
    @Override
    public HomeCatSubProAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.home_catsub_pro_adapter, parent, false);
        return new HomeCatSubProAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeCatSubProAdapter.ViewHolder holder, int position) {

        if (homeCatSubProLists.get(position).getProductLists().size() != 0) {

            holder.linearLayout.setVisibility(View.VISIBLE);

            holder.textViewTitle.setText(homeCatSubProLists.get(position).getTitle());

            holder.recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
            holder.recyclerView.setLayoutManager(layoutManager);
            holder.recyclerView.setFocusable(false);

            HomeProductAdapter homeProductAdapter = new HomeProductAdapter(activity, homeCatSubProLists.get(position).getProductLists(), type, onClick);
            holder.recyclerView.setAdapter(homeProductAdapter);

            holder.textViewViewAll.setOnClickListener(v -> {

                String title = homeCatSubProLists.get(position).getTitle();

                ProductFragment productFragment = new ProductFragment();
                Bundle bundle = new Bundle();
                bundle.putString("type", type);
                bundle.putString("title", title);
                bundle.putString("cat_id", homeCatSubProLists.get(position).getId());
                bundle.putString("sub_cat_id", "0");
                bundle.putString("search", "");
                productFragment.setArguments(bundle);
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager().beginTransaction()
                        .add(R.id.frameLayout_main, productFragment, title).addToBackStack(title)
                        .commitAllowingStateLoss();
            });

        } else {
            holder.linearLayout.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return homeCatSubProLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout linearLayout;
        private RecyclerView recyclerView;
        private MaterialTextView textViewTitle, textViewViewAll;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.ll_main_home_catSubPro_adapter);
            textViewTitle = itemView.findViewById(R.id.textView_title_home_catSubPro_adapter);
            textViewViewAll = itemView.findViewById(R.id.textView_viewAll_home_catSubPro_adapter);
            recyclerView = itemView.findViewById(R.id.recyclerView_home_catSubPro_adapter);

        }
    }

}
