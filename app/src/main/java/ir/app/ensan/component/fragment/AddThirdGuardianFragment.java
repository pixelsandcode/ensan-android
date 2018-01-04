package ir.app.ensan.component.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import ir.app.ensan.R;
import ir.app.ensan.component.activity.AddGuardianActivity;
import ir.app.ensan.component.view.CustomTextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddThirdGuardianFragment extends BaseFragment {

  private CustomTextView addOtherGuardian;
  private ImageView addButton;

  public AddThirdGuardianFragment() {
    // Required empty public constructor
  }

  public static AddThirdGuardianFragment newInstance() {
    AddThirdGuardianFragment addThirdGuardianFragment = new AddThirdGuardianFragment();
    return addThirdGuardianFragment;
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_add_third_guardian, container, false);
  }

  @Override public void registerWidgets() {
    super.registerWidgets();
    addOtherGuardian = (CustomTextView) mainView.findViewById(R.id.sub_title2);
    addButton = (ImageView) mainView.findViewById(R.id.add_button);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    addOtherGuardian.setText(String.format(getString(R.string.add_guardian_sub_title2),1));
  }

  @Override public void setListeners() {
    super.setListeners();

    addButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        ((AddGuardianActivity) getActivity()).openContact();
      }
    });
  }
}
