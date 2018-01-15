package ir.app.ensan.component.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import ir.app.ensan.R;
import ir.app.ensan.common.FirebaseAnalyticManager;
import ir.app.ensan.component.abstraction.AbstractFragment;

/**
 * Created by Khashayar on 21/09/2017.
 */

public abstract class BaseFragment extends Fragment implements AbstractFragment {

  protected View mainView;
  protected MaterialDialog progressDialog;

  public BaseFragment() {
  }

  @Override public void onAttach(Context context) {
    super.onAttach(context);
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return super.onCreateView(inflater, container, savedInstanceState);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mainView = view;
    FirebaseAnalyticManager.getInstance(getActivity()).sendFragmentViewEvent(this);
    registerWidgets();
    getExtra();
    setListeners();

    progressDialog = new MaterialDialog.Builder(getActivity()).content(R.string.please_wait)
        .cancelable(false)
        .progress(true, 0)
        .theme(Theme.LIGHT)
        .build();
  }

  public void showProgressDialog() {
    progressDialog.show();
  }

  public void dismissProgressDialog() {
    progressDialog.dismiss();
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
  }

  @Override public void registerWidgets() {

  }

  @Override public void setListeners() {

  }

  @Override public void getExtra() {

  }

  @Override public void setAdapter() {

  }

  @Override public void notifyAdapter() {

  }

  @Override public void setRecycleAdapter() {

  }

  @Override public void initRecycleView() {

  }

  @Override public void notifyRecycleAdapter() {

  }
}
