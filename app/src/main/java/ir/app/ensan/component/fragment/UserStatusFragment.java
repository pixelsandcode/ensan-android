package ir.app.ensan.component.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import ir.app.ensan.R;
import ir.app.ensan.component.view.CustomTextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserStatusFragment extends BaseFragment {

  private ImageView userStatusImageView;
  private CustomTextView userStatusTextView;
  private CustomTextView notifiedCountTextView;

  private boolean safe;
  private int notifiedCount;

  public UserStatusFragment() {
    // Required empty public constructor
  }

  public static UserStatusFragment newInstance(boolean safe, int notifiedCount) {
    UserStatusFragment userStatusFragment = new UserStatusFragment();
    userStatusFragment.safe = safe;
    userStatusFragment.notifiedCount = notifiedCount;
    return userStatusFragment;
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_user_status, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mainView.setBackgroundColor(getActivity().getResources()
        .getColor(safe ? R.color.add_third_guardian : R.color.add_first_guardian));
    userStatusImageView.setImageResource(safe ? R.drawable.good : R.drawable.hurt);
    userStatusTextView.setText(safe ? R.string.user_status_safe : R.string.user_status_in_danger);
    notifiedCountTextView.setText(String.format(getString(
        safe ? R.string.notified_safe_status_count : R.string.notified_danger_status_count),
        notifiedCount));
  }

  @Override public void registerWidgets() {
    super.registerWidgets();
    userStatusImageView = (ImageView) mainView.findViewById(R.id.user_status_image_view);
    userStatusTextView = (CustomTextView) mainView.findViewById(R.id.user_status_text_view);
    notifiedCountTextView = (CustomTextView) mainView.findViewById(R.id.notified_count_text_view);
  }
}
