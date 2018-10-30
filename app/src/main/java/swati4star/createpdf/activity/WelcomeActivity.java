package swati4star.createpdf.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
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

import static swati4star.createpdf.util.Constants.IS_WELCOME_ACTIVITY_SHOWN;
import static swati4star.createpdf.util.Constants.SHOW_WELCOME_ACT;
import static swati4star.createpdf.util.StringUtils.showSnackbar;

public class WelcomeActivity extends AppCompatActivity {

    @BindView(R.id.view_pager)
    public ViewPager mViewPager;
    @BindView(R.id.layoutDots)
    public LinearLayout mDotsLayout;
    @BindView(R.id.btn_skip)
    public Button mBtnSkip;

    private int[] mLayouts;
    private long mBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (mSharedPreferences.getBoolean(IS_WELCOME_ACTIVITY_SHOWN, false) &&
                !(getIntent() != null && getIntent().getBooleanExtra(SHOW_WELCOME_ACT, false)))
                openMainActivity();
        mSharedPreferences.edit().putBoolean(IS_WELCOME_ACTIVITY_SHOWN, true).apply();

        // layouts of all welcome sliders
        // add few more layouts if you want
        mLayouts = new int[]{
                R.layout.fragment_step_create_pdf,
                R.layout.fragment_step_view_file,
                R.layout.fragment_step_merge_pdf,
                R.layout.fragment_step_change_themes};

        // adding bottom dots
        addBottomDots(0);

        MyViewPagerAdapter adapter = new MyViewPagerAdapter();
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        mViewPager.setOffscreenPageLimit(3);
    }

    @OnClick(R.id.btn_skip)
    public void openMainActivity() {
        if (!(getIntent() != null && getIntent().getBooleanExtra(SHOW_WELCOME_ACT, false))) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
        finish();
    }

    /**
     * Add bottom dots & highligt the given one
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

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

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
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater mLayoutInflater;

        MyViewPagerAdapter() {
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            mLayoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = mLayoutInflater.inflate(mLayouts[position], container, false);
            if (position == 3) {
                Button btnGetStarted = view.findViewById(R.id.getStarted);
                btnGetStarted.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openMainActivity();
                    }
                });
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

    // Double tap to exit
    @Override
    public void onBackPressed() {
        int mTimeDelay = 2000;
        if (mBackPressed + mTimeDelay > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            showSnackbar(WelcomeActivity.this, R.string.confirm_exit_message);
        }
        mBackPressed = System.currentTimeMillis();
    }
}
