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

package us.parshall.ezandroid.model;

import java.util.HashMap;

import us.parshall.ezandroid.contentprovider.EZContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.EditText;

public abstract class EZModel {
	public final HashMap<String, String> values = new HashMap<String, String>();

	abstract public String tableName();

	abstract public String[] resultColumns();

	private long id = -1L;

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public EZModel(Cursor cursor) {
		this.id = cursor.getLong(cursor.getColumnIndex(EZContentProvider.KEY_ID));
		for (String columnName : this.resultColumns()) {
			this.values.put(columnName, cursor.getString(cursor.getColumnIndex(columnName)));
		}
	}

	public EZModel() {

	}

	abstract public String itemLabel();

	public void load(ContentResolver contentResolver, long id) {
		Uri uri = ContentUris.withAppendedId(Uri.withAppendedPath(EZContentProvider.CONTENT_URI, this.tableName()), id);
		Cursor query = contentResolver.query(uri, this.resultColumns(), null, null, null);
		if (query.moveToFirst()) {
			this.id = id;
			for (String columnName : this.resultColumns()) {
				this.values.put(columnName, query.getString(query.getColumnIndex(columnName)));
			}
		}
		query.close();
	}

	public void modelToView(HashMap<String, EditText> valueMap) {
		for (String key : valueMap.keySet()) {
			Log.d("EZModel.modelToView", String.format("Key: %s, EditText: %s", key, valueMap.get(key)));
			valueMap.get(key).setText(this.values.get(key));
		}
	}

	public void viewToModel(HashMap<String, EditText> valueMap) {
		for (String key : valueMap.keySet()) {
			Log.d("viewToModel", String.format("Key: %s", key));
			Log.d("viewToModel", String.format("ValueMap is null? %s", valueMap.get(key) == null));
			this.values.put(key, valueMap.get(key).getText().toString());
		}
	}

	public void save(ContentResolver contentResolver) {
		ContentValues itemValues = new ContentValues();
		for (String columnName : this.resultColumns()) {
			if (columnName != EZContentProvider.KEY_ID) {
				itemValues.put(columnName, this.values.get(columnName));
			}
		}

		if (this.id > 0) {
			Uri uri = ContentUris.withAppendedId(Uri.withAppendedPath(EZContentProvider.CONTENT_URI, this.tableName()), this.getId());
			contentResolver.update(uri, itemValues, null, null);
		} else {
			Uri uri = Uri.withAppendedPath(EZContentProvider.CONTENT_URI, this.tableName());
			contentResolver.insert(uri, itemValues);
		}
	}
}
