package tw.com.aochen.photoer;

import convert.DpToPixel;
import getphoto.ImageBitmap;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class GridAdapter extends BaseAdapter {

	private Context context;
	private Bitmap original, thumbnail;
	private ArrayList<ImageBitmap> list;

	private CreateThumbnail ct;

	public GridAdapter(Context context, ArrayList<ImageBitmap> list) {
		this.context = context;
		this.list = list;

		ct = new CreateThumbnail(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		ImageView imageView;
		if (convertView == null) {
			imageView = new ImageView(context);

			DpToPixel dt = new DpToPixel();

			Float f = new Float(dt.convertDpToPixel(75, context));
			int px = f.intValue();

			imageView.setLayoutParams(new GridView.LayoutParams(px, px));
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

		} else {
			imageView = (ImageView) convertView;

		}

		String uri = list.get(position).uri;
		if (uri != null) {
			ct.getOriginalPathAndThumbnailName(Uri.parse(uri));

			Bitmap bitmap = ct.rotatePicture(list.get(position).thumbnail);

			imageView.setImageBitmap(bitmap);
		} else {

			Bitmap bitmap = list.get(position).thumbnail;

			imageView.setImageBitmap(bitmap);
		}

		return imageView;
	}
}
