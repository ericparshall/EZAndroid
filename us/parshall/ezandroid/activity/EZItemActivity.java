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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public abstract class EZItemActivity extends EZActivity {
	public static final String SELECTED_ID = "SELECTED_ID";
	protected long selectedId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("EZItemActivity.onCreate", "onCreate called");
		if (this.findViewById(android.R.id.content).getRootView() != null) {
			this.bindModelToView();
		}

		Intent intent = this.getIntent();
		this.selectedId = intent.getLongExtra(SELECTED_ID, -1L);
	}

	abstract protected void bindModelToView();
}
