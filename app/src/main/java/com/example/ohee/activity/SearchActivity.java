package com.example.ohee.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.ohee.R;
import com.example.ohee.fragment.ChatsFragment;
import com.example.ohee.fragment.ContactsFragment;
import com.example.ohee.fragment.UniversitySearchFragment;
import com.example.ohee.fragment.UserSearchFragment;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class SearchActivity extends AppCompatActivity {
    private MaterialSearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        toolbar.inflateMenu(R.menu.menu_main);

        final FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(),
                FragmentPagerItems.with(this)
                        .add("Users", UserSearchFragment.class)
                        .add("Universities", UniversitySearchFragment.class)
                        .create());
        final ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = findViewById(R.id.viewPagerTab);
        viewPagerTab.setViewPager(viewPager);

        searchView = findViewById(R.id.materialSearchPrincipal);

        //listener para searchview
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                UserSearchFragment fragment = (UserSearchFragment) adapter.getPage(0);
                fragment.reloadUsers();
            }
        });

        //listener para caixa de texto
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if (viewPager.getCurrentItem() == 0) {
                    UserSearchFragment fragment = (UserSearchFragment) adapter.getPage(0);
                    if (newText != null && !newText.isEmpty()) {
                        fragment.searchUser(newText.toLowerCase());
                    }
                    else {
                        fragment.reloadUsers();
                    }
                    return true;
                } else {
                    UniversitySearchFragment fragment = (UniversitySearchFragment) adapter.getPage(1);
                    if (newText != null && !newText.isEmpty()) {
                        fragment.searchUniversity(newText.toLowerCase());
                    } else {
                        fragment.reloadUniversities();
                    }
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        //botao de pesquisa
        MenuItem item = menu.findItem(R.id.menuPesquisa);
        searchView.setMenuItem(item);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
