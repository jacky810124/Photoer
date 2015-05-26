package tw.com.aochen.photoer;

import getphoto.ImageBitmap;
import getphoto.MyPhoto;

import java.util.ArrayList;

import database.DataBaseHelper;
import database.MyPhotoCursorLoader;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

public class MyAlbum extends Activity implements LoaderCallbacks<Cursor> {

	private TextView textTitle, textLocation, textContent, textDate;
	private ImageView imageCover;
	private MyGridView gridView;
	private LinearLayout menuButton;

	private AlertDialog dialog, menuDialog;
	private Bitmap bitmap;
	private Boolean isDialogOpen = false;

	private String albumId, imageUri;
	private static String shareContent;

	private DataBaseHelper dh = null;
	private SQLiteDatabase db;
	private String columns[];
	private String tableName;

	private ArrayList<ImageBitmap> photoList;

	private GridAdapter ga;

	private final static int GET_DATA = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_album);

		ActionBar actionbar = getActionBar();
		actionbar.hide();

		findView();
		setDataOnPreviousActivity();

		getLoaderManager().initLoader(GET_DATA, null, this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my_album, menu);
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

		textTitle = (TextView) findViewById(R.id.myalbum_title);
		textContent = (TextView) findViewById(R.id.myalbum_content);
		textLocation = (TextView) findViewById(R.id.myalbum_location);
		textDate = (TextView) findViewById(R.id.myalbum_date);

		imageCover = (ImageView) findViewById(R.id.myalbum_cover);

		gridView = (MyGridView) findViewById(R.id.gridView1);
		gridView.setExpanded(true);
		gridView.setOnItemClickListener(itemClickListener);

		menuButton = (LinearLayout) findViewById(R.id.myalbum_menuButton);
		menuButton.setOnClickListener(clickListener);
	}

	private ListView.OnItemClickListener itemClickListener = new ListView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			displayImageWithAlertDialog(position);
		}

	};

	private Button.OnClickListener clickListener = new Button.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int id = v.getId();

			switch (id) {
			case R.id.alert_display_image_cancelButton1:

				if (isDialogOpen) {
					if (bitmap.isRecycled() == false)
						bitmap.recycle();
					dialog.dismiss();
					dialog = null;
					isDialogOpen = !isDialogOpen;
				}

				break;
			case R.id.alert_display_imageView1:

				if (isDialogOpen) {
					if (bitmap.isRecycled() == false)
						bitmap.recycle();
					dialog.dismiss();
					dialog = null;
					isDialogOpen = !isDialogOpen;
				}

				break;

			case R.id.myalbum_menuButton:

				if (showMenuDialog())
					menuDialog.show();
				break;

			case R.id.alertDialogButton1:
				Intent intent = new Intent(MyAlbum.this, EditAlbum.class);

				Bundle bundle = new Bundle();
				bundle.putString("_id", albumId);

				intent.putExtras(bundle);
				startActivity(intent);

				menuDialog.dismiss();
				menuDialog = null;

				finish();
				break;

			case R.id.alertDialogButton2:
				DataBaseHelper dh = new DataBaseHelper(MyAlbum.this);

				if (dh.Delete(Integer.parseInt(albumId)))
					Toast.makeText(MyAlbum.this,
							R.string.toast_delete_album_sucessfully,
							Toast.LENGTH_SHORT).show();

				dh.close();
				dh = null;

				menuDialog.dismiss();
				menuDialog = null;

				finish();

				break;
			case R.id.alertDialogButton3:

				if (shareContent()) {
					menuDialog.dismiss();
					menuDialog = null;

				}
				break;
			}

		}

	};

	private void setDataOnPreviousActivity() {
		/*
		 * 
		 * Check data == null ?
		 */

		Bundle bundle = this.getIntent().getExtras();

		if (bundle.getString("album_title").isEmpty()) {
			textTitle.setText(R.string.my_album_untitled_album);
		} else {
			textTitle.setText(bundle.getString("album_title"));
		}

		if (bundle.getString("album_content").isEmpty()) {
			textContent.setText(R.string.my_album_no_content);
		} else {
			textContent.setText(bundle.getString("album_content"));
			shareContent = textContent.getText().toString();
		}

		if (bundle.getString("album_date").isEmpty()) {
			textDate.setText(R.string.my_album_no_date);
		} else {
			textDate.setText(bundle.getString("album_date"));
		}

		if (bundle.getString("album_location").isEmpty()) {
			textLocation.setText(R.string.my_album_no_location);
		} else {
			textLocation.setText(bundle.getString("album_location"));
		}

		if (bundle.getString("album_cover") == null) {
			imageCover.setImageResource(R.drawable.cover);
		} else {
			imageUri = bundle.getString("album_cover");
			Bitmap bitmap = BitmapFactory.decodeFile(imageUri);
			if (bitmap != null)
				imageCover.setImageBitmap(bitmap);
		}

		albumId = bundle.getString("_id");
		// Toast.makeText(this, "This album's id is :" +
		// bundle.getString("_id"),
		// Toast.LENGTH_LONG).show();
	}

	private Boolean shareContent() {

		// Boolean noImage = true;
		// String chooserTitle;
		ArrayList<Uri> files = new ArrayList<Uri>();

		if (photoList != null) {
			int size = photoList.size();
			if (size > 0) {

				for (int i = 0; i < size; i++) {
					files.add(Uri.parse(photoList.get(i).uri));
				}
				// noImage = false;
			}
		}

		Intent intent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);

		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, textTitle.getText());

		String shareContent = "";
		shareContent = "Album Title: " + textTitle.getText().toString() + "\n"
				+ "\n" + "Album Information: "
				+ textLocation.getText().toString() + "  |  "
				+ textDate.getText().toString() + "\n" + "\n"
				+ "----------------------------" + "\n" + "\n"
				+ textContent.getText() + "\n" + "\n"
				+ "----------------------------" + "\n"
				+ "By Photoer. @facebook/PhotoerTw";
		intent.putExtra(Intent.EXTRA_TEXT, shareContent);

		// if (noImage == false) {
		//
		// }

		intent.setType("image/*");
		intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);

		startActivity(intent);

		return true;
	}

	private Boolean showMenuDialog() {

		View view;
		LayoutInflater inflater = getLayoutInflater();

		menuDialog = new AlertDialog.Builder(this).create();
		view = inflater.inflate(R.layout.custom_alert_dialog, null);
		menuDialog.setView(view);
		menuDialog.setTitle(R.string.alert_dialog_title);

		LinearLayout shareButton, editButton, deleteButton;
		shareButton = (LinearLayout) view.findViewById(R.id.alertDialogButton3);
		deleteButton = (LinearLayout) view
				.findViewById(R.id.alertDialogButton2);
		editButton = (LinearLayout) view.findViewById(R.id.alertDialogButton1);

		shareButton.setOnClickListener(clickListener);
		editButton.setOnClickListener(clickListener);
		deleteButton.setOnClickListener(clickListener);

		return true;
	}

	private void displayImageWithAlertDialog(int position) {

		String path;
		int postion = position;
		Uri uri;
		View view;

		dialog = new AlertDialog.Builder(this).create();

		LayoutInflater inflater = getLayoutInflater();
		view = inflater.inflate(R.layout.display_image_alert_dialog, null);
		dialog.setView(view);

		ImageView alertImageView = (ImageView) view
				.findViewById(R.id.alert_display_imageView1);
		LinearLayout alertCancelButton = (LinearLayout) view
				.findViewById(R.id.alert_display_image_cancelButton1);

		alertImageView.setOnClickListener(clickListener);
		alertCancelButton.setOnClickListener(clickListener);

		uri = Uri.parse(photoList.get(postion).uri);
		path = getAbsolutePath(uri);

		bitmap = BitmapFactory.decodeFile(path);

		if (bitmap != null) {
			alertImageView.setImageBitmap(bitmap);
			isDialogOpen = !isDialogOpen;
			dialog.show();
			// Toast.makeText(this, uri.toString(), Toast.LENGTH_SHORT).show();
		}

	}

	private String getAbsolutePath(Uri uri) {
		String path = "";

		if (FileUtils.isMediaDocument(uri)) {
			path = FileUtils.getPath(MyAlbum.this, uri);
		} else {
			String proj[] = { MediaStore.Images.Media.DATA };

			CursorLoader loader = new CursorLoader(this, uri, proj, null, null,
					null);
			Cursor cursor = loader.loadInBackground();

			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();

			path = cursor.getString(column_index);

			cursor.close();

		}
		return path;
	}

	private void dbOpen() {
		dh = new DataBaseHelper(this);
		db = dh.getReadableDatabase();
	}

	private void dbClose() {
		dh.close();
		db.close();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// TODO Auto-generated method stub
		switch (id) {
		case GET_DATA:
			dbOpen();
			return new MyPhotoCursorLoader(this, dh, db, albumId);
		}
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// TODO Auto-generated method stub
		if (data.getCount() > 0) {

			photoList = new ArrayList<ImageBitmap>();
			ImageBitmap ib;

			data.moveToFirst();

			String photoUri;

			for (int i = 0; i < data.getCount(); i++) {
				data.moveToPosition(i);

				ib = new ImageBitmap();

				photoUri = data.getString(1);
				ib.uri = photoUri;

				MyPhoto mp = new MyPhoto(MyAlbum.this);

				Bitmap tmp = mp.getThumbnail(photoUri);
				if (tmp != null)
					ib.thumbnail = tmp;

				photoList.add(ib);

			}

			ga = new GridAdapter(this, photoList);

			gridView.setAdapter(ga);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		finish();
	}

}
