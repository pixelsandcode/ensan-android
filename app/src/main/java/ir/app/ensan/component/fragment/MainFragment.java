package ir.app.ensan.component.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import ir.app.ensan.R;
import ir.app.ensan.common.ContactManager;
import ir.app.ensan.component.activity.HomeActivity;
import ir.app.ensan.component.view.CustomTextView;
import ir.app.ensan.util.NetworkUtil;
import ir.app.ensan.util.SharedPreferencesUtil;

import static ir.app.ensan.R.id.safe_view;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends BaseFragment {

  public static final String SEND_DANGER_WARNING_KEY = "danger_warning";

  private View safe;
  private View inDanger;
  private ImageView addContactButton;
  private ImageView eyeImageView;
  private CustomTextView seeGuardianListTextView;
  private CustomTextView guardianCountTextView;

  private MaterialDialog warningDialog;
  private boolean firstDangerNotification;

  public static MainFragment newInstance() {
    MainFragment mainFragment = new MainFragment();
    return mainFragment;
  }

  public MainFragment() {
    // Required empty public constructor
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_main, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    firstDangerNotification = SharedPreferencesUtil.loadBoolean(SEND_DANGER_WARNING_KEY, true);
  }

  private void showWarningDialog() {
    warningDialog = new MaterialDialog.Builder(getActivity()).title(R.string.warning)
        .content(R.string.send_in_danger_status_warning)
        .positiveText(R.string.ok)
        .negativeText(R.string.cancel)
        .theme(Theme.LIGHT)
        .onPositive(new MaterialDialog.SingleButtonCallback() {
          @Override
          public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            firstDangerNotification = false;
            SharedPreferencesUtil.saveBoolean(SEND_DANGER_WARNING_KEY, false);
            sendWarningNotify();
          }
        })
        .onNegative(new MaterialDialog.SingleButtonCallback() {
          @Override
          public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            warningDialog.show();
          }
        })
        .show();
  }

  @Override public void registerWidgets() {
    super.registerWidgets();
    safe = mainView.findViewById(safe_view);
    inDanger = mainView.findViewById(R.id.in_danger_view);
    addContactButton = (ImageView) mainView.findViewById(R.id.add_button);
    seeGuardianListTextView = (CustomTextView) mainView.findViewById(R.id.main_title2);
    guardianCountTextView = (CustomTextView) mainView.findViewById(R.id.sub_title2);
    eyeImageView = (ImageView) mainView.findViewById(R.id.eye_image_view);
  }

  @Override public void onStart() {
    super.onStart();
    guardianCountTextView.setText(String.format(getString(R.string.home_sub_title2),
        ContactManager.getInstance(getActivity()).getSelectedContacts().size()));
  }

  @Override public void setListeners() {
    super.setListeners();

    safe.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (NetworkUtil.isInternetConnected()) {
          ((HomeActivity) getActivity()).sendNotify(true);
        } else {
          ((HomeActivity) getActivity()).sendStatusMessage(true);
        }
      }
    });

    inDanger.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (firstDangerNotification) {
          showWarningDialog();
        } else {
          sendWarningNotify();
        }
      }
    });

    addContactButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        ((HomeActivity) getActivity()).openContact();
      }
    });

    seeGuardianListTextView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        ((HomeActivity) getActivity()).openGuardianListFragment();
      }
    });

    guardianCountTextView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        ((HomeActivity) getActivity()).openGuardianListFragment();
      }
    });

    eyeImageView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        ((HomeActivity) getActivity()).openGuardianListFragment();
      }
    });
  }

  private void sendWarningNotify(){
    if (NetworkUtil.isInternetConnected()) {
      ((HomeActivity) getActivity()).sendNotify(false);
      ((HomeActivity) getActivity()).sendStatusMessage(false);
    } else {
      ((HomeActivity) getActivity()).sendStatusMessage(false);
    }
  }
}
