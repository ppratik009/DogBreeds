package com.example.dogbreed;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
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
import java.util.List;

public class ActivityImages extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    public List<DogBreedImagesModel> modelLists = new ArrayList<>();
    public MyAdapter mAdapter;
    private ProgressDialog progressDialog;

    private String breedName;
    private Bundle extras;
    public GridLayoutManager lLayout;
    public int newSpanCount=3;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        toolbar =findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        setSupportActionBar(toolbar);


        mRecyclerView = findViewById(R.id.recycleView);
        mRecyclerView.setHasFixedSize(true);
        lLayout = new GridLayoutManager(this, newSpanCount);
        mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int viewWidth = mRecyclerView.getMeasuredWidth();
                        float cardViewWidth = 230;
                        newSpanCount = (int) Math.floor(viewWidth / cardViewWidth);
                        lLayout.setSpanCount(newSpanCount);
                        lLayout.requestLayout();
                    }
                });
        mRecyclerView.setLayoutManager(lLayout);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(2));
        mAdapter = new MyAdapter(modelLists);
        mRecyclerView.setAdapter(mAdapter);



        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        extras=getIntent().getExtras();
        if(extras.containsKey("name")){
            breedName=extras.getString("name");
            setTitle(breedName);
        }
        else {
            Intent showPhotoIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(showPhotoIntent);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fetchJsonDataForDogBreedsImages(breedName);
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
                fetchJsonDataForDogBreedsImages(breedName);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchJsonDataForDogBreedsImages(final String breedName){
        showDialog();
        progressDialog.setMessage("Please wait...");
        String tag_string_req = "req_dog_breed_images";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                "https://dog.ceo/api/breed/"+breedName+"/images", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    String status = jObj.getString("status");
                    if (status.equals("success")) {
                        JSONArray ja = jObj.getJSONArray("message");
                        for (int i = 0; i < ja.length(); i++) {
                            String image = ja.getString(i);
                            DogBreedImagesModel mymodel = new DogBreedImagesModel(image);
                            modelLists.add(mymodel);
                        }
                    }
                    mAdapter.notifyDataSetChanged();



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
    private class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int TYPE_ITEM = 1;
        private List<DogBreedImagesModel> dogBreedModelList;
        private DogBreedImagesModel myModel;

        public MyAdapter(List<DogBreedImagesModel> mList) {
            this.dogBreedModelList = mList;
        }

        @Override
        public int getItemViewType(int position) {
            return TYPE_ITEM;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

            if (viewType == TYPE_ITEM) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_images, viewGroup, false);
                return new ItemViewHolder(view);
            }

            throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");

        }
        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            if (holder instanceof ItemViewHolder) {
                myModel = dogBreedModelList.get(position);
                ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
                Picasso.with(getApplicationContext()).load(myModel.getImage()).into(itemViewHolder.dog_image);
            }
        }
        @Override
        public int getItemCount() {
            return this.dogBreedModelList.size();
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder{
            public ImageView dog_image;
            public TextView dog_name,view_moretxt;
            public ItemViewHolder(View v) {
                super(v);
                dog_image= v.findViewById(R.id.imageView);
            }
        }

    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.top = space;
            } else {
                outRect.top = space;
            }
        }
    }
}
