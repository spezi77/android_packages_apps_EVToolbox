package com.spezi77.toolbox.superuser;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v4.widget.SlidingPaneLayout.PanelSlideListener;
import android.view.MenuItem;
import android.view.View;

import com.spezi77.toolbox.R;

public class SuperuserAppActivity extends Activity {

    private SlidingPaneLayout mSlidingPaneLayout;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.superuser_app_list_activity);
        setTitle(R.string.superuser_app_policies_title);
        getActionBar().setHomeButtonEnabled(true);

        mSlidingPaneLayout = (SlidingPaneLayout) findViewById(R.id.sliding_pane);
        mSlidingPaneLayout.setParallaxDistance(200);
        mSlidingPaneLayout.setShadowResource(R.drawable.sliding_pane_shadow);
        /*
         * TODO: I don't like the way it looks with a fade color
         * unless we figure a way to make it look good, this is decent.
         */
        mSlidingPaneLayout.setSliderFadeColor(Color.parseColor("#00000000"));
        mSlidingPaneLayout.setPanelSlideListener(new PanelSlideListener() {
            AppInfoFragment infoFrag = (AppInfoFragment) getFragmentManager()
                    .findFragmentById(R.id.info_pane);
            @Override
            public void onPanelClosed(View panel) {
                infoFrag.setHasOptionsMenu(true);
                setTitle(infoFrag.getCurrentPolicyName());
            }

            @Override
            public void onPanelOpened(View panel) {
                infoFrag.setHasOptionsMenu(false);
                setTitle(R.string.superuser_app_policies_title);
            }

            @Override
            public void onPanelSlide(View view, float v) { }
        });

        mSlidingPaneLayout.openPane();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void togglePane() {
        if (mSlidingPaneLayout.isOpen()) {
            mSlidingPaneLayout.closePane();
        } else {
            mSlidingPaneLayout.openPane();
        }
    }
    
}
