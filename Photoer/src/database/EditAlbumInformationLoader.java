package database;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class EditAlbumInformationLoader extends CursorLoader {

	private Context context;

	private DataBaseHelper dh;
	private String tableName, columns[];
	private String mId;

	public EditAlbumInformationLoader(Context context, String mId) {
		super(context);
		// TODO Auto-generated constructor stub

		this.context = context;
		this.mId = mId;
	}

	@Override
	public Cursor loadInBackground() {
		// TODO Auto-generated method stub

		dhOpen();
		SQLiteDatabase db = dh.getReadableDatabase();

		tableName = dh.getTableName();

		int columnCount = dh.getCount();
		columns = new String[columnCount];

		for (int i = 0; i < columnCount; i++) {
			columns[i] = dh.getColumns(i);
		}

		Cursor cursor = db.query(tableName, columns, columns[0] + "=" + mId,
				null, null, null, null);


		return cursor;
	}

	private void dhOpen() {
		dh = new DataBaseHelper(context);
	}

	private void dhClose() {
		dh.close();
	}

}
