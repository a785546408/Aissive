package subway_data;

import android.R.string;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper{

	private static final int VERSION = 1;
	public DataBaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);//调用父类函数中的构�?�函�?
		// TODO Auto-generated constructor stub
	}
	
	public DataBaseHelper(Context context, String name){
		this(context, name, VERSION);
	}

	public DataBaseHelper(Context context, String name, int version){
		this(context, name, null ,version);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
		db.execSQL("create table if not exists data(id_time int,in_max int,in_avr int,in_min int)");//创建�?张存放模拟数据的�?,如果表存在则删除重新创建
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
