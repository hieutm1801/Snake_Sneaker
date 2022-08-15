package hieutm.dev.snakesneaker.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.item.FaqList;
import com.github.florent37.expansionpanel.ExpansionLayout;
import com.github.florent37.expansionpanel.viewgroup.ExpansionLayoutCollection;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.ViewHolder> {

    private Activity activity;
    private List<FaqList> faqLists;

    private final ExpansionLayoutCollection expansionsCollection = new ExpansionLayoutCollection();

    public FaqAdapter(Activity activity, List<FaqList> faqLists) {
        this.activity = activity;
        this.faqLists = faqLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.faq_adapter, parent, false);

        return new FaqAdapter.ViewHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.expansionLayout.collapse(false);
        expansionsCollection.add(holder.expansionLayout);

        holder.expansionLayout.addIndicatorListener((expansionLayout, willExpand) -> {
            if (willExpand) {
                holder.rel.setBackground(activity.getResources().getDrawable(R.drawable.faq_list_view_bg));
            } else {
                holder.rel.setBackground(activity.getResources().getDrawable(R.drawable.faq_list_bg));
            }
        });

        holder.textViewTitle.setText(faqLists.get(position).getQuestion());
        holder.textViewDetail.setText(faqLists.get(position).getAnswer());

    }

    @Override
    public int getItemCount() {
        return faqLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout rel;
        private MaterialTextView textViewTitle;
        private MaterialTextView textViewDetail;
        private ExpansionLayout expansionLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            rel = itemView.findViewById(R.id.rel_faq_adapter);
            textViewTitle = itemView.findViewById(R.id.textView_title_faq_adapter);
            textViewDetail = itemView.findViewById(R.id.textView_detail_faq_adapter);
            expansionLayout = itemView.findViewById(R.id.expansionLayout_faq_adapter);

        }

    }
}
