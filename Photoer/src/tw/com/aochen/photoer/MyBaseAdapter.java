package tw.com.aochen.photoer;

import java.io.File;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyBaseAdapter extends BaseAdapter {

	private Context context;

	private Cursor cursor;

	private LayoutInflater inFlater;

	public MyBaseAdapter(Context context, Cursor cursor) {

		this.context = context;
		this.cursor = cursor;

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
		}

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

		ViewHolder viewHolder;

		if (convertView == null) {

			convertView = inFlater.inflate(R.layout.my_list_layout, null);

			viewHolder = new ViewHolder();
			
			viewHolder.titleContainer = (LinearLayout) convertView
					.findViewById(R.id.ListTitleContainer);
			
			viewHolder.title = (TextView) convertView
					.findViewById(R.id.ListTitle);
			
			viewHolder.cover = (ImageView) convertView
					.findViewById(R.id.ListCover);

			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		cursor.moveToPosition(position);
		getDataFromCursor(viewHolder);

		return convertView;
	}

	private void getDataFromCursor(ViewHolder viewHolder) {

		if (cursor.getString(1).isEmpty() == false) {
			viewHolder.titleContainer.setVisibility(View.VISIBLE);
			viewHolder.title.setText(cursor.getString(1));
		} else {
			viewHolder.titleContainer.setVisibility(View.INVISIBLE);

		}

		if (cursor.isNull(5) == false) {
			String path = cursor.getString(5);
			if (checkThumbnailExist(path)) {
				Bitmap bitmap = BitmapFactory.decodeFile(path);
				viewHolder.cover.setImageBitmap(bitmap);
			} else
				viewHolder.cover.setImageResource(R.drawable.cover);

		} else {
			viewHolder.cover.setImageResource(R.drawable.cover);
		}
	}

	private Boolean checkThumbnailExist(String path) {
		File file = new File(path);
		if (file.exists())
			return true;
		else
			return false;
	}

	static class ViewHolder {
		LinearLayout titleContainer;
		TextView title;
		ImageView cover;
	}
}
