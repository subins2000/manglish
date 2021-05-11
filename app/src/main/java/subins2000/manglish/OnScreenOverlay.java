package subins2000.manglish;

import android.accessibilityservice.AccessibilityService;
import android.app.ActionBar;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.style.ReplacementSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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
public class OnScreenOverlay extends AccessibilityService implements SharedPreferences.OnSharedPreferenceChangeListener {
    private ml2en engine;
    private SharedPreferences prefs;

    private RelativeLayout overlayLayout;
    private int statusBarHeight;
    private int overlayTextPadding;

    private boolean serviceActive = true;
    private boolean transliterated = false;

    private ManglishOverlayButton mob;
    public static int defaultButtonSize = 150;
    private Uri buttonImageURI, buttonActiveImageURI;
    private View removalPaneContainer;

    @Override
    public void onServiceConnected() {
        Log.d("manglish", "Manglish service connected");
        engine = new ml2en();

        final WindowManager mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        /*
         * Add Overlay Layout & Removal Pane
         */
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

        removalPaneContainer = LayoutInflater.from(getApplicationContext()).inflate(R.layout.overlay_button_removal_pane, null);
        WindowManager.LayoutParams removalPaneParams = new WindowManager.LayoutParams();
        removalPaneParams.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
        removalPaneParams.format = PixelFormat.TRANSLUCENT;
        removalPaneParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        removalPaneParams.y = displayMetrics.heightPixels;
        removalPaneContainer.setLayoutParams(removalPaneParams);

        removalPaneContainer.setVisibility(View.INVISIBLE);
        mWindowManager.addView(removalPaneContainer, params);

        mWindowManager.addView(overlayLayout, params);

        /*
         * Add overlay button
         */

        WindowManager.LayoutParams mobParams = new WindowManager.LayoutParams();
        mobParams.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
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
        mob.setDragListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removalPaneContainer.setVisibility(View.VISIBLE);
            }
        });
        final View removalPane = removalPaneContainer.findViewById(R.id.removal_pane);
        mob.setReleaseListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isViewOverlappingVertically(view, removalPane)) {
                    hideOverlay();
                }
                removalPaneContainer.setVisibility(View.INVISIBLE);
            }
        });

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int size = prefs.getInt("overlay_button_size", defaultButtonSize);
        changeButtonSize(size);

        prefs.registerOnSharedPreferenceChangeListener(this);

        buttonImageURI = resourceToUri(getApplicationContext(), R.mipmap.overlay_button);
        buttonActiveImageURI = resourceToUri(getApplicationContext(), R.mipmap.overlay_button_active);
        mob.setImageURI(buttonImageURI);

        mWindowManager.addView(mob, mobParams);
        mob.resetPosition();
    }

    public static Uri resourceToUri(Context context, int resID) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
            context.getResources().getResourcePackageName(resID) + '/' +
            context.getResources().getResourceTypeName(resID) + '/' +
            context.getResources().getResourceEntryName(resID) );
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (serviceActive && event.getEventType() == AccessibilityEvent.TYPE_VIEW_SCROLLED && transliterated) {
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
            CharSequence className = child.getClassName();
            if (className == null) continue;

            Log.i("cc", className.toString());
            if (!className.equals("android.widget.TextView") || child.getText() == null) continue;

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
//            converted.setBackgroundColor(Color.parseColor("#202124"));
            converted.setTextColor(Color.parseColor("#FFFFFF"));
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

    private void changeButtonSize(int size) {
        mob.setMinimumHeight(size);
        mob.setMinimumWidth(size);
        mob.setMaxHeight(size);
        mob.setMaxWidth(size);
    }

    private void hideOverlay() {
        serviceActive = false;
        mob.setVisibility(View.INVISIBLE);
        overlayLayout.setVisibility(View.INVISIBLE);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals("overlay_button_size")) {
            int size = sharedPreferences.getInt("overlay_button_size", defaultButtonSize);
            changeButtonSize(size);
        } else if (s.equals("display_overlay")) {
            // This preference value is only a bus message event for displaying overlay

            serviceActive = true;
            mob.resetPosition();
            mob.setVisibility(View.VISIBLE);
            overlayLayout.setVisibility(View.VISIBLE);
        }
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
        ArrayList<AccessibilityNodeInfo> visited = new ArrayList<AccessibilityNodeInfo>();
        ArrayList<AccessibilityNodeInfo> unvisited = new ArrayList<AccessibilityNodeInfo>();
        unvisited.add(v);

        while (!unvisited.isEmpty()) {
            AccessibilityNodeInfo child = unvisited.remove(0);
            visited.add(child);

            final int childCount = child.getChildCount();
            for (int i=0; i < childCount; i++) unvisited.add(child.getChild(i));
        }

        return visited;
    }

    /**
     * Thanks Abandoned Cart https://stackoverflow.com/a/43640028/1372424
     * @param firstView
     * @param secondView
     * @return bool
     */
    private boolean isViewOverlappingVertically(View firstView, View secondView) {
        int[] firstPosition = new int[2];
        int[] secondPosition = new int[2];

        firstView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        firstView.getLocationOnScreen(firstPosition);
        secondView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        secondView.getLocationOnScreen(secondPosition);

        return firstPosition[1] < secondPosition[1] + secondView.getMeasuredHeight()
            && firstPosition[1] + firstView.getMeasuredHeight() > secondPosition[1];
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
