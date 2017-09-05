package com.SQLite.RW;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {

	private final static String DATABASE_NAME = "Subway";
	private final static int DATABASE_VERSION = 1;
	private final static String TABLE_NAME = "Demo2";
	private static SQLiteHelper helper = null;

	// 构造函数，创建数据库

	private SQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);	
	}

	// 单例的方法返回一个对象
	public static SQLiteHelper getInstance(Context context) {
		if (helper == null) {
			helper = new SQLiteHelper(context);
		}
		return helper;
	}

	// 建表
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE " + TABLE_NAME + "(id INTEGER PRIMARY KEY,"
				+ " Demo VARCHAR(50))";
		db.execSQL(sql);
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
		db.execSQL(sql);
		onCreate(db);
	}

	// 清空本地数据库中数据
	public void clearAllData() {
		SQLiteDatabase db = this.getWritableDatabase();
		String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
		db.execSQL(sql);
		onCreate(db);
	}

	// 获取游标
	public Cursor select() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
		return cursor;
	}

	// 插入一条记录
	public long insert(int Demo_ID, String DemoName) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("id", Demo_ID);
		cv.put("Demo", DemoName);
		long row = db.insert(TABLE_NAME, null, cv);
		return row;
	}

	// 根据条件查询
	public Cursor query() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
		return cursor;
	}

	// 按ID删除记录
	public void delete(String text) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = "Question = ?";
		String[] whereValue = {text };
		db.delete(TABLE_NAME, where, whereValue);
	}

	// 更新记录
	public void update(int id, int Team_ID, String Question) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = "_id = ?";
		String[] whereValue = { Integer.toString(id) };
		ContentValues cv = new ContentValues();
		cv.put("WhichTeam", Team_ID);
		cv.put("Question", Question);
		db.update(TABLE_NAME, cv, where, whereValue);
	}
}
