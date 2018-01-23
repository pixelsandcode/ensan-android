package ir.app.ensan.component.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import ir.app.ensan.R;
import ir.app.ensan.common.ContactManager;
import ir.app.ensan.component.abstraction.GuardianListener;
import ir.app.ensan.component.activity.HomeActivity;
import ir.app.ensan.component.adapter.GuardianListAdapter;
import ir.app.ensan.component.view.CustomRecycleView;
import ir.app.ensan.component.view.CustomTextView;
import ir.app.ensan.model.ContactEntity;
import ir.app.ensan.model.network.NetworkRequestManager;
import ir.app.ensan.model.network.callback.AppCallback;
import ir.app.ensan.model.network.callback.LoginCallback;
import ir.app.ensan.model.network.response.GuardianListResponse;
import ir.app.ensan.model.network.response.LoginResponse;
import ir.app.ensan.util.NetworkUtil;
import ir.app.ensan.util.SharedPreferencesUtil;
import ir.app.ensan.util.SnackUtil;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class GuardianListFragment extends BaseFragment {

  private CustomRecycleView recycleView;
  private RecyclerView.LayoutManager layoutManager;
  private GuardianListAdapter guardianListAdapter;

  private ArrayList<ContactEntity> guardianList;

  private GuardianListener guardianListener;

  private ImageView backButton;
  private CustomTextView backTextView;

  private boolean showAddOther;

  public GuardianListFragment() {
    // Required empty public constructor
  }

  public static GuardianListFragment newInstance(boolean showAddOther) {
    GuardianListFragment guardianListFragment = new GuardianListFragment();
    guardianListFragment.showAddOther = showAddOther;
    return guardianListFragment;
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_guardian_list, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    guardianList = new ArrayList<>();
    initRecycleView();
    getGuardianList();
  }

  @Override public void registerWidgets() {
    super.registerWidgets();
    recycleView = (CustomRecycleView) mainView.findViewById(R.id.guardian_recycler_view);
    backTextView = (CustomTextView) mainView.findViewById(R.id.back_text_view);
    backButton = (ImageView) mainView.findViewById(R.id.arrow_right);
  }

  @Override public void setListeners() {
    super.setListeners();

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

    guardianListener = new GuardianListener() {
      @Override public void onGuardianClick(ContactEntity contactEntity) {
        ((HomeActivity) getActivity()).showResendSmsSnackBar(contactEntity);
      }

      @Override public void onAddGuardianClick() {
        ((HomeActivity) getActivity()).openContact();
      }
    };
  }

  private void getGuardianList() {

    if (!NetworkUtil.isInternetConnected()) {
      guardianList.addAll(ContactManager.getInstance(getActivity()).getGuardians());
      guardianListAdapter.setGuardianList(guardianList);
      notifyRecycleAdapter();
      return;
    }

    showProgressDialog();
    NetworkRequestManager.getInstance().callGuardianList(new AppCallback() {
      @Override public void onRequestSuccess(Call call, Response response) {
        GuardianListResponse guardianListResponse = (GuardianListResponse) response.body();

        dismissProgressDialog();

        for (GuardianListResponse.Guardian guardian : guardianListResponse.getData()) {
          guardianList.add(new ContactEntity(guardian));
        }

        ContactManager.getInstance(getActivity()).clearSelectedContacts();
        ContactManager.getInstance(getActivity()).addSelectedContacts(guardianList);
        ContactManager.getInstance(getActivity()).saveContacts();
        ContactManager.getInstance(getActivity()).loadContacts();

        guardianListAdapter.setGuardianList(guardianList);
        notifyRecycleAdapter();
      }

      @Override public void onTokenExpire(Call call, Response response) {
        loginUser();
      }

      @Override public void onRequestFail(Call call, Response response) {
        dismissProgressDialog();
        SnackUtil.makeNetworkDisconnectSnackBar(getActivity(), mainView, false);
      }

      @Override public void onRequestTimeOut(Call call, Throwable t) {
        dismissProgressDialog();
        SnackUtil.makeNetworkDisconnectSnackBar(getActivity(), mainView, false);
      }

      @Override public void onNullResponse(Call call) {
        dismissProgressDialog();
        SnackUtil.makeNetworkDisconnectSnackBar(getActivity(), mainView, false);
      }
    });
  }

  public void loginUser() {
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
                  getGuardianList();
                } else {
                  showLoginFailedSnack();
                }
              }

              @Override public void onRequestFail(Call call, Response response) {
                dismissProgressDialog();
                showLoginFailedSnack();
              }

              @Override public void onRequestTimeOut(Call call, Throwable t) {
                dismissProgressDialog();
                showLoginFailedSnack();
              }

              @Override public void onNullResponse(Call call) {
                dismissProgressDialog();
                showLoginFailedSnack();
              }
            });
  }

  private void showLoginFailedSnack() {
    SnackUtil.makeLoginFailedSnackBar(getActivity(), mainView, false, new View.OnClickListener() {
      @Override public void onClick(View view) {
        loginUser();
      }
    });
  }

  @Override public void initRecycleView() {
    layoutManager = new LinearLayoutManager(getActivity());
    recycleView.setLayoutManager(this.layoutManager);
    //recycleView.setEmptyView(emptyView);
    recycleView.setItemAnimator(null);

    setRecycleAdapter();
  }

  @Override public void setRecycleAdapter() {
    guardianListAdapter = new GuardianListAdapter(getActivity(), showAddOther, guardianListener);
    recycleView.setAdapter(guardianListAdapter);
  }

  @Override public void notifyRecycleAdapter() {
    guardianListAdapter.notifyDataSetChanged();
  }
}
