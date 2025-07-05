# Holiday Currency Converter
HoliCur is a mobile phone application that uses 96% Java and 4% JavaFX with FXML / CSS to create a simple currency 
converter to use on your holiday. It converts any-to-any currency to and from 22 different currencies!

Most conversion apps require an input and then convert your input to a selected currency. Just depending on the
situation, I want to convert *to* a currency but in some other cases I meant converting *from* (just not caring what it
is I wanted). The HoliCur app just accepts input and converts to and from both.  

## Versions
The mobile app is built with the following artifacts and versions:

| What                   | Version                                             | See                                                                                                                                                     |
|------------------------|-----------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------|
| HoliCur                | 0.0.5                                               | this, see also https://www.dotjava.nl/holicur/                                                                                                          |
| GraalVM 23 with Gluon  | native-image 23 2024-09-17 (23+25.1-dev-2409082136) | https://github.com/gluonhq/graal/releases <br/>note: this is the GraalVM Community Edition which is published<br/>with GNU General Public License (GPL) |
| JavaFX controls & fxml | 25-ea+17                                            | https://mvnrepository.com/artifact/org.openjfx/javafx-controls                                                                                          |
| Controlsfx             | 11.2.2                                              | https://mvnrepository.com/artifact/org.controlsfx/controlsfx                                                                                            |
| GluonHQ storage        | 4.0.22                                              | https://central.sonatype.com/artifact/com.gluonhq.attach/storage                                                                                        |
| Gluonfx maven plugin   | 1.0.25                                              | https://github.com/gluonhq/gluonfx-maven-plugin/                                                                                                        |
| Javafx maven plugin    | 0.0.8                                               | https://mvnrepository.com/artifact/org.openjfx/javafx-maven-plugin                                                                                      |

This JavaFX project is built with GraalVM 23 with Gluon included. Since version 0.0.5 it uses the com.gluonhq.attach.storage
package to store the last used currencies to the local private storage on your phone. But there is no popup from Gluon
Mobile, as this is only displayed when using the charm-glisten package.

JavaFX on mobile is completely independent from Android. It uses GraalVM's native image to compile the JavaFX application
into a native library that Android calls via JNI. As far as Android is concerned, it's the same as using a C++ library,
but we're developing in 100% Java :-)

If you want to setup a working Linux (Ubuntu) environment, see this [blog](https://www.dotjava.nl/2025/04/20/ubuntu-for-mobile-android-java-development/). It describes setting up the environment.

## Build and run on your phone (android)
```
mvn -Pandroid clean gluonfx:build gluonfx:package
mvn -Pandroid gluonfx:install
mvn -Pandroid -X gluonfx:nativerun
cp target/gluonfx/aarch64-android/gvm/HoliCur.apk ~/Downloads
```
last statement can be used to copy the android build to a location for your needs (like your phone).

## Build and run on your iPhone
Still looking for an inexpensive Macbook to generate a version for the iPhone.

## Build and run on your desktop
Using the `org.openjfx.javafx-maven-plugin` artifact, you can run the app also on your desktop with:
```
mvn gluonfx:run
```

## Information
The application is simple and tries to convert any input both ways using a simple converter class. **I cannot be held
responsible for any wrong conversions!** In the first version (0.0.1) the conversion rate was hard coded, as from v0.0.2
currencies are downloaded once at startup from
[dotJava/currency_data/currencies.html](https://www.dotjava.nl/currency_data/currencies.html). I've created another
small java project which fetches currencies from the internet once a day and puts them in a static webpage for the
support of this project (I will make it public when its finished), see the method
`CurrencySupport.extractAllCurrenciesFromSite()` for more information.

From version 0.0.3, I've added some more currencies including flags from around the world. The flags already can be used
freely, see [Flagpedia](https://flagpedia.net/) for this. My daughter created all the currency symbols. When you tap on one of the flags or
symbols, the currency selection screen will show up. Tap on two flags to set new currencies, the selection scene will
close shortly after.

The AndroidManifest has:
```
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```
meaning the fetch should be no problem. See the `CurrencySupport` class also for a simple `java.net.http.HttpClient`
implementation to fetch the webpage. *Again, I cannot be held responsible for wrong currency conversion rates*. This is
a tutorial project to create your own mobile application on your phone. The currencies are stored to the local private 
storage in your phone. When internet is not available, it loads the (last) saved file. If that fails as well, the
currency is set to ISK and the rate is hardcoded to 0.0068 like in the very first version.

Startup screen looks like:<br/>
<img src="https://github.com/user-attachments/assets/815ea076-c6b4-4070-8565-1ffc1ecc36dc" width="250"><br/>

New feature (from 0.0.4) is, you can enter values directly and use the backspace to clear values, the keyboard from the
mobile phone itself isn't necessary anymore.

<img src="https://github.com/user-attachments/assets/905c803f-ded1-498a-838b-d394ad8d80e5" width="250"><br/>
Values tapped will be converted immediately, like this here is obvious a krónas amount of 1000 representing about 7
euros for probably a parking ticket near the tourist site you're at ;-)

When you select one of the flags, the currency selection screen will appear.<br/>
<img src="https://github.com/user-attachments/assets/cecc1402-ca4c-4d4f-9b7d-de561259dce1" width="250"><br/>
Both flags on top are the currently selected currencies. Tap on any flag (do this two times), and it will set the
currencies to convert and close the scene shortly after. New feature in this version is that it will remember this when
you use the mobile application next time.

That's the idea, have fun!

# Additional info
The project uses the com.gluonhq packages as few as possible, so it stays an org.openjfx tutorial project. It is a
useful holiday currency converter mobile application written in 100% Java. See another [nop](https://github.com/michiel-jfx/nop) project for more
interaction with the mobile phone and the use of some com.gluonhq packages.

There are still some `System.out.println` statements present since this is a tutorial project. When you understand the
flow and see how it works, you can remove them.

Next goals:<br/>
<ul>
<li>Add more currencies</li>
<li>Use custom font for the numerical input</li>
<li>Deploy the app in the Google Play Store or on [FDroid](https://f-droid.org)</li>
</ul>

# License
The Holiday Currency Converter (HoliCur) mobile application is released under version 2.0 of the [Apache License](https://www.apache.org/licenses/LICENSE-2.0).
