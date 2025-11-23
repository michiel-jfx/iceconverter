# Changing the page size to 16Kb and other issues

## The issue
From November 2025 it isn't possible to upload mobile application packages to the Google Play Console with a page size 
other than 16Kb. To see the page size, run:
```bash
readelf -l target/gluonfx/aarch64-android/libIceCo.so | grep LOAD
```
The output should be in chunks of 0x4000 (16384 bytes) multiplies.

Or even better, use [Google's](https://cs.android.com/android/platform/superproject/main/+/main:system/extras/tools/check_elf_alignment.sh) `check_elf_alignment.sh` 
script to see if the pages are aligned properly:
```bash
check_elf_alignment target/gluonfx/aarch64-android/gvm/IceCo.apk
check_elf_alignment target/gluonfx/aarch64-android/libIceCo.so
```

## Setup changes
The initial project used the [GraalVM with Gluon 23 dev edition](https://github.com/gluonhq/graal/releases/tag/gluon-23%2B25.1-dev-2409082136).
This is a combination of Java 23 and Gluon packages together. It used to build the project nicely. However, the 
gluonfx-maven-plugin with this GraalVM version was up till version 1.0.25. The GluonFX maven plugin is used to build
your app including all native libraries and automatically includes the substrate library. This did not contain a 16Kb
page size version and using command line arguments I couldn't fix this (since it downloads a static version of the
substrate library). So I made some changes.

Probably I've overdone my alterations because it might be enough to just change one of the options below, but for my
overall learning curve (to develop Java mobile applications) it contributed a great deal.

### 1. Building your local substrate
My first thought was to build the substrate library myself. It can be cloned from [here](https://github.com/gluonhq/substrate) 
and has an active community. Actually, I saw an issue about the page size alignment but nevertheless I wanted to build
it myself, because scrolling through the sources it gave me an idea to add some personal, besides necessary, features:
<ul>
<li>immediately build the assembleRelease instead of the assembleDebug</li>
<li>immediately build the bundleRelease instead of the bundleDebug</li>
<li>add my own location for the storeFile (keystore) with settings</li>
<li>already include my own target Android version</li>
<li>necessary: 16Kb page sizing</li>
</ul>
it's pretty evident that these changes don't make ik to the versions on GitHub ; )

The changes:

`java/com/gluonhq/substrate/target/AbstractTargetConfiguration.java`, look for the part with `processRunner.addArgs` then add:
```java
processRunner.addArgs("-Wl,-z,max-page-size=16384");
```

`java/com/gluonhq/substrate/target/AndroidTargetConfiguration.java`, change the `private final List<String> linkFlags` from:
```java
    private final List<String> linkFlags = Arrays.asList("-target",
            ANDROID_TRIPLET + ANDROID_MIN_SDK_VERSION, "-fPIC",
            "-Wl,--rosegment,--gc-sections,-z,noexecstack", "-shared",
            "-landroid", "-llog", "-lffi", "-llibchelper", "-static-libstdc++");
```
to:
```java
    private final List<String> linkFlags = Arrays.asList("-target",
            ANDROID_TRIPLET + ANDROID_MIN_SDK_VERSION, "-fPIC",
            "-Wl,--rosegment,--gc-sections,-z,noexecstack,-z,max-page-size=16384",
            "-Wl,-z,max-page-size=16384",  // explicit 16Kb page size for Android
            "-shared", "-landroid", "-llog", "-lffi", "-llibchelper", "-static-libstdc++");
```

`resources/native/android/android_project/app/build.gradle`, part until `repositories` tag, from:
```gradle
apply plugin: 'com.android.application'

android {
    namespace 'com.gluonhq.helloandroid'

    compileOptions {
        sourceCompatibility 1.8
        targetCompatibility 1.8
    }

    defaultConfig {
        minSdkVersion 21
        compileSdk 36
        targetSdkVersion 36
    }
```
to:
```gradle
apply plugin: 'com.android.application'

android {
    namespace 'com.gluonhq.helloandroid'

    packagingOptions {
        jniLibs {
            useLegacyPackaging true
        }
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
        targetCompatibility JavaVersion.VERSION_17
    }

    defaultConfig {
        minSdkVersion 35
        compileSdk 36
        targetSdkVersion 36
        externalNativeBuild {
            cmake {
                // passes optional arguments to CMake.
                arguments "-DANDROID_SUPPORT_FLEXIBLE_PAGE_SIZES=ON"
            }
        }
    }
```

`resources/native/android/android_project/app/keystore.properties` :
```
storeFile=/full_path_to_keystore/iceco-release-key.jks
storePassword=my_secret_password
keyAlias=alias_name_of_keystore
keyPassword=my_secret_password
```

`resources/native/android/android_project/build.gradle`, change from:
```gradle
    dependencies {
        classpath 'com.android.tools.build:gradle:8.11.2'
    }
```
to:
```grade
    dependencies {
        classpath 'com.android.tools.build:gradle:8.13.0'
    }
```

`resources/native/android/android_project/project.properties`, change from:
```
target=android-21
```
to:
```
target=android-36
```

Note: the Google Play Console did tell me I lost a lot of target devices when only build for Android 35+ (setting
`minSdkVersion 35`)

### 2. GraalVM with GluonFX plugin
Second change was to downgrade the GraalVM with Gluon to the latest stable version [GraalVM with Gluon 22.1.0.1 final](https://github.com/gluonhq/graal/releases/tag/gluon-22.1.0.1-Final).
This version can be used with the latest GluonFX maven plugin version 1.0.28 which comes with the latest substrate
version 0.0.68 (so, probably I don't need to build my own substrate ;). When you've built the mobile application for a 
first time, the substrate jar is in your repository. Either let it be or just copy your locally build version of the
substrate version (and use the present Gradle command `publishToMavenLocal`), then:
```
cd /pub/repository/com/gluonhq/substrate/0.0.68
cp ../0.0.69-SNAPSHOT/substrate-0.0.69-SNAPSHOT.jar substrate-0.0.68.jar
```
This assumes your local repository is in `/pub/repository`. The jar which is present is called `substrate-0.0.68.jar`
and whatever you have generated can be copied over it.

Unfortunately the GraalVM 22.0.1 final uses Java 17. Hopefully GraalVM with Gluon will come with a new final version
(last version 23 dev edition was released in September 2024).

Now build the project:
```
mvn clean
mvn -Pandroid gluonfx:build gluonfx:package
mvn -Pandroid gluonfx:install
mvn -Pandroid -X gluonfx:nativerun
ls -l target/gluonfx/aarch64-android/libIceCo.so
check_elf_alignment target/gluonfx/aarch64-android/gvm/IceCo.apk
```

During the build, you'll notice something like this in your terminal:
```
[Sun Nov 23 11:56:21 UTC 2025][FINE] PB Command for compile: /pub/software/graalvm-gluon-22/bin/native-image -Djdk.internal.lambda.eagerlyInitialize=false --no-server -H:+SharedLibrary -H:+AddAllCharsets -H:+ReportExceptionStackTraces -H:-DeadlockWatchdogExitOnTimeout -H:DeadlockWatchdogInterval=0 -H:+RemoveSaturatedTypeFlows -H:+ExitAfterRelocatableImageWrite --features=org.graalvm.home.HomeFinderFeature -H:TempDirectory=/pub/gitlab/iceconverter/target/gluonfx/aarch64-android/gvm/tmp -H:EnableURLProtocols=http,https -H:+PrintAnalysisCallTree -H:Log=registerResource: -H:ReflectionConfigurationFiles=/pub/gitlab/iceconverter/target/gluonfx/aarch64-android/gvm/reflectionconfig-aarch64-android.json -H:JNIConfigurationFiles=/pub/gitlab/iceconverter/target/gluonfx/aarch64-android/gvm/jniconfig-aarch64-android.json -H:ResourceConfigurationFiles=/pub/gitlab/iceconverter/target/gluonfx/aarch64-android/gvm/resourceconfig-aarch64-android.json -H:-SpawnIsolates -Dsvm.targetArch=aarch64 -H:+ForceNoROSectionRelocations --libc=bionic -H:+UseCAPCache -H:CAPCacheDir=/pub/gitlab/iceconverter/target/gluonfx/aarch64-android/gvm/capcache -H:CompilerBackend=lir -H:IncludeResourceBundles=com/sun/javafx/scene/control/skin/resources/controls,com/sun/javafx/scene/control/skin/resources/controls-nt,com.sun.javafx.tk.quantum.QuantumMessagesBundle -Dsvm.platform=org.graalvm.nativeimage.Platform$LINUX_AARCH64 -cp /pub/repository/com/gluonhq/substrate/0.0.68/substrate-0.0.68.jar:/pub/gitlab/iceconverter/target/gluonfx/aarch64-android/gvm/tmp/classpathJar.jar -H:PageSize=16384 -H:NativeLinkerOption=-Wl,-z,max-page-size=16384 nl.dotjava.javafx.iceconverter.IceCo
[Sun Nov 23 11:56:21 UTC 2025][FINE] Start process compile...
```
So, the substrate.jar is taken from the local repository. And if you've made the changes mentioned above to the library,
there's a release version that is already signed and contains the right target sdk settings for the project which can be
uploaded in the Google Play Console immediately!
