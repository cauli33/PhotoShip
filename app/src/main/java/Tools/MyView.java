package Tools;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import com.example.emmaveillat.finalproject.R;

/** TODO
 * Created by emmaveillat on 17/02/2017.
 */

public class MyView extends View {

    boolean mShowText;
    int mTextPos;

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MyView, 0, 0);

        try {
            mShowText = a.getBoolean(R.styleable.MyView_showText, false);
            mTextPos = a.getInteger(R.styleable.MyView_labelPosition, 0);
        } finally {
            a.recycle();
        }
    }

    public boolean isShowText() {
        return mShowText;
    }

    public void setShowText(boolean showText) {
        mShowText = showText;
        invalidate();
        requestLayout();
    }

}
