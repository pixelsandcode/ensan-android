package ir.app.ensan.component.receiver;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import ir.app.ensan.component.service.AppMessagingService;

public class FirebaseDataReceiver extends WakefulBroadcastReceiver {

  public FirebaseDataReceiver() {
  }

  @Override public void onReceive(Context context, Intent intent) {

    Intent messagingIntent = new Intent(context, AppMessagingService.class);
    messagingIntent.putExtra(AppMessagingService.NOTIFICATION_DATA,intent.getExtras());
    startWakefulService(context, messagingIntent);
  }
}
