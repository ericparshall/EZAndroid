/*
 * Copyright 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package us.parshall.ezandroid.actionbar;

import java.util.ArrayList;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

/**
 * A <em>really</em> dumb implementation of the {@link android.view.Menu}
 * interface, that's only useful for our actionbar-compatibility purposes. See
 * <code>com.android.internal.view.menu.MenuBuilder</code> in AOSP for a more
 * complete implementation.
 */
public class SimpleMenu implements Menu {

	private Context mContext;
	private Resources mResources;

	private ArrayList<SimpleMenuItem> mItems;

	public SimpleMenu(Context context) {
		this.mContext = context;
		this.mResources = context.getResources();
		this.mItems = new ArrayList<SimpleMenuItem>();
	}

	public Context getContext() {
		return this.mContext;
	}

	public Resources getResources() {
		return this.mResources;
	}

	public MenuItem add(CharSequence title) {
		return this.addInternal(0, 0, title);
	}

	public MenuItem add(int titleRes) {
		return this.addInternal(0, 0, this.mResources.getString(titleRes));
	}

	public MenuItem add(int groupId, int itemId, int order, CharSequence title) {
		return this.addInternal(itemId, order, title);
	}

	public MenuItem add(int groupId, int itemId, int order, int titleRes) {
		return this.addInternal(itemId, order, this.mResources.getString(titleRes));
	}

	/**
	 * Adds an item to the menu. The other add methods funnel to this.
	 */
	private MenuItem addInternal(int itemId, int order, CharSequence title) {
		final SimpleMenuItem item = new SimpleMenuItem(this, itemId, order, title);
		this.mItems.add(findInsertIndex(this.mItems, order), item);
		return item;
	}

	private static int findInsertIndex(ArrayList<? extends MenuItem> items, int order) {
		for (int i = items.size() - 1; i >= 0; i--) {
			MenuItem item = items.get(i);
			if (item.getOrder() <= order)
				return i + 1;
		}

		return 0;
	}

	public int findItemIndex(int id) {
		final int size = this.size();

		for (int i = 0; i < size; i++) {
			SimpleMenuItem item = this.mItems.get(i);
			if (item.getItemId() == id)
				return i;
		}

		return -1;
	}

	public void removeItem(int itemId) {
		this.removeItemAtInt(this.findItemIndex(itemId));
	}

	private void removeItemAtInt(int index) {
		if ((index < 0) || (index >= this.mItems.size()))
			return;
		this.mItems.remove(index);
	}

	public void clear() {
		this.mItems.clear();
	}

	public MenuItem findItem(int id) {
		final int size = this.size();
		for (int i = 0; i < size; i++) {
			SimpleMenuItem item = this.mItems.get(i);
			if (item.getItemId() == id)
				return item;
		}

		return null;
	}

	public int size() {
		return this.mItems.size();
	}

	public MenuItem getItem(int index) {
		return this.mItems.get(index);
	}

	// Unsupported operations.

	public SubMenu addSubMenu(CharSequence charSequence) {
		throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
	}

	public SubMenu addSubMenu(int titleRes) {
		throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
	}

	public SubMenu addSubMenu(int groupId, int itemId, int order, CharSequence title) {
		throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
	}

	public SubMenu addSubMenu(int groupId, int itemId, int order, int titleRes) {
		throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
	}

	public int addIntentOptions(int i, int i1, int i2, ComponentName componentName, Intent[] intents, Intent intent, int i3, MenuItem[] menuItems) {
		throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
	}

	public void removeGroup(int i) {
		throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
	}

	public void setGroupCheckable(int i, boolean b, boolean b1) {
		throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
	}

	public void setGroupVisible(int i, boolean b) {
		throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
	}

	public void setGroupEnabled(int i, boolean b) {
		throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
	}

	public boolean hasVisibleItems() {
		throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
	}

	public void close() {
		throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
	}

	public boolean performShortcut(int i, KeyEvent keyEvent, int i1) {
		throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
	}

	public boolean isShortcutKey(int i, KeyEvent keyEvent) {
		throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
	}

	public boolean performIdentifierAction(int i, int i1) {
		throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
	}

	public void setQwertyMode(boolean b) {
		throw new UnsupportedOperationException("This operation is not supported for SimpleMenu");
	}
}
