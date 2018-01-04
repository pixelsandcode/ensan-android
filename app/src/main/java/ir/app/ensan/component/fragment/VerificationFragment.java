package ir.app.ensan.component.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ir.app.ensan.R;
import ir.app.ensan.component.activity.AddGuardianActivity;
import ir.app.ensan.component.view.CustomButton;
import ir.app.ensan.component.view.CustomEditText;

/**
 * A simple {@link Fragment} subclass.
 */
public class VerificationFragment extends BaseFragment {

  private CustomEditText panNameEditText;
  private CustomButton sendButton;

  private String pin;

  public VerificationFragment() {
    // Required empty public constructor
  }

  public static VerificationFragment newInstance() {
    VerificationFragment verificationFragment = new VerificationFragment();
    return verificationFragment;
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_verification, container, false);
  }

  @Override public void registerWidgets() {
    super.registerWidgets();
    panNameEditText = (CustomEditText) mainView.findViewById(R.id.sms_edit_text);
    sendButton = (CustomButton) mainView.findViewById(R.id.send_button);
  }

  @Override public void setListeners() {
    sendButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        pin = panNameEditText.getText().toString();

        if (pin.isEmpty()) {
          //// TODO: 03/01/2018 show snack bar
          return;
        } else {
          ((AddGuardianActivity) getActivity()).sendVerifyRequest(pin);
        }
      }
    });
  }
}
