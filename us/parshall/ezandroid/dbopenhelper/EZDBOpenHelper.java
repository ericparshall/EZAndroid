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

package us.parshall.ezandroid.dbopenhelper;

import us.parshall.ezandroid.model.dbutility.DBUtility;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class EZDBOpenHelper extends SQLiteOpenHelper {

	public EZDBOpenHelper(Context context, String databaseName, CursorFactory cursorFactory, int databaseVersion) {
		super(context, databaseName, cursorFactory, databaseVersion);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		for (DBUtility modelDBUtility : this.getDBUtilities()) {
			modelDBUtility.createTable(db);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		for (DBUtility modelDBUtility : this.getDBUtilities()) {
			modelDBUtility.upgradeTable(db, oldVersion);
		}
	}

	public abstract DBUtility[] getDBUtilities();
}
