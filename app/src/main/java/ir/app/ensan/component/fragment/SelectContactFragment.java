package ir.app.ensan.component.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ir.app.ensan.R;
import ir.app.ensan.common.ContactManager;
import ir.app.ensan.component.abstraction.ContactSelectListener;
import ir.app.ensan.component.activity.AddGuardianActivity;
import ir.app.ensan.component.activity.HomeActivity;
import ir.app.ensan.component.adapter.ContactSelectAdapter;
import ir.app.ensan.component.view.CustomButton;
import ir.app.ensan.component.view.CustomRecycleView;
import ir.app.ensan.component.view.CustomTextView;
import ir.app.ensan.model.ContactEntity;
import ir.app.ensan.util.SnackUtil;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectContactFragment extends BaseFragment {

  private CustomRecycleView recycleView;
  private RecyclerView.LayoutManager layoutManager;
  private ContactSelectAdapter contactSelectAdapter;

  private ArrayList<ContactEntity> allContacts;
  private ContactEntity selectedContact;

  private ContactSelectListener contactSelectListener;

  private CustomButton confirmButton;
  private CustomTextView emptyView;

  private Handler handler;

  private int selectedIndex = -1;

  public SelectContactFragment() {
    // Required empty public constructor
  }

  public static SelectContactFragment newInstance() {
    SelectContactFragment selectContactFragment = new SelectContactFragment();
    return selectContactFragment;
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_select_contact, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    handler = new Handler();
    initRecycleView();
    setContact();
  }

  @Override public void registerWidgets() {
    super.registerWidgets();
    recycleView = (CustomRecycleView) mainView.findViewById(R.id.contact_recycler_view);
    confirmButton = (CustomButton) mainView.findViewById(R.id.confirm_button);
    emptyView = (CustomTextView) mainView.findViewById(R.id.contact_empty_view);
  }

  @Override public void setListeners() {
    super.setListeners();
    confirmButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {

        if (selectedContact == null) {
          SnackUtil.makeSnackBar(getActivity(), mainView, Snackbar.LENGTH_LONG,
              getString(R.string.select_one_at_least), false);
          return;
        }

        if (getActivity() instanceof AddGuardianActivity) {
          ((AddGuardianActivity) getActivity()).addContact(selectedContact);
        } else if (getActivity() instanceof HomeActivity) {

          ((HomeActivity) getActivity()).setSelectedContactEntity(selectedContact);
          getActivity().onBackPressed();
        }
      }
    });

    contactSelectListener = new ContactSelectListener() {
      @Override public void onContactSelect(ContactEntity contactEntity) {
        selectedContact = contactEntity;
        //selectedIndex = allContacts.indexOf(contactEntity);
        notifyRecycleAdapter();
      }

      @Override public void onContactDeSelect(ContactEntity contactEntity) {
        //selectedContact = null;
        //selectedIndex = -1;
        //notifyRecycleAdapter();
      }
    };
  }

  @Override public void notifyRecycleAdapter() {
    super.notifyAdapter();
    new Handler().post(runnable);
  }

  Runnable runnable = new Runnable() {
    @Override public void run() {
      ///contactSelectAdapter.setSelectedIndex(selectedIndex);
      contactSelectAdapter.notifyDataSetChanged();
    }
  };

  private void showSendSmsSnack() {
    SnackUtil.makeSnackBar(getActivity(), mainView, Snackbar.LENGTH_INDEFINITE,
        getString(R.string.send_sms_description), false, getString(R.string.send),
        new View.OnClickListener() {
          @Override public void onClick(View view) {
            //((AddGuardianActivity) getActivity()).sendMessage(selectedContact);
            //getActivity().onBackPressed();
          }
        });
  }

  @Override public void initRecycleView() {
    layoutManager = new LinearLayoutManager(getActivity());
    recycleView.setLayoutManager(this.layoutManager);
    recycleView.setEmptyView(emptyView);
    recycleView.setItemAnimator(null);

    setRecycleAdapter();
  }

  @Override public void setRecycleAdapter() {
    contactSelectAdapter = new ContactSelectAdapter(getActivity(), contactSelectListener);
    recycleView.setAdapter(contactSelectAdapter);
  }

  private void setContact() {
    allContacts = ContactManager.getInstance(getActivity()).getAllContacts();
    contactSelectAdapter.setContactEntities(allContacts);

    if (allContacts.isEmpty()) {
      recycleView.setEmptyViewVisibility(View.VISIBLE);
      confirmButton.setVisibility(View.GONE);
      return;
    }

    recycleView.setEmptyViewVisibility(View.GONE);
    confirmButton.setVisibility(View.VISIBLE);
    notifyRecycleAdapter();
  }
}