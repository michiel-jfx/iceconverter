# Simple Icelandic Euro to Krónas converter
This mobile phone application uses 100% Java and JavaFX with FXML to create a simple krónas to euro conversion app and vice versa.

Most conversion apps require an input and then convert your input either to euros or to krónas (or any currency). Just depending on the situation, I want to convert to Krónas and in some other cases to Euros, but not caring about what to call when doing so. So this app just accepts input and converts it to and from both.  

## Versions
The mobile app is build with the following versions:

| What                   | Version                                             | See                                                                 |
|------------------------|-----------------------------------------------------|---------------------------------------------------------------------|
| IceCo                  | 0.1                                                 | this                                                                |
| GraalVM 23 with Gluon  | native-image 23 2024-09-17 (23+25.1-dev-2409082136) | https://github.com/gluonhq/graal/releases                           |
| JavaFX controls & fxml | 17.0.6                                              | https://mvnrepository.com/artifact/org.openjfx/javafx-controls      |
| controlsfx             | 11.2.1                                              | https://mvnrepository.com/artifact/org.controlsfx/controlsfx        |
| Gluonfx maven plugin   | 1.0.25                                              | https://github.com/gluonhq/gluonfx-maven-plugin/                    |
| Javafx maven plugin    | 0.0.8                                               | https://mvnrepository.com/artifact/org.openjfx/javafx-maven-plugin  |

Note: this JavaFX project is built with GraalVM 23 with Gluon included, but it doesn't use any com.gluonhq artifacts, it only uses javafx packages. This means there is no popup from Gluon Mobile.

JavaFX on mobile is completely independent from Android. It uses GraalVM's native image to compile the JavaFX application into a native library that Android calls via JNI. As far as Android is concerned, it's the same as using a C++ library, but we're developing in 100% Java :-)

If you want to setup a working Linux (Ubuntu) environment, see this [blog](https://www.dotjava.nl/2025/04/20/ubuntu-for-mobile-android-java-development/). It describes setting up the environment.

## Build and run on your phone (android)
```
mvn -Pandroid clean gluonfx:build gluonfx:package
mvn -Pandroid gluonfx:install
mvn -Pandroid -X gluonfx:nativerun
```

## Build and run on your iPhone
work in progress

## Build and run on your desktop
Using the `org.openjfx.javafx-maven-plugin` artifact, you can run the app also on your desktop with:
```
mvn gluonfx:run
```

## Information
The application is very simple and tries to convert any input both ways around using a simple converter class. **I cannot be hold responsible for any wrong conversions!** The conversion rate is hard coded, before you go on holiday (might be another country than Iceland of course) put the proper conversion at the right place and build your image.

There are also 3 labels in the FXML markup which seem pretty useless. I've added them for convenience reasons. The startup screen shows:<br/><br/>
<img src="https://github.com/user-attachments/assets/da3eb905-7607-4171-9d68-7f781b465b43" width="250"><br/>

Then, when you are about to enter a value, the keyboard shows up, like<br/>
<img src="https://github.com/user-attachments/assets/e6b3f7b8-cef8-4ee7-8c8e-bc33d71989c7" width="250"><br/>
I've not added a feature to move the screen, so when the 3 labels are not present, the keyboard goes over the input, which is not pretty.

Whatever you type, it will convert and you decide what to use, for example<br/>
<img src="https://github.com/user-attachments/assets/616f99b8-badf-4359-bee2-57e027665dc7" width="250"><br/>
Is obvious a krónas amount like 6 or 7 euros for probably a parking ticket near the tourist site you're at ;-)

And<br/>
<img src="https://github.com/user-attachments/assets/9ffd9c5d-71dd-4385-bad2-fa50443b3946" width="250"><br/>
Is what I use to see what I can buy for 5 euros.

That's the idea, have fun!

# Additions
After the very first implementation, I wanted to change the behavior to show the numerical keyboard instead of text keyboard.
Also, converting on focus-lost should also reduce clicks. But this would result in some interactions with the mobile phone
and start the use of the com.gluonhq packages.

I decided to leave this project for what it is as it does what it is supposed to do. See another [nop](https://github.com/michiel-jfx/nop) project for more interaction with the mobile phone.

# License
The iceconverter (IceCo) mobile application is released under version 2.0 of the [Apache License](https://www.apache.org/licenses/LICENSE-2.0).
