package com.project.newsapp.activities;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.newsapp.R;
import com.project.newsapp.adapters.AdapterListNews;
import com.project.newsapp.clicklisteners.AdapterItemClickListener;
import com.project.newsapp.clicklisteners.NewsDialogClickListeners;
import com.project.newsapp.databinding.NewsDialogBinding;
import com.project.newsapp.model.News;
import com.project.newsapp.utils.LocaleHelper;
import com.project.newsapp.utils.Util;
import com.project.newsapp.viewmodels.MainViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LifecycleOwner, AdapterItemClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    MainActivity context;
    MainViewModel viewModel;
    AdapterListNews adapterListNews;
    List<News> newsList;
    private String countryPositionPref = "countryPositionPref";
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getApplicationContext().getSharedPreferences(Util.APP_NAME, MODE_PRIVATE);
        setContentView(R.layout.activity_main);
        context = this;
        ButterKnife.bind(this);
        initToolbar();

        newsList = new ArrayList<>();
        adapterListNews = new AdapterListNews(newsList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapterListNews);


        if (pref.contains(countryPositionPref))
        viewModel = ViewModelProviders.of(context).get(MainViewModel.class);
        viewModel.getNewsLiveData().observe(context, newsListUpdateObserver);
        viewModel.setApiKey(getString(R.string.news_api_key));
        viewModel.setCountryCode(pref.getString(Util.COUNTRY_PREF, "tr"));
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Util.setSystemBarColor(this, android.R.color.white);
        Util.setSystemBarLight(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        Util.changeMenuIconColor(menu, Color.BLACK);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));
        }
        searchView.setQueryHint(getString(R.string.search_in_everything));
        if (searchView != null)
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    if (viewModel != null) viewModel.searchNews(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
        return true;
    }

    public void categoryClicked(View view) {
        viewModel.newsCategoryClick(String.valueOf(view.getTag()));
    }

    Observer<List<News>> newsListUpdateObserver = new Observer<List<News>>() {
        @Override
        public void onChanged(List<News> news) {
            newsList.clear();
            if (news != null) {
                newsList.addAll(news);
            }
            adapterListNews.notifyDataSetChanged();
        }
    };


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    public void onNewsItemClick(News news) {
        showDialogPolygon(news);
    }

    private void showDialogPolygon(News news) {
        final Dialog dialog = new Dialog(this);
        NewsDialogBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.dialog_header_polygon, null, false);
        binding.setNews(news);
        binding.setListener(new NewsDialogClickListeners() {
            @Override
            public void onGotoWebSiteClick(String url) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }

            @Override
            public void onDismissClick() {
                dialog.dismiss();
            }
        });

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        dialog.show();
    }

}
