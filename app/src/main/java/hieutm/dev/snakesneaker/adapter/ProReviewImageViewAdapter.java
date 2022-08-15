package hieutm.dev.snakesneaker.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import hieutm.dev.snakesneaker.item.ReviewProImageList;
import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.util.TouchImageView;

import java.util.List;

public class ProReviewImageViewAdapter extends PagerAdapter {

    private Activity activity;
    private List<ReviewProImageList> reviewProImageLists;

    public ProReviewImageViewAdapter(Activity activity, List<ReviewProImageList> reviewProImageLists) {
        this.activity = activity;
        this.reviewProImageLists = reviewProImageLists;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {

        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.view_pro_review_image_adapter, container, false);

        TouchImageView imageView = view.findViewById(R.id.imageView_vpr_image_adapter);

        Glide.with(activity).load(reviewProImageLists.get(position).getImage())
                .placeholder(R.drawable.placeholder_rectangle).into(imageView);

        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return reviewProImageLists.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
        return view == obj;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

}
