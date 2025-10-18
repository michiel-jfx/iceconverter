# Changing the page size to 16Kb and other issues

From November 2025 it isn't possible to upload mobile application packages to the Google Play Console with a page size 
other than 16Kb. To see your current page size, run:
```
readelf -l target/gluonfx/aarch64-android/libIceCo.so | grep LOAD
```
The output should be in chunks of 0x4000 (16384 bytes) multiplies.

Since I started this project at the beginning of 2025 a lot has changed. At this point, the setup isn't entirely correct
anymore and the best way to fix it is to rewrite it from scratch using another SDK. But since this is a tutorial
project, it is important to keep the progress and process documented.

To sign the output `.apk` and `.aab` files, I created some post-generation steps, which are documented [here](README-signing.md).

## Steps to get the 16Kb pagesize
Importantly, the steps below probably will not work and are for documentation purposes only. This is a last commit to
document the steps taken. I will create a branch to keep this documentation for lookup and then will move on to use
a newer SDK.

Begin ith a fresh build
```
mvn -Pandroid clean gluonfx:build gluonfx:package
```
First check the time and date for the file:  `ls -l target/gluonfx/aarch64-android/libIceCo.so`

Now try the script to fix the alignment after the building process `./fix-pagesize-alignment.sh` :
```
#!/bin/bash

set -e

LIB_FILE="target/gluonfx/aarch64-android/libIceCo.so"

if [ ! -f "$LIB_FILE" ]; then
    echo "Library not found: $LIB_FILE"
    exit 0  # do not fail if file doesn't exist yet
fi
cp "$LIB_FILE" "${LIB_FILE}.org"

echo "==== Current alignment ===="
readelf -l "$LIB_FILE" | grep LOAD

if [ -n "$ANDROID_NDK" ]; then
    OBJCOPY="${ANDROID_NDK}/toolchains/llvm/prebuilt/linux-x86_64/bin/llvm-objcopy"

    if [ -f "$OBJCOPY" ]; then
        echo ""
        echo "==== Attempting to fix alignment with objcopy ===="

        "$OBJCOPY" \
            --set-section-alignment .text=16384 \
            --set-section-alignment .rodata=16384 \
            --set-section-alignment .data=16384 \
            --set-section-alignment .data.rel.ro=16384 \
            --set-section-alignment .bss=16384 \
            "$LIB_FILE" "${LIB_FILE}.fixed"

        if [ $? -eq 0 ]; then
            cp "${LIB_FILE}.fixed" "$LIB_FILE"
            echo ""
            echo "==== After fix ===="
            readelf -l "$LIB_FILE" | grep LOAD
        fi
    fi
fi
```
Recheck the time and date for the file:  `ls -l target/gluonfx/aarch64-android/libIceCo.so`

To start building the packages again and to sign it properly now perform:

edit /pub/gitlab/iceconverter/target/gluonfx/aarch64-android/gensrc/android/build.gradle
```
apply plugin: 'com.android.application'

android {
    namespace 'nl.dotjava.javafx.iceconverter'
    compileSdkVersion 35

    compileOptions {
        sourceCompatibility 1.8
        targetCompatibility 1.8
    }

    defaultConfig {
        minSdkVersion 28
        targetSdkVersion 35
    }
...
```
which is the same as: /pub/gitlab/iceconverter/target/gluonfx/aarch64-android/gvm/android_project/app/build.gradle
```
apply plugin: 'com.android.application'

android {
    namespace 'nl.dotjava.javafx.iceconverter'
    compileSdkVersion 35

    compileOptions {
        sourceCompatibility 1.8
        targetCompatibility 1.8
    }

    defaultConfig {
        minSdkVersion 28
        targetSdkVersion 35
    }
...
```
edit /pub/gitlab/iceconverter/target/gluonfx/aarch64-android/gvm/android_project/build.gradle
```
classpath 'com.android.tools.build:gradle:8.13.0'
```
Shown before, but mentioned here again: edit /pub/gitlab/iceconverter/target/gluonfx/aarch64-android/gvm/android_project/app/keystore.properties
```
storeFile=[full-path-to-jsk]
storePassword=[secret]
keyAlias=iceco-alias
keyPassword=[secret]
```
edit /pub/gitlab/iceconverter/target/gluonfx/aarch64-android/gvm/android_project/gradle/wrapper/gradle-wrapper.properties
```
distributionUrl=https\://services.gradle.org/distributions/gradle-8.13-bin.zip
```

As mentioned in the signing README, re-run the assembling processes:
```
/pub/gitlab/iceconverter/target/gluonfx/aarch64-android/gvm/android_project/gradlew -p /pub/gitlab/iceconverter/target/gluonfx/aarch64-android/gvm/android_project assembleRelease
/pub/gitlab/iceconverter/target/gluonfx/aarch64-android/gvm/android_project/gradlew -p /pub/gitlab/iceconverter/target/gluonfx/aarch64-android/gvm/android_project bundleRelease
cp /pub/gitlab/iceconverter/target/gluonfx/aarch64-android/gvm/android_project/app/build/outputs/bundle/release/app-release.aab ~/Downloads/signed/IceCo.aab
```

to automatically execute the `fix-pagesize-alignment.sh` script, you can add the following to the pom.xml:
```
        ...
        <org.codehaus.mojo.exec.maven.version>3.6.1</org.codehaus.mojo.exec.maven.version>
    </properties>
    ...
    <build>
        <plugins>
            ...
            <!-- post-processing to fix page-size alignment -->
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>exec-maven-plugin</artifactId>
                <version>${org.codehaus.mojo.exec.maven.version}</version>
                <executions>
                    <execution>
                        <id>fix-pagesize-alignment</id>
                        <phase>prepare-package</phase>
                        <goals>
                            <goal>exec</goal>
                        </goals>
                        <configuration>
                            <executable>${project.basedir}/fix-pagesize-alignment.sh</executable>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
            ...
```

And for the `gluonfx-maven-plugin` these are the final native arguments:
```
                    <nativeImageArgs>
                        <arg>-H:+UnlockExperimentalVMOptions</arg>
                        <arg>-H:+StripDebugInfo</arg>
                        <arg>-H:PageSize=16384</arg>
                        <arg>-H:NativeLinkerOption=-Wl,-z,max-page-size=16384</arg>
                        <arg>-H:NativeLinkerOption=-Wl,-z,common-page-size=16384</arg>
                        <arg>-H:NativeLinkerOption=-Wl,--section-start=.text=0x4000</arg>
                        <arg>-H:NativeLinkerOption=-Wl,-T,${project.basedir}/src/main/resources/android-linker-16kb.ld</arg>
                    </nativeImageArgs>
```
With this file: /pub/gitlab/iceconverter/src/main/resources/android-linker-16kb.ld :
```
SECTIONS
{
  . = ALIGN(16384);
  .text : ALIGN(16384) { *(.text .text.*) }
  . = ALIGN(16384);
  .rodata : ALIGN(16384) { *(.rodata .rodata.*) }
  . = ALIGN(16384);
  .data : ALIGN(16384) { *(.data .data.*) }
  . = ALIGN(16384);
  .data.rel.ro : ALIGN(16384) { *(.data.rel.ro .data.rel.ro.*) }
  . = ALIGN(16384);
  .bss : ALIGN(16384) { *(.bss .bss.*) }
}
INSERT AFTER .text;
```
But also no luck here.

Performing al these steps and uploading the `.aab` package to Google Play Console resulted in the following message:
```
Your app must support 16 KB memory page sizes by November 1, 2025
Your app uses native libraries that don't support 16 KB memory page sizes. Recompile your app to support 16 KB by November 1, 2025 to continue releasing updates to your app.
```

So, this is the last update for now. Using a newer version of Gradle and the Android SDK is the way to go. See also
more [here](https://developer.android.com/build/releases/gradle-plugin#api-level-support) on Gradle.
