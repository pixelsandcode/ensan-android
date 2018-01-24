package ir.app.ensan.component.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import ir.app.ensan.R;
import ir.app.ensan.common.ContactManager;
import ir.app.ensan.component.abstraction.SmsListener;
import ir.app.ensan.component.fragment.AddFirstGuardianFragment;
import ir.app.ensan.component.fragment.AddSecondGuardianFragment;
import ir.app.ensan.component.fragment.AddThirdGuardianFragment;
import ir.app.ensan.component.fragment.AddUserFragment;
import ir.app.ensan.component.fragment.GuardianListFragment;
import ir.app.ensan.component.fragment.SelectContactFragment;
import ir.app.ensan.component.fragment.VerificationFragment;
import ir.app.ensan.component.service.AppMessagingIdService;
import ir.app.ensan.model.ContactEntity;
import ir.app.ensan.model.network.NetworkRequestManager;
import ir.app.ensan.model.network.callback.AddGuardianCallback;
import ir.app.ensan.model.network.callback.AppCallback;
import ir.app.ensan.model.network.callback.LoginCallback;
import ir.app.ensan.model.network.callback.RegisterCallback;
import ir.app.ensan.model.network.callback.VerifyCallback;
import ir.app.ensan.model.network.response.GuardianListResponse;
import ir.app.ensan.model.network.response.LoginResponse;
import ir.app.ensan.model.network.response.SignUpResponse;
import ir.app.ensan.model.network.response.VerificationResponse;
import ir.app.ensan.util.SharedPreferencesUtil;
import ir.app.ensan.util.SnackUtil;
import ir.app.ensan.util.TimeUtil;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import retrofit2.Call;
import retrofit2.Response;

public class AddGuardianActivity extends BaseActivity {

  private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1001;
  private static final int PERMISSIONS_REQUEST_SMS = 1002;
  private static final int BACK_DELAY_TIME = 2000;

  private static final String REGISTER_COMPLETE_KEY = "register_complete";

  private Set<ContactEntity> contacts;

  private FragmentManager fragmentManager;
  private FragmentTransaction transaction;

  private ContactEntity selectedContactEntity;

  private SmsListener smsListener;

  private Handler handler;

  private boolean firstTransaction = true;
  private boolean registerComplete = false;
  private boolean login = false;

  private long backPressed;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_guardian);
    contacts = new HashSet<>();
    fragmentManager = getSupportFragmentManager();
    handler = new Handler();

    registerComplete = SharedPreferencesUtil.loadBoolean(REGISTER_COMPLETE_KEY, false);

    if (ContactManager.getInstance(this).isAnyContactExist()) {
      contacts = ContactManager.getInstance(this).getSelectedContacts();
    }

    openAddFirstGuardianFragment();
  }

  @Override public void setListeners() {
    super.setListeners();
    smsListener = new SmsListener() {
      @Override public void onSmsSent(ContactEntity contactEntity) {
        SnackUtil.makeSnackBar(AddGuardianActivity.this, getWindow().getDecorView(),
            Snackbar.LENGTH_LONG,
            String.format(getString(R.string.contact_sms_sent), contactEntity.getName()), true);
        ContactManager.getInstance(AddGuardianActivity.this).saveContacts();
        handler.post(runnable);
      }

      @Override public void onSmsNotSent(ContactEntity contactEntity) {
        ContactManager.getInstance(AddGuardianActivity.this).saveContacts();
        handler.post(runnable);
        SnackUtil.makeSnackBar(AddGuardianActivity.this, getWindow().getDecorView(),
            Snackbar.LENGTH_LONG,
            String.format(getString(R.string.contact_sms_not_sent), contactEntity.getName()), true,
            getString(R.string.send_again), new View.OnClickListener() {
              @Override public void onClick(View view) {
                checkSmsPermission();
              }
            });
      }
    };
  }

  public void openAddFirstGuardianFragment() {
    AddFirstGuardianFragment addFirstGuardianFragment = AddFirstGuardianFragment.newInstance();
    setFragment(addFirstGuardianFragment);
  }

  public void openSecondGuardianFragment() {
    AddSecondGuardianFragment addSecondGuardianFragment = AddSecondGuardianFragment.newInstance();
    setFragment(addSecondGuardianFragment);
  }

  public void openThirdGuardianFragment() {
    AddThirdGuardianFragment addThirdGuardianFragment = AddThirdGuardianFragment.newInstance();
    setFragment(addThirdGuardianFragment);
  }

  public void openSelectContactFragment() {
    SelectContactFragment selectContactFragment = SelectContactFragment.newInstance();
    setFragment(selectContactFragment);
  }

  public void openGuardianListFragment() {
    GuardianListFragment guardianListFragment = GuardianListFragment.newInstance(false);
    setFragment(guardianListFragment);
  }

  public void openAddUserFragment() {
    AddUserFragment addUserFragment = AddUserFragment.newInstance();
    setFragment(addUserFragment);
  }

  private void openVerificationFragment() {
    VerificationFragment verificationFragment = VerificationFragment.newInstance();
    setFragment(verificationFragment);
  }

  public void addContact(ContactEntity contactEntity) {

    contacts.add(contactEntity);
    ContactManager.getInstance(this).setSelectedContacts(contacts);
    selectedContactEntity = contactEntity;

    sendGuardianData();
  }

  public void sendUserData() {
    showProgressDialog();
    NetworkRequestManager.getInstance()
        .callSignUp(SharedPreferencesUtil.loadString(AddUserFragment.USER_NAME_KEY, ""),
            SharedPreferencesUtil.loadString(AddUserFragment.PHONE_NUMBER_KEY, ""),
            new RegisterCallback() {
              @Override public void onRequestSuccess(Call call, Response response) {
                dismissProgressDialog();

                SignUpResponse signUpResponse = (SignUpResponse) response.body();

                NetworkRequestManager.getInstance().saveAuthKey(signUpResponse.getData().getAuth());
                sendNotificationToken();
                handler.post(runnable);
              }

              @Override public void onRequestFail(Call call, Response response) {
                dismissProgressDialog();
                SnackUtil.makeNetworkDisconnectSnackBar(AddGuardianActivity.this,
                    getWindow().getDecorView());
              }

              @Override public void onRegisterFailed(Call call, Response response) {
                dismissProgressDialog();
                loginUser();
              }

              @Override public void onRequestTimeOut(Call call, Throwable t) {
                dismissProgressDialog();
                SnackUtil.makeNetworkDisconnectSnackBar(AddGuardianActivity.this,
                    getWindow().getDecorView());
              }

              @Override public void onNullResponse(Call call) {
                dismissProgressDialog();
                SnackUtil.makeNetworkDisconnectSnackBar(AddGuardianActivity.this,
                    getWindow().getDecorView());
              }
            });
  }

  public void loginUser() {
    login = true;
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
                  openVerificationFragment();
                }
              }

              @Override public void onRequestFail(Call call, Response response) {
                dismissProgressDialog();
                SnackUtil.makeNetworkDisconnectSnackBar(AddGuardianActivity.this,
                    getWindow().getDecorView());
              }

              @Override public void onRequestTimeOut(Call call, Throwable t) {
                dismissProgressDialog();
                SnackUtil.makeNetworkDisconnectSnackBar(AddGuardianActivity.this,
                    getWindow().getDecorView());
              }

              @Override public void onNullResponse(Call call) {
                dismissProgressDialog();
                SnackUtil.makeNetworkDisconnectSnackBar(AddGuardianActivity.this,
                    getWindow().getDecorView());
              }
            });
  }

  private void getGuardianList() {
    NetworkRequestManager.getInstance().callGuardianList(new AppCallback() {
      @Override public void onRequestSuccess(Call call, Response response) {
        GuardianListResponse guardianListResponse = (GuardianListResponse) response.body();

        ArrayList<ContactEntity> guardianList = new ArrayList<>();

        for (GuardianListResponse.Guardian guardian : guardianListResponse.getData()) {
          guardianList.add(new ContactEntity(guardian));
        }

        ContactManager.getInstance(AddGuardianActivity.this).clearSelectedContacts();
        ContactManager.getInstance(AddGuardianActivity.this).addSelectedContacts(guardianList);
        ContactManager.getInstance(AddGuardianActivity.this).saveContacts();
        ContactManager.getInstance(AddGuardianActivity.this).loadContacts();

        if (login) {
          handler.post(runnable);
        }
      }

      @Override public void onRequestFail(Call call, Response response) {

      }

      @Override public void onTokenExpire(Call call, Response response) {

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
                checkSmsPermission();
                getGuardianList();
              }

              @Override public void onGuardianAddBefore(Call call, Response response) {
                dismissProgressDialog();
                getGuardianList();
                SnackUtil.makeSnackBar(AddGuardianActivity.this, getWindow().getDecorView(),
                    Snackbar.LENGTH_INDEFINITE, getString(R.string.duplicate_guardian), true,
                    getString(R.string.understood), new View.OnClickListener() {
                      @Override public void onClick(View view) {
                        checkSmsPermission();
                      }
                    });
              }

              @Override public void onSelfGuardianAdded(Call call, Response response) {
                dismissProgressDialog();
                SnackUtil.makeSnackBar(AddGuardianActivity.this, getWindow().getDecorView(),
                    Snackbar.LENGTH_LONG, getString(R.string.self_guardian_forbidden), true);
              }

              @Override public void onTokenExpire(Call call, Response response) {

              }

              @Override public void onRequestFail(Call call, Response response) {
                dismissProgressDialog();
                SnackUtil.makeNetworkDisconnectSnackBar(AddGuardianActivity.this,
                    getWindow().getDecorView());
              }

              @Override public void onRequestTimeOut(Call call, Throwable t) {
                dismissProgressDialog();
                SnackUtil.makeNetworkDisconnectSnackBar(AddGuardianActivity.this,
                    getWindow().getDecorView());
              }

              @Override public void onNullResponse(Call call) {
                dismissProgressDialog();
                SnackUtil.makeNetworkDisconnectSnackBar(AddGuardianActivity.this,
                    getWindow().getDecorView());
              }
            });
  }

  public void sendVerifyRequest(String pin) {
    showProgressDialog();
    NetworkRequestManager.getInstance()
        .callVerify(pin, SharedPreferencesUtil.loadString(AddUserFragment.PHONE_NUMBER_KEY, ""),
            new VerifyCallback() {
              @Override public void onRequestSuccess(Call call, Response response) {

                dismissProgressDialog();
                VerificationResponse verificationResponse = (VerificationResponse) response.body();

                if (verificationResponse.getData().getSuccess()) {
                  NetworkRequestManager.getInstance()
                      .saveAuthKey(verificationResponse.getData().getAuth());
                  SharedPreferencesUtil.saveBoolean(REGISTER_COMPLETE_KEY, true);
                  SharedPreferencesUtil.saveString(AddUserFragment.USER_NAME_KEY,
                      verificationResponse.getData().getName());
                  registerComplete = true;
                  getGuardianList();
                  sendNotificationToken();
                }
              }

              @Override public void onVerificationCodeInvalid(Call call, Response response) {
                dismissProgressDialog();
                SnackUtil.makeSnackBar(AddGuardianActivity.this, getWindow().getDecorView(),
                    Snackbar.LENGTH_LONG, getString(R.string.invalid_code), true);
              }

              @Override public void onRequestFail(Call call, Response response) {
                dismissProgressDialog();
                SnackUtil.makeNetworkDisconnectSnackBar(AddGuardianActivity.this,
                    getWindow().getDecorView());
              }

              @Override public void onRequestTimeOut(Call call, Throwable t) {
                dismissProgressDialog();
                SnackUtil.makeNetworkDisconnectSnackBar(AddGuardianActivity.this,
                    getWindow().getDecorView());
              }

              @Override public void onNullResponse(Call call) {
                dismissProgressDialog();
                SnackUtil.makeNetworkDisconnectSnackBar(AddGuardianActivity.this,
                    getWindow().getDecorView());
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
      }

      @Override public void onRequestFail(Call call, Response response) {

      }

      @Override public void onTokenExpire(Call call, Response response) {

      }

      @Override public void onRequestTimeOut(Call call, Throwable t) {

      }

      @Override public void onNullResponse(Call call) {

      }
    });
  }

  private void openNextFragment() {

    if (registerComplete) {
      openHomeActivity();
    }

    int contactCount = contacts.size();

    switch (contactCount) {
      case 0:
        openContact();
        break;
      case 1:
        openSecondGuardianFragment();
        break;
      case 2:
        openThirdGuardianFragment();
        break;
      default:
        openHomeActivity();
        break;
    }
  }

  private void openHomeActivity() {
    Intent intent = new Intent(this, HomeActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
    finish();
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

  public void openContact() {
    checkContactPermission();
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

  private void checkSmsPermission() {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        && checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
      requestPermissions(new String[] { Manifest.permission.SEND_SMS }, PERMISSIONS_REQUEST_SMS);
    } else {
      ContactManager.getInstance(this).sendInvitationMessage(selectedContactEntity, smsListener);
    }
  }

  @Override public void onBackPressed() {
    Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);

    boolean allowBack = fragment instanceof GuardianListFragment
        || fragment instanceof AddUserFragment
        || fragment instanceof VerificationFragment
        || fragment instanceof SelectContactFragment;

    if (allowBack) {
      super.onBackPressed();
      return;
    }

    if (backPressed + BACK_DELAY_TIME > System.currentTimeMillis()) {
      android.os.Process.killProcess(android.os.Process.myPid());
    } else {
      SnackUtil.makeSnackBar(this, getWindow().getDecorView(), Snackbar.LENGTH_LONG,
          getString(R.string.tap_back_again), true);
    }

    backPressed = TimeUtil.getCurrentDate();
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
      if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        handler.postDelayed(runnable2, 10);
      } else {
        SnackUtil.makeSnackBar(this, getWindow().getDecorView(), Snackbar.LENGTH_INDEFINITE,
            getString(R.string.contact_permission_description), true,
            getString(R.string.understood), new View.OnClickListener() {
              @Override public void onClick(View view) {
                checkContactPermission();
              }
            });
      }
    } else if (requestCode == PERMISSIONS_REQUEST_SMS) {
      if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        ContactManager.getInstance(this).sendInvitationMessage(selectedContactEntity, smsListener);
      } else {
        showSendSmsSnack();
      }
    }
  }

  private Runnable runnable = new Runnable() {
    @Override public void run() {
      openNextFragment();
    }
  };

  private Runnable runnable2 = new Runnable() {
    @Override public void run() {
      openSelectContactFragment();
    }
  };
}
