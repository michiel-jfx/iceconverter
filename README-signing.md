# Signing your application

## Create a keystore
Create different keystores for different applications, putting them in different base directories, starting with this
application (let's create an iceco folder).
```
mkdir ~/signing
cd ~/signing
mkdir iceco
cd iceco
keytool -genkey -v -keystore iceco-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias iceco-alias
```
The goal is to get the app in the Google Play Store, so put in information you’ll intend to use there.

The keystore called `iceco-release-key.jks` will be created (with an alias called `iceco-alias`), of course remember the
password, since it is used later to sign your app.

## Sign your deployments (simple version)
Assume the Java projects are in the `/pub/gitlab` folder and the project is called `iceconverter`, so the directory
`/pub/gitlab/iceconverter` is the default location for the project. Run the following in an IntelliJ terminal:
```
cd /pub/gitlab/iceconverter
mvn -Pandroid clean gluonfx:build gluonfx:package
cp target/gluonfx/aarch64-android/gvm/IceCo.apk ~/Downloads
cp target/gluonfx/aarch64-android/gvm/IceCo.aab ~/Downloads
```
The copy command assumes the presence of a `Downloads` folder in the home directory. Now, the app packages are available
to put on your mobile phone (look [here](https://www.dotjava.nl/2025/05/10/developer-debug-mode-for-android-devices/) on
how to get the packages on your phone). To get them in the store, they need to be signed.

Create a `signed` folder in the downloads:
```
mkdir ~/Downloads/signed
```

Sign the `.apk`:
```
cd $ANDROID_SDK/build-tools/34.0.0
./apksigner sign --ks ~/signing/iceco/iceco-release-key.jks --out ~/Downloads/signed/IceCo.apk /pub/gitlab/iceconverter/target/gluonfx/aarch64-android/gvm/IceCo.apk
./apksigner verify ~/Downloads/signed/IceCo.apk
cd -
```

Sign the `.aab`:
```
jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 -keystore ~/signing/iceco/iceco-release-key.jks -tsa http://timestamp.digicert.com target/gluonfx/aarch64-android/gvm/IceCo.aab iceco-alias
jarsigner -verify -verbose -certs target/gluonfx/aarch64-android/gvm/IceCo.aab
cp target/gluonfx/aarch64-android/gvm/IceCo.aab ~/Downloads/signed/
```
It says `The signer's certificate is self-signed`, but that should be no problem.

Now there are two signed packages in the `~/Downloads/signed` folder. Unfortunately, the Google Play Console does not
accept this `.aab` package. It says the `.aab` file contains debug information, which is probably from the building and
packaging process from the `gluonfx-maven-plugin`.

## Signing for Google Play Console
The first thing I tried was to change the `AndroidManifest.xml`, this line:
```
<application android:label='IceCo' android:icon="@mipmap/ic_launcher">
```
to this line:
```
<application android:debuggable='false' android:label='IceCo' android:icon="@mipmap/ic_launcher">
```
Also, I added the following two lines to the `pom.xml`:
```
<nativeImageArgs>
    <arg>-H:+UnlockExperimentalVMOptions</arg>
    <arg>-H:+StripDebugInfo</arg>
</nativeImageArgs>
```
To use the `StripDebugInfo`, the `UnlockExperimentalVMOptions` was necessary. And the following is necessary to avoid
duplicates in your uploads:
```
<releaseConfiguration>
    <appLabel>IceCo</appLabel>
    <versionCode>1</versionCode>
    <versionName>1.0</versionName>
</releaseConfiguration>
```
No luck. When you look at the target folder and specifically the `android_project` folder, you’ll find the build files
and I noticed the `AndroidManifest.xml` changes were not adopted here.

Furthermore, the build and packaging process showed the following two interesting lines passing by during the build:
```
[Sun Oct 05 07:58:27 UTC 2025][FINE] PB Command for package-task: /pub/gitlab/iceconverter/target/gluonfx/aarch64-android/gvm/android_project/gradlew -p /pub/gitlab/iceconverter/target/gluonfx/aarch64-android/gvm/android_project assembleDebug
[Sun Oct 05 07:58:55 UTC 2025][FINE] PB Command for bundle-task: /pub/gitlab/iceconverter/target/gluonfx/aarch64-android/gvm/android_project/gradlew -p /pub/gitlab/iceconverter/target/gluonfx/aarch64-android/gvm/android_project bundleDebug
```
The `bundleDebug` and `assembleDebug` commands are used to build the `.aab` package, but obviously they probably will
contain debug information. Looking at the folder `/pub/gitlab/iceconverter/target/gluonfx/aarch64-android/gvm/android_project/app/build/outputs/bundle`
there is only a debug folder.

So try the following (because why not) :
```
/pub/gitlab/iceconverter/target/gluonfx/aarch64-android/gvm/android_project/gradlew -p /pub/gitlab/iceconverter/target/gluonfx/aarch64-android/gvm/android_project assembleRelease
/pub/gitlab/iceconverter/target/gluonfx/aarch64-android/gvm/android_project/gradlew -p /pub/gitlab/iceconverter/target/gluonfx/aarch64-android/gvm/android_project bundleRelease
```
This got me further! But the build complained about the `keystore.properties` file:
```
FAILURE: Build failed with an exception.
* What went wrong:
Execution failed for task ':app:validateSigningRelease'.
> Keystore file '/pub/gitlab/iceconverter/target/gluonfx/aarch64-android/gvm/android_project/app/KEYSTORE_FILE' not found for signing config 'release'.
```
The build process tries to sign the packages, looking at the file (in the target folder) `/pub/gitlab/iceconverter/target/gluonfx/aarch64-android/gvm/android_project/app/keystore.properties` shows:
```
storeFile=KEYSTORE_FILE
storePassword=KEYSTORE_PASSWORD
keyAlias=KEY_ALIAS
keyPassword=KEY_PASSWORD
```
Change this to (keystore location created earlier):
```
storeFile=/home/user/signing/iceco/iceco-release-key.jks
storePassword=[secret_key]
keyAlias=iceco-alias
keyPassword=[secret_key]
```
I ran the both commands and copied the resulting `.aab` file to the `~/Downloads/signed` folder:
```
/pub/gitlab/iceconverter/target/gluonfx/aarch64-android/gvm/android_project/gradlew -p /pub/gitlab/iceconverter/target/gluonfx/aarch64-android/gvm/android_project assembleRelease
/pub/gitlab/iceconverter/target/gluonfx/aarch64-android/gvm/android_project/gradlew -p /pub/gitlab/iceconverter/target/gluonfx/aarch64-android/gvm/android_project bundleRelease
cp /pub/gitlab/iceconverter/target/gluonfx/aarch64-android/gvm/android_project/app/build/outputs/bundle/release/app-release.aab ~/Downloads/signed/IceCo.aab
```

## Google Play Console
The package is now accepted! I started this project at the beginning of 2025, but since 31 of August 2025 Google has
adjusted the minimum requirements for the target SDK to 35. My setup is still running on 34. No surprise, the package
was rejected one more time ; )

I made the following addition to some files:
```
/pub/gitlab/iceconverter/target/gluonfx/aarch64-android/gvm/android_project/gradle.properties
android.debuggable=false
/pub/gitlab/iceconverter/target/gluonfx/aarch64-android/gvm/android_project/project.properties
android.debuggable=false
/pub/gitlab/iceconverter/target/gluonfx/aarch64-android/gvm/android_project/app/build.gradle
android {
    compileSdkVersion 35
    defaultConfig {
        targetSdkVersion 35
```
I’m not sure if the `android.debuggable` statement is actually necessary, since I started using the `assembleRelease`
statement with gradle. The next upload I will omit both and will see if it’s accepted. But for now, to summarize the
steps:
```
mvn -Pandroid clean gluonfx:build gluonfx:package
vi /pub/gitlab/iceconverter/target/gluonfx/aarch64-android/gvm/android_project/gradle.properties
vi /pub/gitlab/iceconverter/target/gluonfx/aarch64-android/gvm/android_project/project.properties
vi /pub/gitlab/iceconverter/target/gluonfx/aarch64-android/gvm/android_project/app/build.gradle
cp target/gluonfx/aarch64-android/gvm/IceCo.apk ~/Downloads
rm target/gluonfx/aarch64-android/gvm/IceCo.apk
rm target/gluonfx/aarch64-android/gvm/IceCo.aab
rm ~/Downloads/signed/IceCo.aab
vi /pub/gitlab/iceconverter/target/gluonfx/aarch64-android/gvm/android_project/app/keystore.properties
/pub/gitlab/iceconverter/target/gluonfx/aarch64-android/gvm/android_project/gradlew -p /pub/gitlab/iceconverter/target/gluonfx/aarch64-android/gvm/android_project assembleRelease
/pub/gitlab/iceconverter/target/gluonfx/aarch64-android/gvm/android_project/gradlew -p /pub/gitlab/iceconverter/target/gluonfx/aarch64-android/gvm/android_project bundleRelease
cp /pub/gitlab/iceconverter/target/gluonfx/aarch64-android/gvm/android_project/app/build/outputs/bundle/release/app-release.aab ~/Downloads/signed/IceCo.aab
mvn -Pandroid gluonfx:install
```
Last step is to make sure everything still works on your phone (phone must be plugged in to install it).

# Page size (16Kb)
The app was accepted in the Google Play Console, see it [here](https://play.google.com/store/apps/details?id=nl.dotjava.javafx.iceconverter&hl=en&pli=1),
the page size however is not 16Kb. As from 1 november 2025, the page size must be 16Kb. See [here](README-substrate.md) for more on that.
