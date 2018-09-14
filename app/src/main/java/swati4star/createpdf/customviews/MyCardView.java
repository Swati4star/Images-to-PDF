package swati4star.createpdf.customviews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import swati4star.createpdf.R;

public class MyCardView extends LinearLayout {

    ImageView icon;
    TextView text;

    public MyCardView(@NonNull Context context) {
        super(context);
        init();
    }

    public MyCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.item_view_enhancement_option, this);
        /*this.header = (TextView)findViewById(R.id.header);
        this.description = (TextView)findViewById(R.id.description);
        this.thumbnail = (ImageView)findViewById(R.id.thumbnail);
        this.icon = (ImageView)findViewById(R.id.icon);*/
        this.text = findViewById(R.id.option_name);
        this.icon = findViewById(R.id.option_image);


        this.text.setText(R.string.merge_file_select);
        this.icon.setImageResource(R.drawable.baseline_crop_rotate_24);
    }
}
