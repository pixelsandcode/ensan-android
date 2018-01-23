package ir.app.ensan.component.fragment;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import ir.app.ensan.BuildConfig;
import ir.app.ensan.R;
import ir.app.ensan.component.activity.AddGuardianActivity;
import ir.app.ensan.component.view.CustomButton;
import ir.app.ensan.component.view.CustomEditText;
import ir.app.ensan.component.view.CustomTextView;
import ir.app.ensan.util.SharedPreferencesUtil;
import ir.app.ensan.util.SnackUtil;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddUserFragment extends BaseFragment {

  public static final String USER_NAME_KEY = "username";
  public static final String PHONE_NUMBER_KEY = "phone_number";
  private CustomButton sendButton;
  private ImageView backButton;
  private CustomTextView backTextView;
  private CustomEditText userNameEditText;
  private CustomEditText phoneNumberEditText;

  public AddUserFragment() {
    // Required empty public constructor
  }

  public static AddUserFragment newInstance() {
    AddUserFragment addUserFragment = new AddUserFragment();
    return addUserFragment;
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_add_user, container, false);
  }

  @Override public void registerWidgets() {
    super.registerWidgets();
    sendButton = (CustomButton) mainView.findViewById(R.id.send_button);
    userNameEditText = (CustomEditText) mainView.findViewById(R.id.user_name_edit_text);
    phoneNumberEditText = (CustomEditText) mainView.findViewById(R.id.user_phone_edit_text);
    backTextView = (CustomTextView) mainView.findViewById(R.id.back_text_view);
    backButton = (ImageView) mainView.findViewById(R.id.arrow_right);
  }

  @Override public void setListeners() {
    super.setListeners();
    sendButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        checkValidation();
      }
    });

    backButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        getActivity().onBackPressed();
      }
    });

    backTextView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        getActivity().onBackPressed();
      }
    });

    userNameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_NEXT) {
          phoneNumberEditText.requestFocus();
          return true;
        }
        return false;
      }
    });

    phoneNumberEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
          checkValidation();
          return true;
        }
        return false;
      }
    });
  }

  private void checkValidation() {

    String userName = userNameEditText.getText().toString();
    String phoneNumber = phoneNumberEditText.getText().toString();

    if (userName.isEmpty()) {
      SnackUtil.makeSnackBar(getActivity(), mainView, Snackbar.LENGTH_LONG,
          getString(R.string.please_enter_your_name), false);
      return;
    }

    if (phoneNumber.isEmpty()) {
      SnackUtil.makeSnackBar(getActivity(), mainView, Snackbar.LENGTH_LONG,
          getString(R.string.please_enter_your_phone_number), false);
      return;
    }

    Pattern p = Pattern.compile("^(0|\\+[1-9][0-9]{0,4})[0-9]{7,10}$");
    Matcher m = p.matcher(phoneNumber);
    if (!m.find()) {
      SnackUtil.makeSnackBar(getActivity(), mainView, Snackbar.LENGTH_LONG,
          getString(R.string.phone_number_not_valid), false);
      return;
    }

    if (phoneNumber.startsWith("0")) {
      phoneNumber = phoneNumber.substring(1);
      phoneNumber = "+98" + phoneNumber;
    }

    if (BuildConfig.STG) {
      Toast.makeText(getActivity(), "phone number To save " + phoneNumber, Toast.LENGTH_LONG)
          .show();
    }

    SharedPreferencesUtil.saveString(USER_NAME_KEY, userName);
    SharedPreferencesUtil.saveString(PHONE_NUMBER_KEY, phoneNumber);

    ((AddGuardianActivity) getActivity()).sendUserData();
  }
}
