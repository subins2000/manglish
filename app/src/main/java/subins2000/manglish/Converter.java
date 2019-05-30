package subins2000.manglish;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Converter extends AsyncTask<String, String, String> {

    public AsyncResponse delegate = null;

    private Scriptable scope;

    public void makeScope(View view) {
        // Every Rhino VM begins with the enter()
        // This Context is not Android's Context
        Context rhino = Context.enter();

        // Turn off optimization to make Rhino Android compatible
        rhino.setOptimizationLevel(-1);

        BufferedReader reader = null;
        try {
            scope = rhino.initStandardObjects();

            reader = new BufferedReader(
                    new InputStreamReader(view.getContext().getAssets().open("ml2en.js"), "UTF-8"));

            // Note the forth argument is 1, which means the JavaScript source has
            // been compressed to only one line using something like YUI
            rhino.evaluateReader(scope, reader, "JavaScript", 1, null);
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

    @Override
    protected String doInBackground(String... text) {
        Object[] params = new Object[] { text[0] };

        // Every Rhino VM begins with the enter()
        // This Context is not Android's Context
        Context rhino = Context.enter();

        // Turn off optimization to make Rhino Android compatible
        rhino.setOptimizationLevel(-1);

        try {
            // Get the functionName defined in JavaScriptCode
            Object obj = scope.get("ml2en", scope);

            if (obj instanceof Function) {
                Function jsFunction = (Function) obj;

                // Call the function with params
                Object jsResult = jsFunction.call(rhino, scope, scope, params);
                // Parse the jsResult object to a String
                String result = Context.toString(jsResult);

                return result;
            }
        } finally {
            Context.exit();
        }
        return "";
    }

    @Override
    protected void onPostExecute(String result) {
        if (!isCancelled())
            delegate.processFinish(result);
    }
}
