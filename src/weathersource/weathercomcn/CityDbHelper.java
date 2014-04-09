package weathersource.weathercomcn;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import cn.kli.utils.klilog;
import cn.kli.weather.engine.City;

class CityDbHelper extends SQLiteOpenHelper {
	//database
	static final String DB_NAME = "weather.com.cn.database";
	private static final int DB_VERSION = 1;
	
	//table city
	private static final String TABLE_CITY = "city";
	public static final String CITY_NAME = "name";
	public static final String CITY_INDEX = "_index";
	public static final String CITY_CODE = "code";
	public static final String CITY_LEVEL = "level";
	
	private Context mContext;
	
	public CityDbHelper(Context context){
		this(context, DB_NAME, null, DB_VERSION);
	}

	private CityDbHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String createTableCity = "create table " + TABLE_CITY+"("+
				CITY_NAME + " text,"+
				CITY_INDEX + " text,"+
				CITY_CODE + " text,"+
				CITY_LEVEL + " int)";
		klilog.info(createTableCity);
		db.execSQL(createTableCity);
		
	}

      
	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

	}


	
	public void addCity(MyCity city){
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(CITY_NAME, city.name);
		cv.put(CITY_INDEX, city.index);
		cv.put(CITY_CODE, city.code);
		cv.put(CITY_LEVEL, city.level);
		db.insert(TABLE_CITY, null, cv);
	}

	public MyCity getCityByIndex(String index){
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(TABLE_CITY, 
				null,
				CITY_INDEX+"=?", 
				new String[]{index}, 
				null, null, null);

		cursor.moveToFirst();
		
		MyCity city = new MyCity();
		city.name = cursor.getString(cursor.getColumnIndex(CITY_NAME));
		city.index = cursor.getString(cursor.getColumnIndex(CITY_INDEX));
		city.code = cursor.getString(cursor.getColumnIndex(CITY_CODE));
		city.level = cursor.getInt(cursor.getColumnIndex(CITY_LEVEL));
			
		return city;
	}

	public List<City> getCityList(City city){
		List<City> list = new ArrayList<City>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = null;
		
		//get cursor by query the database
		if(city == null){
			cursor = db.query(TABLE_CITY, 
					null,
					CITY_LEVEL+"=?", 
					new String[]{"1"}, 
					null, null, null);
		}else{
			MyCity myCity = getCityByIndex(city.index);
			cursor = db.query(TABLE_CITY, 
					null,
					CITY_LEVEL+"=? and "+CITY_INDEX+" like ?", 
					new String[]{String.valueOf(myCity.level+1), myCity.index+"%"}, 
					null, null, null);
		}
		
		//translate cursor to list
		list = getCityFromCursor(cursor);
		return list;
	}
	
	private List<City> getCityFromCursor(Cursor cursor){
		List<City> list = new ArrayList<City>();
		if(cursor.moveToFirst()){
			do{
				MyCity city = new MyCity();
				city.name = cursor.getString(cursor.getColumnIndex(CITY_NAME));
				city.index = cursor.getString(cursor.getColumnIndex(CITY_INDEX));
				city.code = cursor.getString(cursor.getColumnIndex(CITY_CODE));
				city.level = cursor.getInt(cursor.getColumnIndex(CITY_LEVEL));
				list.add(city);
			}while(cursor.moveToNext());
		}
		return list;
	}
}
