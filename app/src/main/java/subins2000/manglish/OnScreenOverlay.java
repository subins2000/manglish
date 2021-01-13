package subins2000.manglish;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.text.style.ReplacementSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Transliterate text to Manglish on every app/screen
 */
public class OnScreenOverlay extends AccessibilityService {
    private ml2en engine;
    private RelativeLayout overlayLayout;
    private int statusBarHeight;
    private boolean gestureActive = false;

    @Override
    public void onServiceConnected() {
        Log.d("bbb", "bb");
        engine = new ml2en();

        WindowManager mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        overlayLayout = new RelativeLayout(getApplicationContext());

        DisplayMetrics displayMetrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(displayMetrics);

        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                width,
                height,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                        WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS |
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.TOP;

        overlayLayout.setLayoutParams(params);
        overlayLayout.setPadding(0, 0, 0, 0);

        statusBarHeight = getStatusBarHeight();

        mWindowManager.addView(overlayLayout, params);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            overlayLayout.removeAllViewsInLayout();
            return;
        }

        Log.i("aa", "aaa");

        if (!gestureActive) return;
        gestureActive = false;

        AccessibilityNodeInfo source;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            source = getRootInActiveWindow();
        } else {
            source = event.getSource();
        }

        if (source == null) {
            return;
        }

        ArrayList<AccessibilityNodeInfo> children = getAllChildren(source);
        AccessibilityNodeInfo child;

        for (int i = 0;i < children.size();i++) {
            child = children.get(i);
            Log.i("cc", child.getClassName().toString());

            if (!child.getClassName().equals("android.widget.TextView") || child.getText() == null) continue;

            // here level is iteration of for loop
            String text = child.getText().toString();
            Log.i("bb", text);

            if (!hasMalayalam(text)) continue;

            Rect chRect = new Rect();
            Rect prRect = new Rect();

            source.getBoundsInScreen(prRect);
            child.getBoundsInScreen(chRect);

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(
                    chRect.left,
                    chRect.top - statusBarHeight,
                    0,
                    0
            );

            TextView converted = new TextView(getApplicationContext());
            converted.setLayoutParams(layoutParams);

            converted.setText(engine.convert(text, false));
            converted.setBackgroundColor(Color.parseColor("#C1E8F9"));

            overlayLayout.addView(converted);
        }
    }

    @Override
    public boolean onGesture(int gestureId) {
        Log.i("g", "g");
        if (gestureId == GESTURE_SWIPE_LEFT_AND_RIGHT) {
            Log.i("g", "ggg");
            gestureActive = true;
            return true;
        }
        return true;
    }

    @Override
    public void onInterrupt() {
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private ArrayList<AccessibilityNodeInfo> getAllChildren(AccessibilityNodeInfo v) {
        if (v == null) return new ArrayList<AccessibilityNodeInfo>();

        ArrayList<AccessibilityNodeInfo> result = new ArrayList<AccessibilityNodeInfo>();

        if (v.getChildCount() == 0) {
            result.add(v);
            return result;
        }

        for (int i = 0; i < v.getChildCount(); i++) {
            AccessibilityNodeInfo child = v.getChild(i);

            ArrayList<AccessibilityNodeInfo> viewArrayList = new ArrayList<AccessibilityNodeInfo>();
            viewArrayList.addAll(getAllChildren(child));

            result.addAll(viewArrayList);
        }
        return result;
    }

    /**
     * Check if input has characters from Malayalam
     * Ported from JavaScript: https://github.com/subins2000/indicen/blob/master/src/scripts/contentScript.js
     * @param input
     * @return boolean
     */
    public boolean hasMalayalam(String input) {
        int charCode = 0;
        int start = 3328;
        int end = 3455;

        for (int i = 0; i < input.length(); i++) {
            charCode = (int) input.charAt(i);
            if (charCode >= start && charCode <= end) {
                return true;
            }
        }
        return false;
    }

    private class OverlayTextSpannable extends ReplacementSpan {
        private int CORNER_RADIUS = 10;
        private int backgroundColor = Color.parseColor("#C1E8F9");
        private int textColor = Color.BLACK;

        @Override
        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
            RectF rect = new RectF(x, top, x + measureText(paint, text, start, end), bottom);
            paint.setColor(backgroundColor);
            canvas.drawRoundRect(rect, CORNER_RADIUS, CORNER_RADIUS, paint);
            paint.setColor(textColor);
            canvas.drawText(text, start, end, x, y, paint);
        }

        @Override
        public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
            return Math.round(paint.measureText(text, start, end));
        }

        private float measureText(Paint paint, CharSequence text, int start, int end) {
            return paint.measureText(text, start, end);
        }
    }
}
