package ir.app.ensan.component.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ir.app.ensan.R;
import ir.app.ensan.component.view.CustomTextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class IntroductionFragment extends BaseFragment {

  private CustomTextView mainTitleTextView;
  private CustomTextView subTitleTextView;

  private String mainTitle;
  private String subTitle;

  public IntroductionFragment() {
    // Required empty public constructor
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_intoduction, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mainTitleTextView.setText(mainTitle);
    subTitleTextView.setText(subTitle);
  }

  @Override public void registerWidgets() {
    super.registerWidgets();
    mainTitleTextView = (CustomTextView) mainView.findViewById(R.id.splash_main_title);
    subTitleTextView = (CustomTextView) mainView.findViewById(R.id.splash_sub_title);
  }

  public void setMainTitle(String mainTitle) {
    this.mainTitle = mainTitle;
  }

  public void setSubTitle(String subTitle) {
    this.subTitle = subTitle;
  }
}
