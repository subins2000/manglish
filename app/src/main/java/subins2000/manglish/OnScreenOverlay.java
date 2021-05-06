package subins2000.manglish;

import android.accessibilityservice.AccessibilityService;
import android.app.ActionBar;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.text.style.ReplacementSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Transliterate text to Manglish on every app/screen
 */
public class OnScreenOverlay extends AccessibilityService {
    private ml2en engine;
    private RelativeLayout overlayLayout;
    private int statusBarHeight;
    private int overlayTextPadding;

    private boolean transliterated = false;

    private ManglishOverlayButton mob;
    private Uri buttonImageURI, buttonActiveImageURI;

    @Override
    public void onServiceConnected() {
        Log.d("manglish", "Manglish service connected");
        engine = new ml2en();

        final WindowManager mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        overlayLayout = new RelativeLayout(getApplicationContext());

        DisplayMetrics displayMetrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(displayMetrics);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.TOP;
        params.alpha = 100;

        overlayLayout.setLayoutParams(params);
        overlayLayout.setPadding(0, 0, 0, 0);

        statusBarHeight = getStatusBarHeight();
        overlayTextPadding = (int) Math.ceil(4 * getResources().getDisplayMetrics().density);

        mWindowManager.addView(overlayLayout, params);

        WindowManager.LayoutParams mobParams = new WindowManager.LayoutParams();
        mobParams.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
        mobParams.x = 0;
        mobParams.y = 0;
        mobParams.format = PixelFormat.TRANSLUCENT;
        mobParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mobParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mobParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mobParams.gravity = Gravity.TOP | Gravity.LEFT;

        mob = new ManglishOverlayButton(
                getApplicationContext(),
                mWindowManager,
                mobParams
        );

        mob.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (transliterated) {
                    // deactivate
                    removeTransliteration();
                } else {
                    // activate
                    removeTransliteration();
                    transliterateScreen();
                }
            }
        });
        mob.setHoldListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWindowManager.removeView(mob);
            }
        });

        mob.setMaxHeight(200);
        mob.setMaxWidth(200);

        buttonImageURI = resourceToUri(getApplicationContext(), R.mipmap.overlay_button);
        buttonActiveImageURI = resourceToUri(getApplicationContext(), R.mipmap.overlay_button_active);
        mob.setImageURI(buttonImageURI);

        mWindowManager.addView(mob, mobParams);
    }

    public static Uri resourceToUri(Context context, int resID) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
            context.getResources().getResourcePackageName(resID) + '/' +
            context.getResources().getResourceTypeName(resID) + '/' +
            context.getResources().getResourceEntryName(resID) );
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_SCROLLED && transliterated) {
            removeTransliteration();
        }
    }

    private void transliterateScreen() {
        AccessibilityNodeInfo source = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // Works only above or equal to API level 16
            source = getRootInActiveWindow();
        } else {
            Toast.makeText(getApplicationContext(), "This feature works only on Android Jelly Bean and above",
                    Toast.LENGTH_LONG).show();
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

            // blue like in whatsapp date bg - C1E8F9
            converted.setTextColor(Color.parseColor("#FFFFFF"));
//            converted.setBackgroundColor(Color.parseColor("#202124"));
            converted.setPadding(overlayTextPadding, overlayTextPadding, overlayTextPadding, overlayTextPadding);
            converted.setBackgroundResource(R.drawable.overlay_text_gradient);

            overlayLayout.addView(converted);
        }

        transliterated = true;
        mob.setImageURI(buttonActiveImageURI);
    }

    private void removeTransliteration() {
        if (!transliterated) return;
        overlayLayout.removeAllViewsInLayout();
        transliterated = false;
        mob.setImageURI(buttonImageURI);
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
