package database;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MyPhotoCursorLoader extends CursorLoader {

	private DataBaseHelper dh = null;
	private SQLiteDatabase db;

	private String tableName;
	private String columns[];

	private String albumId;

	public MyPhotoCursorLoader(Context context, DataBaseHelper dh,
			SQLiteDatabase db, String albumId) {
		super(context);
		// TODO Auto-generated constructor stub

		this.dh = dh;
		this.db = db;

		this.albumId = albumId;

		getColumns();
	}

	@Override
	public Cursor loadInBackground() {
		// TODO Auto-generated method stub
		
		Cursor cursor = db.query(tableName, columns, columns[3] + "=" + albumId, null, null, null,
				null);
		return cursor;
	}

	private void getColumns() {
		tableName = dh.getPhotoTableName();

		int columnsCount = dh.getPhotoCount();

		columns = new String[columnsCount];

		for (int i = 0; i < columnsCount; i++) {
			columns[i] = dh.getPhotoColumns(i);
		}

	}

}
