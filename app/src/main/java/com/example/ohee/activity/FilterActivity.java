package com.example.ohee.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.example.ohee.R;
import com.example.ohee.adapter.AdapterFilters;
import com.example.ohee.helpers.RecyclerItemClickListener;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.util.ArrayList;
import java.util.List;

public class FilterActivity extends AppCompatActivity {

    static
    {
        System.loadLibrary("NativeImageProcessor");
    }

    private ImageView imgSelected;
    private Bitmap imgOriginal;
    private Bitmap imgFilter;
    private RecyclerView recyclerFilters;

    private AdapterFilters adapter;

    private List<ThumbnailItem> listFilters = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        imgSelected = findViewById(R.id.imgSelected);
        recyclerFilters = findViewById(R.id.recyclerFilters);

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Post");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_navigation_close);

        // Getting the img
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            byte[] imgData = bundle.getByteArray("selectedPic");
            imgOriginal = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
            imgSelected.setImageBitmap(imgOriginal);

            // Set adapter
            adapter = new AdapterFilters(listFilters, getApplicationContext());

            // Set recycler
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            recyclerFilters.setLayoutManager(layoutManager);
            recyclerFilters.setHasFixedSize(true);
            recyclerFilters.setAdapter(adapter);

            // Set recycler click
            recyclerFilters.addOnItemTouchListener(
                    new RecyclerItemClickListener(
                            getApplicationContext(),
                            recyclerFilters,
                            new RecyclerItemClickListener.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    ThumbnailItem item = listFilters.get(position);

                                    imgFilter = imgOriginal.copy(imgOriginal.getConfig(), true);
                                    Filter filter = item.filter;
                                    imgSelected.setImageBitmap(filter.processFilter(imgFilter));
                                }

                                @Override
                                public void onLongItemClick(View view, int position) {

                                }

                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                }
                            }
                    )
            );

            getFilters();
        }
    }

    private void getFilters() {
        listFilters.clear();
        ThumbnailsManager.clearThumbs();

        // Set no filter
        ThumbnailItem noFilter = new ThumbnailItem();
        noFilter.image = imgOriginal;
        noFilter.filterName = "Original";
        ThumbnailsManager.addThumb(noFilter);

        List<Filter> filters = FilterPack.getFilterPack(getApplicationContext());

        for (Filter filter : filters) {
            ThumbnailItem item = new ThumbnailItem();
            item.image = imgOriginal;
            item.filter = filter;
            item.filterName = filter.getName();
            ThumbnailsManager.addThumb(item);
        }

        listFilters.addAll(ThumbnailsManager.processThumbs(getApplicationContext()));
        adapter.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_post, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btPost:

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
