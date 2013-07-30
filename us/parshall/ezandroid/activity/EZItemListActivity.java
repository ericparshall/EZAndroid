/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2013 Eric Parshall
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package us.parshall.ezandroid.activity;

import us.parshall.ezandroid.contentprovider.EZContentProvider;
import us.parshall.ezandroid.cursoradapter.EZCursorAdapter;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public abstract class EZItemListActivity extends EZActivity implements LoaderManager.LoaderCallbacks<Cursor> {
	protected ListView listView;
	protected EZCursorAdapter listCursorAdapter;

	public ListView getListView() {
		return this.listView;
	}

	public EZCursorAdapter getListCursorAdapter() {
		return this.listCursorAdapter;
	}

	protected OnItemClickListener defaultItemClickListener(final EZItemListActivity itemListActivity) {
		return new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				itemListActivity.invalidateOptionsMenu();
				Intent intent = new Intent(itemListActivity, itemListActivity.getListCursorAdapter().getItemIntentClass());
				intent.putExtra(EZItemActivity.SELECTED_ID, id);
				itemListActivity.startActivity(intent);
			}
		};
	}

	protected OnClickListener deleteItemsListener(final EZItemListActivity activity, final String tableName) {
		return new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				long[] selectedItemIds = activity.getListView().getCheckedItemIds();
				String[] itemIds = new String[selectedItemIds.length];
				String[] parameters = new String[selectedItemIds.length];
				for (int i = 0; i < selectedItemIds.length; i++) {
					itemIds[i] = String.valueOf(selectedItemIds[i]);
					parameters[i] = "?";
				}
				String where = EZContentProvider.KEY_ID + " in ( " + TextUtils.join(",", parameters) + " )";

				ContentResolver cr = activity.getContentResolver();

				cr.delete(Uri.withAppendedPath(EZContentProvider.CONTENT_URI, tableName), where, itemIds);
				cr.notifyChange(Uri.withAppendedPath(EZContentProvider.CONTENT_URI, tableName), null);
				dialog.dismiss();
				activity.finish();
				activity.startActivity(activity.getIntent());
			}

		};
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cursor) {
		this.listCursorAdapter.swapCursor(null);
	}
}
