package subins2000.manglish;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView fab = findViewById(R.id.outputText);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateOutput();
            }
        });
    }

    void updateOutput() {
        EditText inputText = findViewById(R.id.inputText);
        String text = inputText.getText().toString();

        TextView outputText = findViewById(R.id.outputText);

        Object[] params = new Object[] { text };

        // Every Rhino VM begins with the enter()
        // This Context is not Android's Context
        Context rhino = Context.enter();

        // Turn off optimization to make Rhino Android compatible
        rhino.setOptimizationLevel(-1);

        BufferedReader reader = null;
        try {
            Scriptable scope = rhino.initStandardObjects();

            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("ml2en.js"), "UTF-8"));

            // Note the forth argument is 1, which means the JavaScript source has
            // been compressed to only one line using something like YUI
            rhino.evaluateReader(scope, reader, "JavaScript", 1, null);

            // Get the functionName defined in JavaScriptCode
            Object obj = scope.get("ml2en", scope);

            if (obj instanceof Function) {
                Function jsFunction = (Function) obj;

                // Call the function with params
                Object jsResult = jsFunction.call(rhino, scope, scope, params);
                // Parse the jsResult object to a String
                String result = Context.toString(jsResult);

                outputText.setText(result);

                Log.d("resul", result);
            }
        } catch (IOException e) {
            //log the exception
            Log.e("outputText", e.toString());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
            Context.exit();
        }

    }

}
