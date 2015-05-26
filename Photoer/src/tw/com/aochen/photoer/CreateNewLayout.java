package tw.com.aochen.photoer;


import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.*;

public class CreateNewLayout {

	private int cover = R.drawable.cover2;
	private String title = "My album title";
	private String content = "My album content.";
	private Context context;

	private ImageView imageview;
	private RelativeLayout relativelayout;
	private TextView textview_title, textview_content;

	public CreateNewLayout(Context context) {
		this.context = context;
	}

	public CreateNewLayout(Context context, int cover, String title,
			String content) {
		this.context = context;
		this.cover = cover;
		this.title = title;
		this.content = content;
	}

	public void createLayout() {

		relativelayout = new RelativeLayout(context);

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);

		imageview = new ImageView(context);
		imageview.setImageDrawable(context.getResources().getDrawable(
				R.drawable.cover2));
		imageview.setLayoutParams(params);
		imageview.setScaleType(ImageView.ScaleType.FIT_XY);

		textview_title = new TextView(context);
		textview_title.setText(title);
		// textview_title.setBackgroundColor(Color.parseColor("#333333"));
		textview_title.setLayoutParams(params);

		textview_content = new TextView(context);
		textview_content.setText(content);

		RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		params2.addRule(RelativeLayout.CENTER_IN_PARENT);
		textview_content.setLayoutParams(params2);

		relativelayout.addView(imageview);
		relativelayout.addView(textview_title);
		relativelayout.addView(textview_content);
	}

	public void setStyle(Context context) {
		this.context = context;
		textview_title.setTextAppearance(context, R.style.NewAlbumTitleLayout);
		textview_content.setTextAppearance(context, R.style.MyAlbumContent);
	}

	public View getView() {
		return relativelayout;
	}
}
