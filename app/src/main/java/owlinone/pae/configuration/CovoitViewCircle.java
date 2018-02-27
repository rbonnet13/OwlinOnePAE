package owlinone.pae.configuration;

/**
 * Created by AnthonyCOPPIN on 23/02/2018.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class CovoitViewCircle extends View
{
    private static final int DEFAULT_CIRCLE_COLOR = Color.WHITE;

    private int circleColor = DEFAULT_CIRCLE_COLOR;
    private Paint paint;

    public CovoitViewCircle(Context context)
    {
        super(context);
        init(context, null);
    }

    public CovoitViewCircle(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs)
    {
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    public void setCircleColor(int circleColor)
    {
        this.circleColor = circleColor;
        invalidate();
    }

    public int getCircleColor()
    {
        return circleColor;
    }

    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        int w = getWidth();
        int h = getHeight();

        int pl = getPaddingLeft();
        int pr = getPaddingRight();
        int pt = getPaddingTop();
        int pb = getPaddingBottom();

        int usableWidth = w - (pl + pr);
        int usableHeight = h - (pt + pb);

        int radius = Math.min(usableWidth, usableHeight) / 2;
        int cx = pl + (usableWidth / 2);
        int cy = (pt + (usableHeight / 2)) +  820;

        paint.setColor(circleColor);
        canvas.drawCircle(cx, cy, 200, paint);
        Log.e("Draw X:", String.valueOf(cx));
        Log.e("Draw Y:", String.valueOf(cy));

    }
}