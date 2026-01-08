package swati4star.createpdf.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import swati4star.createpdf.R;
import swati4star.createpdf.databinding.ActivityWelcomeBinding;
import swati4star.createpdf.util.ThemeUtils;

public class WelcomeActivity extends AppCompatActivity {

    private int[] mLayouts;
    private ActivityWelcomeBinding mBinding;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.getInstance().setThemeApp(this);
        super.onCreate(savedInstanceState);

        mBinding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setContentView(view);

        /***
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
        mBinding.viewPager.setAdapter(adapter);
        mBinding.viewPager.addOnPageChangeListener(mViewPagerPageChangeListener);
        mBinding.viewPager.setOffscreenPageLimit(3);

        mBinding.btnSkip.setOnClickListener(v -> {
            finish();
        });
    }

    /**
     * Add bottom dots & highlight the given one
     *
     * @param currentPage - current page to highlight
     */
    private void addBottomDots(int currentPage) {
        TextView[] mDots = new TextView[mLayouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        mBinding.layoutDots.removeAllViews();
        for (int i = 0; i < mDots.length; i++) {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(colorsInactive[currentPage]);
            mBinding.layoutDots.addView(mDots[i]);
        }

        if (mDots.length > 0)
            mDots[currentPage].setTextColor(colorsActive[currentPage]);
    }

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
                btnGetStarted.setOnClickListener(v -> finish());
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
