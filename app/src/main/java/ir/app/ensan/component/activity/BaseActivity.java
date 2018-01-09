package ir.app.ensan.component.activity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import com.afollestad.materialdialogs.MaterialDialog;
import ir.app.ensan.R;
import ir.app.ensan.common.FirebaseAnalyticManager;
import ir.app.ensan.component.abstraction.AbstractActivity;

/**
 * Created by Khashayar on 21/09/2017.
 */

public abstract class BaseActivity extends AppCompatActivity implements AbstractActivity {

  protected MaterialDialog progressDialog;

  @Override public void setContentView(@LayoutRes int layoutResID) {
    super.setContentView(layoutResID);
    registerWidgets();
    setListeners();
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    getExtra();

    progressDialog = new MaterialDialog.Builder(this).content(R.string.please_wait)
        .cancelable(false)
        .progress(true, 0)
        .build();

    FirebaseAnalyticManager.getInstance(this).sendActivityViewEvent(this);
  }

  @Override protected void onResume() {
    super.onResume();
  }

  public void showProgressDialog() {
    progressDialog.show();
  }

  public void dismissProgressDialog() {
    progressDialog.dismiss();
  }

  @Override public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
    super.onPostCreate(savedInstanceState, persistentState);
  }

  @Override public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
  }

  @Override public void onBackPressed() {
    super.onBackPressed();
  }

  @Override protected void onRestart() {
    super.onRestart();
  }

  @Override public void registerWidgets() {

  }

  @Override public void setListeners() {

  }

  @Override public void initBroadcastReceiver() {

  }

  @Override public void setMenuListeners() {

  }

  @Override public void getExtra() {

  }

  @Override public void setAdapter() {

  }

  @Override public void setRecycleAdapter() {

  }

  @Override public void initRecycleView() {

  }

  @Override public void notifyRecycleAdapter() {

  }

  @Override public void onInternetConnected() {

  }

  @Override public void setEmptyView() {

  }

  @Override public void progressStart() {
    //progressDialog = new ProgressDialog(this);
    //progressDialog.show();
  }

  @Override public void progressFinish() {
    progressDialog.dismiss();
  }
}
