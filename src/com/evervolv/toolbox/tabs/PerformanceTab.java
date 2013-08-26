/*
 * Copyright (C) 2013 The Evervolv Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.evervolv.toolbox.tabs;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v13.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.evervolv.toolbox.R;
import com.evervolv.toolbox.custom.PagerFragment;
import com.evervolv.toolbox.fragments.PerformanceMemory;
import com.evervolv.toolbox.fragments.PerformanceProcessor;

public class PerformanceTab extends PagerFragment {

    public PerformanceTab() {
        super();
    }

    public PerformanceTab(String title, int pageIndex) {
        super(title, pageIndex);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Resources res = getResources();
        mTabHost = new FragmentTabHost(getActivity());
        mTabHost.setup(getActivity(), getChildFragmentManager(), container.getId());

        mTabHost.addTab(mTabHost.newTabSpec("processor").setIndicator(
                res.getString(R.string.performance_processor_title)),
                PerformanceProcessor.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("memory").setIndicator(
                res.getString(R.string.performance_memory_title)),
                PerformanceMemory.class, null);
        mTabHost.setOnTabChangedListener(this);
        return mTabHost;
    }

}