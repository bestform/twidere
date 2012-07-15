/*
 *				Twidere - Twitter client for Android
 * 
 * Copyright (C) 2012 Mariotaku Lee <mariotaku.lee@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mariotaku.twidere.fragment;

import static org.mariotaku.twidere.util.Utils.buildDirectMessageConversationUri;
import static org.mariotaku.twidere.util.Utils.openUserProfile;

import org.mariotaku.popupmenu.PopupMenu;
import org.mariotaku.popupmenu.PopupMenu.OnMenuItemClickListener;
import org.mariotaku.twidere.adapter.DirectMessagesConversationAdapter;
import org.mariotaku.twidere.adapter.UserAutoCompleteAdapter;
import org.mariotaku.twidere.app.TwidereApplication;
import org.mariotaku.twidere.model.DMConversationViewHolder;
import org.mariotaku.twidere.model.ParcelableDirectMessage;
import org.mariotaku.twidere.provider.WeiboStore.DirectMessages;
import org.mariotaku.twidere.sinaweibo.R;
import org.mariotaku.twidere.util.LazyImageLoader;
import org.mariotaku.twidere.util.ServiceInterface;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.twitter.Validator;

public class DirectMessagesConversationFragment extends BaseFragment implements LoaderCallbacks<Cursor>,
		OnItemClickListener, OnItemLongClickListener, OnMenuItemClickListener, TextWatcher, OnClickListener {

	private ServiceInterface mService;

	private SharedPreferences mPreferences;

	private ListView mListView;
	private EditText mEditText;
	private AutoCompleteTextView mEditScreenName;
	private ImageButton mSendButton;
	private Button mScreenNameConfirmButton;
	private View mConversationContainer, mScreenNameContainer;

	private PopupMenu mPopupMenu;

	private ParcelableDirectMessage mSelectedDirectMessage;

	private DirectMessagesConversationAdapter mAdapter;
	private UserAutoCompleteAdapter mUserAutoCompleteAdapter;

	private BroadcastReceiver mStatusReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BROADCAST_RECEIVED_DIRECT_MESSAGES_DATABASE_UPDATED.equals(action)
					|| BROADCAST_SENT_DIRECT_MESSAGES_DATABASE_UPDATED.equals(action)) {
				getLoaderManager().restartLoader(0, mArguments, DirectMessagesConversationFragment.this);
			} else if (BROADCAST_REFRESHSTATE_CHANGED.equals(action)) {
				setProgressBarIndeterminateVisibility(mService.isReceivedDirectMessagesRefreshing()
						|| mService.isSentDirectMessagesRefreshing());
			}
		}
	};

	final Validator mValidator = new Validator();
	final Bundle mArguments = new Bundle();

	private TextWatcher mScreenNameTextWatcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable s) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if (mScreenNameConfirmButton == null) return;
			mScreenNameConfirmButton.setEnabled(s.length() > 0 && s.length() < 20);
		}
	};

	@Override
	public void afterTextChanged(Editable s) {

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		mService = getApplication().getServiceInterface();

		final LazyImageLoader imageloader = ((TwidereApplication) getActivity().getApplication())
				.getProfileImageLoader();
		mAdapter = new DirectMessagesConversationAdapter(getActivity(), imageloader);
		mListView.setAdapter(mAdapter);
		mListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
		mListView.setStackFromBottom(true);
		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);
		final Bundle args = savedInstanceState == null ? getArguments() : savedInstanceState.getBundle(INTENT_KEY_DATA);
		if (args != null) {
			mArguments.putAll(args);
		}
		getLoaderManager().initLoader(0, mArguments, this);

		mEditText.addTextChangedListener(this);
		final String text = savedInstanceState != null ? savedInstanceState.getString(INTENT_KEY_TEXT) : null;
		if (text != null) {
			mEditText.setText(text);
		}

		mUserAutoCompleteAdapter = new UserAutoCompleteAdapter(getActivity());

		mEditScreenName.addTextChangedListener(mScreenNameTextWatcher);
		mEditScreenName.setAdapter(mUserAutoCompleteAdapter);

		mSendButton.setOnClickListener(this);
		mSendButton.setEnabled(false);
		mScreenNameConfirmButton.setOnClickListener(this);
		mScreenNameConfirmButton.setEnabled(false);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.send: {
				final Editable text = mEditText.getText();
				if (text == null) return;
				final String message = text.toString();
				if (mValidator.isValidTweet(message)) {
					final long account_id = mArguments.getLong(INTENT_KEY_ACCOUNT_ID, -1);
					final long conversation_id = mArguments.getLong(INTENT_KEY_CONVERSATION_ID, -1);
					final String screen_name = mArguments.getString(INTENT_KEY_SCREEN_NAME);
					mService.sendDirectMessage(account_id, screen_name, conversation_id, message);
					text.clear();
				}
				break;
			}
			case R.id.screen_name_confirm: {
				final CharSequence text = mEditScreenName.getText();
				if (text == null) return;
				final String screen_name = text.toString();
				mArguments.putString(INTENT_KEY_SCREEN_NAME, screen_name);
				getLoaderManager().restartLoader(0, mArguments, this);
				break;
			}
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Tell the framework to try to keep this fragment around
		// during a configuration change.
		setRetainInstance(true);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		if (args == null || !args.containsKey(INTENT_KEY_ACCOUNT_ID)) return null;
		final String[] cols = DirectMessages.COLUMNS;
		final long account_id = args != null ? args.getLong(INTENT_KEY_ACCOUNT_ID, -1) : -1;
		final long conversation_id = args != null ? args.getLong(INTENT_KEY_CONVERSATION_ID, -1) : -1;
		final String screen_name = args != null ? args.getString(INTENT_KEY_SCREEN_NAME) : null;
		final Uri uri = buildDirectMessageConversationUri(account_id, conversation_id, screen_name);
		mConversationContainer.setVisibility(account_id <= 0 || conversation_id <= 0 && screen_name == null ? View.GONE
				: View.VISIBLE);
		mScreenNameContainer
				.setVisibility(account_id <= 0 || conversation_id <= 0 && screen_name == null ? View.VISIBLE
						: View.GONE);
		return new CursorLoader(getActivity(), uri, cols, null, null, DirectMessages.Conversation.DEFAULT_SORT_ORDER);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.direct_messages_conversation, null);
		mListView = (ListView) view.findViewById(android.R.id.list);
		mEditText = (EditText) view.findViewById(R.id.edit_text);
		mSendButton = (ImageButton) view.findViewById(R.id.send);
		mConversationContainer = view.findViewById(R.id.conversation_container);
		mScreenNameContainer = view.findViewById(R.id.screen_name_container);
		mEditScreenName = (AutoCompleteTextView) view.findViewById(R.id.screen_name);
		mScreenNameConfirmButton = (Button) view.findViewById(R.id.screen_name_confirm);
		return view;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		final Object tag = view.getTag();
		if (tag instanceof DMConversationViewHolder) {
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> adapter, View view, int position, long id) {
		final Object tag = view.getTag();
		if (tag instanceof DMConversationViewHolder) {
			final ParcelableDirectMessage dm = mSelectedDirectMessage = mAdapter.findItem(id);
			mPopupMenu = PopupMenu.getInstance(getActivity(), view);
			mPopupMenu.inflate(R.menu.action_direct_message);
			final Menu menu = mPopupMenu.getMenu();
			final MenuItem view_profile_item = menu.findItem(MENU_VIEW_PROFILE);
			if (view_profile_item != null && dm != null) {
				view_profile_item.setVisible(dm.account_id != dm.sender_id);
			}
			mPopupMenu.setOnMenuItemClickListener(this);
			mPopupMenu.show();
			return true;
		}
		return false;
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mAdapter.swapCursor(cursor);

	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		if (mSelectedDirectMessage != null) {
			final long message_id = mSelectedDirectMessage.message_id;
			final long account_id = mSelectedDirectMessage.account_id;
			switch (item.getItemId()) {
				case MENU_REPLY: {
					break;
				}
				case MENU_DELETE: {
					mService.destroyDirectMessage(account_id, message_id);
					break;
				}
				case MENU_VIEW_PROFILE: {
					if (mSelectedDirectMessage == null) return false;
					openUserProfile(getActivity(), account_id, mSelectedDirectMessage.sender_id,
							mSelectedDirectMessage.sender_screen_name);
					break;
				}
				default:
					return false;
			}
		}
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		final boolean display_profile_image = mPreferences.getBoolean(PREFERENCE_KEY_DISPLAY_PROFILE_IMAGE, true);
		final boolean display_name = mPreferences.getBoolean(PREFERENCE_KEY_DISPLAY_NAME, true);
		final float text_size = mPreferences.getFloat(PREFERENCE_KEY_TEXT_SIZE, PREFERENCE_DEFAULT_TEXT_SIZE);
		mAdapter.setDisplayProfileImage(display_profile_image);
		mAdapter.setDisplayName(display_name);
		mAdapter.setTextSize(text_size);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putString(INTENT_KEY_TEXT, String.valueOf(mEditText.getText()));
		outState.putBundle(INTENT_KEY_DATA, mArguments);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onStart() {
		super.onStart();
		final IntentFilter filter = new IntentFilter(BROADCAST_REFRESHSTATE_CHANGED);
		filter.addAction(BROADCAST_RECEIVED_DIRECT_MESSAGES_DATABASE_UPDATED);
		filter.addAction(BROADCAST_SENT_DIRECT_MESSAGES_DATABASE_UPDATED);
		registerReceiver(mStatusReceiver, filter);
	}

	@Override
	public void onStop() {
		unregisterReceiver(mStatusReceiver);
		if (mPopupMenu != null) {
			mPopupMenu.dismiss();
		}
		super.onStop();
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (mSendButton == null || s == null) return;
		mSendButton.setEnabled(mValidator.isValidTweet(s.toString()));
	}

	public void showConversation(long account_id, long conversation_id) {
		final Bundle args = new Bundle();
		args.putLong(INTENT_KEY_ACCOUNT_ID, account_id);
		args.putLong(INTENT_KEY_CONVERSATION_ID, conversation_id);
		getLoaderManager().restartLoader(0, args, this);
	}

}