package ir.app.ensan.component.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ir.app.ensan.R;
import ir.app.ensan.component.abstraction.GuardianListener;
import ir.app.ensan.component.activity.HomeActivity;
import ir.app.ensan.component.adapter.GuardianListAdapter;
import ir.app.ensan.component.view.CustomRecycleView;
import ir.app.ensan.model.ContactEntity;
import ir.app.ensan.model.network.NetworkRequestManager;
import ir.app.ensan.model.network.callback.AppCallback;
import ir.app.ensan.model.network.response.GuardianListResponse;
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

  public GuardianListFragment() {
    // Required empty public constructor
  }

  public static GuardianListFragment newInstance() {
    GuardianListFragment guardianListFragment = new GuardianListFragment();
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
  }

  @Override public void setListeners() {
    super.setListeners();
    guardianListener = new GuardianListener() {
      @Override public void onGuardianClick(ContactEntity contactEntity) {
        showResendSmsSnackBar(contactEntity);
      }

      @Override public void onAddGuardianClick() {
        ((HomeActivity) getActivity()).openSelectContactFragment();
      }
    };
  }

  private void showResendSmsSnackBar(final ContactEntity contactEntity) {
    SnackUtil.makeSnackBar(getActivity(), mainView, Snackbar.LENGTH_INDEFINITE,
        String.format(getString(R.string.resend_sms_description), contactEntity.getName()), false,
        getString(R.string.send), new View.OnClickListener() {
          @Override public void onClick(View view) {
           // ContactManager.getInstance(getActivity()).sendMessage(contactEntity);
          }
        });
  }

  private void getGuardianList(){
    showProgressDialog();
    NetworkRequestManager.getInstance().callGuardianList(new AppCallback() {
      @Override public void onRequestSuccess(Call call, Response response) {
        GuardianListResponse guardianListResponse = (GuardianListResponse) response.body();

        dismissProgressDialog();

        for (GuardianListResponse.Guardian guardian : guardianListResponse.getData()){
          guardianList.add(new ContactEntity(guardian));
        }
        guardianListAdapter.setGuardianList(guardianList);
        notifyRecycleAdapter();
      }

      @Override public void onRequestFail(Call call, Response response) {
        dismissProgressDialog();
        SnackUtil.makeNetworkDisconnectSnackBar(getActivity(), mainView,false);
      }

      @Override public void onRequestTimeOut(Call call, Throwable t) {
        dismissProgressDialog();
        SnackUtil.makeNetworkDisconnectSnackBar(getActivity(), mainView,false);
      }

      @Override public void onNullResponse(Call call) {
        dismissProgressDialog();
        SnackUtil.makeNetworkDisconnectSnackBar(getActivity(), mainView,false);
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
    guardianListAdapter = new GuardianListAdapter(getActivity(), guardianListener);
    recycleView.setAdapter(guardianListAdapter);
  }

  @Override public void notifyRecycleAdapter() {
    guardianListAdapter.notifyDataSetChanged();
  }
}
