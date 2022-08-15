package hieutm.dev.snakesneaker.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.item.ProImageList;
import hieutm.dev.snakesneaker.util.TouchImageView;

import java.util.List;

public class ProViewDetailAdapter extends PagerAdapter {

    private Activity activity;
    private List<ProImageList> proImageLists;

    public ProViewDetailAdapter(Activity activity, List<ProImageList> proImageLists) {
        this.activity = activity;
        this.proImageLists = proImageLists;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {

        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.pro_view_detail_adapter, container, false);

        TouchImageView imageView = view.findViewById(R.id.imageView_pro_view_detail_adapter);

        Glide.with(activity).load(proImageLists.get(position).getProduct_image_thumb())
                .placeholder(R.drawable.placeholder_rectangle).into(imageView);

        container.addView(view);
        return view;

    }


    @Override
    public int getCount() {
        return proImageLists.size();
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
