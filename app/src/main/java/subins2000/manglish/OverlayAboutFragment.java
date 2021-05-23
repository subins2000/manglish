package subins2000.manglish;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OverlayAboutFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OverlayAboutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OverlayAboutFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private View view;
    private SharedPreferences prefs;

    public OverlayAboutFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AboutFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OverlayAboutFragment newInstance() {
        OverlayAboutFragment fragment = new OverlayAboutFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_overlay_about, container, false);
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        Button overlayAccessibilityEnableButton = view.findViewById(R.id.overlay_accessibility_enable_button);
        overlayAccessibilityEnableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
            }
        });

        int progress = prefs.getInt("overlay_button_size", OnScreenOverlay.defaultButtonSize);

        final AppCompatTextView sliderValue = view.findViewById(R.id.slider_value);
        AppCompatSeekBar seekBar = view.findViewById(R.id.overlay_button_size_slider);

        sliderValue.setText(String.valueOf(progress));
        seekBar.setProgress(progress);
        seekBar.incrementProgressBy(10);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                progress = progress / 10;
                progress = progress * 10;
                sliderValue.setText(String.valueOf(progress));
                prefs.edit().putInt("overlay_button_size", progress).apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        Button overlayAccessibilityEnableButton = view.findViewById(R.id.overlay_accessibility_enable_button);
        View overlaySettings = view.findViewById(R.id.overlay_settings);
        if (isAccessibilityServiceEnabled(getContext(), OnScreenOverlay.class)) {
            overlayAccessibilityEnableButton.setText(R.string.overlay_accessibility_enabled_button);
            overlaySettings.setVisibility(View.VISIBLE);
        } else {
            overlayAccessibilityEnableButton.setText(R.string.overlay_accessibility_enable_button);
            overlaySettings.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public static boolean isAccessibilityServiceEnabled(Context context, Class<? extends AccessibilityService> service) {
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);

        for (AccessibilityServiceInfo enabledService : enabledServices) {
            ServiceInfo enabledServiceInfo = enabledService.getResolveInfo().serviceInfo;
            if (enabledServiceInfo.packageName.equals(context.getPackageName()) && enabledServiceInfo.name.equals(service.getName()))
                return true;
        }

        return false;
    }
}
