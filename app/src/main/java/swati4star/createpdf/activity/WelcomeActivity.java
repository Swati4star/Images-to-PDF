package swati4star.createpdf.activity;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swati4star.createpdf.R;
import swati4star.createpdf.util.ThemeUtils;

public class WelcomeActivity extends AppCompatActivity {

    @BindView(R.id.view_pager)
    public ViewPager mViewPager;
    @BindView(R.id.layoutDots)
    public LinearLayout mDotsLayout;
    @BindView(R.id.btn_skip)
    public Button mBtnSkip;

    private int[] mLayouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.getInstance().setThemeApp(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        /**
         * layouts of all welcome sliders
         * add few more layouts if you want
         */
        mLayouts = new int[]{
                R.layout.fragment_step_create_pdf,
                R.layout.fragment_step_view_file,
                R.layout.fragment_step_merge_pdf,
                R.layout.fragment_step_text_to_pdf,
                R.layout.fragment_step_qrcode_to_pdf,
                R.layout.fragment_step_remove_pages,
                R.layout.fragment_step_reorder_pages,
                R.layout.fragment_step_extract_images,
                R.layout.fragment_step_excel_to_pdf,
                R.layout.fragment_step_change_themes};

        /**
         * adding bottom dots
         */
        addBottomDots(0);

        MyViewPagerAdapter adapter = new MyViewPagerAdapter();
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(mViewPagerPageChangeListener);
        mViewPager.setOffscreenPageLimit(3);
    }

    @OnClick(R.id.btn_skip)
    public void openMainActivity() {
        finish();
    }

    /**
     * Add bottom dots & highlight the given one
     * @param currentPage - current page to highlight
     */
    private void addBottomDots(int currentPage) {
        TextView[] mDots = new TextView[mLayouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        mDotsLayout.removeAllViews();
        for (int i = 0; i < mDots.length; i++) {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(colorsInactive[currentPage]);
            mDotsLayout.addView(mDots[i]);
        }

        if (mDots.length > 0)
            mDots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    /**
     * viewpager change listener
     */
    private final ViewPager.OnPageChangeListener mViewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    };


    /**
     * View pager adapter
     */
    class MyViewPagerAdapter extends PagerAdapter {

        MyViewPagerAdapter() {
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(mLayouts[position], container, false);
            if (position == 9) {
                Button btnGetStarted = view.findViewById(R.id.getStarted);
                btnGetStarted.setOnClickListener(v -> openMainActivity());
            }
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return mLayouts.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
