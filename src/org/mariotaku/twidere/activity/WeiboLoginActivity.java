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

import static org.mariotaku.twidere.util.Utils.getActivatedAccountIds;
import static org.mariotaku.twidere.util.Utils.getColorPreviewBitmap;
import static org.mariotaku.twidere.util.Utils.isNullOrEmpty;
import static org.mariotaku.twidere.util.Utils.isUserLoggedIn;
import static org.mariotaku.twidere.util.Utils.makeAccountContentValues;
import static org.mariotaku.twidere.util.Utils.parseInt;
import static org.mariotaku.twidere.util.Utils.setIgnoreSSLError;
import static org.mariotaku.twidere.util.Utils.showErrorToast;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.mariotaku.twidere.provider.WeiboStore.Accounts;
import org.mariotaku.twidere.sinaweibo.R;
import org.mariotaku.twidere.util.ColorAnalyser;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class WeiboLoginActivity extends BaseActivity implements OnClickListener {

	private static final String WEIBO_SIGNUP_URL = "http://www.weibo.com/signup/signup.php";

	private String mUsername;

	private int mUserColor;

	private boolean mUserColorSet;

	private Button mSignInButton, mSignUpButton;

	private ImageButton mSetColorButton;

	private AbstractTask<?> mTask;

	private RequestToken mRequestToken;

	private long mLoggedId;


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_GOTO_AUTHORIZATION:
				if (resultCode == RESULT_OK) {
					Bundle bundle = new Bundle();
					if (data != null) {
						bundle = data.getExtras();
					}
					if (bundle != null) {
						final String oauth_verifier = bundle.getString(OAUTH_VERIFIER);
						if (oauth_verifier != null && mRequestToken != null) {
							if (mTask != null) {
								mTask.cancel(true);
							}
							mTask = new CallbackAuthTask(mRequestToken, oauth_verifier);
							mTask.execute();
						}
					}
				}
				break;
			case REQUEST_SET_COLOR:
				if (resultCode == BaseActivity.RESULT_OK) if (data != null && data.getExtras() != null) {
					mUserColor = data.getIntExtra(Accounts.USER_COLOR, Color.TRANSPARENT);
					mUserColorSet = true;
				} else {
					mUserColor = Color.TRANSPARENT;
					mUserColorSet = false;
				}
				setUserColorButton();
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.sign_up: {
				final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(WEIBO_SIGNUP_URL));
				startActivity(intent);
				break;
			}
			case R.id.sign_in: {
				if (mTask != null) {
					mTask.cancel(true);
				}
				mTask = new LoginTask();
				mTask.execute();
				break;
			}
			case R.id.set_color: {
				final Intent intent = new Intent(INTENT_ACTION_SET_COLOR);
				final Bundle bundle = new Bundle();
				bundle.putInt(Accounts.USER_COLOR, mUserColor);
				intent.putExtras(bundle);
				startActivityForResult(intent, REQUEST_SET_COLOR);
				break;
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestSupportWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.twitter_login);
		mSignInButton = (Button) findViewById(R.id.sign_in);
		mSignUpButton = (Button) findViewById(R.id.sign_up);
		mSetColorButton = (ImageButton) findViewById(R.id.set_color);
		setSupportProgressBarIndeterminateVisibility(false);
		final long[] account_ids = getActivatedAccountIds(this);
		getSupportActionBar().setDisplayHomeAsUpEnabled(account_ids.length > 0);

		Bundle bundle = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;
		if (bundle == null) {
			bundle = new Bundle();
		}


		mUsername = bundle.getString(Accounts.USERNAME);

		setUserColorButton();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_login, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onDestroy() {
		if (mTask != null) {
			mTask.cancel(true);
		}
		super.onDestroy();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		Intent intent = new Intent();

		switch (item.getItemId()) {
			case MENU_HOME: {
				final long[] account_ids = getActivatedAccountIds(this);
				if (account_ids.length > 0) {
					finish();
				}
				break;
			}
			case MENU_SETTINGS: {
				intent = new Intent(INTENT_ACTION_SETTINGS);
				startActivity(intent);
				break;
			}
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putString(Accounts.USERNAME, mUsername);
		outState.putInt(Accounts.USER_COLOR, mUserColor);
		super.onSaveInstanceState(outState);
	}

	private void analyseUserProfileColor(String url_string) {
		final boolean ignore_ssl_error = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE).getBoolean(
				PREFERENCE_KEY_IGNORE_SSL_ERROR, false);
		try {
			final URL url = new URL(url_string);
			final URLConnection conn = url.openConnection();
			final InputStream is = conn.getInputStream();
			if (ignore_ssl_error) {
				setIgnoreSSLError(conn);
			}
			final Bitmap bm = BitmapFactory.decodeStream(is);
			mUserColor = ColorAnalyser.analyse(bm);
			mUserColorSet = true;
			return;
		} catch (final MalformedURLException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		mUserColorSet = false;
	}

	private ConfigurationBuilder setAPI(ConfigurationBuilder cb) {
		final SharedPreferences preferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		final boolean enable_gzip_compressing = preferences.getBoolean(PREFERENCE_KEY_GZIP_COMPRESSING, false);
		final boolean ignore_ssl_error = preferences.getBoolean(PREFERENCE_KEY_IGNORE_SSL_ERROR, false);
		final boolean enable_proxy = preferences.getBoolean(PREFERENCE_KEY_ENABLE_PROXY, false);
		final String consumer_key = preferences.getString(PREFERENCE_KEY_CONSUMER_KEY, CONSUMER_KEY);
		final String consumer_secret = preferences.getString(PREFERENCE_KEY_CONSUMER_SECRET, CONSUMER_SECRET);
		if (isNullOrEmpty(consumer_key) || isNullOrEmpty(consumer_secret)) {
			cb.setOAuthConsumerKey(CONSUMER_KEY);
			cb.setOAuthConsumerSecret(CONSUMER_SECRET);
		} else {
			cb.setOAuthConsumerKey(consumer_key);
			cb.setOAuthConsumerSecret(consumer_secret);
		}
		cb.setGZIPEnabled(enable_gzip_compressing);
		cb.setIgnoreSSLError(ignore_ssl_error);
		if (enable_proxy) {
			final String proxy_host = preferences.getString(PREFERENCE_KEY_PROXY_HOST, null);
			final int proxy_port = parseInt(preferences.getString(PREFERENCE_KEY_PROXY_PORT, "-1"));
			if (isNullOrEmpty(proxy_host) && proxy_port > 0) {
				cb.setHttpProxyHost(proxy_host);
				cb.setHttpProxyPort(proxy_port);
			}

		}
		return cb;
	}

	private void setUserColorButton() {
		if (mUserColorSet) {
			mSetColorButton.setImageBitmap(getColorPreviewBitmap(this, mUserColor));
		} else {
			mSetColorButton.setImageResource(R.drawable.ic_menu_color_palette);
		}

	}

	private abstract class AbstractTask<Result> extends AsyncTask<Void, Void, Result> {

		@Override
		protected void onPostExecute(Result result) {
			setSupportProgressBarIndeterminateVisibility(false);
			mTask = null;
			mSignInButton.setEnabled(true);
			mSignUpButton.setEnabled(true);
			mSetColorButton.setEnabled(true);
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			setSupportProgressBarIndeterminateVisibility(true);
			mSignInButton.setEnabled(false);
			mSignUpButton.setEnabled(false);
			mSetColorButton.setEnabled(false);
		}

	}

	private class CallbackAuthTask extends AbstractTask<CallbackAuthTask.Response> {

		private RequestToken requestToken;
		private String oauthVerifier;

		public CallbackAuthTask(RequestToken requestToken, String oauthVerifier) {
			this.requestToken = requestToken;
			this.oauthVerifier = oauthVerifier;
		}

		@Override
		protected Response doInBackground(Void... params) {
			final ContentResolver resolver = getContentResolver();
			final ConfigurationBuilder cb = new ConfigurationBuilder();
			setAPI(cb);
			final Twitter twitter = new TwitterFactory(cb.build()).getInstance();
			AccessToken accessToken = null;
			User user = null;
			try {
				accessToken = twitter.getOAuthAccessToken(requestToken, oauthVerifier);
				user = twitter.showUser(accessToken.getUserId());
			} catch (final TwitterException e) {
				return new Response(false, false, e);
			}
			if (!mUserColorSet) {
				analyseUserProfileColor(user.getProfileImageURL().toString());
			}
			mLoggedId = user.getId();
			if (isUserLoggedIn(WeiboLoginActivity.this, mLoggedId)) return new Response(false, true, null);
			final ContentValues values = makeAccountContentValues(mUserColor, accessToken, user, null);
			resolver.insert(Accounts.CONTENT_URI, values);
			return new Response(true, false, null);
		}

		@Override
		protected void onPostExecute(Response result) {
			if (result.succeed) {
				final Intent intent = new Intent(INTENT_ACTION_HOME);
				final Bundle bundle = new Bundle();
				bundle.putLongArray(INTENT_KEY_IDS, new long[] { mLoggedId });
				intent.putExtras(bundle);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);
				finish();
			} else if (result.is_logged_in) {

			} else {
				showErrorToast(WeiboLoginActivity.this, result.exception, true);
			}
			super.onPostExecute(result);
		}

		private class Response {
			public boolean succeed, is_logged_in;
			public TwitterException exception;

			public Response(boolean succeed, boolean is_logged_in, TwitterException exception) {
				this.succeed = succeed;
				this.is_logged_in = is_logged_in;
				this.exception = exception;
			}
		}

	}

	private class LoginTask extends AbstractTask<LoginTask.Response> {

		@Override
		protected Response doInBackground(Void... params) {
			return doAuth();
		}

		@Override
		protected void onPostExecute(Response result) {

			if (result.succeed) {
				final Intent intent = new Intent(INTENT_ACTION_HOME);
				final Bundle bundle = new Bundle();
				bundle.putLongArray(INTENT_KEY_IDS, new long[] { mLoggedId });
				intent.putExtras(bundle);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);
				finish();
			} else if (result.open_browser) {
				mRequestToken = result.request_token;
				final Uri uri = Uri.parse(mRequestToken.getAuthorizationURL());
				startActivityForResult(new Intent(Intent.ACTION_DEFAULT, uri, getApplicationContext(),
						AuthorizationActivity.class), REQUEST_GOTO_AUTHORIZATION);
			} else if (result.already_logged_in) {
				Toast.makeText(WeiboLoginActivity.this, R.string.error_already_logged_in, Toast.LENGTH_SHORT).show();
			} else {
				showErrorToast(WeiboLoginActivity.this, result.exception, true);
			}
			super.onPostExecute(result);
		}

		private Response doAuth() {
			final ConfigurationBuilder cb = new ConfigurationBuilder();
			setAPI(cb);
			final Twitter twitter = new TwitterFactory(cb.build()).getInstance();
			RequestToken requestToken = null;
			try {
				requestToken = twitter.getOAuthRequestToken(DEFAULT_OAUTH_CALLBACK);
			} catch (final TwitterException e) {
				return new Response(false, false, false, null, e);
			}
			if (requestToken != null)
				return new Response(true, false, false, requestToken, null);
			return new Response(false, false, false, null, null);
		}

		private class Response {

			public boolean open_browser, already_logged_in, succeed;
			public RequestToken request_token;
			public TwitterException exception;

			public Response(boolean open_browser, boolean already_logged_in, boolean succeed,
					RequestToken request_token, TwitterException exception) {
				this.open_browser = open_browser;
				this.already_logged_in = already_logged_in;
				this.succeed = succeed;
				if (exception != null) {
					this.exception = exception;
					return;
				}
				if (open_browser && request_token == null)
					throw new IllegalArgumentException("Request Token cannot be null in oauth mode!");
				this.request_token = request_token;
			}
		}
	}

}
