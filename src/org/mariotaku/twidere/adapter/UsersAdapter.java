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

package org.mariotaku.twidere.adapter;

import java.util.List;

import org.mariotaku.twidere.app.TwidereApplication;
import org.mariotaku.twidere.model.ParcelableUser;
import org.mariotaku.twidere.model.UserViewHolder;
import org.mariotaku.twidere.sinaweibo.R;
import org.mariotaku.twidere.util.BaseAdapterInterface;
import org.mariotaku.twidere.util.LazyImageLoader;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class UsersAdapter extends ArrayAdapter<ParcelableUser> implements BaseAdapterInterface {

	private final LazyImageLoader mImageLoader;
	private boolean mDisplayProfileImage, mShowLastItemAsGap, mDisplayName;
	private float mTextSize;

	public UsersAdapter(Context context) {
		super(context, R.layout.user_list_item, R.id.description);
		final TwidereApplication application = (TwidereApplication) context.getApplicationContext();
		mImageLoader = application.getProfileImageLoader();
		application.getServiceInterface();
	}

	public ParcelableUser findItem(long id) {
		for (int i = 0; i < getCount(); i++) {
			if (getItemId(i) == id) return getItem(i);
		}
		return null;
	}

	public ParcelableUser findItemByUserId(long user_id) {
		for (int i = 0; i < getCount(); i++) {
			final ParcelableUser item = getItem(i);
			if (item.user_id == user_id) return item;
		}
		return null;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final View view = super.getView(position, convertView, parent);
		final Object tag = view.getTag();
		UserViewHolder holder = null;
		if (tag instanceof UserViewHolder) {
			holder = (UserViewHolder) tag;
		} else {
			holder = new UserViewHolder(view);
			view.setTag(holder);
		}
		final boolean show_gap = mShowLastItemAsGap && position == getCount() - 1;
		holder.setShowAsGap(show_gap);
		if (!show_gap) {
			final ParcelableUser user = getItem(position);
			holder.setTextSize(mTextSize);
			holder.name.setText(mDisplayName ? user.name : user.screen_name);
			holder.name.setCompoundDrawablesWithIntrinsicBounds(
					user.is_protected ? R.drawable.ic_indicator_is_protected : 0, 0, 0, 0);
			holder.profile_image.setVisibility(mDisplayProfileImage ? View.VISIBLE : View.GONE);
			if (mDisplayProfileImage) {
				mImageLoader.displayImage(user.profile_image_url, holder.profile_image);
			}
		}
		return view;
	}

	public boolean isGap(int position) {
		return mShowLastItemAsGap && position == getCount() - 1;
	}

	public void setData(List<ParcelableUser> data) {
		setData(data, false);
	}

	public void setData(List<ParcelableUser> data, boolean clear_old) {
		if (clear_old) {
			clear();
		}
		if (data == null) return;
		for (final ParcelableUser user : data) {
			if (clear_old || findItemByUserId(user.user_id) == null) {
				add(user);
			}
		}
	}

	@Override
	public void setDisplayName(boolean display) {
		if (display != mDisplayName) {
			mDisplayName = display;
			notifyDataSetChanged();
		}
	}

	@Override
	public void setDisplayProfileImage(boolean display) {
		if (display != mDisplayProfileImage) {
			mDisplayProfileImage = display;
			notifyDataSetChanged();
		}
	}

	public void setShowLastItemAsGap(boolean gap) {
		if (gap != mShowLastItemAsGap) {
			mShowLastItemAsGap = gap;
			notifyDataSetChanged();
		}
	}

	@Override
	public void setTextSize(float text_size) {
		if (text_size != mTextSize) {
			mTextSize = text_size;
			notifyDataSetChanged();
		}
	}

}
