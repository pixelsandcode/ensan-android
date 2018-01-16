package ir.app.ensan.component.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import ir.app.ensan.EnsanApp;
import ir.app.ensan.R;
import ir.app.ensan.common.FirebaseAnalyticManager;
import ir.app.ensan.component.abstraction.AbstractActivity;
import ir.app.ensan.component.service.AppMessagingService;
import ir.app.ensan.util.SnackUtil;

/**
 * Created by Khashayar on 21/09/2017.
 */

public abstract class BaseActivity extends AppCompatActivity implements AbstractActivity {

  protected MaterialDialog progressDialog;
  private BroadcastReceiver appBroadcastReceiver;
  private MaterialDialog generalNotificationDialog;

  @Override public void setContentView(@LayoutRes int layoutResID) {
    super.setContentView(layoutResID);
    registerWidgets();
    setListeners();
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setSoftInputMode(
        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    getExtra();

    initProgressDialog();
    sendFireBaseEvent();
    initGeneralNotificationReceiver();
  }

  private void initGeneralNotificationReceiver() {
    appBroadcastReceiver = new BroadcastReceiver() {
      @Override public void onReceive(Context context, final Intent intent) {

        SnackUtil.makeSnackBar(BaseActivity.this, getWindow().getDecorView(), Snackbar.LENGTH_LONG,
            getString(R.string.new_general_notification), true,
            getString(R.string.general_notification_action), new View.OnClickListener() {

              @Override public void onClick(View view) {

                Bundle bundle = intent.getBundleExtra(AppMessagingService.NOTIFICATION_DATA);
                String title = bundle.getString(AppMessagingService.NOTIFICATION_GENERAL_TITLE);
                String body = bundle.getString(AppMessagingService.NOTIFICATION_GENERAL_BODY);

                if (title == null || body == null) {
                  return;
                }

                generalNotificationDialog =
                    new MaterialDialog.Builder(BaseActivity.this).title(title)
                        .content(body)
                        .positiveText(R.string.ok)
                        .theme(Theme.LIGHT)
                        .build();
                generalNotificationDialog.show();
              }
            });
      }
    };
  }

  private void sendFireBaseEvent() {
    FirebaseAnalyticManager.getInstance(this).sendActivityViewEvent(this);
  }

  private void initProgressDialog() {
    progressDialog = new MaterialDialog.Builder(this).content(R.string.please_wait)
        .cancelable(false)
        .progress(true, 0)
        .theme(Theme.LIGHT)
        .build();
  }

  @Override protected void onResume() {
    super.onResume();
    EnsanApp.setAppInForeground(true);
    registerReceiver(appBroadcastReceiver, new IntentFilter("android.intent.action.MAIN"));
  }

  @Override protected void onPause() {
    super.onPause();
    EnsanApp.setAppInForeground(false);
    unregisterReceiver(this.appBroadcastReceiver);
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

  //@Override public void onConfigurationChanged(Configuration newConfig) {
  //  super.onConfigurationChanged(newConfig);
  //}

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
