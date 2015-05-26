package database;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MyCursorLoader extends CursorLoader {

	private DataBaseHelper dh = null;
	private SQLiteDatabase db;
	private Context context;
	private String columns[], tableName;

	public MyCursorLoader(Context context,DataBaseHelper dh,SQLiteDatabase db) {
		super(context);
		this.context = context;
		this.dh = dh;
		this.db = db;
	}

	public Cursor loadInBackground() {

		tableName = dh.getTableName();

		int i = dh.getCount();
		columns = new String[i];
		for (int j = 0; j < i; j++) {
			columns[j] = dh.getColumns(j);
		}

		Cursor cursor = db.query(tableName, columns, null, null, null, null,
				null);

		return cursor;
	}

}
