# Simple Icelandic Euros to Krónur converter
This mobile phone application uses 100% Java and JavaFX with FXML to create a simple króna to euro conversion app and
vice versa.

Most conversion apps require an input and then convert your input either to euros or to krónur. Just depending on the
situation, I want to convert to Krónur and in some other cases to Euros, but not caring about what to call when doing.
So this app just accepts input and converts to and from both.  

## Versions
The mobile app is built with the following versions:

| What                   | Version                                             | See                                                                |
|------------------------|-----------------------------------------------------|--------------------------------------------------------------------|
| IceCo                  | 0.4                                                 | this, see https://www.dotjava.nl/iceco                             |
| GraalVM 23 with Gluon  | native-image 23 2024-09-17 (23+25.1-dev-2409082136) | https://github.com/gluonhq/graal/releases                          |
| JavaFX controls & fxml | 26-ea+1                                             | https://mvnrepository.com/artifact/org.openjfx/javafx-controls     |
| Controlsfx             | 11.2.2                                              | https://mvnrepository.com/artifact/org.controlsfx/controlsfx       |
| GluonHQ storage        | 4.0.23                                              | https://central.sonatype.com/artifact/com.gluonhq.attach/storage   |
| Gluonfx maven plugin   | 1.0.25                                              | https://github.com/gluonhq/gluonfx-maven-plugin/                   |
| Javafx maven plugin    | 0.0.8                                               | https://mvnrepository.com/artifact/org.openjfx/javafx-maven-plugin |

Note: this JavaFX project is built with GraalVM 23 with Gluon included, but it mostly uses javafx packages only. This
means there is no popup from Gluon Mobile. JavaFX on mobile is completely independent from Android. It uses GraalVM's
native image to compile the JavaFX application into a native library that Android calls via JNI. As far as Android is 
concerned, it's the same as using a C++ library but we're developing in 100% Java :-)

If you want to setup a working Linux (Ubuntu) environment, see this [blog](https://www.dotjava.nl/2025/04/20/ubuntu-for-mobile-android-java-development/). It describes setting up the environment.

Note: gluonfx-maven-plugin 1.0.26 and 1.0.27 are present, but when used result in the following (open) [issue](https://github.com/gluonhq/gluonfx-maven-plugin/issues/539)

## Build and run on your phone (android)
```
mvn -Pandroid clean gluonfx:build gluonfx:package
mvn -Pandroid gluonfx:install
mvn -Pandroid -X gluonfx:nativerun
cp target/gluonfx/aarch64-android/gvm/IceCo.apk ~/Downloads
```
last statement can be used to copy the android build to a location for your needs.

## Build and run on your iPhone
As soon as I get my hands on a Apple laptop, I will build a iOS version.

## Build and run on your desktop
Using the `org.openjfx.javafx-maven-plugin` artifact, you can run the app also on your desktop with:
```
mvn gluonfx:run
```

## Information
The application is very basic and tries to convert any input both ways using a simple converter class. *I cannot be held
responsible for any wrong conversions!* In the first version (0.1) the conversion rate was hard coded, in this version
the currency is downloaded from [dotJava/currency_data/króna.html](https://www.dotjava.nl/currency_data/króna.html).

Since the AndroidManifest has:
```
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```
the fetch should be no problem. See the `CurrencySupport` class also for a `java.net.http.HttpClient` implementation to
fetch the webpage. **Again, I cannot be held responsible for wrong currency codes**. This is a tutorial project to create
your own mobile app on your phone. If the fetch fails, the conversion rate is hardcoded to 0.00703 like in the very first
version. This will be the case when no internet is available.

There is only a portrait version available with an input keyboard present directly on screen to allow faster number
entry. In this version I've added a custom (free to use) font for the keypad numbers. It was a good exercise to get the
font to work on the mobile phone as it behaves a bit differently than when running on the desktop. Streaming from the
resources to the font directly simply didn't work on the mobile phone (as it does work when running on your desktop).

See this [commit](https://github.com/michiel-jfx/iceconverter/commit/8994b351dea340e175b3eaffd1a70a0af43767dc) to see
what (small) additions are necessary to get the font to work.

The startup screen shows:<br/>
<img src="https://github.com/user-attachments/assets/db9dbb64-e10f-4c7e-8276-5f720d69dfb0" width="250"><br/>

You can enter values directly and use the '&lt;' to clear values (double tapping clears entire input), the keyboard
from the mobile phone itself isn't necessary anymore.<br/>
<img src="https://github.com/user-attachments/assets/24b8d89c-3803-497d-9518-c9afd5895b70" width="250"><br/>
Values tapped will be converted immediately *from* and *to*, like above here is obvious a krónur amount of 1000 
representing about 7 euros for probably a parking ticket near the tourist site you're at ;-)

And<br/>
<img src="https://github.com/user-attachments/assets/ef0e3915-d108-4277-bd3c-2ff465366f85" width="250"><br/>
is what I use to see how I can spend my 12 euros.

That's the idea, have fun!

# Additions
After the very first implementation, I wanted to change the behavior to show the numerical keyboard instead of text
keyboard immediately. This would require some more interaction with the mobile phone and start the use of the
com.gluonhq packages.

I've decided to use the com.gluonhq packages as less as possible, so it stays an org.openjfx tutorial project. For the 
custom font, I've started to use the `com.gluonhq.attach.storage` package, but this didn't result in the popup from
Gluon Mobile (to note it's a free edition). It remains a nice tutorial currency converter mobile application written
in 100% Java. See another [nop](https://github.com/michiel-jfx/nop) project for more interaction with the mobile phone and the use of some com.gluonhq
packages.

There are still a lot `System.out.println` statements present since this is a tutorial project. When you understand the
flow and see how it works, remove them all please.

Next goals:<br/>
<ul>
<li>Store currency locally (when no internet)</li>
<li>Make a donate button ;)</li>
<li>Deploy the app on [F-Droid](https://f-droid.org) </li>
</ul>

# License
The iceconverter (IceCo) mobile application is released under version 2.0 of the [Apache License](https://www.apache.org/licenses/LICENSE-2.0).
