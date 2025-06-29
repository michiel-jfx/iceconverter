# Simple Icelandic Euro to Krónas converter
This mobile phone application uses 100% Java and JavaFX with FXML to create a simple krónas to euro conversion app and
vice versa. Actually, since version 0.0.4 it can convert from and to 22 different currencies!

Most conversion apps require an input and then convert your input either to euros or to krónas (or any currency). Just
depending on the situation, I want to convert to Krónas and in some other cases to Euros, but not caring about what to
call when doing so. So this app just accepts input and converts to and from both.  

## Versions
The mobile app is built with the following versions:

| What                   | Version                                             | See                                                                                                                                                     |
|------------------------|-----------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------|
| IceCo                  | 0.0.4                                               | this, see also https://www.dotjava.nl/iceco/                                                                                                            |
| GraalVM 23 with Gluon  | native-image 23 2024-09-17 (23+25.1-dev-2409082136) | https://github.com/gluonhq/graal/releases <br/>note: this is the GraalVM Community Edition which is published<br/>with GNU General Public License (GPL) |
| JavaFX controls & fxml | 25-ea+17                                            | https://mvnrepository.com/artifact/org.openjfx/javafx-controls                                                                                          |
| Controlsfx             | 11.2.2                                              | https://mvnrepository.com/artifact/org.controlsfx/controlsfx                                                                                            |
| Gluonfx maven plugin   | 1.0.25                                              | https://github.com/gluonhq/gluonfx-maven-plugin/                                                                                                        |
| Javafx maven plugin    | 0.0.8                                               | https://mvnrepository.com/artifact/org.openjfx/javafx-maven-plugin                                                                                      |

Note: this JavaFX project is built with GraalVM 23 with Gluon included, but it doesn't use any com.gluonhq artifacts, it
only uses javafx packages. This means there is no popup from Gluon Mobile. JavaFX on mobile is completely independent
from Android. It uses GraalVM's native image to compile the JavaFX application into a native library that Android calls
via JNI. As far as Android is concerned, it's the same as using a C++ library but we're developing in 100% Java :-)

If you want to setup a working Linux (Ubuntu) environment, see this [blog](https://www.dotjava.nl/2025/04/20/ubuntu-for-mobile-android-java-development/). It describes setting up the environment.

## Build and run on your phone (android)
```
mvn -Pandroid clean gluonfx:build gluonfx:package
mvn -Pandroid gluonfx:install
mvn -Pandroid -X gluonfx:nativerun
cp target/gluonfx/aarch64-android/gvm/IceCo.apk ~/Downloads
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
The application is very basic and tries to convert any input both ways using a simple converter class. **I cannot be held
responsible for any wrong conversions!** In the first version (0.0.1) the conversion rate was hard coded, in this version
currencies are downloaded once at startup from
[dotJava/currency_data/currencies.html](https://www.dotjava.nl/currency_data/currencies.html). I've created another
small java project which fetches currencies from the internet once a day and puts them in a static webpage for the
support of this project (I will make it public when its finished), see the method
`CurrencySupport.extractAllCurrenciesFromSite()` for more information.

As of 30-may-2025 I've added some more currencies including flags from around the world. The flags already can be used
freely, see [Flagpedia](https://flagpedia.net/) for this. My daughter created all the currency symbols. When you tap on
on of the flags or symbols, the currency selection screen will show up. Tap on two flags to set new currencies, the
selection scene will close shortly after.

Since the AndroidManifest has:
```
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```
The fetch should be no problem. See the `CurrencySupport` class also for a `java.net.http.HttpClient` implementation to
fetch the webpage. *Again, I cannot be held responsible for wrong currency conversion rates*. This is a tutorial project
to create your own mobile app on your phone. If the fetch fails, the currency is set to ISK and the rate is hardcoded to
0.0068 like in the very first version. This will be the case when no internet is available.

Startup screen looks like:<br/>
<img src="https://github.com/user-attachments/assets/ca65ea69-ceac-4963-acb7-0420737cb7d5" width="250"><br/>

New feature (from 0.0.4) is, you can enter values directly and use the backspace to clear values, the keyboard from the
mobile phone itself isn't necessary anymore. I've considered to add the 'c' (clear) as character instead of the '.' 
value (indicating decimal values) for smaller conversions. Sometimes a decimal value can make the difference as it's not
always about the big bucks. So to clear, you have to tap the backspace a few times. Maybe I can add a double tap on the 
backspace to clear the amount field at once. Something todo later.

<img src="https://github.com/user-attachments/assets/77e33280-13c6-4a3c-9ff4-15f81dec60d4" width="250"><br/>
Values tapped will be converted immediately, like this is obvious a krónas amount representing about 7 euros for probably a parking ticket near the tourist site you're at ;-)

When you select one of the flags, the currency selection screen will appear.<br/>
<img src="https://github.com/user-attachments/assets/7817d3de-8a4f-4869-b83d-4495bcc2e010" width="250"><br/>
Both flags on top are the currently selected currencies. Tap on any flag (do this twice), and it will set the currencies
to convert and close the scene shortly after.

That's the idea, have fun!

# Additional info
After the very first implementation, I wanted to change the behavior to show the numerical keyboard from your phone 
instead of text keyboard. Also, converting on focus-lost of the textfield should reduce clicks. But this would require
some more interaction with the mobile phone and start the use of the com.gluonhq packages.

I've decided not to use the com.gluonhq packages, so it stays an org.openjfx tutorial project. It is a useful currency
converter mobile application written in 100% Java. See another [nop](https://github.com/michiel-jfx/nop) project for more interaction with the mobile
phone and the use of some com.gluonhq packages.

In this version 0.0.4 I've removed the landscape version. Switching between scenes and keeping track of the orientation
didn't quite went smooth. If you want to, take a look at this 
[branch](https://github.com/michiel-jfx/iceconverter/tree/orientation_issues). It did give the idea of a SceneSupport
class though to put in common methods to use when switching between scenes.

There are still some `System.out.println` statements present since this is a tutorial project. When you understand the
flow and see how it works, remove them please.

Next goals:<br/>
<ul>
<li>Store last currency settings for next usage</li>
<li>Add more currencies</li>
<li>Change the name of the project since it will be no longer an Icelandic Converter (IceCo) but a currency converter for the holidays</li>
<li>Deploy the app on [FDroid](https://f-droid.org) or in the Google Play Store</li>
</ul>

# License
The iceconverter (IceCo) mobile application is released under version 2.0 of the [Apache License](https://www.apache.org/licenses/LICENSE-2.0).
