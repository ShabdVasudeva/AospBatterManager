package org.aospbased.battery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.divider.MaterialDivider;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.color.utilities.DynamicColor;
import org.aospbased.battery.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private BroadcastReceiver mBatInfoReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context ctxt, Intent intent) {
                    int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                    int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                    int batteryPct = level * 100 / (int) scale;
                    String technology =
                            intent.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY);
                    int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
                    int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
                    binding.percentage.setText(String.valueOf(batteryPct));
                    binding.progressbar.setProgress(batteryPct);
                    binding.tech.setText(technology);
                    binding.temp.setText(temperature / 10.0 + "°c");
                    if (health == BatteryManager.BATTERY_HEALTH_COLD) {
                        binding.hlth.setText("Cold");
                    } else if (health == BatteryManager.BATTERY_HEALTH_GOOD) {
                        binding.hlth.setText("Good");
                    } else if (health == BatteryManager.BATTERY_HEALTH_DEAD) {
                        binding.hlth.setText("Dead");
                    } else if (health == BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE) {
                        binding.hlth.setText("Over Voltage");
                    } else if (health == BatteryManager.BATTERY_HEALTH_OVERHEAT) {
                        binding.hlth.setText("Over Heating");
                    } else {
                        binding.hlth.setText("Unknown");
                    }
                    if (batteryPct <= 30) {
                        binding.advice.setText("• the battery is low you should charge !!");
                    } else if (batteryPct <= 50) {
                        binding.advice.setText("• the battery is about half you can play more");
                    } else if (batteryPct <= 75) {
                        binding.advice.setText("• the battery may last upto 6-7 hrs");
                    } else if (batteryPct == 100) {
                        binding.advice.setText("• the battery if full");
                    } else if (batteryPct <= 99) {
                        binding.advice.setText("• the battery is almost full");
                    }
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(
                v -> {
                    onBackPressed();
                });
        this.registerReceiver(
                this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = this.registerReceiver(null, ifilter);
        Intent batteryTechnology = this.registerReceiver(null, ifilter);
        int status =
                batteryStatus != null
                        ? batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                        : -1;
        boolean isCharging =
                status == BatteryManager.BATTERY_STATUS_CHARGING
                        || status == BatteryManager.BATTERY_STATUS_FULL;
        if (isCharging) {
            binding.advice.setText("Charging rapidly • please wait untill full charge");
            binding.status.setText("charging");
        } else {
            binding.status.setText("Not Charging");
        }
        binding.powersv.setOnClickListener(
                v -> {
                    startActivity(new Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS));
                });
        binding.usage.setOnClickListener(
                v -> {
                    Intent powerUsageIntent = new Intent(Intent.ACTION_POWER_USAGE_SUMMARY);
                    ResolveInfo resolveInfo =
                            getPackageManager().resolveActivity(powerUsageIntent, 0);
                    if (resolveInfo != null) {
                        startActivity(powerUsageIntent);
                    }
                });
        int currentNightMode =
                getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                binding.toolbar.setNavigationIconTint(Color.BLACK);
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                binding.divide.setDividerColor(Color.WHITE);
                binding.divide2.setDividerColor(Color.WHITE);
                binding.divide3.setDividerColor(Color.WHITE);
                binding.divide4.setDividerColor(Color.WHITE);
                binding.divide5.setDividerColor(Color.WHITE);
                binding.toolbar.setNavigationIconTint(Color.WHITE);
                binding.info.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }
}
