# Simple Icelandic Euro to Krónas converter
This mobile phone application uses 100% Java and JavaFX with FXML to create a simple krónas to euro conversion app and vice versa.

Most conversion apps require an input and then convert your input either to euros or to krónas. Just depending on the situation I want to convert to Krónas and in some other cases I would like to convert to Euros.  

## Versions
The mobile app is build with the following versions:

| What                  | Version                                             | See                                                                                            |
|-----------------------|-----------------------------------------------------|------------------------------------------------------------------------------------------------|
| IceCo                 | 0.1                                                 | here                                                                                           |
| GraalVM 23 with Gluon | native-image 23 2024-09-17 (23+25.1-dev-2409082136) | https://github.com/gluonhq/graal/releases                                                      |
| JavaFX controls       | 17.0.6                                              | https://mvnrepository.com/artifact/org.openjfx/javafx-controls                                 |
| gluonfx maven plugin  | 1.0.25                                              | https://github.com/gluonhq/gluonfx-maven-plugin/                                               |

Note: this JavaFX project is built with GraalVM 23 with Gluon included, but it doesn't use any com.gluonhq artifacts, it only uses javafx packages. This means there is no popup from Gluon to support their product.

If you want to setup a working Linux (Ubuntu) environment, see the [nop](https://www.dotjava.nl/nop) homepage. It describes setting up the environment. 

## Build and run on your phone (android)
```
mvn clean gluonfx:build gluonfx:package -Pandroid
mvn -Pandroid gluonfx:install
mvn -Pandroid -X gluonfx:nativerun
```

## Build and run on your iPhone
work in progress

## Information
work in progress

# License
The Nop mobile application is released under version 2.0 of the [Apache License](https://www.apache.org/licenses/LICENSE-2.0).
