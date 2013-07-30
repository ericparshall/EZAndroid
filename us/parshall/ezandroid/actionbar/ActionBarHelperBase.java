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

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import me.ezparty.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A class that implements the action bar pattern for pre-Honeycomb devices.
 */
public class ActionBarHelperBase extends ActionBarHelper {
	private static final String MENU_RES_NAMESPACE = "http://schemas.android.com/apk/res/android";
	private static final String MENU_ATTR_ID = "id";
	private static final String MENU_ATTR_SHOW_AS_ACTION = "showAsAction";

	protected Set<Integer> menuActionItemIds = new HashSet<Integer>();
	public Activity activity;

	protected ActionBarHelperBase(Activity activity) {
		super(activity);
	}

	private ViewGroup getActionBar() {
		return (ViewGroup) this.mActivity.findViewById(R.id.actionbar);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.mActivity.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		this.mActivity.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.actionbar);
		this.setupActionBar();
	}

	public void setupActionBar() {
		final ViewGroup actionBar = this.getActionBar();
		if (actionBar == null)
			return;

		LinearLayout.LayoutParams springLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.FILL_PARENT);
		springLayoutParams.weight = 1;

		// Add Home button
		SimpleMenu tempMenu = new SimpleMenu(this.mActivity);
		SimpleMenuItem homeItem = new SimpleMenuItem(tempMenu, android.R.id.home, 0, this.mActivity.getString(R.string.app_name));
		homeItem.setIcon(R.drawable.ic_home);
		this.addActionItemFromMenuItem(homeItem);

		// Add title text
		TextView titleText = new TextView(this.mActivity, null, R.attr.actionbarCompatibilityTitleStyle);
		titleText.setLayoutParams(springLayoutParams);
		titleText.setText(this.mActivity.getTitle());
		actionBar.addView(titleText);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Hides on-screen action items from the options menu.
		Log.v("ActionBarHelperBase", "onCreateOptionsMenu called");
		//this.activity.onPrepareOptionsMenu(menu);
		//for (Integer id : menuActionItemIds) {
		//     menu.findItem(id).setVisible(false);
		//}
		return true;
	}

	/**
	 * Returns a {@link android.view.MenuInflater} that can read action bar
	 * metadata on pre-Honeycomb devices.
	 */
	public MenuInflater getMenuInflater(MenuInflater superMenuInflater) {
		return new WrappedMenuInflater(this.mActivity, superMenuInflater);
	}

	/** {@inheritDoc} */
	@Override
	public void invalidateOptionsMenu() {

	}

	private View addActionItemFromMenuItem(final MenuItem item) {
		final int itemId = item.getItemId();

		final ViewGroup actionBar = this.getActionBar();
		if (actionBar == null)
			return null;

		// Create the button
		int defStyle = itemId == android.R.id.home ? R.attr.actionbarCompatibilityItemHomeStyle : R.attr.actionbarCompatibilityItemStyle;
		ImageButton actionButton = new ImageButton(this.mActivity, null, defStyle);

		int layoutWidth = (int) this.mActivity.getResources().getDimension(itemId == android.R.id.home ? R.dimen.actionbar_button_home_width : R.dimen.actionbar_button_width);

		ViewGroup.LayoutParams layoutParameters = new ViewGroup.LayoutParams(layoutWidth, ViewGroup.LayoutParams.MATCH_PARENT);

		actionButton.setLayoutParams(layoutParameters);

		actionButton.setImageDrawable(item.getIcon());
		actionButton.setScaleType(ImageView.ScaleType.CENTER);
		actionButton.setContentDescription(item.getTitle());
		actionButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				ActionBarHelperBase.this.mActivity.onMenuItemSelected(Window.FEATURE_OPTIONS_PANEL, item);
			}
		});

		actionBar.addView(actionButton);

		return actionButton;
	}

	private class WrappedMenuInflater extends MenuInflater {
		MenuInflater menuInflater;

		public WrappedMenuInflater(Context context, MenuInflater inflater) {
			super(context);
			this.menuInflater = inflater;
		}

		@Override
		public void inflate(int menuRes, Menu menu) {
			this.loadActionBarMetadata(menuRes);

			SimpleMenu simpleMenu = new SimpleMenu(ActionBarHelperBase.this.mActivity);

			ActionBarHelperBase.this.mActivity.onPrepareOptionsMenu(menu);
			for (int i = 0; i < menu.size(); i++) {
				MenuItem item = menu.getItem(i);
				if (ActionBarHelperBase.this.menuActionItemIds.contains(item.getItemId())) {
					ActionBarHelperBase.this.addActionItemFromMenuItem(item);
				}
			}

			this.menuInflater.inflate(menuRes, menu);
		}

		/**
		 * Loads action bar metadata from a menu resource, storing a list of
		 * menu item IDs that should be shown on-screen (i.e. those with
		 * showAsAction set to always or ifRoom).
		 * 
		 * @param menuResId
		 */
		private void loadActionBarMetadata(int menuResId) {
			XmlResourceParser parser = null;
			try {
				parser = ActionBarHelperBase.this.mActivity.getResources().getXml(menuResId);

				int eventType = parser.getEventType();
				int itemId;
				int showAsAction;

				boolean eof = false;
				while (!eof) {
					switch (eventType) {
					case XmlPullParser.START_TAG:
						if (!parser.getName().equals("item")) {
							break;
						}

						itemId = parser.getAttributeResourceValue(MENU_RES_NAMESPACE, MENU_ATTR_ID, 0);
						if (itemId == 0) {
							break;
						}

						showAsAction = parser.getAttributeIntValue(MENU_RES_NAMESPACE, MENU_ATTR_SHOW_AS_ACTION, -1);
						if (showAsAction == MenuItem.SHOW_AS_ACTION_ALWAYS || showAsAction == MenuItem.SHOW_AS_ACTION_IF_ROOM) {
							ActionBarHelperBase.this.menuActionItemIds.add(itemId);
						}
						break;

					case XmlPullParser.END_DOCUMENT:
						eof = true;
						break;
					}

					eventType = parser.next();
				}
			} catch (XmlPullParserException e) {
				throw new InflateException("Error inflating menu XML", e);
			} catch (IOException e) {
				throw new InflateException("Error inflating menu XML", e);
			} finally {
				if (parser != null) {
					parser.close();
				}
			}
		}

	}

}
