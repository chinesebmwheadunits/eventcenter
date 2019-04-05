# Readme for the Dexpatcher EventCenter Android Studio project

This Android Studio project patches an EventCenter Apk from an
Ugode (or similar) Android BMW Head unit to remove it's process
killing abilities. These head units are severely crippled as
any process that is not a foreground process is automatically
killed.

## Getting The original APK from the device

Copy /system/EventCenter.apk with ES file explorer or something
similar to either the SD card or an USB stick. You will need the
APK file for the next step.

## Cloning The dexpatcher tools repository

Navigate to https://github.com/DexPatcher/dexpatcher-gradle-tools
and clone the repository to a local directory on your hard drive.

## Patching the Apk

Copy the original EventCenter.Apk file to the source\apk subdirectory
relative to this Readme file and the root of the project.

Add a local.properties file to the root of the project with the following content:

```
## This file must *NOT* be checked into Version Control Systems,
# as it contains information specific to your local configuration.
#
# Location of the SDK. This is only used by Gradle.
# For customization when using a Version Control System, please read the
# header note.
#Wed Apr 03 13:31:10 CEST 2019
sdk.dir=D\:\\Android\\sdk

dexpatcher.dir=D\:\\ugode\\dexpatcher-gradle-tools
```

Replace the SDK directory with the directory to the Android SDK.
Replace the dexpatcher.dir with the directory where you just cloned the dexpatcher
tools repository.

Now open Android Studio, open the project and let it sync. If it asks to update the
Gradle plugin, select NO.

Using the Build variant, switch to a release build.

Running Build -> Make Project will build the project. It will generate an apk file named:

Sign the Apk using Build -> Generate Signed APK. Use your own key. For more info:

https://developer.android.com/studio/publish/app-signing

The apk file will be in the patched directory and will be called patched-release.apk.
Rename this file to EventCenter.apk
