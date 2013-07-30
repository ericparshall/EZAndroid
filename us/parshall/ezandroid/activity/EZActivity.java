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

import java.util.Stack;

import us.parshall.ezandroid.actionbar.ActionBarHelper;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

public abstract class EZActivity extends FragmentActivity {
	final ActionBarHelper mActionBarHelper = ActionBarHelper.createInstance(this);
	protected final Stack<Integer> promptDialogList = new Stack<Integer>();

	/**
	 * Returns the {@link ActionBarHelper} for this activity.
	 */
	protected ActionBarHelper getActionBarHelper() {
		return this.mActionBarHelper;
	}

	public abstract int getMenuResource();

	/** {@inheritDoc} */
	@Override
	public MenuInflater getMenuInflater() {
		return this.mActionBarHelper.getMenuInflater(super.getMenuInflater());
	}

	/** {@inheritDoc} */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mActionBarHelper.onCreate(savedInstanceState);
	}

	/** {@inheritDoc} */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		this.mActionBarHelper.onPostCreate(savedInstanceState);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return this.mActionBarHelper.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.v("ActionBarActivity", "onCreateOptionsMenu called");
		if (this.getMenuResource() > 0) {
			MenuInflater menuInflater = this.getMenuInflater();
			menuInflater.inflate(this.getMenuResource(), menu);
		}

		boolean retValue = false;
		retValue |= this.mActionBarHelper.onCreateOptionsMenu(menu);
		retValue |= super.onCreateOptionsMenu(menu);
		return retValue;
	}

	@Override
	public void invalidateOptionsMenu() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			super.invalidateOptionsMenu();
		} else {
			this.mActionBarHelper.invalidateOptionsMenu();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		this.displayPendingDialogs();
	}

	@Override
	public void onRestart() {
		super.onRestart();
		this.displayPendingDialogs();

	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		int[] tmpPromptDialogList = savedInstanceState.getIntArray("promptDialogList");
		if (tmpPromptDialogList != null) {
			Log.i("EZActivity", String.format("Number of pending dialogs %d", tmpPromptDialogList.length));
			for (int i : tmpPromptDialogList) {
				this.promptDialogList.push(i);
			}
		} else {
			Log.i("EZActivity", "tmpPromptDialogList is null");
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		this.savePromptState();
		Log.i("EZActivity", String.format("promptDialogList size: %d", this.promptDialogList.size()));
		if (this.promptDialogList.size() > 0) {
			int[] tmpPromptDialogList = new int[this.promptDialogList.size()];
			for (int i = 0; i < this.promptDialogList.size(); i++) {
				tmpPromptDialogList[i] = this.promptDialogList.get(i);
			}
			savedInstanceState.putIntArray("promptDialogList", tmpPromptDialogList);
		}
	}

	@Override
	public void onStop() {
		AlertDialog[] alertDialogList = this.alertDialogList();
		if (alertDialogList != null) {
			for (AlertDialog dialog : this.alertDialogList()) {
				if (dialog != null) {
					dialog.cancel();
				}
			}
		}
		super.onStop();
	}

	public void cancel(View view) {
		this.finish();
	}

	abstract protected void displayPendingDialogs();

	protected void savePromptState() {
		AlertDialog[] alertDialogList = this.alertDialogList();
		if (alertDialogList != null) {
			for (int i = 0; i < alertDialogList.length; i++) {
				AlertDialog dialog = alertDialogList[i];
				if (dialog != null && dialog.isShowing()) {
					this.promptDialogList.push(i);
				}
			}
		}
	}

	abstract protected AlertDialog[] alertDialogList();

	protected OnClickListener cancelDialogListener() {
		return new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}

		};
	}

	protected AlertDialog.Builder buildMessageDialog(String title, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title);

		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {

			}
		});

		builder.setMessage(message);

		return builder;
	}
}
