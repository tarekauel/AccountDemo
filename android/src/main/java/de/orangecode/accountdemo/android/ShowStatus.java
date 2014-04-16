package de.orangecode.accountdemo.android;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.ArrayList;

public class ShowStatus extends Activity {

    private Spinner accountSpinner;
    private CustomAdapter adapterSpinner;
    private CustomAdapter adapterList;
    private Context mContext;
    private int mCurrentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_show_status);
        accountSpinner = (Spinner) findViewById(R.id.user_chooser);
        adapterSpinner = new CustomAdapter(mContext, android.R.layout.simple_spinner_dropdown_item, new ArrayList<String>());
        accountSpinner.setAdapter(adapterSpinner);
        accountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String username = adapterSpinner.getItem(position);
                mCurrentId = position;
                new GetAllPosts().execute(username);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ListView listView = (ListView) findViewById(R.id.listView);
        adapterList = new CustomAdapter(mContext, android.R.layout.simple_list_item_1, new ArrayList<String>());
        listView.setAdapter(adapterList);

        new GetAllUser().execute();
    }

    public void refresh(View view) {
        String usernname = adapterSpinner.getItem(mCurrentId);
        new GetAllPosts().execute(usernname);
    }

    private class CustomAdapter extends ArrayAdapter<String> {

        private final ArrayList<String> mData;
        private final Context context;
        private final int resourceId;

        private CustomAdapter(Context context, int resourceId, ArrayList<String> data) {
            super(context, resourceId, data);
            mData = data;
            this.context = context;
            this.resourceId = resourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(resourceId, parent, false);
            }

            ((TextView) convertView.findViewById(android.R.id.text1)).setText(mData.get(position));

            return convertView;
        }

        public String getItem(int id) {
            return mData.get(id);
        }
    }


    private class GetAllUser extends AsyncTask<Void, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            try {
                String response = NetworkHelper.doRequest(mContext, "user", "GET", null);
                if (response != null) {
                    ArrayList<String> userList = new ArrayList<String>();
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i=0; i < jsonArray.length(); i++) {
                        userList.add(jsonArray.get(i).toString());
                    }
                    return userList;
                }
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            if (strings == null) {
                Toast.makeText(mContext, "User could not be loaded", Toast.LENGTH_LONG).show();
            } else {
                for (String string : strings) adapterSpinner.add(string);
                adapterSpinner.notifyDataSetChanged();
            }
        }
    }

    private class GetAllPosts extends AsyncTask<String, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            try {
                String json = "{\"username\":\""+strings[0]+"\"}";
                String response = NetworkHelper.doRequest(mContext, "status", "PUT", json);
                if (response != null) {
                    ArrayList<String> status = new ArrayList<String>();
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i=0; i < jsonArray.length(); i++) {
                        status.add(jsonArray.get(i).toString());
                    }
                    return status;
                }
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            if (strings != null) {
                adapterList.clear();
                for (String string : strings) adapterList.add(string);
                adapterList.notifyDataSetChanged();
            } else {
                Toast.makeText(mContext, "Status could not be loaded", Toast.LENGTH_LONG).show();
            }
        }
    }

}
