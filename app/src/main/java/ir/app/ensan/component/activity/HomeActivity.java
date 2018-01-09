package ir.app.ensan.component.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import ir.app.ensan.R;
import ir.app.ensan.common.ContactManager;
import ir.app.ensan.component.abstraction.SmsListener;
import ir.app.ensan.component.fragment.GuardianListFragment;
import ir.app.ensan.component.fragment.MainFragment;
import ir.app.ensan.component.fragment.SelectContactFragment;
import ir.app.ensan.component.fragment.UserStatusFragment;
import ir.app.ensan.component.service.AppMessagingIdService;
import ir.app.ensan.model.ContactEntity;
import ir.app.ensan.model.network.NetworkRequestManager;
import ir.app.ensan.model.network.callback.AddGuardianCallback;
import ir.app.ensan.model.network.callback.AppCallback;
import ir.app.ensan.model.network.request.NotifyRequest;
import ir.app.ensan.model.network.response.NotifyResponse;
import ir.app.ensan.util.SharedPreferencesUtil;
import ir.app.ensan.util.SnackUtil;
import retrofit2.Call;
import retrofit2.Response;

public class HomeActivity extends BaseActivity {

  private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1001;
  private static final int PERMISSIONS_REQUEST_SMS = 1002;
  private static final int PERMISSIONS_REQUEST_CALL = 1003;

  private FragmentManager fragmentManager;
  private FragmentTransaction transaction;
  private ContactEntity selectedContactEntity;

  private boolean firstTransaction = true;
  private boolean callPermissionDenied = false;

  private SmsListener smsListener;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);
    fragmentManager = getSupportFragmentManager();

    openMainFragment();
    checkCallPermission();
    sendNotificationToken();
  }

  @Override public void setListeners() {
    super.setListeners();

    smsListener = new SmsListener() {
      @Override public void onSmsSent(ContactEntity contactEntity) {
        SnackUtil.makeSnackBar(HomeActivity.this, getWindow().getDecorView(), Snackbar.LENGTH_LONG,
            String.format(getString(R.string.contact_sms_sent), contactEntity.getName()), true);
        ContactManager.getInstance(HomeActivity.this).saveContacts();
      }

      @Override public void onSmsNotSent(ContactEntity contactEntity) {
        SnackUtil.makeSnackBar(HomeActivity.this, getWindow().getDecorView(), Snackbar.LENGTH_LONG,
            String.format(getString(R.string.contact_sms_sent), contactEntity.getName()), true,
            getString(R.string.send_again), new View.OnClickListener() {
              @Override public void onClick(View view) {
                checkSmsPermission();
              }
            });
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
    GuardianListFragment guardianListFragment = GuardianListFragment.newInstance();
    setFragment(guardianListFragment);
  }

  public void sendNotify(final boolean safe) {
    showProgressDialog();
    NotifyRequest notifyRequest;
    NotifyRequest.Builder builder = new NotifyRequest.Builder();

    builder.type(safe ? "healthy" : "inDanger");
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

      @Override public void onRequestFail(Call call, Response response) {

      }

      @Override public void onRequestTimeOut(Call call, Throwable t) {

      }

      @Override public void onNullResponse(Call call) {

      }
    });
  }

  public void sendGuardianData() {
    showProgressDialog();
    NetworkRequestManager.getInstance()
        .callAddGuardian(selectedContactEntity.getName(), selectedContactEntity.getPhoneNumber(),
            new AddGuardianCallback() {
              @Override public void onRequestSuccess(Call call, Response response) {
                dismissProgressDialog();
                ContactManager.getInstance(HomeActivity.this)
                    .addSelectedContact(selectedContactEntity);
                ContactManager.getInstance(HomeActivity.this).saveContacts();
                checkSmsPermission();
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

              @Override public void onRequestFail(Call call, Response response) {
                dismissProgressDialog();
              }

              @Override public void onRequestTimeOut(Call call, Throwable t) {
                dismissProgressDialog();
              }

              @Override public void onNullResponse(Call call) {
                dismissProgressDialog();
              }
            });
  }

  public void openContact() {
    checkContactPermission();
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
      ContactManager.getInstance(this).sendMessage(selectedContactEntity, smsListener);
    }
  }

  @Override public void onBackPressed() {

    if (fragmentManager.getBackStackEntryCount() == 1) {
      finish();
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
      if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        ContactManager.getInstance(this).sendMessage(selectedContactEntity, smsListener);
      } else {
        showSendSmsSnack();
      }
    } else if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
      if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        openSelectContactFragment();
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

  public void setSelectedContactEntity(ContactEntity selectedContactEntity) {
    this.selectedContactEntity = selectedContactEntity;
    sendGuardianData();
  }
}
