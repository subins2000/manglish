package subins2000.manglish;

import android.os.AsyncTask;

public class Converter extends AsyncTask<String, String, String> {

    public AsyncResponse delegate = null;

    @Override
    protected String doInBackground(String... text) {
        ml2en m = new ml2en();
        return m.convert(text[0], false);
    }

    @Override
    protected void onPostExecute(String result) {
        if (!isCancelled())
            delegate.processFinish(result);
    }
}
