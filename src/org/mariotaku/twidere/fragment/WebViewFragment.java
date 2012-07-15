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

import org.mariotaku.twidere.activity.BaseActivity;
import org.mariotaku.twidere.sinaweibo.R;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewFragment extends BaseFragment {

	private WebView mWebView;

	public final WebView getWebView() {
		return mWebView;
	}

	public final void loadUrl(String url) {
		mWebView.loadUrl(url == null ? "about:blank" : url);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mWebView = (WebView) getView().findViewById(R.id.webview);
		mWebView.setWebViewClient(new DefaultWebViewClient(getActivity()));
		mWebView.getSettings().setBuiltInZoomControls(true);
		mWebView.getSettings().setJavaScriptEnabled(true);
		final Bundle bundle = getArguments();
		if (bundle != null) {
			final String url = bundle.getString(INTENT_KEY_URI);
			loadUrl(url);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.webview, container, false);
	}

	public final void setWebViewClient(WebViewClient client) {
		mWebView.setWebViewClient(client);
	}

	public static class DefaultWebViewClient extends WebViewClient {

		private FragmentActivity mActivity;

		public DefaultWebViewClient(FragmentActivity activity) {
			mActivity = activity;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			if (mActivity instanceof BaseActivity) {
				((BaseActivity) mActivity).setSupportProgressBarIndeterminateVisibility(false);
			}
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			if (mActivity instanceof BaseActivity) {
				((BaseActivity) mActivity).setSupportProgressBarIndeterminateVisibility(true);
			}
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			return false;
		}
	}
}
