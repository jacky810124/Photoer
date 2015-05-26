package tw.com.aochen.photoer;

import getphoto.ImageBitmap;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import database.DataBaseHelper;
import database.MyCursorLoader;

public class MainActivity extends Activity implements LoaderCallbacks<Cursor> {

	private ListView mainList;

	private LinearLayout mainNewAlbumButton, noAlbumText;
	//
	// §PÂ_ª¬ºA
	//
	private Boolean _internetStatus = false;

	private int positionInList;

	private DataBaseHelper dh = null;

	private SQLiteDatabase db;

	private MyBaseAdapter myBaseAdapter;
	private TitleBaseAdapter tb;

	private Cursor cursor = null;

	private final static int GET_DATA_BY_CURSORLOADER = 1;

	private final static String FIRST_TIME = "FIRST_TIME";
	private final static String FIRST_TIME_DRAWER_LIST = "FIRST_TIME_DRAWER_LIST";

	private ArrayList<ImageBitmap> photoList;

	private AlertDialog dialog;
	private LinearLayout alertDialogEditButton, alertDialogDeleteButton;

	private DrawerLayout mDrawer;
	private ListView mDrawerList;
	private ImageView drawerHeader;

	private static boolean isInsertData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// preSetting();
		// checkStatus();
		// showStatus();
		dbOpen();
		findView();
		setListener();
		initSetting();

	}

	private void initSetting() {

		getActionBar().hide();

		getLoaderManager().initLoader(GET_DATA_BY_CURSORLOADER, null, this);

		showcaseView1();

		// isInsertData = false;
		
		View header = getLayoutInflater().inflate(R.layout.drawer_list_header, null);
		drawerHeader = (ImageView)header.findViewById(R.id.drawerHeaderImage1);
		drawerHeader.setOnClickListener(onClickListener);
		
		mDrawerList.addHeaderView(header);

	}

	private void showcaseView1() {
		/*
		 * SharedPreferences can let you know is this app have executed
		 */
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);

		boolean isFirstTime = preferences.getBoolean(FIRST_TIME, true);
		if (isFirstTime) {

			ShowcaseView showcaseView = new ShowcaseView.Builder(this)
					.setTarget(new ViewTarget(R.id.mainNewAlbumButton, this))
					.setContentTitle(R.string.showcaseview_new_album_title)
					.setContentText(R.string.showcaseview_new_album_content)
					.hideOnTouchOutside()
					.setShowcaseEventListener(new OnShowcaseEventListener() {

						@Override
						public void onShowcaseViewHide(ShowcaseView showcaseView) {
							// TODO Auto-generated method stub
							showcaseView2();
						}

						@Override
						public void onShowcaseViewDidHide(
								ShowcaseView showcaseView) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onShowcaseViewShow(ShowcaseView showcaseView) {
							// TODO Auto-generated method stub

						}
					}).build();

			showcaseView.hideButton();

			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean(FIRST_TIME, false);
			editor.apply();
		}

	}

	private void showcaseView2() {
		/*
		 * SharedPreferences can let you know is this app have executed //
		 */
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);

		boolean isFirstTime = preferences.getBoolean(FIRST_TIME_DRAWER_LIST,
				true);
		if (isFirstTime) {

			ShowcaseView showcaseView = new ShowcaseView.Builder(this)
					.setStyle(R.layout.showcase_button).hideOnTouchOutside()
					.setContentTitle(R.string.showcaseview_show_drawer_title)
					.setContentText(R.string.showcaseview_show_drawer_content)
					.build();

			showcaseView.hideButton();
			showcaseView.setBackgroundResource(R.drawable.swipe_right);

			// RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(
			// LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			// rl.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			// rl.addRule(RelativeLayout.ALIGN_BOTTOM);
			// showcaseView.setButtonPosition(rl);
			// showcaseView.setBackgroundResource(R.drawable.swipe_right);

			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean(FIRST_TIME_DRAWER_LIST, false);
			editor.apply();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		// int id = item.getItemId();
		// if (id == R.id.action_settings) {
		// return true;
		// }
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		case R.id.new_album:
			Intent intent = new Intent(this, NewAlbum.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void preSetting() {
		//
		// ActionBar change color
		//
		ActionBar actionbar = getActionBar();
		actionbar.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor("#255CAD")));
		//
		// // ³]ActionBar¬°³z©ú
		// actionbar.setBackgroundDrawable(new ColorDrawable(
		// android.R.color.transparent));
		// // ÁôÂÃHome icon©MTitle text
		// actionbar.setDisplayShowHomeEnabled(false);
		// actionbar.setDisplayShowTitleEnabled(false);
		//
		// LayoutInflater inflater = LayoutInflater.from(this);
		// View customView = inflater.inflate(R.layout.actionbar_layout, null);
		// TextView t1 = (TextView) customView.findViewById(R.id.textView1);
		// t1.setText("Photoer");
		//
		// actionbar.setCustomView(customView);
		// actionbar.setDisplayShowCustomEnabled(true);
	}

	private void checkStatus() {
		//
		// §PÂ_ª¬ºA
		//
		// Network status
		ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) {
			_internetStatus = true;
		}
	}

	private void showStatus() {
		if (_internetStatus)
			Toast.makeText(getApplicationContext(),
					"Network service is avilable", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(getApplicationContext(),
					"Network service isn't avilable", Toast.LENGTH_SHORT)
					.show();
	}

	private void findView() {

		mainList = (ListView) findViewById(R.id.mainList);

		mainNewAlbumButton = (LinearLayout) findViewById(R.id.mainNewAlbumButton);
		noAlbumText = (LinearLayout) findViewById(R.id.noAlbum);

		mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
	}

	private void setListener() {

		mainList.setOnItemClickListener(onItemClickListener);
		mainList.setOnItemLongClickListener(onItemLongClickListener);

		mainNewAlbumButton.setOnClickListener(onClickListener);

		mDrawer.setDrawerListener(drawerListener);
		mDrawerList.setOnItemClickListener(onItemClickListener);
	}

	private Button.OnClickListener onClickListener = new Button.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.mainNewAlbumButton:
				Intent intent = new Intent(MainActivity.this, NewAlbum.class);
				startActivity(intent);
				break;
			case R.id.alertDialogButton1:

				cursor.moveToPosition(positionInList);

				intent = new Intent(MainActivity.this, EditAlbum.class);

				Bundle bundle = new Bundle();
				bundle.putString("_id", cursor.getString(0));

				intent.putExtras(bundle);
				startActivity(intent);

				dialog.dismiss();

				break;
			case R.id.alertDialogButton2:

				cursor.moveToPosition(positionInList);
				dbOpen();

				if (dh.Delete(Integer.parseInt(cursor.getString(0))))
					Toast.makeText(MainActivity.this,
							R.string.toast_delete_album_sucessfully,
							Toast.LENGTH_SHORT).show();

				dbClose();

				dialog.dismiss();

				onResume();
				break;
			case R.id.drawerHeaderImage1:
				
				intent = new Intent(MainActivity.this, AboutPhotoer.class);
				startActivity(intent);
				break;
			}
		}
	};

	private ListView.OnItemClickListener onItemClickListener = new ListView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			
			int resourceId = parent.getId();
			if(resourceId == R.id.left_drawer){
				position -= 1;
			}

			if (cursor != null) {

				cursor.moveToPosition(position);

				Intent intent = new Intent(MainActivity.this, MyAlbum.class);
				Bundle bundle = new Bundle();

				bundle.putString("_id", cursor.getString(0));
				bundle.putString("album_title", cursor.getString(1));
				bundle.putString("album_content", cursor.getString(2));
				bundle.putString("album_date", cursor.getString(3));
				bundle.putString("album_cover", cursor.getString(5));
				bundle.putString("album_location", cursor.getString(4));

				intent.putExtras(bundle);
				startActivity(intent);

			}
		}

	};

	private ListView.OnItemLongClickListener onItemLongClickListener = new ListView.OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub

			positionInList = position;

			createAlertDialog(view);

			return true;
		}

	};

	private DrawerLayout.DrawerListener drawerListener = new DrawerListener() {

		@Override
		public void onDrawerStateChanged(int arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onDrawerSlide(View arg0, float arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onDrawerOpened(View arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onDrawerClosed(View arg0) {
			// TODO Auto-generated method stub

		}
	};

	private void dbOpen() {
		dh = new DataBaseHelper(this);
		db = dh.getReadableDatabase();
	}

	private void dbClose() {
		dh.close();
		db.close();
	}

	private void getDataFromPreviousActivity() {

		Bundle bundle = getIntent().getExtras();

		if (bundle != null && bundle.isEmpty() == false) {
			photoList = new ArrayList<ImageBitmap>();
			photoList = (ArrayList<ImageBitmap>) bundle
					.getSerializable("photoList");

			if (photoList.size() > 0) { // && isInsertData == false) {
				insertData();
			}
			getIntent().removeExtra("photoList");
		}

	}

	private void insertData() {

		cursor.moveToLast();

		int lastIndex = Integer.parseInt(cursor.getString(0));

		int count = photoList.size();

		dbOpen();
		db = dh.getWritableDatabase();

		int columnsCount = dh.getPhotoCount();

		String tableName = dh.getPhotoTableName();
		String tableColumns[] = new String[columnsCount];

		for (int j = 0; j < columnsCount; j++) {
			tableColumns[j] = dh.getPhotoColumns(j);
		}

		ContentValues value = new ContentValues();

		for (int i = 0; i < count; i++) {
			value.put(tableColumns[1], photoList.get(i).uri);
			value.put(tableColumns[2], photoList.get(i).mId);
			value.put(tableColumns[3], lastIndex);

			db.insert(tableName, null, value);
		}
		dbClose();
		photoList.clear();
		photoList = null;

		// isInsertData = true;
	}

	private void createAlertDialog(View view) {

		dialog = new AlertDialog.Builder(this).create();

		LayoutInflater inflater = getLayoutInflater();

		view = inflater.inflate(R.layout.custom_alert_dialog, null);

		dialog.setView(view);
		dialog.setTitle(R.string.alert_dialog_title);

		alertDialogEditButton = (LinearLayout) view
				.findViewById(R.id.alertDialogButton1);
		alertDialogDeleteButton = (LinearLayout) view
				.findViewById(R.id.alertDialogButton2);

		alertDialogEditButton.setOnClickListener(onClickListener);
		alertDialogDeleteButton.setOnClickListener(onClickListener);

		dialog.show();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// TODO Auto-generated method stub
		return new MyCursorLoader(getApplication(), dh, db);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// TODO Auto-generated method stub

		/*
		 * Show no-album hint in MainActivity
		 */
		if (data.getCount() > 0) {

			noAlbumText.setVisibility(View.INVISIBLE);

			cursor = data;
			data.moveToFirst();

		} else {
			noAlbumText.setVisibility(View.VISIBLE);

		}
		// data.moveToLast();
		// Toast.makeText(this, "The last index is: " + data.getString(0),
		// Toast.LENGTH_SHORT).show();

		myBaseAdapter = new MyBaseAdapter(this, data);

		mainList.setAdapter(myBaseAdapter);

		tb = new TitleBaseAdapter(this, data);
		mDrawerList.setAdapter(tb);

		getDataFromPreviousActivity();

		dbClose();

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		dbOpen();

		getLoaderManager().restartLoader(GET_DATA_BY_CURSORLOADER, null, this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		dbClose();
		getLoaderManager().destroyLoader(GET_DATA_BY_CURSORLOADER);
	}

}
