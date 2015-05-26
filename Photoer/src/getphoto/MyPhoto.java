package getphoto;

import tw.com.aochen.photoer.FileUtils;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.CancellationSignal;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class MyPhoto {

	private ContentResolver cr;

	private Context context;

	private ImageBitmap ib;

	public MyPhoto(Context context) {
		cr = context.getContentResolver();
		this.context = context;
	}

	/*
	 * 
	 */
	public ImageBitmap getPhoto(Intent data) {

		ib = new ImageBitmap();

		if (data.getData() == null && data.getClipData() == null) {
		} else if (data.getData() == null) {

		} else {
			ib.thumbnail = getThumbnail(data.getData());
		}

		return ib;

	}

	/*
	 * return thumbnail's bitmap and ImageBitmap class
	 */
	public Bitmap getThumbnail(Uri uri) {

		Bitmap bitmap = null;

		// DocumentsContract.getDocumentThumbnail(cr, uri, size, signal)

		// if (FileUtils.isMediaDocument(uri)) {
		//
		// bitmap = DocumentsContract.getDocumentThumbnail(cr, uri, new Point(
		// 40, 40), new CancellationSignal());
		//
		// Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		//
		// int index;
		// String id = DocumentsContract.getDocumentId(uri);
		// index = id.lastIndexOf(":") + 1;
		//
		// id = id.substring(index);
		//
		// ib.uri = contentUri + id;
		// ib.mId = Long.parseLong(id);
		//
		// } else {
		String[] projection = { MediaColumns._ID };

		Cursor cursor = cr.query(uri, projection, null, null, null);

		if (cursor != null) {
			cursor.moveToFirst();
			long id = cursor.getLong(0);
			ib.mId = id;
			bitmap = MediaStore.Images.Thumbnails.getThumbnail(cr, id,
					MediaStore.Images.Thumbnails.MINI_KIND,
					(BitmapFactory.Options) null);
			ib.uri = uri.toString();
		}

		return bitmap;

	}

	/*
	 * Only return thumbnail's bitmap,
	 */
	public Bitmap getThumbnail(String path) {
		String[] projection = { MediaColumns._ID };
		Uri uri = Uri.parse(path);

		Bitmap bitmap = null;

		// if (FileUtils.isMediaDocument(uri)) {
		//
		// int index;
		// String id = DocumentsContract.getDocumentId(uri);
		// index = id.lastIndexOf(":") + 1;
		//
		// id = id.substring(index);
		//
		// bitmap = MediaStore.Images.Thumbnails.getThumbnail(cr,
		// Long.parseLong(id), MediaStore.Images.Thumbnails.MINI_KIND,
		// (BitmapFactory.Options) null);
		//
		// System.out.println("");
		//
		// } else {
		Cursor cursor = cr.query(uri, projection, null, null, null);

		if (cursor.getCount() > 0) {

			cursor.moveToFirst();
			long id = cursor.getLong(0);

			bitmap = MediaStore.Images.Thumbnails.getThumbnail(cr, id,
					MediaStore.Images.Thumbnails.MINI_KIND,
					(BitmapFactory.Options) null);
		}

		return bitmap;

	}

	/*
	 * return photo's id
	 */
	public Long getPhotoId(Uri uri) {

		String[] projection = { MediaColumns._ID };

		Cursor cursor = cr.query(uri, projection, null, null, null);

		if (cursor != null) {
			cursor.moveToFirst();
			long id = cursor.getLong(0);
			return id;
		}
		return null;
	}

	private void getOriginal() {

	}
}
