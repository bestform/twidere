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

package org.mariotaku.twidere.provider;

import org.mariotaku.twidere.Constants;

import android.net.Uri;
import android.provider.BaseColumns;

public final class WeiboStore implements Constants {

	public static final String AUTHORITY = "org.mariotaku.twidere.sinaweibo.provider.WeiboStore";

	public static final Uri[] STATUSES_URIS = new Uri[] { Statuses.CONTENT_URI, Mentions.CONTENT_URI };

	private static final String TYPE_PRIMARY_KEY = "INTEGER PRIMARY KEY AUTOINCREMENT";

	private static final String TYPE_TEXT = "TEXT";

	private static final String TYPE_TEXT_NOT_NULL = "TEXT NOT NULL";

	private static final String TYPE_INT = "INTEGER";

	private static final String TYPE_INT_UNIQUE = "INTEGER UNIQUE";

	private static final String TYPE_BOOLEAN = "INTEGER(1)";

	public static final String NULL_CONTENT_PATH = "null_content";

	public static final Uri NULL_CONTENT_URI = Uri.withAppendedPath(Uri.parse(PROTOCOL_CONTENT + AUTHORITY),
			NULL_CONTENT_PATH);

	public static interface Accounts extends BaseColumns {

		public static final String CONTENT_PATH = "accounts";
		public static final Uri CONTENT_URI = Uri.withAppendedPath(Uri.parse(PROTOCOL_CONTENT + AUTHORITY),
				CONTENT_PATH);

		/**
		 * Login name of the account<br>
		 * Type: TEXT NOT NULL
		 */
		public static final String USERNAME = "username";

		/**
		 * Unique ID of the account<br>
		 * Type: INTEGER (long)
		 */
		public static final String USER_ID = "user_id";

		/**
		 * OAuth Token of the account.<br>
		 * Type: TEXT
		 */
		public static final String OAUTH_TOKEN = "oauth_token";

		/**
		 * Token Secret of the account.<br>
		 * Type: TEXT
		 */
		public static final String TOKEN_SECRET = "token_secret";

		public static final String USER_COLOR = "user_color";

		/**
		 * Set to a non-zero integer if the account is activated. <br>
		 * Type: INTEGER (boolean)
		 */
		public static final String IS_ACTIVATED = "is_activated";

		/**
		 * User's profile image URL of the status. <br>
		 * Type: TEXT
		 */
		public static final String PROFILE_IMAGE_URL = "profile_image_url";

		public static final String[] COLUMNS = new String[] { _ID, USERNAME, USER_ID, OAUTH_TOKEN, TOKEN_SECRET, PROFILE_IMAGE_URL, USER_COLOR, IS_ACTIVATED };

		public static final String[] TYPES = new String[] { TYPE_PRIMARY_KEY, TYPE_TEXT_NOT_NULL, TYPE_INT_UNIQUE,
				TYPE_TEXT, TYPE_TEXT, TYPE_TEXT, TYPE_INT, TYPE_BOOLEAN };

	}

	public static interface CachedUsers extends BaseColumns {

		public static final String CONTENT_PATH = "cached_users";

		public static final Uri CONTENT_URI = Uri.withAppendedPath(Uri.parse(PROTOCOL_CONTENT + AUTHORITY),
				CONTENT_PATH);

		/**
		 * User's ID of the status.<br>
		 * Type: INTEGER (long)
		 */
		public static final String USER_ID = "user_id";

		/**
		 * User name of the status.<br>
		 * Type: TEXT
		 */
		public static final String NAME = "name";

		/**
		 * User's screen name of the status.<br>
		 * Type: TEXT
		 */
		public static final String SCREEN_NAME = "screen_name";

		/**
		 * User's profile image URL of the status.<br>
		 * Type: TEXT NOT NULL
		 */
		public static final String PROFILE_IMAGE_URL = "profile_image_url";

		public static final String[] COLUMNS = new String[] { _ID, USER_ID, NAME, SCREEN_NAME, PROFILE_IMAGE_URL };

		public static final String[] TYPES = new String[] { TYPE_PRIMARY_KEY, TYPE_INT_UNIQUE, TYPE_TEXT, TYPE_TEXT,
				TYPE_TEXT };

	}

	public static interface DirectMessages extends BaseColumns {

		public static final String CONTENT_PATH = "messages";

		public static final Uri CONTENT_URI = Uri.withAppendedPath(Uri.parse(PROTOCOL_CONTENT + AUTHORITY),
				CONTENT_PATH);

		public static final String ACCOUNT_ID = "account_id";
		public static final String MESSAGE_ID = "message_id";
		public static final String MESSAGE_TIMESTAMP = "message_timestamp";
		public static final String SENDER_ID = "sender_id";
		public static final String RECIPIENT_ID = "recipient_id";

		public static final String IS_GAP = "is_gap";

		public static final String TEXT = "text";
		public static final String SENDER_NAME = "sender_name";
		public static final String RECIPIENT_NAME = "recipient_name";
		public static final String SENDER_SCREEN_NAME = "sender_screen_name";
		public static final String RECIPIENT_SCREEN_NAME = "recipient_screen_name";
		public static final String SENDER_PROFILE_IMAGE_URL = "sender_profile_image_url";
		public static final String RECIPIENT_PROFILE_IMAGE_URL = "recipient_profile_image_url";

		public static final String[] COLUMNS = new String[] { _ID, ACCOUNT_ID, MESSAGE_ID, MESSAGE_TIMESTAMP,
				SENDER_ID, RECIPIENT_ID, IS_GAP, TEXT, SENDER_NAME, RECIPIENT_NAME, SENDER_SCREEN_NAME,
				RECIPIENT_SCREEN_NAME, SENDER_PROFILE_IMAGE_URL, RECIPIENT_PROFILE_IMAGE_URL };
		public static final String[] TYPES = new String[] { TYPE_PRIMARY_KEY, TYPE_INT, TYPE_INT, TYPE_INT, TYPE_INT,
				TYPE_INT, TYPE_BOOLEAN, TYPE_TEXT, TYPE_TEXT, TYPE_TEXT, TYPE_TEXT, TYPE_TEXT, TYPE_TEXT, TYPE_TEXT };

		public static final String DEFAULT_SORT_ORDER = MESSAGE_ID + " DESC";

		public static interface Conversation extends DirectMessages {

			public static final String DEFAULT_SORT_ORDER = MESSAGE_TIMESTAMP + " ASC";

			public static final String CONTENT_PATH = "messages_conversation";

			public static final String CONTENT_PATH_SCREEN_NAME = "messages_conversation_screen_name";

			public static final Uri CONTENT_URI = Uri.withAppendedPath(Uri.parse(PROTOCOL_CONTENT + AUTHORITY),
					CONTENT_PATH);

			public static final Uri CONTENT_URI_SCREEN_NAME = Uri.withAppendedPath(
					Uri.parse(PROTOCOL_CONTENT + AUTHORITY), CONTENT_PATH_SCREEN_NAME);
		}

		public static class ConversationsEntry implements BaseColumns {

			public static final String CONTENT_PATH = "messages_conversations_entry";

			public static final Uri CONTENT_URI = Uri.withAppendedPath(Uri.parse(PROTOCOL_CONTENT + AUTHORITY),
					CONTENT_PATH);

			public static final String MAX_TIMESTAMP = "max_timestamp";
			public static final String MESSAGE_TIMESTAMP = "message_timestamp";
			public static final String IS_OUTGOING = "is_outgoing";
			public static final String NAME = "name";
			public static final String SCREEN_NAME = "screen_name";
			public static final String PROFILE_IMAGE_URL = "profile_image_url";
			public static final String TEXT = "text";
			public static final String CONVERSATION_ID = "conversation_id";

			public static final int IDX__ID = 0;
			public static final int IDX_MAX_TIMESTAMP = 1;
			public static final int IDX_IS_OUTGOING = 3;
			public static final int IDX_NAME = 4;
			public static final int IDX_SCREEN_NAME = 5;
			public static final int IDX_PROFILE_IMAGE_URL = 6;
			public static final int IDX_TEXT = 7;
			public static final int IDX_CONVERSATION_ID = 8;
			public static final int IDX_MESSAGE_TIMESTAMP = 9;

			public static String buildSQL(long account_id) {
				final StringBuilder builder = new StringBuilder();
				builder.append("SELECT * FROM(");
				builder.append("SELECT " + _ID + ", MAX(" + DirectMessages.MESSAGE_TIMESTAMP + ") AS " + MAX_TIMESTAMP
						+ ", " + ACCOUNT_ID + ", " + "0 AS " + IS_OUTGOING + ", " + DirectMessages.SENDER_NAME + " AS "
						+ NAME + ", " + DirectMessages.SENDER_SCREEN_NAME + " AS " + SCREEN_NAME + ", "
						+ DirectMessages.SENDER_PROFILE_IMAGE_URL + " AS " + PROFILE_IMAGE_URL + ", " + TEXT + ", "
						+ DirectMessages.SENDER_ID + " AS " + CONVERSATION_ID + ", " + DirectMessages.MESSAGE_TIMESTAMP);
				builder.append(" FROM " + TABLE_DIRECT_MESSAGES_INBOX);
				builder.append(" GROUP BY " + CONVERSATION_ID);
				builder.append(" HAVING " + MAX_TIMESTAMP + " NOT NULL");
				builder.append(" UNION ");
				builder.append("SELECT " + _ID + ", MAX(" + DirectMessages.MESSAGE_TIMESTAMP + ") AS " + MAX_TIMESTAMP
						+ ", " + ACCOUNT_ID + ", " + "1 AS " + IS_OUTGOING + ", " + DirectMessages.RECIPIENT_NAME
						+ " AS " + NAME + ", " + DirectMessages.RECIPIENT_SCREEN_NAME + " AS " + SCREEN_NAME + ", "
						+ DirectMessages.RECIPIENT_PROFILE_IMAGE_URL + " AS " + PROFILE_IMAGE_URL + ", " + TEXT + ", "
						+ DirectMessages.RECIPIENT_ID + " AS " + CONVERSATION_ID + ", "
						+ DirectMessages.MESSAGE_TIMESTAMP);
				builder.append(" FROM " + TABLE_DIRECT_MESSAGES_OUTBOX);
				builder.append(" GROUP BY " + CONVERSATION_ID);
				builder.append(" HAVING " + MAX_TIMESTAMP + " NOT NULL");
				builder.append(")");
				builder.append(" GROUP BY " + CONVERSATION_ID);
				builder.append(" HAVING " + DirectMessages.ACCOUNT_ID + " = " + account_id);
				builder.append(" ORDER BY " + MESSAGE_TIMESTAMP + " DESC");
				return builder.toString();
			}
		}

		public static interface Inbox extends DirectMessages {

			public static final String CONTENT_PATH = "messages_inbox";

			public static final Uri CONTENT_URI = Uri.withAppendedPath(Uri.parse(PROTOCOL_CONTENT + AUTHORITY),
					CONTENT_PATH);

		}

		public static interface Outbox extends DirectMessages {

			public static final String CONTENT_PATH = "messages_outbox";

			public static final Uri CONTENT_URI = Uri.withAppendedPath(Uri.parse(PROTOCOL_CONTENT + AUTHORITY),
					CONTENT_PATH);

		}

	}

	public static interface Drafts extends BaseColumns {

		public static final String CONTENT_PATH = "drafts";

		public static final Uri CONTENT_URI = Uri.withAppendedPath(Uri.parse(PROTOCOL_CONTENT + AUTHORITY),
				CONTENT_PATH);

		public static final String IN_REPLY_TO_STATUS_ID = "in_reply_to_status_id";

		/**
		 * Account IDs of unsent status.<br>
		 * Type: TEXT
		 */
		public static final String ACCOUNT_IDS = "account_ids";

		/**
		 * Status content.<br>
		 * Type: TEXT
		 */
		public static final String TEXT = "text";

		public static final String MEDIA_URI = "media_uri";

		public static final String[] COLUMNS = new String[] { _ID, IN_REPLY_TO_STATUS_ID, ACCOUNT_IDS, TEXT, MEDIA_URI };

		public static final String[] TYPES = new String[] { TYPE_PRIMARY_KEY, TYPE_INT, TYPE_TEXT, TYPE_TEXT, TYPE_TEXT };
	}

	public static interface Filters extends BaseColumns {

		public static final String TEXT = "text";

		public static final String[] COLUMNS = new String[] { _ID, TEXT };

		public static final String[] TYPES = new String[] { TYPE_PRIMARY_KEY, TYPE_TEXT_NOT_NULL };

		public static interface Keywords extends Filters {

			public static final String CONTENT_PATH = "filtered_keywords";
			public static final Uri CONTENT_URI = Uri.withAppendedPath(Uri.parse(PROTOCOL_CONTENT + AUTHORITY),
					CONTENT_PATH);
		}

		public static interface Sources extends Filters {

			public static final String CONTENT_PATH = "filtered_sources";
			public static final Uri CONTENT_URI = Uri.withAppendedPath(Uri.parse(PROTOCOL_CONTENT + AUTHORITY),
					CONTENT_PATH);
		}

		public static interface Users extends Filters {

			public static final String CONTENT_PATH = "filtered_users";
			public static final Uri CONTENT_URI = Uri.withAppendedPath(Uri.parse(PROTOCOL_CONTENT + AUTHORITY),
					CONTENT_PATH);
		}
	}

	public static interface Mentions extends Statuses {

		public static final String CONTENT_PATH = "mentions";

		public static final Uri CONTENT_URI = Uri.withAppendedPath(Uri.parse(PROTOCOL_CONTENT + AUTHORITY),
				CONTENT_PATH);

	}

	public static interface Statuses extends BaseColumns {

		public static final String CONTENT_PATH = "statuses";

		public static final Uri CONTENT_URI = Uri.withAppendedPath(Uri.parse(PROTOCOL_CONTENT + AUTHORITY),
				CONTENT_PATH);
		/**
		 * Account ID of the status.<br>
		 * Type: TEXT
		 */
		public static final String ACCOUNT_ID = "account_id";

		/**
		 * Status content.<br>
		 * Type: TEXT
		 */
		public static final String TEXT = "text";
		public static final String TEXT_PLAIN = "text_plain";

		/**
		 * User name of the status.<br>
		 * Type: TEXT
		 */
		public static final String NAME = "name";

		/**
		 * User's screen name of the status.<br>
		 * Type: TEXT
		 */
		public static final String SCREEN_NAME = "screen_name";

		/**
		 * User's profile image URL of the status.<br>
		 * Type: TEXT NOT NULL
		 */
		public static final String PROFILE_IMAGE_URL = "profile_image_url";

		/**
		 * Unique id of the status.<br>
		 * Type: INTEGER UNIQUE(long)
		 */
		public static final String STATUS_ID = "status_id";

		/**
		 * Repost count of the status.<br>
		 * Type: INTEGER (long)
		 */
		public static final String RETWEET_COUNT = "repost_count";

		/**
		 * Set to an non-zero integer if the status is a repost, set to
		 * negative value if the status is reposted by user.<br>
		 * Type: INTEGER
		 */
		public static final String IS_REPOST = "is_repost";

		/**
		 * Set to 1 if the status is a favorite.<br>
		 * Type: INTEGER (boolean)
		 */
		public static final String IS_FAVORITE = "is_favorite";

		public static final String HAS_MEDIA = "has_media";
		public static final String LOCATION = "location";

		/**
		 * Set to 1 if the status is a gap.<br>
		 * Type: INTEGER (boolean)
		 */
		public static final String IS_GAP = "is_gap";

		/**
		 * User's ID of the status.<br>
		 * Type: INTEGER (long)
		 */
		public static final String USER_ID = "user_id";

		public static final String IN_REPLY_TO_STATUS_ID = "in_reply_to_status_id";

		public static final String IN_REPLY_TO_USER_ID = "in_reply_to_user_id";

		public static final String IN_REPLY_TO_SCREEN_NAME = "in_reply_to_screen_name";

		public static final String SOURCE = "source";

		public static final String IS_PROTECTED = "is_protected";

		public static final String REPOST_ID = "repost_id";

		public static final String REPOSTED_BY_ID = "reposted_by_id";

		public static final String REPOSTED_BY_NAME = "reposted_by_name";

		public static final String REPOSTED_BY_SCREEN_NAME = "reposted_by_screen_name";

		/**
		 * Timestamp of the status.<br>
		 * Type: INTEGER (long)
		 */
		public static final String STATUS_TIMESTAMP = "status_timestamp";

		public static final String SORT_ORDER_TIMESTAMP_DESC = STATUS_TIMESTAMP + " DESC";

		public static final String SORT_ORDER_STATUS_ID_DESC = STATUS_ID + " DESC";

		public static final String DEFAULT_SORT_ORDER = SORT_ORDER_STATUS_ID_DESC;

		public static final String[] COLUMNS = new String[] { _ID, ACCOUNT_ID, STATUS_ID, USER_ID, STATUS_TIMESTAMP,
				TEXT, TEXT_PLAIN, NAME, SCREEN_NAME, PROFILE_IMAGE_URL, IN_REPLY_TO_STATUS_ID, IN_REPLY_TO_USER_ID,
				IN_REPLY_TO_SCREEN_NAME, SOURCE, LOCATION, RETWEET_COUNT, REPOST_ID, REPOSTED_BY_ID,
				REPOSTED_BY_NAME, REPOSTED_BY_SCREEN_NAME, IS_REPOST, IS_FAVORITE, HAS_MEDIA, IS_PROTECTED, IS_GAP };

		public static final String[] TYPES = new String[] { TYPE_PRIMARY_KEY, TYPE_INT, TYPE_INT, TYPE_INT, TYPE_INT,
				TYPE_TEXT, TYPE_TEXT, TYPE_TEXT, TYPE_TEXT, TYPE_TEXT, TYPE_INT, TYPE_INT, TYPE_TEXT, TYPE_TEXT,
				TYPE_TEXT, TYPE_INT, TYPE_INT, TYPE_INT, TYPE_TEXT, TYPE_TEXT, TYPE_BOOLEAN, TYPE_BOOLEAN,
				TYPE_BOOLEAN, TYPE_BOOLEAN, TYPE_BOOLEAN };

	}

	public static interface Trends extends BaseColumns {

		public static final String NAME = "name";
		public static final String TIMESTAMP = "timestamp";

		public static final String[] COLUMNS = new String[] { _ID, NAME, TIMESTAMP };
		public static final String[] TYPES = new String[] { TYPE_PRIMARY_KEY, TYPE_INT, TYPE_INT };

		public static interface Daily extends Trends {
			public static final String CONTENT_PATH = "daily_trends";

			public static final Uri CONTENT_URI = Uri.withAppendedPath(Uri.parse(PROTOCOL_CONTENT + AUTHORITY),
					CONTENT_PATH);
		}

		public static interface Local extends Trends {
			public static final String CONTENT_PATH = "local_trends";

			public static final Uri CONTENT_URI = Uri.withAppendedPath(Uri.parse(PROTOCOL_CONTENT + AUTHORITY),
					CONTENT_PATH);

		}

		public static interface Weekly extends Trends {
			public static final String CONTENT_PATH = "weekly_trends";

			public static final Uri CONTENT_URI = Uri.withAppendedPath(Uri.parse(PROTOCOL_CONTENT + AUTHORITY),
					CONTENT_PATH);
		}

	}
}
