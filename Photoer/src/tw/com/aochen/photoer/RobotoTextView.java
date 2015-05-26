package tw.com.aochen.photoer;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class RobotoTextView extends TextView {

	public RobotoTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
        
        setType(context);
		
	}
	
	
	public RobotoTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		setType(context);
	}


	public RobotoTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		setType(context);
	}


	private void setType(Context context){
		
		this.setTypeface(Typeface.createFromAsset(context.getAssets(), "Roboto.ttf"));
	}
}
