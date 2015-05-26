package tw.com.aochen.photoer;

import java.util.ArrayList;
import java.util.Calendar;

import tw.com.aochen.photoer.NewAlbum.NewThread;

import getphoto.AlbumInformation;
import getphoto.ImageBitmap;
import getphoto.MyPhoto;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import database.DataBaseHelper;
import database.EditAlbumInformationLoader;
import database.MyPhotoCursorLoader;

public class EditAlbum extends Activity implements LoaderCallbacks<Cursor> {

	private final static int GET_ALBUM_INFORMATION = 0;
	private final static int CHOOSE_COVER = 1;
	private final static int ADDRESS_RESULT = 2;
	private final static int GET_PHOTOS_IN_THIS_ALBUM = 3;
	private final static int CHOOSE_PHOTOS = 4;

	private int positionInList;

	private String mId, mCover, mTitle, mLocation, mDate, mContent;

	private ImageView imageCover;
	private EditText edtTitle, edtLocation, edtDate, edtContent;
	private LinearLayout buttonCancel, buttonSave, buttonLocation, buttonDate,
			alertDialogEditButton, alertDialogDeleteButton;
	private RelativeLayout buttonCover;
	private MyGridView gridPhotos;

	private AlbumInformation ai;
	private DataBaseHelper dh;
	private Cursor cursor;

	private CreateThumbnail ct;
	private Bitmap saveThumbnail;

	private MyLocationListener ll;

	private Calendar calendar;
	private int year, month, day, yesterday;
	private String mSelectedDate = "";

	private Boolean changeCover = false;

	private String list[][];

	private AlertDialog dialog;

	private ArrayList<ImageBitmap> photoList, deletedList;
	private GridAdapter ga;
	private ImageBitmap ib;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_album);

		initialSetting();
		findView();
		setListener();
		getCurrentDate();
		setAdapter();

		getLoaderManager().initLoader(GET_ALBUM_INFORMATION, null, this);
		getLoaderManager().initLoader(GET_PHOTOS_IN_THIS_ALBUM, null, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_album, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void initialSetting() {

		ActionBar actionBar = getActionBar();
		actionBar.hide();

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mId = bundle.getString("_id");
		}

		photoList = new ArrayList<ImageBitmap>();
		if (photoList.isEmpty()) {
			ib = new ImageBitmap();

			ib.isNew = false;
			ib.thumbnail = BitmapFactory.decodeResource(getResources(),
					R.drawable.add);

			photoList.add(ib);
		}

		ai = new AlbumInformation();

		deletedList = new ArrayList<ImageBitmap>();
	}

	private void getAlbumInformation() {

		if (cursor.getString(1).isEmpty() == false) {
			edtTitle.setText(cursor.getString(1));
		}

		if (cursor.getString(2).isEmpty() == false) {
			edtContent.setText(cursor.getString(2));
		}

		if (cursor.getString(3).isEmpty() == false) {
			edtDate.setText(cursor.getString(3));
			mSelectedDate = cursor.getString(3);
		}

		if (cursor.getString(4).isEmpty() == false) {
			edtLocation.setText(cursor.getString(4));
		}

		if (cursor.getString(5) != null) {

			Bitmap bitmap = BitmapFactory.decodeFile(cursor.getString(5));

			imageCover.setImageBitmap(bitmap);
		}

	}

	private void findView() {

		imageCover = (ImageView) findViewById(R.id.edit_album_imageCover);

		buttonCover = (RelativeLayout) findViewById(R.id.edit_album_coverLayout);

		edtTitle = (EditText) findViewById(R.id.edit_album_editTitle);
		edtLocation = (EditText) findViewById(R.id.edit_album_editLocation);
		edtDate = (EditText) findViewById(R.id.edit_album_editDate);
		edtContent = (EditText) findViewById(R.id.edit_album_editContent);

		buttonCancel = (LinearLayout) findViewById(R.id.edit_album_cancelButton);
		buttonSave = (LinearLayout) findViewById(R.id.edit_album_addButton);
		buttonLocation = (LinearLayout) findViewById(R.id.edit_album_newalbum_location);
		buttonDate = (LinearLayout) findViewById(R.id.edit_album_newalbum_Date);

		gridPhotos = (MyGridView) findViewById(R.id.edit_album_gridChoose);
		gridPhotos.setExpanded(true);
	}

	private void setListener() {
		buttonCancel.setOnClickListener(clickListener);
		buttonSave.setOnClickListener(clickListener);
		buttonCover.setOnClickListener(clickListener);
		buttonLocation.setOnClickListener(clickListener);
		buttonDate.setOnClickListener(clickListener);

		gridPhotos.setOnItemLongClickListener(itemLongClickListener);

		gridPhotos.setOnItemClickListener(itemClickListener);

	}

	private void setAdapter() {

		ga = new GridAdapter(this, photoList);

		gridPhotos.setAdapter(ga);
	}

	/*
	 * get current date and yesterday date
	 */
	private void getCurrentDate() {

		calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());

		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);

		calendar.add(Calendar.DAY_OF_MONTH, -1);
		yesterday = calendar.get(Calendar.DAY_OF_MONTH);
	}

	private Button.OnClickListener clickListener = new Button.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			switch (v.getId()) {

			case R.id.edit_album_coverLayout:

				Intent intent = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				intent.setType("image/*");
				startActivityForResult(intent, CHOOSE_COVER);

				break;

			case R.id.edit_album_cancelButton:
				finish();
				break;
			case R.id.edit_album_addButton:

				dhOpen();
				deletePhoto();
				insertDataToAlbumTable();
				insertDataToPhotoTable();
				dhClose();

				finish();

				break;
			case R.id.edit_album_newalbum_Date:

				showDatePicker();

				break;
			case R.id.edit_album_newalbum_location:

				ll = new MyLocationListener(EditAlbum.this);
				new NewThread().start();

				break;

			/*
			 * AlertDialog: Edit
			 * 
			 * case R.id.alertDialogButton1:
			 * 
			 * 
			 * 
			 * break;
			 */

			/*
			 * AlertDialog: Delete, Copy value which you want to delete to
			 * deletedList
			 */
			case R.id.alertDialogButton2:

				deletedList.add(photoList.get(positionInList));

				photoList.remove(positionInList);
				ga.notifyDataSetChanged();

				dialog.dismiss();

				break;
			}
		}

	};

	private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub
			checkSelectedDate(year, monthOfYear, dayOfMonth);
		}
	};

	private ListView.OnItemLongClickListener itemLongClickListener = new ListView.OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			if (position != 0) {

				createAlertDialog(view);
				positionInList = position;

				return true;
			}

			return false;
		}

	};

	private ListView.OnItemClickListener itemClickListener = new ListView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub

			if (position == 0) {

				Intent intent = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				intent.setType("image/*");
				startActivityForResult(intent, CHOOSE_PHOTOS);

			}
		}

	};

	private void showDatePicker() {

		DatePickerDialog dialog = new DatePickerDialog(this, dateSetListener,
				year, month, day);

		dialog.show();
	}

	private void checkSelectedDate(int year, int month, int day) {

		if (this.year == year && this.month == month && this.day == day) {

			edtDate.setText("Today");

			mSelectedDate = String.valueOf(year) + "/"
					+ String.valueOf(month + 1) + "/" + String.valueOf(day);
		} else if (this.year == year && this.month == month && yesterday == day) {

			edtDate.setText("Yesterday");

			mSelectedDate = String.valueOf(year) + "/"
					+ String.valueOf(month + 1) + "/" + String.valueOf(day);
		} else {
			edtDate.setText(String.valueOf(year) + "/"
					+ String.valueOf(month + 1) + "/" + String.valueOf(day));

			mSelectedDate = edtDate.getText().toString();
		}

	}

	private void createAlertDialog(View view) {

		dialog = new AlertDialog.Builder(this).create();

		LayoutInflater inflater = getLayoutInflater();

		view = inflater.inflate(R.layout.custom_alert_dialog, null);

		dialog.setView(view);
		dialog.setTitle(R.string.alert_dialog_title);

		LinearLayout layout = (LinearLayout) view
				.findViewById(R.id.alert_layout);
		layout.removeView(view.findViewById(R.id.alertDialogButton1));
		layout.removeView(view.findViewById(R.id.view1));

		alertDialogDeleteButton = (LinearLayout) view
				.findViewById(R.id.alertDialogButton2);

		alertDialogDeleteButton.setOnClickListener(clickListener);

		dialog.show();
	}

	private void deletePhoto() {

		String rowId = "";

		for (int i = 0; i < deletedList.size(); i++) {

			rowId = deletedList.get(i).photoTableId;
			dh.deletePhoto(rowId);
		}

	}

	private void insertDataToAlbumTable() {

		ai.Title = edtTitle.getText().toString();
		ai.Content = edtContent.getText().toString();
		ai.Date = mSelectedDate;
		ai.Location = edtLocation.getText().toString();

		if (changeCover) {
			if (ct.checkThumbnailExist(ct.originalPath))
				ai.Cover = ct.originalPath;
			else {
				ct.saveThumbnail(saveThumbnail);
				ai.Cover = ct.thumbnailWholePath;
			}
		}

		if (dh.Update(mId, ai, changeCover))
			Toast.makeText(EditAlbum.this,
					R.string.toast_edit_album_sucessfully, Toast.LENGTH_SHORT)
					.show();
	}

	private void insertDataToPhotoTable() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				SQLiteDatabase db = dh.getWritableDatabase();

				String columns[] = new String[dh.getPhotoCount()];

				for (int i = 1; i < photoList.size(); i++) {

					if (photoList.get(i).isNew) {

						ContentValues value = new ContentValues();

						for (int count = 1; count < dh.getPhotoCount(); count++) {

							columns[count] = dh.getPhotoColumns(count);

							switch (count) {
							case 1:
								value.put(columns[count], photoList.get(i).uri);
								break;
							case 2:
								value.put(columns[count], photoList.get(i).mId);
								break;

							case 3:
								value.put(columns[count], mId);
							}
						}

						db.insert(dh.getPhotoTableName(), null, value);

					} else {

					}
				}
			}

		}).start();
	}

	private void dhOpen() {
		dh = new DataBaseHelper(this);
	}

	private void dhClose() {
		dh.close();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case CHOOSE_COVER:

				if (data != null) {

					Uri uri = data.getData();

					changeCover = true;

					Bitmap bitmap = null;

					ct = new CreateThumbnail(this);

					ct.getOriginalPathAndThumbnailName(uri);
					bitmap = ct.CreateThumbnailPicture();

					if (bitmap != null) {
						imageCover.setImageBitmap(bitmap);
						saveThumbnail = bitmap;
					}

				}

				break;

			case CHOOSE_PHOTOS:

				MyPhoto mp = new MyPhoto(this);
				ib = new ImageBitmap();

				// String rowId = photoList.get(positionInList).photoTableId;

				Uri uri = data.getData();

				ib.uri = uri.toString();
				ib.mId = mp.getPhotoId(uri);
				ib.thumbnail = mp.getThumbnail(uri.toString());
				ib.isNew = true;

				photoList.add(ib);

				/*
				 * photoList.get(positionInList).uri = uri.toString();
				 * photoList.get(positionInList).mId = mp.getPhotoId(uri);
				 * photoList.get(positionInList)
				 */

				ga.notifyDataSetChanged();

				break;
			}

		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// TODO Auto-generated method stub
		switch (id) {
		case GET_ALBUM_INFORMATION:
			return new EditAlbumInformationLoader(EditAlbum.this, mId);

		case GET_PHOTOS_IN_THIS_ALBUM:
			dhOpen();
			return new MyPhotoCursorLoader(EditAlbum.this, dh,
					dh.getReadableDatabase(), mId);
		}

		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// TODO Auto-generated method stub

		switch (loader.getId()) {
		case GET_ALBUM_INFORMATION:

			cursor = data;
			cursor.moveToFirst();

			getAlbumInformation();
			break;
		case GET_PHOTOS_IN_THIS_ALBUM:

			data.moveToFirst();

			for (int i = 0; i < data.getCount(); i++) {
				ib = new ImageBitmap();

				ib.photoTableId = data.getString(0);
				ib.uri = data.getString(1);
				ib.mId = Long.parseLong(data.getString(2));
				ib.thumbnail = new MyPhoto(EditAlbum.this).getThumbnail(ib.uri);
				ib.isNew = false;

				photoList.add(ib);

				data.moveToNext();

			}

			ga.notifyDataSetChanged();

			break;
		}

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub

	}

	/*
	 * 新增Thread來讀取所在的地區
	 */
	class NewThread extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			Looper.prepare();

			Message message = new Message();

			message.what = ADDRESS_RESULT;
			message.obj = ll.getAddress();

			handler.sendMessage(message);

			super.run();
		}

	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub

			switch (msg.what) {
			case ADDRESS_RESULT:
				edtLocation.setText((String) msg.obj);
				break;
			}
		}

	};

}
