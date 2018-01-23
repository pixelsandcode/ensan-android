package ir.app.ensan.model.network;

import ir.app.ensan.model.network.callback.AddGuardianCallback;
import ir.app.ensan.model.network.callback.AppCallback;
import ir.app.ensan.model.network.callback.LoginCallback;
import ir.app.ensan.model.network.callback.RegisterCallback;
import ir.app.ensan.model.network.callback.VerifyCallback;
import ir.app.ensan.model.network.request.NotifyRequest;
import ir.app.ensan.model.network.response.AddDeviceResponse;
import ir.app.ensan.model.network.response.AddGuardianResponse;
import ir.app.ensan.model.network.response.GuardianListResponse;
import ir.app.ensan.model.network.response.LoginResponse;
import ir.app.ensan.model.network.response.NotifyResponse;
import ir.app.ensan.model.network.response.SignUpResponse;
import ir.app.ensan.model.network.response.VerificationResponse;
import ir.app.ensan.model.network.service.GuardianService;
import ir.app.ensan.model.network.service.UserService;
import ir.app.ensan.util.SharedPreferencesUtil;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Khashayar on 21/09/2017.
 */

public class NetworkRequestManager {

  private static NetworkRequestManager instance;

  private UserService userService;
  private GuardianService guardianService;

  public static final String AUTHORIZATION_KEY = "authorization_key";
  public static final String AUTH_KEY = "auth_key";

  private String authorization = "";
  private String auth = "";

  private NetworkRequestManager() {
    authorization = SharedPreferencesUtil.loadString(AUTHORIZATION_KEY, "");
    auth = SharedPreferencesUtil.loadString(AUTH_KEY, "");
    userService = ServiceGenerator.getInstance().createService(UserService.class);
    guardianService = ServiceGenerator.getInstance().createService(GuardianService.class);
  }

  public static NetworkRequestManager getInstance() {

    if (instance == null) {
      instance = new NetworkRequestManager();
    }

    return instance;
  }

  public void callSignUp(String name, String mobile, RegisterCallback callback) {
    Call<SignUpResponse> signUpCallback = userService.signUp(name, mobile);
    signUpCallback.enqueue(callback);
  }

  public void callLogin(String mobile, LoginCallback callback) {
    Call<LoginResponse> loginCall;

    if (auth.isEmpty()){
      loginCall = userService.login(mobile);
    } else {
      loginCall = userService.login(auth, mobile);
    }
    loginCall.enqueue(callback);
  }

  public void callVerify(String pin, String mobile, VerifyCallback callback) {
    Call<VerificationResponse> verifyCall = userService.verify(pin, mobile);
    verifyCall.enqueue(callback);
  }

  public void callAddGuardian(String name, String mobile, AddGuardianCallback callback) {
    Call<AddGuardianResponse> addGuardianCall = guardianService.addGuardian(authorization, name, mobile);
    addGuardianCall.enqueue(callback);
  }

  public void callNotifyGuardian(NotifyRequest notifyRequest, AppCallback callback) {
    Call<NotifyResponse> notifyGuardiansCall =
        guardianService.notifyGuardians(authorization, notifyRequest);
    notifyGuardiansCall.enqueue(callback);
  }

  public void callGuardianList(AppCallback callback) {
    Call<GuardianListResponse> guardianListCall = guardianService.getGuardianList(authorization);
    guardianListCall.enqueue(callback);
  }

  public void callAddDevice(String token, AppCallback callback) {
    Call<AddDeviceResponse> guardianListCall = userService.addDevice(authorization,token);
    guardianListCall.enqueue(callback);
  }

  public void saveAuthorizationKey(Response response) {
    String authorization = response.headers().get("authorization");

    if (authorization == null || authorization.isEmpty()){
      return;
    }

    this.authorization = authorization;
    SharedPreferencesUtil.saveString(AUTHORIZATION_KEY, authorization);
  }

  public void saveAuthKey(String authKey) {

    if (authKey == null || authKey.isEmpty()){
      return;
    }

    this.auth = authKey;
    SharedPreferencesUtil.saveString(AUTH_KEY, authKey);
  }
}
