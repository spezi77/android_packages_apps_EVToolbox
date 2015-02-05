/*
 * Copyright (C) 2012 The CyanogenMod Project
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

package com.spezi77.toolbox.receivers;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemProperties;
import android.preference.PreferenceManager;
import android.util.Log;

import com.spezi77.toolbox.R;
import com.spezi77.toolbox.Toolbox;
import com.spezi77.toolbox.categories.DeviceCategory;
import com.spezi77.toolbox.fragments.PerformanceMemory;
import com.spezi77.toolbox.fragments.PerformanceProcessor;
import com.spezi77.toolbox.fragments.SystemNetwork;
import com.spezi77.toolbox.misc.FileUtil;

import java.util.Arrays;
import java.util.List;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "Toolbox";

    private static final String CPU_SETTINGS_PROP = "sys.cpufreq.restored";
    private static final String KSM_SETTINGS_PROP = "sys.ksm.restored";
    private static final String ZRAM_SETTINGS_PROP = "sys.zram.restored";
    private static final String SSHD_SETTINGS_PROP = "sys.sshd.restored";

    @Override
    public void onReceive(Context ctx, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

            if (!Toolbox.isEnabled(ctx)) {
                return;
            }

            if (!SystemProperties.getBoolean(CPU_SETTINGS_PROP, false)) {
                SystemProperties.set(CPU_SETTINGS_PROP, "true");
                configureCPU(ctx);
            } else {
                SystemProperties.set(CPU_SETTINGS_PROP, "false");
            }

            if (FileUtil.fileExists(PerformanceMemory.KSM_RUN_FILE)) {
                if (!SystemProperties.getBoolean(KSM_SETTINGS_PROP, false)) {
                    SystemProperties.set(KSM_SETTINGS_PROP, "true");
                    configureKSM(ctx);
                } else {
                    SystemProperties.set(KSM_SETTINGS_PROP, "false");
                }
            }

            if (!SystemProperties.getBoolean(ZRAM_SETTINGS_PROP, false)) {
                maybeEnableZram(ctx);
            }

            if (!SystemProperties.getBoolean(SSHD_SETTINGS_PROP, false)) {
                maybeEnableSshd(ctx);
            }

            DeviceCategory.restoreSettings(ctx);
        }
    }

    private void initFreqCapFiles(Context ctx)
    {
        if (PerformanceProcessor.freqCapFilesInitialized) return;
        PerformanceProcessor.FREQ_MAX_FILE = ctx.getResources().getString(R.string.pref_max_cpu_freq_file);
        PerformanceProcessor.FREQ_MIN_FILE = ctx.getResources().getString(R.string.pref_min_cpu_freq_file);
        PerformanceProcessor.freqCapFilesInitialized = true;
    }

    private void configureCPU(Context ctx) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);

        if (!prefs.getBoolean(PerformanceProcessor.SOB_PREF, false)) {
            Log.i(TAG, "Restore disabled by user preference.");
            return;
        }

        String governor = prefs.getString(PerformanceProcessor.GOV_PREF, null);
        String minFrequency = prefs.getString(PerformanceProcessor.FREQ_MIN_PREF, null);
        String maxFrequency = prefs.getString(PerformanceProcessor.FREQ_MAX_PREF, null);
        String availableFrequenciesLine = FileUtil.fileReadOneLine(PerformanceProcessor.FREQ_LIST_FILE);
        String availableGovernorsLine = FileUtil.fileReadOneLine(PerformanceProcessor.GOV_LIST_FILE);
        boolean noSettings = ((availableGovernorsLine == null) || (governor == null)) &&
                             ((availableFrequenciesLine == null) || ((minFrequency == null) && (maxFrequency == null)));
        List<String> frequencies = null;
        List<String> governors = null;

        if (noSettings) {
            Log.d(TAG, "No CPU settings saved. Nothing to restore.");
        } else {
            initFreqCapFiles(ctx);
            if (availableGovernorsLine != null){
                governors = Arrays.asList(availableGovernorsLine.split(" "));
            }
            if (availableFrequenciesLine != null){
                frequencies = Arrays.asList(availableFrequenciesLine.split(" "));
            }
            if (governor != null && governors != null && governors.contains(governor)) {
                FileUtil.fileWriteOneLine(PerformanceProcessor.GOV_FILE, governor);
                SystemProperties.set(PerformanceProcessor.GOV_CHANGED_PROP, governor);
            }
            if (maxFrequency != null && frequencies != null && frequencies.contains(maxFrequency)) {
                FileUtil.fileWriteOneLine(PerformanceProcessor.FREQ_MAX_FILE, maxFrequency);
            }
            if (minFrequency != null && frequencies != null && frequencies.contains(minFrequency)) {
                FileUtil.fileWriteOneLine(PerformanceProcessor.FREQ_MIN_FILE, minFrequency);
            }
            Log.d(TAG, "CPU settings restored.");
        }
    }

    private void configureKSM(Context ctx) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);

        boolean ksm = prefs.getBoolean(PerformanceMemory.KSM_PREF, ActivityManager.isLowRamDeviceStatic());

        FileUtil.fileWriteOneLine(PerformanceMemory.KSM_RUN_FILE, ksm ? "1" : "0");
        Log.d(TAG, "KSM settings restored.");
    }

    private void maybeEnableZram(Context ctx) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);

        if (PerformanceMemory.isZramAvailable()
                && Integer.valueOf(prefs.getString(PerformanceMemory.ZRAM_PREF, "0")) > 0
                && FileUtil.fileExists(PerformanceMemory.ZRAM_FSTAB_FILENAME)) {
            SystemProperties.set(ZRAM_SETTINGS_PROP, "true");
        } else {
            SystemProperties.set(ZRAM_SETTINGS_PROP, "false");
        }
    }

    private void maybeEnableSshd(Context ctx) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);

        if (prefs.getBoolean(SystemNetwork.PREF_SSHD, false)) {
            SystemProperties.set("ctl.start", "sshd");
            SystemProperties.set(SSHD_SETTINGS_PROP, "true");
        } else {
            SystemProperties.set(SSHD_SETTINGS_PROP, "false");
        }
    }

}
