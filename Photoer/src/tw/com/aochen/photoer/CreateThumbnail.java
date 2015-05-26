package tw.com.aochen.photoer;

import java.io.File;
import java.io.FileOutputStream;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

public class CreateThumbnail {

	private Context context;

	public String originalPath, thumbnailName, thumbnailWholePath;

	public CreateThumbnail(Context context) {
		this.context = context;
	}

	/*
	 * 代入Uri，可取得相片的絕對路徑和其自製縮圖的名稱
	 */
	public void getOriginalPathAndThumbnailName(Uri uri) {
		String proj[] = { MediaStore.Images.Media.DATA };

		CursorLoader loader = new CursorLoader(context, uri, proj, null, null,
				null);
		Cursor cursor = loader.loadInBackground();

		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();

			originalPath = cursor.getString(column_index);
			originalPath = FileUtils.getPath(context, uri);
			thumbnailName = originalPath.substring(
					originalPath.lastIndexOf("/") + 1,
					originalPath.lastIndexOf("."))
					+ "_thumbnail.jpg";

			cursor.close();

		}

	}

	/*
	 * 製作縮圖，且回傳縮圖Bitmap
	 */
	public Bitmap CreateThumbnailPicture() {
		Bitmap thumbnail = null;
		try {

			// First decode : get bitmap width and height only.
			BitmapFactory.Options option = new BitmapFactory.Options();
			option.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(originalPath, option);

			option.inSampleSize = computeSampleSize(option, -1, 512 * 512);

			// Second decode : create thumbnail.
			option.inJustDecodeBounds = false;
			thumbnail = BitmapFactory.decodeFile(originalPath, option);

		} catch (Exception e) {

		}
		if (thumbnail != null) {
			return rotatePicture(thumbnail);
		} else {
			return null;
		}
	}

	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	public static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	/*
	 * 旋轉相片
	 */
	public Bitmap rotatePicture(Bitmap bitmap) {
		try {
			int degree = 0;

			ExifInterface exif = new ExifInterface(originalPath);
			int orientation = exif.getAttributeInt(exif.TAG_ORIENTATION,
					exif.ORIENTATION_NORMAL);

			if (orientation == exif.ORIENTATION_ROTATE_90) {
				degree = 90;
			} else if (orientation == exif.ORIENTATION_ROTATE_180) {
				degree = 180;
			} else if (orientation == exif.ORIENTATION_ROTATE_270) {
				degree = 270;
			}

			Matrix matrix = new Matrix();
			matrix.postRotate(degree);

			Bitmap correct_bitmap = Bitmap.createBitmap(bitmap, 0, 0,
					bitmap.getWidth(), bitmap.getHeight(), matrix, true);

			return correct_bitmap;

		} catch (Exception e) {
			return null;
		}

	}

	/*
	 * 帶縮圖Bitmap，儲存縮圖，並取得縮圖的絕對路徑
	 */
	public void saveThumbnail(Bitmap thumbnail) {

		String external_storage = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/Photoer_thumbnail";

		File dir = new File(external_storage);
		if (dir.exists() == false) {
			dir.mkdirs();
		}

		File file = new File(external_storage, thumbnailName);

		Bitmap bitmap = thumbnail;
		try {
			if (file.exists() != true) {
				FileOutputStream os = new FileOutputStream(file);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
				os.flush();
				os.close();
			} else {
				// Toast.makeText(getApplicationContext(),
				// file.toString() + " is existed already.",
				// Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}

		thumbnailWholePath = file.getPath();
	}

	/*
	 * 利用自製縮圖的絕對路徑來判斷是否存在，若無則回傳false；若有回傳true
	 */
	public Boolean checkThumbnailExist(String path) {
		if (path == null) {
			Toast.makeText(context, R.string.toast_no_cover, Toast.LENGTH_SHORT)
					.show();
			return true;

		} else {
			File file = new File(originalPath);
			if (file.getAbsolutePath().contains("/Photoer_thumbnail")) {
				return true;
			} else {
				return false;
			}
		}

	}
}
