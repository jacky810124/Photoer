package tw.com.aochen.photoer;

import java.io.File;

import com.mikhaellopez.circularimageview.CircularImageView;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TitleBaseAdapter extends BaseAdapter {

	private Context context;
	private Cursor cursor;
	private LayoutInflater inFlater;

	public TitleBaseAdapter(Context context, Cursor cursor) {
		// TODO Auto-generated constructor stub

		this.context = context;
		this.cursor = cursor;

		inFlater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return cursor.getCount();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		ViewHolder vHolder;

		if (convertView == null) {
			convertView = inFlater.inflate(R.layout.drawer_list, null);

			vHolder = new ViewHolder();

			vHolder.txtTitle = (TextView) convertView
					.findViewById(R.id.drawer_textView1);
			vHolder.imgCover = (CircularImageView) convertView
					.findViewById(R.id.drawer_imageView1);

			convertView.setTag(vHolder);
		} else {
			vHolder = (ViewHolder) convertView.getTag();
		}

		cursor.moveToPosition(position);

		String title = cursor.getString(1);
		if (title.isEmpty() == false) {
			vHolder.txtTitle.setText(title);
		} else {
			vHolder.txtTitle.setText(R.string.album_title_hint);
		}

		String path = cursor.getString(5);
		if (path != null && checkImageExist(path)) {
			vHolder.imgCover.setImageBitmap(BitmapFactory.decodeFile(path));
		} else {
			vHolder.imgCover.setImageResource(R.drawable.cover);
		}

		return convertView;
	}

	private Boolean checkImageExist(String path) {
		File file = new File(path);
		if (file.exists())
			return true;
		else
			return false;

	}

	static class ViewHolder {
		TextView txtTitle;
		CircularImageView imgCover;
	}

}
