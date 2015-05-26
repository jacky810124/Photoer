package database;

import getphoto.AlbumInformation;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {

	private final static int dbVersion = 1;
	private final static String dbName = "PhotoerDatabase";

	private final static String tableName = "AlbumTable";
	private String tableColumns[] = { "_id", "Title", "Content", "Date",
			"Location", "Cover" };

	private final static String photoTableName = "PhotoTable";
	private String photoTableColumns[] = { "_id", "Uri", "PhotoId", "AlbumId" };

	private SQLiteDatabase db;

	public DataBaseHelper(Context context) {
		super(context, dbName, null, dbVersion);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sqlCommand = "CREATE TABLE IF NOT EXISTS " + tableName
				+ "( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + tableColumns[1]
				+ " TEXT, " + tableColumns[2] + " TEXT, " + tableColumns[3]
				+ " TEXT, " + tableColumns[4] + " TEXT, " + tableColumns[5]
				+ " TEXT);";
		db.execSQL(sqlCommand);

		sqlCommand = "CREATE TABLE IF NOT EXISTS " + photoTableName
				+ "( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ photoTableColumns[1] + " TEXT, " + photoTableColumns[2]
				+ " TEXT, " + photoTableColumns[3] + " TEXT);";
		db.execSQL(sqlCommand);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		String sqlCommand = "DROP TABLE IF EXISTS " + tableName;
		db.execSQL(sqlCommand);

		sqlCommand = "DROP TABLE IF EXISTS " + photoTableName;
		db.execSQL(sqlCommand);

		onCreate(db);
	}

	public Boolean Delete(int rowId) {
		db = this.getWritableDatabase();
		db.delete("AlbumTable", "_id=" + String.valueOf(rowId), null);
		db.delete("PhotoTable", "AlbumId=" + String.valueOf(rowId), null);

		db.close();
		return true;
	}

	public Boolean deletePhoto(String rowId) {

		db = this.getWritableDatabase();
		db.delete("PhotoTable", "_id=" + rowId, null);

		db.close();

		return true;
	}

	public Boolean Update(String rowId, AlbumInformation ai, Boolean changeCover) {

		db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(tableColumns[1], ai.Title);
		values.put(tableColumns[2], ai.Content);
		values.put(tableColumns[3], ai.Date);
		values.put(tableColumns[4], ai.Location);

		if (changeCover)
			values.put(tableColumns[5], ai.Cover);

		db.update(tableName, values, "_id=" + rowId, null);
		db.close();
		return true;
	}

	public Boolean updatePhotoTable(String rowId, String uri, long photoId) {

		db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(photoTableColumns[1], uri);
		values.put(photoTableColumns[2], String.valueOf(photoId));

		db.update(photoTableName, values, "_id=" + rowId, null);
		db.close();
		return true;
	}

	public int getCount() {
		return tableColumns.length;// return 5
	}

	public String getColumns(int index) {
		return tableColumns[index];
	}

	public String getTableName() {
		return tableName;
	}

	public int getPhotoCount() {
		return photoTableColumns.length;
	}

	public String getPhotoColumns(int index) {
		return photoTableColumns[index];
	}

	public String getPhotoTableName() {
		return photoTableName;
	}

}
