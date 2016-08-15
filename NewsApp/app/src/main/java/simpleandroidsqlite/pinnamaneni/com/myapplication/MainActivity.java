package simpleandroidsqlite.pinnamaneni.com.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import simpleandroidsqlite.pinnamaneni.com.myapplication.Pojos.NewYork;
import simpleandroidsqlite.pinnamaneni.com.myapplication.Pojos.Result;

/**
 * Created by pinnamak on 8/12/16.
 */

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private ProgressDialog pDialog;
    private RequestQueue mRequestQueue;
    public static final String URL = "https://api.nytimes.com/svc/topstories/v2/home.json?api-key=340fe1949bbc2b893c4a336bb072412a:18:74255139";
    private RecyclerView recyclerView;
    private static NewsAdapter mAdapter;
    public static List<Result> newsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        final ImageView searchIcon = (ImageView) findViewById(R.id.searchIcon);
        makeJsonObjReq();
        final SearchView searchBar = (SearchView) findViewById(R.id.search_bar);
        searchBar.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    searchIcon.setVisibility(View.GONE);
                } else {
                    searchIcon.setVisibility(View.VISIBLE);
                }
            }
        });
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub

                Toast.makeText(getBaseContext(), query,
                        Toast.LENGTH_SHORT).show();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });


        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Object status = view.getTag(view.getId());
                if (status == null || status.toString().equalsIgnoreCase("open")) {
                    searchIcon.setImageResource(R.drawable.ic_close_search);
                    searchBar.setVisibility(View.VISIBLE);
                    searchBar.setFocusable(true);
                    searchBar.setIconified(false);
                    searchBar.setOnCloseListener(new SearchView.OnCloseListener() {
                        @Override
                        public boolean onClose() {
                            searchIcon.setVisibility(View.VISIBLE);
                            searchBar.setVisibility(View.GONE);
                            searchIcon.setImageResource(R.drawable.ic_search);
                            view.setTag(view.getId(), "open");
                            return false;
                        }
                    });
                    searchBar.requestFocusFromTouch();
                    view.setTag(view.getId(), "close");

                } else {
                    searchIcon.setImageResource(R.drawable.ic_search);
                    searchBar.setVisibility(View.GONE);
                    view.setTag(view.getId(), "open");
                }
            }
        });
    }

    /**
     * Making json object request
     */
    private void makeJsonObjReq() {
        showProgressDialog();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                URL, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        GsonBuilder gsonBuilder = new GsonBuilder();

                        Gson gson = gsonBuilder.create();

                        NewYork newYork = gson.fromJson(response.toString(), NewYork.class);
                        MainActivity.newsList = newYork.getResults();

                        mAdapter = new NewsAdapter(newsList);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(mAdapter);
                        Log.d(TAG, newsList.get(0).toString());
                        hideProgressDialog();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hideProgressDialog();
            }
        }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };

        // Adding request to request queue
        addToRequestQueue(jsonObjReq,
                URL);

    }


    private void showProgressDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideProgressDialog() {
        if (pDialog.isShowing())
            pDialog.hide();
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

}
