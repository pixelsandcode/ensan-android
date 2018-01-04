package ir.app.ensan.component.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import ir.app.ensan.common.NotificationManager;
import ir.app.ensan.util.LogUtil;

public class NotificationReceiver extends BroadcastReceiver {

  public static String NOTIFICATION_ID = "notification_id";

  public NotificationReceiver() {
  }

  @Override public void onReceive(Context context, Intent intent) {

    LogUtil.logE("notification called", "yes");
    NotificationManager.getInstance().fireNoGuardianNotification(0);
  }
}
