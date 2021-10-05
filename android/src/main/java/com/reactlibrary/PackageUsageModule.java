// PackageUsageModule.java

package com.reactlibrary;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.os.Build;
import android.os.Process;
import android.provider.Settings;
import android.widget.Toast;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;

public class PackageUsageModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;

    public PackageUsageModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "PackageUsage";
    }

    @ReactMethod
    public void sampleMethod(String stringArgument, int numberArgument, Callback callback) {
        // TODO: Implement some actually useful functionality
        callback.invoke("Received numberArgument: " + numberArgument + " stringArgument: " + stringArgument);
    }

    @ReactMethod
    public void getUseAppList(Callback callback) {
        // 퍼미션 체크
        AppOpsManager appOps = (AppOpsManager) reactContext.getSystemService(reactContext.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), reactContext.getPackageName());

        if (mode != AppOpsManager.MODE_ALLOWED) {
            Toast.makeText(
                    reactContext,
                    "Failed to retrieve app usage statistics. " +
                            "You may need to enable access for this app through " +
                            "Settings > Security > Apps with usage access",
                    Toast.LENGTH_LONG
            ).show();


//            reactContext.startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
            callback.invoke("사용 권한이 없습니다. " +
                    "다음 경로에서 사용 추적 허용을 켜주세요." +
                    "Settings > Security > 사용정보 접근 허용",null);

        } else {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, -1);    // 1

            UsageStatsManager usageStatsManager = (UsageStatsManager)reactContext.getSystemService(reactContext.USAGE_STATS_SERVICE);

            List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(
                    UsageStatsManager.INTERVAL_DAILY, cal.getTimeInMillis(), System.currentTimeMillis()
            );

            JSONArray jsonArray = new JSONArray();

            for(UsageStats usItem: queryUsageStats) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("getPackageName",usItem.getPackageName());
                    jsonObject.put("getFirstTimeStamp",usItem.getFirstTimeStamp());
                    jsonObject.put("getLastTimeStamp",usItem.getLastTimeStamp());
                    jsonObject.put("getLastTimeUsed",usItem.getLastTimeUsed());
                    jsonObject.put("getTotalTimeInForeground",usItem.getTotalTimeInForeground());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        jsonObject.put("getLastTimeVisible",usItem.getLastTimeVisible());
                        jsonObject.put("getLastTimeForegroundServiceUsed",usItem.getLastTimeForegroundServiceUsed());
                        jsonObject.put("getTotalTimeForegroundServiceUsed",usItem.getTotalTimeForegroundServiceUsed());
                        jsonObject.put("getTotalTimeVisible",usItem.getTotalTimeVisible());
                    }

                    jsonArray.put((jsonObject));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            callback.invoke("success",jsonArray.toString());
        }
    }
}
