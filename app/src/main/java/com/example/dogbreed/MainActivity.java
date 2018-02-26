package com.example.dogbreed;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.dogbreed.app.AppController;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    public List<DogBreedModel> modelLists = new ArrayList<>();
    public MyAdapter mAdapter;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar =findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Dog Breeds");

        mRecyclerView = findViewById(R.id.recycleView);
        mRecyclerView.setHasFixedSize(true);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyAdapter(modelLists);
        mRecyclerView.setAdapter(mAdapter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        fetchJsonDataForDogBreeds();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                modelLists.clear();
                fetchJsonDataForDogBreeds();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchJsonDataForDogBreeds(){
        showDialog();
        progressDialog.setMessage("Please wait...");
        String tag_string_req = "req_dog_breed";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                "https://dog.ceo/api/breeds/list", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    String status = jObj.getString("status");
                    if (status.equals("success")) {
                        JSONArray ja = jObj.getJSONArray("message");
                        for (int i = 0; i < ja.length(); i++) {
                            String name = ja.getString(i);
                            fetchJsonDataForDogBreedsImages(name);
                        }


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
                Toast.makeText(getApplicationContext(),"ERROR LOADING DATA",Toast.LENGTH_LONG).show();
            }
        });

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void fetchJsonDataForDogBreedsImages(final String breedName){
        String tag_string_req = "req_dog_breed_images";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                "https://dog.ceo/api/breed/"+breedName+"/images", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    String status = jObj.getString("status");
                    if (status.equals("success")) {
                        JSONArray ja = jObj.getJSONArray("message");

                        String image1 = ja.getString(0);
                        String image2 = ja.getString(1);
                        String image3 = ja.getString(2);
                        DogBreedModel mymodel = new DogBreedModel(breedName,image1,image2,image3);
                        modelLists.add(mymodel);

                    }
                    mAdapter.notifyDataSetChanged();



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int TYPE_ITEM = 1;
        private List<DogBreedModel> dogBreedModelList;
        private DogBreedModel myModel;

        public MyAdapter(List<DogBreedModel> mList) {
            this.dogBreedModelList = mList;
        }

        @Override
        public int getItemViewType(int position) {
            return TYPE_ITEM;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

            if (viewType == TYPE_ITEM) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);
                return new ItemViewHolder(view);
            }

            throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");

        }
        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            if (holder instanceof ItemViewHolder) {
                myModel = dogBreedModelList.get(position);
                ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
                itemViewHolder.dog_name.setText(myModel.getName());
                Picasso.with(getApplicationContext()).load(myModel.getImage1()).into(itemViewHolder.dog_image1);
                Picasso.with(getApplicationContext()).load(myModel.getImage2()).into(itemViewHolder.dog_image2);
                Picasso.with(getApplicationContext()).load(myModel.getImage3()).into(itemViewHolder.dog_image3);
            }
        }
        @Override
        public int getItemCount() {
            return this.dogBreedModelList.size();
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            public ImageView dog_image1,dog_image2,dog_image3;
            public TextView dog_name,view_moretxt;
            public ItemViewHolder(View v) {
                super(v);
                dog_image1= v.findViewById(R.id.imageView1);
                dog_image2= v.findViewById(R.id.imageView2);
                dog_image3= v.findViewById(R.id.imageView3);
                dog_name= v.findViewById(R.id.breed_name);
                view_moretxt= v.findViewById(R.id.view_more);
                view_moretxt.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                AppController.getInstance().cancelPendingRequests();
                myModel = dogBreedModelList.get(getAdapterPosition());
                Intent showPhotoIntent = new Intent(getApplicationContext(), ActivityImages.class);
                showPhotoIntent.putExtra("name", myModel.getName());
                startActivity(showPhotoIntent);
            }
        }

    }
}
