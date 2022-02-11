# VersionControl

In your project level  `build.gradle`:

```groovy
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

In your app level `build.gradle`:

```groovy
dependencies {
	 implementation 'com.github.Appyhigh:VersionControl:1.0.3'
}
```
## Initialization

// Pass activity context, any view from that activity, your app version code and create a listener and pass it. This listener will help you listen th update type and take actions accordingly

```kotlin
VersionControlSdk.initializeSdk(
    this,
    findViewById(R.id.tvhello),
    BuildConfig.VERSION_CODE,
    versionControlListener
)

versionControlListener = object : VersionControlListener {
    override fun onUpdateDetectionSuccess(updateType: VersionControlConstants.UpdateType) {
        when (updateType) {
            VersionControlConstants.UpdateType.SOFT_UPDATE -> {
            }
            VersionControlConstants.UpdateType.HARD_UPDATE -> {
            }
            else -> {
            }
        }
    }
}
```

In your onActivityResult method add this. This is important for Hard Update to work properly:

```kotlin
if (requestCode == VersionControlSdk.MY_REQUEST_CODE) {
    if (resultCode != RESULT_OK) {
        VersionControlSdk.initializeSdk(
            this,
            findViewById(R.id.tvhello),
            BuildConfig.VERSION_CODE,
            versionControlListener
        )
    }
}
```

