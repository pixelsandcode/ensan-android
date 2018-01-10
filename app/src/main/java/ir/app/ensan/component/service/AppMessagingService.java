package ir.app.ensan.component.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import ir.app.ensan.common.FireBaseManager;
import ir.app.ensan.common.NotificationManager;
import ir.app.ensan.component.receiver.FirebaseDataReceiver;

public class AppMessagingService extends IntentService {

  public static final String NOTIFICATION_DATA = "notification_data";

  public AppMessagingService() {
    super("AppMessagingService");
  }

  @Override protected void onHandleIntent(Intent intent) {

    FireBaseManager.getInstance().init();
    Bundle notificationData = intent.getBundleExtra(NOTIFICATION_DATA);

    //LogUtil.logI("type", notificationData.getString("type"));
    //LogUtil.logI("location",notificationData.getString("location"));
    //LogUtil.logI("userKey", notificationData.getString("userKey"));
    //LogUtil.logI("name", notificationData.getString("name"));
    //LogUtil.logI("mobile", notificationData.getString("mobile"));
    //LogUtil.logI("at", notificationData.getString("at"));

    String type = notificationData.getString("type");

    if (type == null) {
      return;
    }

    switch (type) {
      case "healthy":
        NotificationManager.getInstance()
            .fireSafeNotification(notificationData.getString("name"),
                notificationData.getString("at"));
        return;
      case "inDanger":
        NotificationManager.getInstance()
            .fireWarningNotification(notificationData.getString("name"),
                notificationData.getString("at"), notificationData.getString("mobile"));
        break;

      case "notifyInviter":
        NotificationManager.getInstance()
            .firePendingGuardianNotification(notificationData.getString("pending"));
        break;
      default:
        break;
    }

    FirebaseDataReceiver.completeWakefulIntent(intent);
  }
}
