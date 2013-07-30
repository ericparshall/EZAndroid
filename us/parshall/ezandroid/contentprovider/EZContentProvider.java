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

package us.parshall.ezandroid.contentprovider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import us.parshall.ezandroid.dbopenhelper.EZDBOpenHelper;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public abstract class EZContentProvider extends ContentProvider {
	public static final Uri CONTENT_URI = Uri.parse("content://us.parshall.ezandroid.contentprovider.ezcontentprovider");
	private static final String LOG_TAG = "EZProvider";
	protected static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);;

	private static final int ALLROWS = 1;
	private static final int SINGLE_ROW = 2;

	public static final String CURSOR_ITEM = "vnd.android.cursor.item";
	public static final String CURSOR_DIR = "vnd.android.cursor.dir";

	public static final String KEY_ID = "_id";
	public static final String UNIVERSAL_ID = "_uuid";

	protected EZDBOpenHelper openHelper;

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		this.logUriCall("delete", uri.toString());
		SQLiteDatabase db = this.openHelper.getWritableDatabase();

		if (selection == null) {
			selection = "1";
		}

		int deleteCount = db.delete(this.tableName(uri), this.selection(uri, selection), selectionArgs);
		this.getContext().getContentResolver().notifyChange(uri, null);
		return deleteCount;
	}

	@Override
	public abstract String getType(Uri uri);

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		this.logUriCall("insert", uri.toString());
		for (Map.Entry<String, Object> entry : values.valueSet()) {
			Log.d("EZProvider.insert", String.format("%s : %s", entry.getKey(), entry.getValue()));
		}

		SQLiteDatabase db = this.openHelper.getWritableDatabase();

		String nullColumnHack = null;

		values.put(UNIVERSAL_ID, UUID.randomUUID().toString());

		long id = db.insert(this.tableName(uri), nullColumnHack, values);
		Log.d("EZProvider.insert", String.format("Table Name: %s, ID: %s", this.tableName(uri), id));
		if (id > -1) {
			Uri insertedId = ContentUris.withAppendedId(CONTENT_URI, id);
			this.getContext().getContentResolver().notifyChange(insertedId, null);
			return insertedId;
		} else
			return null;
	}

	@Override
	public abstract boolean onCreate(); //{ }

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		this.logUriCall("query", uri.toString());
		SQLiteDatabase db = this.openHelper.getReadableDatabase();

		String groupBy = null;
		String having = null;

		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(this.tableName(uri));

		ArrayList<String> tmpProjection = new ArrayList<String>(Arrays.asList(projection));
		tmpProjection.add(KEY_ID);
		Cursor cursor = queryBuilder.query(db, tmpProjection.toArray(new String[tmpProjection.size()]), this.selection(uri, selection), selectionArgs, groupBy, having, sortOrder);
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		this.logUriCall("update", uri.toString());
		SQLiteDatabase db = this.openHelper.getWritableDatabase();

		int updateCount = db.update(this.tableName(uri), values, this.selection(uri, selection), selectionArgs);
		this.getContext().getContentResolver().notifyChange(uri, null);
		return updateCount;
	}

	private void logUriCall(String action, String uri) {
		Log.i(LOG_TAG, String.format("Action %s called on Uri: %s", action, uri));
	}

	private int queryType(Uri uri) {
		String type = this.getType(uri);
		Log.i(LOG_TAG, String.format("Type: %s", type));
		int queryType = type.startsWith(CURSOR_ITEM) ? SINGLE_ROW : ALLROWS;
		Log.i(LOG_TAG, String.format("Query Type: %d", queryType));
		return queryType;
	}

	private String tableName(Uri uri) {
		String tableName = uri.getPathSegments().get(0);
		Log.i(LOG_TAG, String.format("Table Name: %s", tableName));
		return tableName;
	}

	private String selection(Uri uri, String selection) {
		if (this.queryType(uri) == SINGLE_ROW) {
			String rowId = uri.getPathSegments().get(1);
			selection = KEY_ID + " = " + rowId + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
		}
		return selection;
	}
}
