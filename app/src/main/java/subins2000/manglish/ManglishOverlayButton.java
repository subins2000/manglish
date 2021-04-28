package subins2000.manglish;

import android.content.Context;
import android.util.AttributeSet;
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
    OnClickListener clickListener;

    public ManglishOverlayButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ManglishOverlayButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ManglishOverlayButton(Context context, WindowManager wmL, WindowManager.LayoutParams paramsL, OnClickListener callback) {
        super(context);
        wm = wmL;
        params = paramsL;
        clickListener = callback;
        init();
    }

    private void init() {
        setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event){
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                dX = params.x - event.getRawX();
                dY = params.y - event.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:
                params.y = (int) (event.getRawY() + dY);
                params.x = (int) (event.getRawX() + dX);
                wm.updateViewLayout(this, params);
                break;

            case MotionEvent.ACTION_UP:
                long duration = event.getEventTime() - event.getDownTime();
                if (duration < CLICK_THRESHOLD) {
                    Log.d("zz", "cccc");
                    clickListener.onClick(view);
                }
                break;
            default:
                return false;
        }
        return true;
    }
}