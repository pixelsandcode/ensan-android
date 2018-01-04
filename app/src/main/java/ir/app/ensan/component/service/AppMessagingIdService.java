package ir.app.ensan.component.service;

import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import ir.app.ensan.common.FireBaseManager;
import ir.app.ensan.util.SharedPreferencesUtil;

import static com.google.android.gms.internal.zzt.TAG;

public class AppMessagingIdService extends FirebaseInstanceIdService {

  public static final String NOTIFICATION_TOKEN_KEY = "notification_token";
  public static final String NOTIFICATION_TOKEN_CHANGE_KEY = "notification_token_change";

  public AppMessagingIdService() {
  }

  @Override public void onTokenRefresh() {
    // Get updated InstanceID token.
    String refreshedToken = FirebaseInstanceId.getInstance().getToken();
    Log.d(TAG, "Refreshed token: " + refreshedToken);
    String currentToken = SharedPreferencesUtil.loadString(NOTIFICATION_TOKEN_KEY,"");

    if (!refreshedToken.equals(currentToken)){
      SharedPreferencesUtil.saveString(NOTIFICATION_TOKEN_KEY, refreshedToken);
      SharedPreferencesUtil.saveBoolean(NOTIFICATION_TOKEN_CHANGE_KEY, true);
    }

    // If you want to send messages to this application instance or
    // manage this apps subscriptions on the server side, send the
    // Instance ID token to your app server.
    FireBaseManager.getInstance().sendMessagingToken(refreshedToken);
  }
}
