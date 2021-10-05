# react-native-package-usage

✅ Only Android

✅ minSdkVersion ≥ 29

🚫 yarn/npm install

> AndroidManifest.xml
> 

```jsx
<uses-permission
        android:name="android.permission.PACKAGE_USAGE_STATS"
        tools:ignore="ProtectedPermissions" />
```

> example/android/PackageUsageModule.java
> 

```jsx
public void getUseAppList(Callback callback) {
	...
}
```

# 사용 방법

```jsx
import PackageUsage from 'react-native-package-usage';

PackageUsage.getUseAppList((msg, appList) => {
      if (msg === 'success' && appList !== null) {
        const toJson = JSON.parse(appList);
        console.log('appList : ' + JSON.stringify(toJson));
      } else {
        Alert.alert('', msg);
      }
    });
```