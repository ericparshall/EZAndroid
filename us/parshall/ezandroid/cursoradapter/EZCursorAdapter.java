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

package us.parshall.ezandroid.cursoradapter;

import us.parshall.ezandroid.activity.EZItemListActivity;
import us.parshall.ezandroid.model.EZModel;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public abstract class EZCursorAdapter extends CursorAdapter {
	private EZItemListActivity itemListActivity;

	public EZCursorAdapter(Context context, Cursor cursor, int flags) {
		super(context, cursor, flags);
	}

	public EZCursorAdapter(Context context, Cursor cursor, int flags, EZItemListActivity itemListActivity) {
		this(context, cursor, flags);
		this.itemListActivity = itemListActivity;
	}

	public EZItemListActivity getItemListActivity() {
		return this.itemListActivity;
	}

	abstract protected EZModel newItem(Cursor cursor);

	abstract public Class<?> getItemIntentClass();

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
		LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
		View retView = inflater.inflate(android.R.layout.simple_list_item_activated_1, viewGroup, false);
		return retView;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		final EZModel ezModel = this.newItem(cursor);
		TextView textView = (TextView) view.findViewById(android.R.id.text1);
		textView.setText(ezModel.itemLabel());
	}

}
