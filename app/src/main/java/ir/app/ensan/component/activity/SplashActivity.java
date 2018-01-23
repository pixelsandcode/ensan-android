package ir.app.ensan.component.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import ir.app.ensan.BuildConfig;
import ir.app.ensan.R;
import ir.app.ensan.common.ContactManager;
import ir.app.ensan.common.NotificationManager;
import ir.app.ensan.component.fragment.AddUserFragment;
import ir.app.ensan.model.network.NetworkRequestManager;
import ir.app.ensan.model.network.callback.LoginCallback;
import ir.app.ensan.model.network.response.LoginResponse;
import ir.app.ensan.util.SharedPreferencesUtil;
import ir.app.ensan.util.SnackUtil;
import ir.app.ensan.util.TimeUtil;
import retrofit2.Call;
import retrofit2.Response;

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
  }

  @Override protected void onResume() {
    super.onResume();
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

  private void scheduleNotifications() {

    if (!firstTime) {
      return;
    }

    NotificationManager.getInstance()
        .scheduleNotification(BuildConfig.STG ? TimeUtil.getCurrentDate() + 10000
            : TimeUtil.addDayFromNow(1));// first day
    NotificationManager.getInstance().scheduleNotification(TimeUtil.addDayFromNow(3));// 3 day later
    NotificationManager.getInstance().scheduleNotification(TimeUtil.addDayFromNow(7));// 7 day later
  }

  private Runnable runnable = new Runnable() {
    @Override public void run() {

      String phoneNumber = SharedPreferencesUtil.loadString(AddUserFragment.PHONE_NUMBER_KEY, "");
      if (firstTime || phoneNumber.isEmpty()) {
        openIntroductionActivity();
      } else {
        loginUser(phoneNumber);
      }
    }
  };

  public void loginUser(String phoneNumber) {
    NetworkRequestManager.getInstance().callLogin(phoneNumber, new LoginCallback() {
      @Override public void onRequestSuccess(Call call, Response response) {

        dismissProgressDialog();
        LoginResponse loginResponse = (LoginResponse) response.body();

        if (loginResponse.getData().getSuccess()) {
          SharedPreferencesUtil.saveString(AddUserFragment.USER_NAME_KEY,
              loginResponse.getData().getName());
          if (ContactManager.getInstance(SplashActivity.this).isContactExist()) {
            openHomeActivity();
          } else {
            openAddGuardianActivity();
          }
        }
      }

      @Override public void onRequestFail(Call call, Response response) {
        dismissProgressDialog();
        SnackUtil.makeNetworkDisconnectSnackBar(SplashActivity.this, getWindow().getDecorView());
        openHomeActivity();
      }

      @Override public void onRequestTimeOut(Call call, Throwable t) {
        dismissProgressDialog();
        SnackUtil.makeNetworkDisconnectSnackBar(SplashActivity.this, getWindow().getDecorView());
        openHomeActivity();
      }

      @Override public void onNullResponse(Call call) {
        dismissProgressDialog();
        SnackUtil.makeNetworkDisconnectSnackBar(SplashActivity.this, getWindow().getDecorView());
        openHomeActivity();
      }
    });
  }
}
