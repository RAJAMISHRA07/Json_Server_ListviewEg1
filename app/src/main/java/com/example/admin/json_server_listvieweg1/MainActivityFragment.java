package com.example.admin.json_server_listvieweg1;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static com.example.admin.json_server_listvieweg1.R.id.listview1;
import static com.example.admin.json_server_listvieweg1.R.id.tv2;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    //8.declare required variables
    Button button;
    ListView listView;
    ArrayList<Contact>arrayList;
    MyAdapter myAdapter;
    MyTask myTask;
    //10.Actual logic
    public  boolean checkInternet(){
        ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info =  manager.getActiveNetworkInfo();
        if (info==null||info.isConnected()==false)
        {
            return false;
        }
        return true;
    }
    //7.a create inner class for async task
    public class MyTask extends AsyncTask<String,Void,String>{
        URL myurl;
        HttpURLConnection htpurlconnection;
        InputStream inputstream;
        InputStreamReader isr;
        BufferedReader br;
        String line;
        StringBuilder result;


        @Override
        protected String doInBackground(String... params) {
            //12a=write logic or connecting server and get json data
            try {
                myurl=new URL(params[0]);
                htpurlconnection= (HttpURLConnection) myurl.openConnection();
                inputstream=htpurlconnection.getInputStream();
                isr=new InputStreamReader(inputstream);
                br=new BufferedReader(isr);
                line=br.readLine();
                result=new StringBuilder();
                while (line!=null){
                    result.append(line);
                    line=br.readLine();
                }
                return result.toString();//return final result (json ) to on post  execute
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.d("B34", "error");

            } catch (IOException e) {
                e.printStackTrace();
                Log.d("B34", "error");
            }

            return "something went wrong";
        }
//7.1a
        @Override
        protected void onPostExecute(String s) {
            //12b==reverse json parsing
            try {
                JSONObject j=new JSONObject(s);
                JSONArray ja=j.getJSONArray("contacts");
                for (int i=0;i<ja.length();i++)
                {
                JSONObject m=ja.getJSONObject(i);
                String name=m.getString("name");
                String email=m.getString("email");
                JSONObject phone=m.getJSONObject("phone");
                String mobile=phone.getString("mobile");
                //let us push above data to array list <contact>
                Contact c=new Contact();
                c.setName(name);
                c.setEmail(email);
                c.setMobile(mobile);
                c.setSno(i+1);
// now push contact object to array list
                arrayList.add(c);}
                myAdapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }

            super.onPostExecute(s);
        }
    }
    //7.b=create an inner calss for custom adapter
    public  class MyAdapter extends BaseAdapter{

        /**
         * How many items are in the data set represented by this Adapter.
         *
         * @return Count of items.
         */
        @Override
        public int getCount() {
            return arrayList.size();
        }


        @Override
        public Object getItem(int position) {
            return arrayList.get(position);
        }


        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Contact c=arrayList.get(position);
            View v=getActivity().getLayoutInflater().inflate(R.layout.row,null);//b.load row
            TextView  t1 = (TextView)v.findViewById(R.id.tv1);
            TextView  t2 = (TextView)v.findViewById(R.id.tv2);
            TextView  t3 = (TextView)v.findViewById(R.id.tv3);
            TextView  t4 = (TextView)v.findViewById(R.id.tv4);
        //fill data into above views-use getters
            t1.setText(""+c.getSno());
            t2.setText(""+c.getName());
            t3.setText(""+c.getEmail());
            t4.setText(""+c.getMobile());
            return v;
        }
    }

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_main, container, false);
        //9 decalre all variables
        listView= (ListView) v.findViewById(R.id.listview1);
        button= (Button) v.findViewById(R.id.button1);
        myAdapter = new MyAdapter();
        myTask=new MyTask();
        arrayList=new ArrayList<Contact>();
        listView.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //11 check internet is available or not
                if (checkInternet()) {

                    myTask.execute("http://api.androidhive.info/contacts");
                }else {
                    Toast.makeText(getActivity(), "available", Toast.LENGTH_SHORT).show();
                }

            }
        });
        return v;
    }
}
