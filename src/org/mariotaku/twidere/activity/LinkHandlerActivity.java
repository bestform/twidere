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

package org.mariotaku.twidere.activity;

import static org.mariotaku.twidere.util.Utils.getAccountId;
import static org.mariotaku.twidere.util.Utils.getDefaultAccountId;
import static org.mariotaku.twidere.util.Utils.isMyAccount;
import static org.mariotaku.twidere.util.Utils.isNullOrEmpty;
import static org.mariotaku.twidere.util.Utils.parseInt;
import static org.mariotaku.twidere.util.Utils.parseLong;

import org.mariotaku.twidere.fragment.DirectMessagesConversationFragment;
import org.mariotaku.twidere.fragment.ListMembersFragment;
import org.mariotaku.twidere.fragment.ListSubscribersFragment;
import org.mariotaku.twidere.fragment.ListTimelineFragment;
import org.mariotaku.twidere.fragment.UserBlocksFragment;
import org.mariotaku.twidere.fragment.UserFavoritesFragment;
import org.mariotaku.twidere.fragment.UserFollowersFragment;
import org.mariotaku.twidere.fragment.UserFriendsFragment;
import org.mariotaku.twidere.fragment.UserProfileFragment;
import org.mariotaku.twidere.fragment.UserTimelineFragment;
import org.mariotaku.twidere.fragment.ViewConversationFragment;
import org.mariotaku.twidere.fragment.ViewStatusFragment;
import org.mariotaku.twidere.sinaweibo.R;

import android.content.Intent;
import android.content.UriMatcher;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.Window;
import android.widget.FrameLayout;

public class LinkHandlerActivity extends BaseActivity {

	private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
	private static final int CODE_STATUS = 1;
	private static final int CODE_USER = 2;
	private static final int CODE_USER_TIMELINE = 3;
	private static final int CODE_USER_FAVORITES = 4;
	private static final int CODE_USER_FOLLOWERS = 5;
	private static final int CODE_USER_FRIENDS = 6;
	private static final int CODE_USER_BLOCKS = 7;
	private static final int CODE_CONVERSATION = 8;
	private static final int CODE_DIRECT_MESSAGES_CONVERSATION = 9;
	private static final int CODE_LIST_DETAILS = 10;
	private static final int CODE_LIST_TYPES = 11;
	private static final int CODE_LIST_TIMELINE = 12;
	private static final int CODE_LIST_MEMBERS = 13;
	private static final int CODE_LIST_SUBSCRIBERS = 14;

	static {
		URI_MATCHER.addURI(AUTHORITY_STATUS, null, CODE_STATUS);
		URI_MATCHER.addURI(AUTHORITY_USER, null, CODE_USER);
		URI_MATCHER.addURI(AUTHORITY_USER_TIMELINE, null, CODE_USER_TIMELINE);
		URI_MATCHER.addURI(AUTHORITY_USER_FOLLOWERS, null, CODE_USER_FOLLOWERS);
		URI_MATCHER.addURI(AUTHORITY_USER_FRIENDS, null, CODE_USER_FRIENDS);
		URI_MATCHER.addURI(AUTHORITY_USER_FAVORITES, null, CODE_USER_FAVORITES);
		URI_MATCHER.addURI(AUTHORITY_USER_BLOCKS, null, CODE_USER_BLOCKS);
		URI_MATCHER.addURI(AUTHORITY_CONVERSATION, null, CODE_CONVERSATION);
		URI_MATCHER.addURI(AUTHORITY_DIRECT_MESSAGES_CONVERSATION, null, CODE_DIRECT_MESSAGES_CONVERSATION);
		URI_MATCHER.addURI(AUTHORITY_LIST_TIMELINE, null, CODE_LIST_TIMELINE);
		URI_MATCHER.addURI(AUTHORITY_LIST_MEMBERS, null, CODE_LIST_MEMBERS);
		URI_MATCHER.addURI(AUTHORITY_LIST_SUBSCRIBERS, null, CODE_LIST_SUBSCRIBERS);
	}

	private Fragment mFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestSupportWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(new FrameLayout(this));
		setSupportProgressBarIndeterminateVisibility(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		final Intent intent = getIntent();
		final Uri data = intent.getData();
		if (data != null) {
			if (setFragment(data)) {
				if (mFragment != null) {
					final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
					ft.replace(android.R.id.content, mFragment);
					ft.commit();
					return;
				} else {
					finish();
				}
			}
		} else {
			finish();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case MENU_HOME:
				onBackPressed();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private boolean setFragment(Uri uri) {
		final Bundle extras = getIntent().getExtras();
		Fragment fragment = null;
		if (uri != null) {
			final Bundle bundle = new Bundle();
			switch (URI_MATCHER.match(uri)) {
				case CODE_STATUS: {
					setTitle(R.string.view_status);
					fragment = new ViewStatusFragment();
					final String param_status_id = uri.getQueryParameter(QUERY_PARAM_STATUS_ID);
					if (extras != null) {
						bundle.putAll(extras);
					}
					bundle.putLong(INTENT_KEY_STATUS_ID, parseLong(param_status_id));
					break;
				}
				case CODE_USER: {
					setTitle(R.string.view_user_profile);
					fragment = new UserProfileFragment();
					final String param_screen_name = uri.getQueryParameter(QUERY_PARAM_SCREEN_NAME);
					final String param_user_id = uri.getQueryParameter(QUERY_PARAM_USER_ID);
					if (!isNullOrEmpty(param_screen_name)) {
						bundle.putString(INTENT_KEY_SCREEN_NAME, param_screen_name);
					}
					if (!isNullOrEmpty(param_user_id)) {
						bundle.putLong(INTENT_KEY_USER_ID, parseLong(param_user_id));
					}
					break;
				}
				case CODE_USER_TIMELINE: {
					setTitle(R.string.tweets);
					fragment = new UserTimelineFragment();
					final String param_screen_name = uri.getQueryParameter(QUERY_PARAM_SCREEN_NAME);
					final String param_user_id = uri.getQueryParameter(QUERY_PARAM_USER_ID);
					if (!isNullOrEmpty(param_screen_name)) {
						bundle.putString(INTENT_KEY_SCREEN_NAME, param_screen_name);
					}
					if (!isNullOrEmpty(param_user_id)) {
						bundle.putLong(INTENT_KEY_USER_ID, parseLong(param_user_id));
					}
					break;
				}
				case CODE_USER_FAVORITES: {
					setTitle(R.string.favorites);
					fragment = new UserFavoritesFragment();
					final String param_screen_name = uri.getQueryParameter(QUERY_PARAM_SCREEN_NAME);
					final String param_user_id = uri.getQueryParameter(QUERY_PARAM_USER_ID);
					if (!isNullOrEmpty(param_screen_name)) {
						bundle.putString(INTENT_KEY_SCREEN_NAME, param_screen_name);
					}
					if (!isNullOrEmpty(param_user_id)) {
						bundle.putLong(INTENT_KEY_USER_ID, parseLong(param_user_id));
					}
					break;
				}
				case CODE_USER_FOLLOWERS: {
					setTitle(R.string.followers);
					fragment = new UserFollowersFragment();
					final String param_screen_name = uri.getQueryParameter(QUERY_PARAM_SCREEN_NAME);
					final String param_user_id = uri.getQueryParameter(QUERY_PARAM_USER_ID);
					if (!isNullOrEmpty(param_screen_name)) {
						bundle.putString(INTENT_KEY_SCREEN_NAME, param_screen_name);
					}
					if (!isNullOrEmpty(param_user_id)) {
						bundle.putLong(INTENT_KEY_USER_ID, parseLong(param_user_id));
					}
					break;
				}
				case CODE_USER_FRIENDS: {
					setTitle(R.string.following);
					fragment = new UserFriendsFragment();
					final String param_screen_name = uri.getQueryParameter(QUERY_PARAM_SCREEN_NAME);
					final String param_user_id = uri.getQueryParameter(QUERY_PARAM_USER_ID);
					if (!isNullOrEmpty(param_screen_name)) {
						bundle.putString(INTENT_KEY_SCREEN_NAME, param_screen_name);
					}
					if (!isNullOrEmpty(param_user_id)) {
						bundle.putLong(INTENT_KEY_USER_ID, parseLong(param_user_id));
					}
					break;
				}
				case CODE_USER_BLOCKS: {
					setTitle(R.string.blocked_users);
					fragment = new UserBlocksFragment();
					break;
				}
				case CODE_CONVERSATION: {
					setTitle(R.string.view_conversation);
					fragment = new ViewConversationFragment();
					final String param_status_id = uri.getQueryParameter(QUERY_PARAM_STATUS_ID);
					bundle.putLong(INTENT_KEY_STATUS_ID, parseLong(param_status_id));
					break;
				}
				case CODE_DIRECT_MESSAGES_CONVERSATION: {
					setTitle(R.string.direct_messages);
					fragment = new DirectMessagesConversationFragment();
					final String param_conversation_id = uri.getQueryParameter(QUERY_PARAM_CONVERSATION_ID);
					final String param_screen_name = uri.getQueryParameter(QUERY_PARAM_SCREEN_NAME);
					final long conversation_id = parseLong(param_conversation_id);
					if (conversation_id > 0) {
						bundle.putLong(INTENT_KEY_CONVERSATION_ID, conversation_id);
					} else if (param_screen_name != null) {
						bundle.putString(INTENT_KEY_SCREEN_NAME, param_screen_name);
					}
					break;
				}
				case CODE_LIST_TIMELINE: {
					setTitle(R.string.list_timeline);
					fragment = new ListTimelineFragment();
					final String param_screen_name = uri.getQueryParameter(QUERY_PARAM_SCREEN_NAME);
					final String param_user_id = uri.getQueryParameter(QUERY_PARAM_USER_ID);
					final String param_list_id = uri.getQueryParameter(QUERY_PARAM_LIST_ID);
					final String param_list_name = uri.getQueryParameter(QUERY_PARAM_LIST_NAME);
					if (isNullOrEmpty(param_list_id)
							&& (isNullOrEmpty(param_list_name) || isNullOrEmpty(param_screen_name)
									&& isNullOrEmpty(param_user_id))) {
						finish();
						return false;
					}
					bundle.putInt(INTENT_KEY_LIST_ID, parseInt(param_list_id));
					bundle.putLong(INTENT_KEY_USER_ID, parseLong(param_user_id));
					bundle.putString(INTENT_KEY_SCREEN_NAME, param_screen_name);
					bundle.putString(INTENT_KEY_LIST_NAME, param_list_name);
					break;
				}
				case CODE_LIST_MEMBERS: {
					setTitle(R.string.list_members);
					fragment = new ListMembersFragment();
					final String param_screen_name = uri.getQueryParameter(QUERY_PARAM_SCREEN_NAME);
					final String param_user_id = uri.getQueryParameter(QUERY_PARAM_USER_ID);
					final String param_list_id = uri.getQueryParameter(QUERY_PARAM_LIST_ID);
					final String param_list_name = uri.getQueryParameter(QUERY_PARAM_LIST_NAME);
					if (isNullOrEmpty(param_list_id)
							&& (isNullOrEmpty(param_list_name) || isNullOrEmpty(param_screen_name)
									&& isNullOrEmpty(param_user_id))) {
						finish();
						return false;
					}
					bundle.putInt(INTENT_KEY_LIST_ID, parseInt(param_list_id));
					bundle.putLong(INTENT_KEY_USER_ID, parseLong(param_user_id));
					bundle.putString(INTENT_KEY_SCREEN_NAME, param_screen_name);
					bundle.putString(INTENT_KEY_LIST_NAME, param_list_name);
					break;
				}
				case CODE_LIST_SUBSCRIBERS: {
					setTitle(R.string.list_subscribers);
					fragment = new ListSubscribersFragment();
					final String param_screen_name = uri.getQueryParameter(QUERY_PARAM_SCREEN_NAME);
					final String param_user_id = uri.getQueryParameter(QUERY_PARAM_USER_ID);
					final String param_list_id = uri.getQueryParameter(QUERY_PARAM_LIST_ID);
					final String param_list_name = uri.getQueryParameter(QUERY_PARAM_LIST_NAME);
					if (isNullOrEmpty(param_list_id)
							&& (isNullOrEmpty(param_list_name) || isNullOrEmpty(param_screen_name)
									&& isNullOrEmpty(param_user_id))) {
						finish();
						return false;
					}
					bundle.putInt(INTENT_KEY_LIST_ID, parseInt(param_list_id));
					bundle.putLong(INTENT_KEY_USER_ID, parseLong(param_user_id));
					bundle.putString(INTENT_KEY_SCREEN_NAME, param_screen_name);
					bundle.putString(INTENT_KEY_LIST_NAME, param_list_name);
					break;
				}
				default: {
					break;
				}
			}
			final String param_account_id = uri.getQueryParameter(QUERY_PARAM_ACCOUNT_ID);
			if (param_account_id != null) {
				bundle.putLong(INTENT_KEY_ACCOUNT_ID, parseLong(param_account_id));
			} else {
				final String param_account_name = uri.getQueryParameter(QUERY_PARAM_ACCOUNT_NAME);
				if (param_account_name != null) {
					bundle.putLong(INTENT_KEY_ACCOUNT_ID, getAccountId(this, param_account_name));
				} else {
					final long account_id = getDefaultAccountId(this);
					if (isMyAccount(this, account_id)) {
						bundle.putLong(INTENT_KEY_ACCOUNT_ID, account_id);
					} else {
						finish();
						return false;
					}
				}
			}
			if (fragment != null) {
				fragment.setArguments(bundle);
			}
		}
		mFragment = fragment;
		return true;
	}
}
