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

package org.mariotaku.twidere.loader;

import java.util.Collections;
import java.util.List;

import org.mariotaku.twidere.model.ParcelableStatus;

import android.content.Context;

public class DummyParcelableStatusLoader extends ParcelableStatusesLoader {

	public DummyParcelableStatusLoader(Context context, long account_id, List<ParcelableStatus> data) {
		super(context, account_id, data);
	}

	@Override
	public List<ParcelableStatus> loadInBackground() {
		return Collections.emptyList();
	}

}
