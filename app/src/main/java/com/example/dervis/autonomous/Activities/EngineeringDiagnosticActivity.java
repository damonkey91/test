package com.example.dervis.autonomous.Activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dervis.autonomous.Constants.ListItems;
import com.example.dervis.autonomous.Constants.SocketObjects;
import com.example.dervis.autonomous.Helpers.GsonConv;
import com.example.dervis.autonomous.Helpers.ResourceGetter;
import com.example.dervis.autonomous.Objects.ConnectedSocketObj;
import com.example.dervis.autonomous.Objects.ListObjIcon;
import com.example.dervis.autonomous.R;
import com.example.dervis.autonomous.RecyclerView.RecyclerListAdapterDiagnostic;
import com.example.dervis.autonomous.ViewModels.MainViewModel;

import java.util.HashMap;

public class EngineeringDiagnosticActivity extends AppCompatActivity {

    MainViewModel viewModel;
    RecyclerView listView;
    RecyclerListAdapterDiagnostic listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_engineering_diagnostic);
        setHeaderText();

        listView = findViewById(R.id.recyclerViewEngineeringDia);
        listAdapter = new RecyclerListAdapterDiagnostic(ListItems.objListEngineeringDiagnostic);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        listView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        listView.setLayoutManager(mLayoutManager);
        listView.setItemAnimator(new DefaultItemAnimator());
        listView.setAdapter(listAdapter);

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getConnectedSockets().observe(this, connectedSocketObjObserver);
        viewModel.startDataGathering(SocketObjects.ENGINEERING_DIAGNOSTIC_SOCKETOBJ_LIST);

        viewModel.getUpdateTime().observe(this, timeObserver);
    }

    private void setHeaderText() {
        String json = getIntent().getStringExtra(MainActivity.HEADER);
        ListObjIcon obj = GsonConv.toObject(json);
        ((TextView)findViewById(R.id.activityTitle)).setText(obj.title);
        ((TextView) findViewById(R.id.activityDescription)).setText(obj.description);
        ((ImageView) findViewById(R.id.activityIcon)).setImageDrawable(ResourceGetter.getDrawable(obj.iconId));
    }

    /**
     * takes user back to previous activity and closes this activity
     * @param view view
     */
    public void clickBackArrow(View view) {
        finish();
        overridePendingTransition(R.anim.enter_back_anim, R.anim.exit_back_anim);
    }

    final Observer<ConnectedSocketObj> connectedSocketObjObserver = new Observer<ConnectedSocketObj>() {
        @Override
        public void onChanged(@Nullable ConnectedSocketObj connectedSocketObj) {
            listAdapter.updateView(connectedSocketObj);
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        viewModel.killSubscriberThreads();
    }

    final Observer<String> timeObserver = new Observer<String>() {
        @Override
        public void onChanged(@Nullable String s) {
            ((TextView) findViewById(R.id.timeStampTextView)).setText(s);
        }
    };
}
