package hieutm.dev.snakesneaker.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import hieutm.dev.snakesneaker.activity.ShowImage;
import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.util.Method;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class SizeChart extends BottomSheetDialogFragment {

    private Method method;
    private ImageView imageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.size_chart, container, false);

        method = new Method(getActivity());
        if (method.isRtl()) {
            view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        Bundle bundle = getArguments();
        assert bundle != null;
        String url = bundle.getString("url");

        imageView = view.findViewById(R.id.imageView_size_chart);
        LinearLayout llClose = view.findViewById(R.id.ll_close_size_chart);

        Glide.with(getActivity().getApplicationContext()).load(url).placeholder(R.drawable.placeholder_rectangle)
                .into(imageView);

        imageView.setOnClickListener(v -> startActivity(new Intent(getActivity(), ShowImage.class)
                .putExtra("path", url)));

        llClose.setOnClickListener(v -> dismiss());

        return view;
    }

}
