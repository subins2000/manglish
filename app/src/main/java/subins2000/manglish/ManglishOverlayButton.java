package subins2000.manglish;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.widget.AppCompatImageButton;

public class ManglishOverlayButton extends AppCompatImageButton implements View.OnTouchListener {
    private final static float CLICK_DRAG_TOLERANCE = 10;

    float dX;
    float dY;

    private static int CLICK_THRESHOLD = 200;

    WindowManager wm;
    WindowManager.LayoutParams params;
    OnClickListener clickListener, dragListener, releaseListener;

    public ManglishOverlayButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ManglishOverlayButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ManglishOverlayButton(Context context, WindowManager wmL, WindowManager.LayoutParams paramsL) {
        super(context);
        wm = wmL;
        params = paramsL;
        init();
    }

    public void setClickListener(OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setDragListener(OnClickListener dragListener) {
        this.dragListener = dragListener;
    }
    public void setReleaseListener(OnClickListener releaseListener) { this.releaseListener = releaseListener; }

    private void init() {
        setOnTouchListener(this);
        this.setScaleType(ScaleType.FIT_CENTER);
        this.setAdjustViewBounds(true);
    }

    public void resetPosition() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);

        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        params.x = ((int) (width * 0.7)) - params.width;
        params.y = height / 2 - params.height;
        wm.updateViewLayout(this, params);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                dX = params.x - event.getRawX();
                dY = params.y - event.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:
                params.y = (int) (event.getRawY() + dY);
                params.x = (int) (event.getRawX() + dX);
                wm.updateViewLayout(this, params);
                dragListener.onClick(view);
                break;

            case MotionEvent.ACTION_UP:
                long duration = event.getEventTime() - event.getDownTime();
                if (duration < CLICK_THRESHOLD) {
                    Log.d("manglish-action", "click");
                    clickListener.onClick(view);
                }
                releaseListener.onClick(view);
                break;
            default:
                return false;
        }
        return true;
    }
}