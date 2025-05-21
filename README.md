# Simple Icelandic Euro to Krónas converter
This mobile phone application uses 100% Java and JavaFX with FXML to create a simple krónas to euro conversion app and vice versa.

Most conversion apps require an input and then convert your input either to euros or to krónas (or any currency). Just depending on the situation, I want to convert to Krónas and in some other cases to Euros, but not caring about what to call when doing so. So this app just accepts input and converts to and from both.  

## Versions
The mobile app is built with the following versions:

| What                   | Version                                             | See                                                                 |
|------------------------|-----------------------------------------------------|---------------------------------------------------------------------|
| IceCo                  | 0.2                                                 | this                                                                |
| GraalVM 23 with Gluon  | native-image 23 2024-09-17 (23+25.1-dev-2409082136) | https://github.com/gluonhq/graal/releases                           |
| JavaFX controls & fxml | 25-ea+17                                            | https://mvnrepository.com/artifact/org.openjfx/javafx-controls      |
| controlsfx             | 11.2.2                                              | https://mvnrepository.com/artifact/org.controlsfx/controlsfx        |
| Gluonfx maven plugin   | 1.0.25                                              | https://github.com/gluonhq/gluonfx-maven-plugin/                    |
| Javafx maven plugin    | 0.0.8                                               | https://mvnrepository.com/artifact/org.openjfx/javafx-maven-plugin  |

Note: this JavaFX project is built with GraalVM 23 with Gluon included, but it doesn't use any com.gluonhq artifacts, it only uses javafx packages. This means there is no popup from Gluon Mobile. JavaFX on mobile is completely independent from Android. It uses GraalVM's native image to compile the JavaFX application into a native library that Android calls via JNI. As far as Android is concerned it's the same as using a C++ library but we're developing in 100% Java :-)

If you want to setup a working Linux (Ubuntu) environment, see this [blog](https://www.dotjava.nl/2025/04/20/ubuntu-for-mobile-android-java-development/). It describes setting up the environment.

## Build and run on your phone (android)
```
mvn -Pandroid clean gluonfx:build gluonfx:package
mvn -Pandroid gluonfx:install
mvn -Pandroid -X gluonfx:nativerun
```

## Build and run on your iPhone
For the time being, I will not make and or maintain a version for an iPhone.

## Build and run on your desktop
Using the `org.openjfx.javafx-maven-plugin` artifact, you can run the app also on your desktop with:
```
mvn gluonfx:run
```

## Information
The application is very basic and tries to convert any input both ways using a simple converter class. I cannot be held
responsible for any wrong conversions! In the first version (0.1) the conversion rate was hard coded, in this version
currencies are downloaded from [dotJava/currencies.html](https://www.dotjava.nl/currencies.html). I've created another
small java project which fetches currencies from the internet once a day and puts them in a static webpage for the 
support of this project (I will make it public when its finished), see the method `CurrencySupport.extractAllCurrenciesFromSite()`
for more information.

Since the AndroidManifest has:
```
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```
The fetch should be no problem. See the `CurrencySupport` class also for a `java.net.http.HttpClient` implementation to
fetch the webpage. Again, I cannot be held responsible for wrong currency codes. This is a tutorial project to create
your own mobile app on your phone. If the fetch fails, the conversion rate is hardcoded to 0.0068 like in the very first
version. This will be the case when no internet is available.

I've changed the markup @FXML page since the first version a bit and put a nice logo in portrait and landscape mode.
Later goal is to allow a tap on the logo to select another currency (and change the picture of course ;-).

The startup screen shows:<br/><br/> <img src="https://github.com/user-attachments/assets/7a1442d7-0d8a-4d5d-86b0-e638018f91f7" width="250"><br/>

Then, when you are about to enter a value, the keyboard shows up, like<br/>
<img src="https://github.com/user-attachments/assets/f907accc-66d0-4a87-bf43-2cb45a645655" width="250"><br/>
I've not added a feature to move the screen, so that's why all information is in the upper part of the screen (to allow
the keyboard to take some space).

Whatever you type, after tapping it will convert from and to and you decide which to look at, for example<br/>
<img src="https://github.com/user-attachments/assets/1c913f16-ee88-431e-ad01-391df8bba57b" width="250"><br/>
is obvious a krónas amount representing 6 or 7 euros for probably a parking ticket near the tourist site you're at ;-)

And<br/>
<img src="https://github.com/user-attachments/assets/cca651af-e116-4872-bd80-55df472aee23" height="250"><br/>
is what I use to see how I can spend my 8 euros.

That's the idea, have fun!

# Additions
After the very first implementation, I wanted to change the behavior to show the numerical keyboard instead of text
keyboard immediately. Also, converting on focus-lost should reduce clicks. But this would require some more interaction
with the mobile phone and start the use of the com.gluonhq packages.

I've decided not to use the com.gluonhq packages, so it stays a org.openjfx tutorial project. It is a useful currency
converter mobile application written in 100% Java. See another [nop](https://github.com/michiel-jfx/nop) project for more
interaction with the mobile phone and the use of some com.gluonhq packages.

There are still a lot `System.out.println` statements present since this is a tutorial project. When you understand the
flow and see how it works, remove them all please.

Next goals:<br/>
<ul>
<li>Upgrade to more recent JavaFX Controls</li>
<li>Tap logo to change currency</li>
<li>Deploy the app on [F-Droid](https://f-droid.org) </li>
</ul>

# License
The iceconverter (IceCo) mobile application is released under version 2.0 of the [Apache License](https://www.apache.org/licenses/LICENSE-2.0).
