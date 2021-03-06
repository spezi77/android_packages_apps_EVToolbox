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

package com.spezi77.toolbox.custom;

public class DrawerItem {

    private String mTitle;
    private int mIconResource;
    private boolean mIsHeader;

    public DrawerItem(String title, int iconRes, boolean isHeader) {
        mTitle = title;
        mIconResource = iconRes;
        mIsHeader = isHeader;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getIconResource() {
        return mIconResource;
    }

    public boolean isHeader() {
        return mIsHeader;
    }

}
