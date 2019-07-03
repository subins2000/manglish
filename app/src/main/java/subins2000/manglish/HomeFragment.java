package subins2000.manglish;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements AsyncResponse {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;

    Converter converterAsyncTask;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Button convertBtn = view.findViewById(R.id.convert_button);
        convertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateOutput(getView());
            }
        });

        Button copyBtn = view.findViewById(R.id.copy_button);
        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView outputText = getView().findViewById(R.id.outputText);

                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("manglish_output", outputText.getText());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(getActivity(), getString(R.string.copied), Toast. LENGTH_SHORT).show();
            }
        });

        EditText inputText = view.findViewById(R.id.inputText);
        inputText.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence cs, int s, int b, int c) {
                String text = cs.toString();

                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                editor.putString("text", text);
                editor.commit();
            }
            public void afterTextChanged(Editable editable) { }
            public void beforeTextChanged(CharSequence cs, int i, int j, int
                    k) { }
        });

        // Handle text share to this app
        Intent intent = getActivity().getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleIncomingText(intent, view); // Handle text being received
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Intent.ACTION_PROCESS_TEXT.equals(action)) {
            handleIncomingText(intent, view);
        } else {
            // Handle other intents, such as being started from the home screen
            inputText.setText(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("text", ""));
        }

        constructAsyncThread(view);

        return view;
    }

    private void constructAsyncThread(View view) {
        converterAsyncTask = new Converter();
        converterAsyncTask.delegate = this;
    }

    //this override the implemented method from asyncTask
    @Override
    public void processFinish(String result){
        //Here you will receive the result fired from async class
        //of onPostExecute(result) method.
        View view = getView();

        TextView outputText = view.findViewById(R.id.outputText);
        outputText.setText(result);

        Button copyBtn = view.findViewById(R.id.copy_button);
        copyBtn.setEnabled(true);
    }

    void updateOutput(View view) {
        TextView outputText = view.findViewById(R.id.outputText);
        outputText.setText(R.string.converting);

        if (converterAsyncTask.getStatus() != AsyncTask.Status.PENDING){
            converterAsyncTask.cancel(true);
            constructAsyncThread(view);
        }

        EditText inputText = view.findViewById(R.id.inputText);
        String text = inputText.getText().toString();
        converterAsyncTask.execute(text);
    }

    /**
     * Handle share text
     * @param intent
     * @param view
     */
    void handleIncomingText(Intent intent, View view) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);

        if (sharedText == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            sharedText = intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT);

        if (sharedText != null) {
            EditText inputText = view.findViewById(R.id.inputText);
            inputText.setText(sharedText);

            constructAsyncThread(view);
            updateOutput(view);
        }
    }

    @Override
    public void onAttach(android.content.Context context) {
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
}
