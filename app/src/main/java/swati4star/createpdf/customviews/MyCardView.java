package swati4star.createpdf.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
        init(attrs);
    }

    public MyCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init() {
        inflate(getContext(), R.layout.item_view_enhancement_option, this);
        this.text = findViewById(R.id.option_name);
        this.icon = findViewById(R.id.option_image);
    }

    private void init(AttributeSet attrs) {
        inflate(getContext(), R.layout.item_view_enhancement_option, this);

        TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.MyCardView);

        this.text = findViewById(R.id.option_name);
        this.icon = findViewById(R.id.option_image);

        this.text.setText(a.getString(R.styleable.MyCardView_option_text));
        Drawable drawable = a.getDrawable(R.styleable.MyCardView_option_icon);
        this.icon.setImageDrawable(drawable);

        a.recycle();
    }
}
