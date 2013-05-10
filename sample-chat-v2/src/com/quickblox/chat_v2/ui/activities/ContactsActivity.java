package com.quickblox.chat_v2.ui.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;

import com.quickblox.chat_v2.R;
import com.quickblox.chat_v2.adapters.ContactsAdapter;
import com.quickblox.chat_v2.core.ChatApplication;
import com.quickblox.chat_v2.interfaces.ContactSectionListener;
import com.quickblox.chat_v2.utils.GlobalConsts;
import com.quickblox.module.custom.model.QBCustomObject;

/**
 * Created with IntelliJ IDEA. User: Andrew Dmitrenko Date: 4/12/13 Time: 4:39
 * PM
 */
public class ContactsActivity extends ListActivity implements ContactSectionListener {
	
	private ChatApplication app;
	
	private boolean isContactButtonEnable;
	
	private ListView contactsTable;
	private ContactsAdapter contactsAdapter;
	private Button contactsButton;
	private Button requestButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts);
		
		app = ChatApplication.getInstance();
		
		contactsTable = (ListView) findViewById(android.R.id.list);
		contactsTable.setClickable(true);
		
		contactsButton = (RadioButton) findViewById(R.id.contacts_contact_button);
		requestButton = (RadioButton) findViewById(R.id.contacts_request_button);
		
		contactsAdapter = new ContactsAdapter(this, ChatApplication.getInstance().getContactsList(), quickReview(contactsButton));
		isContactButtonEnable = true;
		setListAdapter(contactsAdapter);
		
		OnClickListener oclBtn = new OnClickListener() {
			
			public void onClick(View v) {
				switch (v.getId()) {
					case R.id.contacts_contact_button :
						Log.w("Contact Activity section contacts", "setting list = " + app.getContactsList());
						contactsAdapter = new ContactsAdapter(ContactsActivity.this, app.getContactsList(), true);
						setListAdapter(contactsAdapter);
						contactsAdapter.notifyDataSetChanged();
						isContactButtonEnable = true;
						break;
					case R.id.contacts_request_button :
						Log.w("Contact Activity section candidate", "setting list = " + app.getContactsCandidateList());
						contactsAdapter = new ContactsAdapter(ContactsActivity.this, app.getContactsCandidateList(), false);
						setListAdapter(contactsAdapter);
						contactsAdapter.notifyDataSetChanged();
						isContactButtonEnable = false;
						break;
				}
			}
		};
		
		contactsButton.setOnClickListener(oclBtn);
		requestButton.setOnClickListener(oclBtn);
		contactsTable.setOnItemClickListener(onClicListener);
		
	}
	private OnItemClickListener onClicListener = new OnItemClickListener() {
		
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			Intent i = new Intent(ContactsActivity.this, ChatActivity.class);
			int tmpId = 0;
			String tmpDialogId = null;
			
			if (isContactButtonEnable) {
				i.putExtra(GlobalConsts.USER_ID, app.getContactsList().get(position).getId());
				tmpId = app.getContactsList().get(position).getId();
				System.out.println("test 1 =" + tmpId);
			} else {
				i.putExtra(GlobalConsts.USER_ID, app.getContactsCandidateList().get(position).getId());
				tmpId = app.getContactsCandidateList().get(position).getId();
				System.out.println("test 2 = " + tmpId);
			}
			
			for (QBCustomObject dialogs : app.getDialogList()) {
				if (Integer.parseInt(dialogs.getFields().get(GlobalConsts.RECEPIENT_ID_FIELD).toString()) == tmpId) {
					tmpDialogId = dialogs.getCustomObjectId();
					System.out.println("dialogId = " + tmpDialogId);
				}
				
			}
			i.putExtra(GlobalConsts.DIALOG_ID, tmpDialogId);
			i.putExtra(GlobalConsts.PREVIOUS_ACTIVITY, GlobalConsts.DIALOG_ACTIVITY);
			
			startActivity(i);
		}
	};
	
	@Override
	public void refreshCurrentList() {
		this.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				contactsAdapter.notifyDataSetChanged();
			}
		});
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		app.getQbm().setContactActivityListener(this);
	}
	
	private boolean quickReview(Button currentButton) {
		if (currentButton.isPressed()) {
			return true;
		} else {
			return false;
		}
	}
}
