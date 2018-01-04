package ir.app.ensan.component.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import ir.app.ensan.R;
import ir.app.ensan.component.adapter.ViewPagerAdapter;
import ir.app.ensan.component.fragment.IntroductionFragment;
import ir.app.ensan.component.view.CustomButton;
import ir.app.ensan.component.view.CustomTextView;

public class IntroductionActivity extends BaseActivity {

  private ViewPager viewPager;
  private ViewPagerAdapter viewPagerAdapter;

  private CustomTextView pervTextView;
  private CustomTextView nextTextView;
  private CustomButton enterToApp;

  private String[] mainTitles;
  private String[] subTitles;

  private int viewpagerIndex;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_introduction);

    mainTitles = getResources().getStringArray(R.array.main_titles);
    subTitles = getResources().getStringArray(R.array.sub_titles);
    setViewPager();
  }

  @Override public void registerWidgets() {
    super.registerWidgets();
    viewPager = (ViewPager) findViewById(R.id.splash_view_pager);
    pervTextView = (CustomTextView) findViewById(R.id.splash_perv_text_view);
    nextTextView = (CustomTextView) findViewById(R.id.splash_next_text_view);
    enterToApp = (CustomButton) findViewById(R.id.enter_to_app);
  }

  @Override public void setListeners() {
    super.setListeners();

    pervTextView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (viewpagerIndex < 3) {
          viewpagerIndex++;
        }
        viewPager.setCurrentItem(viewpagerIndex);
      }
    });

    enterToApp.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        openAddGuardianActivity();
      }
    });

    nextTextView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (viewpagerIndex > 0) {
          viewpagerIndex--;
          viewPager.setCurrentItem(viewpagerIndex);
        } else {
          openAddGuardianActivity();
        }
      }
    });

    viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

      }

      @Override public void onPageSelected(int position) {
          pervTextView.setVisibility(position < 2 ? View.VISIBLE : View.GONE);
          nextTextView.setVisibility(position >= 0 ? View.VISIBLE : View.GONE);
      }

      @Override public void onPageScrollStateChanged(int state) {

      }
    });
  }

  private void openAddGuardianActivity() {
    Intent intent = new Intent(this, AddGuardianActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
  }

  private void setViewPager() {

    viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

    for (int i = 2; i >= 0; i--) {
      IntroductionFragment introductionFragment = new IntroductionFragment();
      introductionFragment.setMainTitle(mainTitles[i]);
      introductionFragment.setSubTitle(subTitles[i]);
      viewPagerAdapter.addFragment(introductionFragment);
    }

    viewPager.setAdapter(viewPagerAdapter);
    viewPager.setCurrentItem(2);
    viewpagerIndex = 2;
  }
}
