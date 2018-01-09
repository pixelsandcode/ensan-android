package ir.app.ensan.component.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import ir.app.ensan.BuildConfig;
import ir.app.ensan.R;
import ir.app.ensan.common.ContactManager;
import ir.app.ensan.common.NotificationManager;
import ir.app.ensan.util.SharedPreferencesUtil;
import ir.app.ensan.util.TimeUtil;

public class SplashActivity extends BaseActivity {

  public static final String FIRST_TIME_KEY = "first_time";
  private Handler handler;
  private boolean firstTime;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);

    firstTime = SharedPreferencesUtil.loadBoolean(FIRST_TIME_KEY, true);
    SharedPreferencesUtil.saveBoolean(FIRST_TIME_KEY, false);

    scheduleNotifications();
    handler = new Handler();
    handler.postDelayed(runnable, 2000);
  }

  private void openAddGuardianActivity() {
    Intent intent = new Intent(this, AddGuardianActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
  }

  private void openHomeActivity() {
    Intent intent = new Intent(this, HomeActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
  }

  private void openIntroductionActivity() {
    Intent intent = new Intent(this, IntroductionActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
  }

  private void scheduleNotifications(){

    if (!firstTime){
      return;
    }

    NotificationManager.getInstance().scheduleNotification(BuildConfig.STG?
        TimeUtil.getCurrentDate()+10000 : TimeUtil.addDayFromNow(1));// first day
    NotificationManager.getInstance().scheduleNotification(TimeUtil.addDayFromNow(3));// 3 day later
    NotificationManager.getInstance().scheduleNotification(TimeUtil.addDayFromNow(7));// 7 day later
  }

  private Runnable runnable = new Runnable() {
    @Override public void run() {
      if (firstTime) {
        openIntroductionActivity();
      } else {
        if (ContactManager.getInstance(SplashActivity.this).isContactExist()) {
          openHomeActivity();
        } else {
          openAddGuardianActivity();
        }
      }
    }
  };
}
