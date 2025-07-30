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
| IceCo                  | 0.3                                                 | this, see https://www.dotjava.nl/iceco                             |
| GraalVM 23 with Gluon  | native-image 23 2024-09-17 (23+25.1-dev-2409082136) | https://github.com/gluonhq/graal/releases                          |
| JavaFX controls & fxml | 26-ea+1                                             | https://mvnrepository.com/artifact/org.openjfx/javafx-controls     |
| controlsfx             | 11.2.2                                              | https://mvnrepository.com/artifact/org.controlsfx/controlsfx       |
| Gluonfx maven plugin   | 1.0.25                                              | https://github.com/gluonhq/gluonfx-maven-plugin/                   |
| Javafx maven plugin    | 0.0.8                                               | https://mvnrepository.com/artifact/org.openjfx/javafx-maven-plugin |

Note: this JavaFX project is built with GraalVM 23 with Gluon included, but it doesn't use any com.gluonhq artifacts, it
only uses javafx packages. This means there is no popup from Gluon Mobile. JavaFX on mobile is completely independent
from Android. It uses GraalVM's native image to compile the JavaFX application into a native library that Android calls
via JNI. As far as Android is concerned, it's the same as using a C++ library but we're developing in 100% Java :-)

If you want to setup a working Linux (Ubuntu) environment, see this [blog](https://www.dotjava.nl/2025/04/20/ubuntu-for-mobile-android-java-development/). It describes setting up the environment.

Note: gluonfx-maven-plugin 1.0.26 and 1.0.27 are present, but result in the following (open) [issue](https://github.com/gluonhq/gluonfx-maven-plugin/issues/539)

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

From this version (v0.3) there only is a portrait version with an input keyboard present directly on screen to allow
faster number entry.

The startup screen shows:<br/>
<img src="https://github.com/user-attachments/assets/0cb2731e-de69-4134-81c2-2286b76e9727" width="250"><br/>

You can enter values directly and use the '&lt;' to clear values (double tapping clears entire input), the keyboard
from the mobile phone itself isn't necessary anymore.<br/>
<img src="https://github.com/user-attachments/assets/79cd0b15-86b0-42ca-a54a-5741aeedf87f" width="250"><br/>
Values tapped will be converted immediately *from* and *to*, like above here is obvious a krónur amount of 1000 
representing about 7 euros for probably a parking ticket near the tourist site you're at ;-)

And<br/>
<img src="https://github.com/user-attachments/assets/37a47bd0-4e0f-4b4e-9245-139e6a66457a" width="250"><br/>
is what I use to see how I can spend my 12 euros.

That's the idea, have fun!

# Additions
After the very first implementation, I wanted to change the behavior to show the numerical keyboard instead of text
keyboard immediately. This would require some more interaction with the mobile phone and start the use of the
com.gluonhq packages.

I've decided not to use the com.gluonhq packages, so it stays a org.openjfx tutorial project. It is a useful Icelandic 
currency converter mobile application written in 100% Java. See another [nop](https://github.com/michiel-jfx/nop) 
project for more interaction with the mobile phone and the use of some com.gluonhq packages.

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
