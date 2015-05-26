package getphoto;

import java.io.Serializable;

import android.graphics.Bitmap;

public class ImageBitmap implements Serializable {
	/**
	 * @param isNew: 判斷是否新加入ArrayList
	 */
	public transient Bitmap original, thumbnail;
	public long mId;
	public String uri, photoTableId;
	public Boolean isNew;
}
