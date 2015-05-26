package tw.com.aochen.photoer;

import getphoto.ImageBitmap;
import getphoto.MyPhoto;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import database.DataBaseHelper;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

public class NewAlbum extends Activity implements Serializable {

	private LinearLayout _addButton, _cancelButton, _getLocation, _layoutCover,
			_getDate;
	private RelativeLayout _coverLayout;
	private EditText _edtTitle, _edtLocation, _edtContent, _edtDate;
	private ImageView _imgCover;
	private TextView _txtCover;
	private MyGridView _gridChoosePhotos;

	private String _albumTitle, _albumLocation, _albumContent, _albumDate,
			_albumCover, _tableColumns[], _tableName;

	private DataBaseHelper dh = null;

	private ImageBitmap ib;

	private CreateThumbnail ct;

	private MyLocationListener ll;

	private Bitmap bitmap;

	private GridAdapter ga;

	private static ArrayList<ImageBitmap> photoList;

	private Boolean is_choose_cover = false;

	private Calendar calendar;

	private int year, month, day, yesterday;
	private String mSelectedDate = "";

	private final static int CHOOSE_PICTURE = 1;
	private final static int CHOOSE_PHOTO_IN_ALBUM = 2;
	final static int ADDRESS_RESULT = 0;

	private AlertDialog dialog;
	private LinearLayout alertDialogDeleteButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_album);

		initSetting();
		getCurrentDate();
		findView();
		setListener();
		setAdapter();

		ct = new CreateThumbnail(this);

	}

	private void initSetting() {

		getActionBar().hide();

		photoList = new ArrayList<ImageBitmap>();

		if (photoList.isEmpty()) {
			ib = new ImageBitmap();
			ib.thumbnail = BitmapFactory.decodeResource(getResources(),
					R.drawable.add);

			photoList.add(ib);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_album, menu);
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

	private void findView() {
		_addButton = (LinearLayout) findViewById(R.id.addButton);
		_cancelButton = (LinearLayout) findViewById(R.id.cancelButton);
		_getLocation = (LinearLayout) findViewById(R.id.newalbum_location);
		_layoutCover = (LinearLayout) findViewById(R.id.layoutCover);
		_getDate = (LinearLayout) findViewById(R.id.newalbum_Date);

		_coverLayout = (RelativeLayout) findViewById(R.id.coverLayout);

		_edtTitle = (EditText) findViewById(R.id.editTitle);
		_edtLocation = (EditText) findViewById(R.id.editLocation);
		_edtContent = (EditText) findViewById(R.id.editContent);
		_edtDate = (EditText) findViewById(R.id.editDate);

		_imgCover = (ImageView) findViewById(R.id.imageCover);

		_txtCover = (TextView) findViewById(R.id.textCover);

		_gridChoosePhotos = (MyGridView) findViewById(R.id.gridChoose);
		_gridChoosePhotos.setExpanded(true);
	}

	private void setListener() {
		_addButton.setOnClickListener(clickListener);
		_cancelButton.setOnClickListener(clickListener);
		_coverLayout.setOnClickListener(clickListener);
		_getLocation.setOnClickListener(clickListener);
		_getDate.setOnClickListener(clickListener);
		_edtDate.setOnClickListener(clickListener);

		_gridChoosePhotos.setOnItemClickListener(grid_itemClickListener);
		_gridChoosePhotos.setOnItemLongClickListener(onItemLongClickListener);
	}

	private void setAdapter() {
		ga = new GridAdapter(this, photoList);
		_gridChoosePhotos.setAdapter(ga);
	}

	private Button.OnClickListener clickListener = new Button.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.addButton:

				if (is_choose_cover) {

					if (ct.checkThumbnailExist(ct.originalPath) == false) {
						Runnable runnable = new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								ct.saveThumbnail(bitmap);
							}
						};

						Thread thread = new Thread(runnable);
						thread.run();
					}

				}

				insertData();

				photoList.remove(0);

				Intent intent = new Intent(NewAlbum.this, MainActivity.class);

				Bundle bundle = new Bundle();
				bundle.putSerializable("photoList", photoList);

				intent.putExtras(bundle);

				// intent.putExtra("list", photoList);
				startActivity(intent);
				finish();

				break;
			case R.id.cancelButton:
				finish();
				break;
			case R.id.coverLayout:
				choosePicture();
				break;
			case R.id.newalbum_location:
				ll = new MyLocationListener(NewAlbum.this);
				new NewThread().start();
				break;
			case R.id.newalbum_Date:
				showDatePicker();
				break;
			case R.id.editDate:
				showDatePicker();
				break;
			}
		}

	};

	private ListView.OnItemClickListener grid_itemClickListener = new ListView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub

			// switch (view.getId()) {
			// case R.id.gridChoose:
			//
			// break;
			// }
			System.out.println(String.valueOf(ga.getCount()));

			if (position == 0) {

				// Intent intent = new Intent();
				// intent.setType("image/*");
				//
				// intent.setAction(Intent.ACTION_GET_CONTENT);
				// intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
				// intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

				// Intent creatChooser = Intent.createChooser(intent, "?");

				Intent intent = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				intent.setType("image/*");
				/*if (Build.VERSION.SDK_INT >= 18) {
					intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
				}*/
				startActivityForResult(intent, CHOOSE_PHOTO_IN_ALBUM);
				/*
				 * if (VERSION.SDK_INT >= 18) {
				 * 
				 * startActivityForResult(intent, CHOOSE_PHOTOS_IN_ALBUM); }
				 * else { startActivityForResult(intent, CHOOSE_PHOTO_IN_ALBUM);
				 * }
				 */

			}

		}

	};

	private ListView.OnItemLongClickListener onItemLongClickListener = new ListView.OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			int mId = parent.getId();
			switch (mId) {
			case R.id.gridChoose:
				if (position != 0) {

					showAlertDialog(position);

				}

				break;
			}
			return true;
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

	private void insertData() {

		dbOpen();
		getAlbumContent();
		getDatabaseColumns();
		getDatabaseTableName();

		SQLiteDatabase db = dh.getWritableDatabase();

		ContentValues value = new ContentValues();
		value.put(_tableColumns[1], _albumTitle);
		value.put(_tableColumns[2], _albumContent);
		value.put(_tableColumns[3], _albumDate);
		value.put(_tableColumns[4], _albumLocation);
		value.put(_tableColumns[5], _albumCover);

		db.insert(_tableName, null, value);

		dbClose();

	}

	private void dbOpen() {
		dh = new DataBaseHelper(this);
	}

	private void getAlbumContent() {

		_albumTitle = _edtTitle.getText().toString();
		_albumLocation = _edtLocation.getText().toString();
		_albumContent = _edtContent.getText().toString();
		_albumDate = mSelectedDate;

		if (ct.checkThumbnailExist(ct.originalPath))
			_albumCover = ct.originalPath;
		else
			_albumCover = ct.thumbnailWholePath;

	}

	private void getDatabaseColumns() {
		int i = dh.getCount();
		_tableColumns = new String[i];
		for (int j = 0; j < i; j++) {
			_tableColumns[j] = dh.getColumns(j);
		}
	}

	private void getDatabaseTableName() {
		_tableName = dh.getTableName();
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

	private void showDatePicker() {

		DatePickerDialog dialog = new DatePickerDialog(NewAlbum.this,
				dateSetListener, year, month, day);

		dialog.show();
	}

	private void checkSelectedDate(int year, int month, int day) {

		if (this.year == year && this.month == month && this.day == day) {

			_edtDate.setText("Today");

			mSelectedDate = String.valueOf(year) + "/"
					+ String.valueOf(month + 1) + "/" + String.valueOf(day);
		} else if (this.year == year && this.month == month && yesterday == day) {

			_edtDate.setText("Yesterday");

			mSelectedDate = String.valueOf(year) + "/"
					+ String.valueOf(month + 1) + "/" + String.valueOf(day);
		} else {
			_edtDate.setText(String.valueOf(year) + "/"
					+ String.valueOf(month + 1) + "/" + String.valueOf(day));

			mSelectedDate = _edtDate.getText().toString();
		}

	}

	private void dbClose() {
		dh.close();
	}

	private void choosePicture() {
		// Intent intent = new Intent();
		// intent.setType("image/*");
		// intent.setAction(Intent.ACTION_GET_CONTENT);
		// startActivityForResult(intent, CHOOSE_PICTURE);
		Intent intent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		intent.setType("image/*");
		startActivityForResult(intent, CHOOSE_PICTURE);
	}

	private void showAlertDialog(final int position) {
		View view;

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

		alertDialogDeleteButton
				.setOnClickListener(new Button.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						// photoList.remove(position);
						Toast.makeText(NewAlbum.this, String.valueOf(position),
								Toast.LENGTH_SHORT).show();
						photoList.remove(position);

						ga.notifyDataSetChanged();
						dialog.dismiss();
						dialog = null;

					}

				});

		dialog.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case CHOOSE_PICTURE:
				Uri uri = data.getData();

				if (uri != null) {

					ct.getOriginalPathAndThumbnailName(uri);

					bitmap = ct.CreateThumbnailPicture();

					if (bitmap != null) {
						_layoutCover.setVisibility(View.INVISIBLE);
						_imgCover.setImageBitmap(bitmap);
						is_choose_cover = true;
					}
				}

				break;
			case CHOOSE_PHOTO_IN_ALBUM:

				MyPhoto mp = new MyPhoto(NewAlbum.this);

				if (mp.getPhoto(data) != null)
					photoList.add(mp.getPhoto(data));

				ga.notifyDataSetChanged();

				/*
				 * ImageBitmap ib = new ImageBitmap(); ib.thumbnail =
				 * BitmapFactory.decodeResource(getResources(),
				 * R.drawable.ic_action_new);
				 * 
				 * photoList.add(ib);
				 */

				// ga = new GridAdapter(getApplicationContext(), photoList);
				// _gridChoosePhotos.setAdapter(ga);

				break;
			}
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// ga.notifyDataSetChanged();
		// Toast.makeText(this, "Resume", Toast.LENGTH_SHORT).show();

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// Toast.makeText(this, "Pause", Toast.LENGTH_SHORT).show();
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
				_edtLocation.setText((String) msg.obj);
				break;
			}
		}

	};

}
