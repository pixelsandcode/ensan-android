package ir.app.ensan.component.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import ir.app.ensan.BuildConfig;
import ir.app.ensan.R;
import ir.app.ensan.common.ContactManager;
import ir.app.ensan.common.LocationManager;
import ir.app.ensan.component.abstraction.SmsListener;
import ir.app.ensan.component.fragment.AddUserFragment;
import ir.app.ensan.component.fragment.GuardianListFragment;
import ir.app.ensan.component.fragment.MainFragment;
import ir.app.ensan.component.fragment.SelectContactFragment;
import ir.app.ensan.component.fragment.UserStatusFragment;
import ir.app.ensan.component.service.AppMessagingIdService;
import ir.app.ensan.model.ContactEntity;
import ir.app.ensan.model.network.NetworkRequestManager;
import ir.app.ensan.model.network.callback.AddGuardianCallback;
import ir.app.ensan.model.network.callback.AppCallback;
import ir.app.ensan.model.network.callback.LoginCallback;
import ir.app.ensan.model.network.request.AddGuardianRequest;
import ir.app.ensan.model.network.request.Guardian;
import ir.app.ensan.model.network.request.NotifyRequest;
import ir.app.ensan.model.network.response.LoginResponse;
import ir.app.ensan.model.network.response.NotifyResponse;
import ir.app.ensan.util.NetworkUtil;
import ir.app.ensan.util.SharedPreferencesUtil;
import ir.app.ensan.util.SnackUtil;
import ir.app.ensan.util.TimeUtil;
import java.util.ArrayList;
import java.util.Date;
import retrofit2.Call;
import retrofit2.Response;

public class HomeActivity extends BaseActivity {

  private static final int PERMISSIONS_REQUEST_LOCATION = 1000;
  private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1001;
  private static final int PERMISSIONS_REQUEST_SMS = 1002;
  private static final int PERMISSIONS_REQUEST_SMS_STATUS = 1003;
  private static final int PERMISSIONS_REQUEST_CALL = 1005;

  private static final int SEND_SMS_ACTIVITY_TOKEN = 2018;
  private static final int SEND_SMS_NOTIFICATION_TOKEN = 2019;

  private FragmentManager fragmentManager;
  private FragmentTransaction transaction;
  private ArrayList<ContactEntity> contactEntities;

  private boolean firstTransaction = true;
  private boolean callPermissionDenied = false;

  private SmsListener smsListener;

  private Handler handler;

  private boolean sendingStatusBySms = false;
  private boolean locationPermissionNotGranted = false;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);
    fragmentManager = getSupportFragmentManager();
    handler = new Handler();
    openMainFragment();
    checkLocationPermission();
    sendNotificationToken();
  }

  @Override public void setListeners() {
    super.setListeners();

    smsListener = new SmsListener() {
      @Override public void onSmsSent() {
        //SnackUtil.makeSnackBar(HomeActivity.this, getWindow().getDecorView(), Snackbar.LENGTH_LONG,
        //    getString(R.string.contact_sms_sent), true);
        ContactManager.getInstance(HomeActivity.this).saveContacts();
        onBackPressed();
      }

      @Override public void onSmsNotSent() {
        //SnackUtil.makeSnackBar(HomeActivity.this, getWindow().getDecorView(), Snackbar.LENGTH_LONG,
        //    String.format(getString(R.string.contact_sms_not_sent), contactEntity.getName()), true,
        //    getString(R.string.send_again), new View.OnClickListener() {
        //      @Override public void onClick(View view) {
        //        checkSmsPermission();
        //      }
        //    });
        ContactManager.getInstance(HomeActivity.this).saveContacts();
        onBackPressed();
      }
    };
  }

  private void openMainFragment() {
    MainFragment mainFragment = MainFragment.newInstance();
    setFragment(mainFragment);
  }

  public void openSafeStatusFragment(int notifiedGuardian) {
    UserStatusFragment userStatusFragment = UserStatusFragment.newInstance(true, notifiedGuardian);
    setFragment(userStatusFragment);
  }

  public void openDangerStatusFragment(int notifiedGuardian) {
    UserStatusFragment userStatusFragment = UserStatusFragment.newInstance(false, notifiedGuardian);
    setFragment(userStatusFragment);
  }

  public void openSelectContactFragment() {
    SelectContactFragment selectContactFragment = SelectContactFragment.newInstance();
    setFragment(selectContactFragment);
  }

  public void openGuardianListFragment() {
    GuardianListFragment guardianListFragment = GuardianListFragment.newInstance(true);
    setFragment(guardianListFragment);
  }

  public void sendStatusMessage(boolean safe) {

    if (BuildConfig.STG) {
      return;
    }

    String text =
        String.format(getString(safe ? R.string.safe_description : R.string.in_danger_description),
            SharedPreferencesUtil.loadString(AddUserFragment.USER_NAME_KEY, ""),
            TimeUtil.getFormattedTime(new Date()));

    StringBuilder smsRecipients = new StringBuilder("smsto:");

    for (ContactEntity contactEntity : ContactManager.getInstance(this).getGuardians()) {
      smsRecipients.append(contactEntity.getPhoneNumber()).append(';');
    }

    try {
      Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse(smsRecipients.toString()));
      smsIntent.putExtra("sms_body", text);
      startActivityForResult(smsIntent, SEND_SMS_NOTIFICATION_TOKEN);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public void sendNotify(final boolean safe) {
    showProgressDialog();
    NotifyRequest notifyRequest;
    NotifyRequest.Builder builder = new NotifyRequest.Builder();

    builder.type(safe ? "healthy" : "inDanger");

    if (!locationPermissionNotGranted) {
      Location location = LocationManager.getInstance(this).getLocation();

      if (location != null) {
        builder.lat(location.getLatitude()).lon(location.getLongitude());
      }
    }

    notifyRequest = builder.build();

    NetworkRequestManager.getInstance().callNotifyGuardian(notifyRequest, new AppCallback() {
      @Override public void onRequestSuccess(Call call, Response response) {
        NotifyResponse notifyResponse = (NotifyResponse) response.body();

        dismissProgressDialog();
        if (notifyResponse == null) {
          return;
        }

        if (notifyResponse.getData().getSuccess()) {
          if (safe) {
            openSafeStatusFragment(notifyResponse.getData().getSendTo());
          } else {
            openDangerStatusFragment(notifyResponse.getData().getSendTo());
          }
        }
      }

      @Override public void onTokenExpire(Call call, Response response) {
        loginUser(safe ? Requests.SEND_SAFE_NOTIFICATION : Requests.SEND_WARNING_NOTIFICATION);
      }

      @Override public void onRequestFail(Call call, Response response) {
        dismissProgressDialog();
        SnackUtil.makeNetworkDisconnectSnackBar(HomeActivity.this, getWindow().getDecorView());
      }

      @Override public void onRequestTimeOut(Call call, Throwable t) {
        dismissProgressDialog();
        SnackUtil.makeNetworkDisconnectSnackBar(HomeActivity.this, getWindow().getDecorView());
      }

      @Override public void onNullResponse(Call call) {
        dismissProgressDialog();
        SnackUtil.makeNetworkDisconnectSnackBar(HomeActivity.this, getWindow().getDecorView());
      }
    });
  }

  public void sendNotificationToken() {

    String token =
        SharedPreferencesUtil.loadString(AppMessagingIdService.NOTIFICATION_TOKEN_KEY, "");

    if (token.isEmpty()) {
      return;
    }

    NetworkRequestManager.getInstance().callAddDevice(token, new AppCallback() {
      @Override public void onRequestSuccess(Call call, Response response) {
        // AddDeviceResponse addDeviceResponse = (AddDeviceResponse) response.body();

        //if (!addDeviceResponse.getData().getSuccess()) {
        //  sendNotificationToken();
        //} else {
        //  SharedPreferencesUtil.saveBoolean(
        //      AppMessagingIdService.NOTIFICATION_TOKEN_CHANGE_KEY, false);
        //}
      }

      @Override public void onTokenExpire(Call call, Response response) {
        loginUser(Requests.SEND_NOTIFICATION_TOKEN);
      }

      @Override public void onRequestFail(Call call, Response response) {

      }

      @Override public void onRequestTimeOut(Call call, Throwable t) {

      }

      @Override public void onNullResponse(Call call) {

      }
    });
  }

  public void sendGuardianData(AddGuardianRequest addGuardianRequest) {

    if (!NetworkUtil.isInternetConnected(this,getWindow().getDecorView())){
      return;
    }

    showProgressDialog();
    NetworkRequestManager.getInstance()
        .callAddGuardian(addGuardianRequest, new AddGuardianCallback() {
          @Override public void onRequestSuccess(Call call, Response response) {
            dismissProgressDialog();
            ContactManager.getInstance(HomeActivity.this).addSelectedContacts(contactEntities);
            ContactManager.getInstance(HomeActivity.this).saveContacts();
            sendInvitationMessage(contactEntities, smsListener);
          }

          @Override public void onGuardianAddBefore(Call call, Response response) {
            dismissProgressDialog();
            SnackUtil.makeSnackBar(HomeActivity.this, getWindow().getDecorView(),
                Snackbar.LENGTH_INDEFINITE, getString(R.string.duplicate_guardian), true,
                getString(R.string.understood), new View.OnClickListener() {
                  @Override public void onClick(View view) {
                  }
                });
          }

          @Override public void onSelfGuardianAdded(Call call, Response response) {
            dismissProgressDialog();
            SnackUtil.makeSnackBar(HomeActivity.this, getWindow().getDecorView(),
                Snackbar.LENGTH_LONG, getString(R.string.self_guardian_forbidden), true);
          }

          @Override public void onTokenExpire(Call call, Response response) {
            loginUser(Requests.SEND_GUARDIAN_DATA);
          }

          @Override public void onRequestFail(Call call, Response response) {
            dismissProgressDialog();
            SnackUtil.makeNetworkDisconnectSnackBar(HomeActivity.this, getWindow().getDecorView());
          }

          @Override public void onRequestTimeOut(Call call, Throwable t) {
            dismissProgressDialog();
            SnackUtil.makeNetworkDisconnectSnackBar(HomeActivity.this, getWindow().getDecorView());
          }

          @Override public void onNullResponse(Call call) {
            dismissProgressDialog();
            SnackUtil.makeNetworkDisconnectSnackBar(HomeActivity.this, getWindow().getDecorView());
          }
        });
  }

  public void loginUser(final Requests requests) {

    if (!NetworkUtil.isInternetConnected(this,getWindow().getDecorView())){
      return;
    }

    showProgressDialog();
    NetworkRequestManager.getInstance()
        .callLogin(SharedPreferencesUtil.loadString(AddUserFragment.PHONE_NUMBER_KEY, ""),
            new LoginCallback() {
              @Override public void onRequestSuccess(Call call, Response response) {

                dismissProgressDialog();
                LoginResponse loginResponse = (LoginResponse) response.body();

                if (loginResponse.getData().getSuccess()) {
                  SharedPreferencesUtil.saveString(AddUserFragment.USER_NAME_KEY,
                      loginResponse.getData().getName());
                  switch (requests) {
                    case SEND_SAFE_NOTIFICATION:
                      sendNotify(true);
                      break;
                    case SEND_WARNING_NOTIFICATION:
                      sendNotify(false);
                      break;
                    case SEND_NOTIFICATION_TOKEN:
                      sendNotificationToken();
                      break;
                    case SEND_GUARDIAN_DATA:
                      //endGuardianData();
                      break;
                  }
                } else {
                  showLoginFailedSnack(requests);
                }
              }

              @Override public void onRequestFail(Call call, Response response) {
                dismissProgressDialog();
                showLoginFailedSnack(requests);
              }

              @Override public void onRequestTimeOut(Call call, Throwable t) {
                dismissProgressDialog();
                showLoginFailedSnack(requests);
              }

              @Override public void onNullResponse(Call call) {
                dismissProgressDialog();
                showLoginFailedSnack(requests);
              }
            });
  }

  private void showLoginFailedSnack(final Requests requests) {
    SnackUtil.makeLoginFailedSnackBar(HomeActivity.this, getWindow().getDecorView(), false,
        new View.OnClickListener() {
          @Override public void onClick(View view) {
            loginUser(requests);
          }
        });
  }

  public void openContact() {
    checkContactPermission();
  }

  public void checkLocationPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        && ActivityCompat.checkSelfPermission(HomeActivity.this,
        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(HomeActivity.this,
        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      locationPermissionNotGranted = true;
      requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
          PERMISSIONS_REQUEST_LOCATION);
    } else {
      locationPermissionNotGranted = false;
      makeTurnOnLocationPermissionSnack();
      LocationManager.getInstance(this);
    }
  }

  private void checkCallPermission() {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        && checkSelfPermission(Manifest.permission.CALL_PHONE)
        != PackageManager.PERMISSION_GRANTED) {
      requestPermissions(new String[] { Manifest.permission.CALL_PHONE }, PERMISSIONS_REQUEST_CALL);
    }
  }

  private void checkContactPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        && checkSelfPermission(Manifest.permission.READ_CONTACTS)
        != PackageManager.PERMISSION_GRANTED) {
      requestPermissions(new String[] { Manifest.permission.READ_CONTACTS },
          PERMISSIONS_REQUEST_READ_CONTACTS);
    } else {
      openSelectContactFragment();
    }
  }

  public void checkSmsPermission() {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        && checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
      requestPermissions(new String[] { Manifest.permission.SEND_SMS }, PERMISSIONS_REQUEST_SMS);
    } else {

    }
  }

  public void checkSmsPermissionForSendStatus(boolean safe) {

    sendingStatusBySms = safe;

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        && checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
      requestPermissions(new String[] { Manifest.permission.SEND_SMS },
          PERMISSIONS_REQUEST_SMS_STATUS);
    } else {
      sendStatusMessage(safe);
    }
  }

  @Override public void onBackPressed() {

    if (fragmentManager.getBackStackEntryCount() == 1) {
      android.os.Process.killProcess(android.os.Process.myPid());
    } else {
      super.onBackPressed();
    }
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    if (requestCode == PERMISSIONS_REQUEST_CALL) {

      if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {

        if (callPermissionDenied) {
          return;
        }

        makeCallPermissionSnack();
        callPermissionDenied = true;
      }
    } else if (requestCode == PERMISSIONS_REQUEST_SMS) {
      //if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      //  //ContactManager.getInstance(this).sendInvitationMessage(contactEntities, smsListener);
      //} else {
      //  //showSendSmsSnack();
      //}
    } else if (requestCode == PERMISSIONS_REQUEST_SMS_STATUS) {
      if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        sendStatusMessage(sendingStatusBySms);
      }
    } else if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
      if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        handler.postDelayed(runnable, 10);
      }
    } else if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
      if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        locationPermissionNotGranted = false;
        makeTurnOnLocationPermissionSnack();
        LocationManager.getInstance(this);
      } else {
        locationPermissionNotGranted = true;
      }
    }
  }

  private void setFragment(Fragment fragment) {
    String backStateName = fragment.getClass().getName();

    transaction = fragmentManager.beginTransaction();

    if (!firstTransaction) {
      transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_from_right,
          R.anim.enter_from_right, R.anim.exit_from_left);
    }

    firstTransaction = false;

    transaction.replace(R.id.fragment_container, fragment, backStateName)
        .addToBackStack(backStateName)
        .commit();
  }

  private void sendInvitationMessage(ArrayList<ContactEntity> contactEntities,
      SmsListener smsListener) {

    StringBuilder smsRecipients = new StringBuilder("smsto:");

    for (ContactEntity contactEntity : contactEntities) {
      smsRecipients.append(contactEntity.getPhoneNumber()).append(';');
    }

    try {

      Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse(smsRecipients.toString()));
      smsIntent.putExtra("sms_body", getString(R.string.invitation_message));
      startActivityForResult(smsIntent, SEND_SMS_ACTIVITY_TOKEN);
    } catch (Exception ex) {
      ex.printStackTrace();
      smsListener.onSmsNotSent();
    }
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == SEND_SMS_ACTIVITY_TOKEN) {
      smsListener.onSmsSent();
    }

    if (requestCode == SEND_SMS_NOTIFICATION_TOKEN) {
    }
  }

  private void makeTurnOnLocationPermissionSnack() {

    if (NetworkUtil.canGetLocation()) {
      return;
    }

    SnackUtil.makeSnackBar(this, getWindow().getDecorView(), Snackbar.LENGTH_LONG,
        getString(R.string.turn_on_location), true, getString(R.string.enabling),
        new View.OnClickListener() {
          @Override public void onClick(View view) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
          }
        });
  }

  private void makeCallPermissionSnack() {
    SnackUtil.makeSnackBar(this, getWindow().getDecorView(), Snackbar.LENGTH_INDEFINITE,
        getString(R.string.call_permission_description), true, getString(R.string.understood),
        new View.OnClickListener() {
          @Override public void onClick(View view) {
            checkCallPermission();
          }
        });
  }

  private void showSendSmsSnack() {
    SnackUtil.makeSnackBar(this, getWindow().getDecorView(), Snackbar.LENGTH_INDEFINITE,
        getString(R.string.send_sms_description), true, getString(R.string.send),
        new View.OnClickListener() {
          @Override public void onClick(View view) {
            checkSmsPermission();
          }
        });
  }

  public void showResendSmsSnackBar(final ContactEntity contactEntity) {
    //contactEntities = contactEntity;
    SnackUtil.makeSnackBar(this, getWindow().getDecorView(), Snackbar.LENGTH_INDEFINITE,
        String.format(getString(R.string.resend_sms_description), contactEntity.getName()), true,
        getString(R.string.send), new View.OnClickListener() {
          @Override public void onClick(View view) {
            checkSmsPermission();
          }
        });
  }

  public void setContactEntities(ArrayList<ContactEntity> contactEntities) {
    this.contactEntities = contactEntities;
    addGuardians();
  }

  private void addGuardians() {
    ArrayList<Guardian> guardianArrayList = new ArrayList<>();

    Guardian.Builder builder;

    for (ContactEntity contactEntity : contactEntities) {
      builder = new Guardian.Builder();
      builder.mobile(contactEntity.getPhoneNumber()).name(contactEntity.getName());
      guardianArrayList.add(builder.build());
    }

    AddGuardianRequest.Builder addGuardianBuilder = new AddGuardianRequest.Builder();
    addGuardianBuilder.guardians(guardianArrayList);

    sendGuardianData(addGuardianBuilder.build());
  }

  private Runnable runnable = new Runnable() {
    @Override public void run() {
      openSelectContactFragment();
    }
  };

  enum Requests {
    SEND_SAFE_NOTIFICATION, SEND_WARNING_NOTIFICATION, SEND_NOTIFICATION_TOKEN, SEND_GUARDIAN_DATA;
  }
}
