# Android method hook plugin

An Android Gradle plugin to inject method call at the beginning and end of methods in
Android application at compile time using the [ASM](https://asm.ow2.io) library.

Build and tested for AGP version: `8.2.2`.

> [!CAUTION]  
> This plugin is provided "as is" and without warranty of any kind, express or implied.  
> The authors accept no responsibility for any damages arising from the use of this plugin.

## Installation (WIP)

Apply Gradle plugin to Android application

```gradle
plugins {
    id("com.android.application") version <version>
    // todo: id("dev.aleksrychkov.methodhook") version <version>
}
```

## Usage

### Quick start

Create `methodhook_activity.conf` file to instruct plugin what and where to inject calls. You can
place it adjacent to
application's build.gradle[.kts] file

```conf
activity {
    superClass = "android.app.Activity"
    methods = [
        "onCreate"        
    ]
    packageId = "org.example"
    beginMethodWith = "org.example.MethodHook.start"
    endMethodWith = "org.example.MethodHook.end"
}
```

Add plugin's configuration to an Android application's `build.gradle[.kts]` file. In `addConfig`
method specify a path
to
a created `methodhook_activity.conf` file previously

```gradle
// build.gradle.kts
plugins {
    id("com.android.application") version <version>
    id("dev.aleksrychkov.methodhook") version <version>
}

android { … }

androidMethodHook {
    configs {
        create("debug") {
            addConfig("./methodhook_activity.conf")
        }
    }
}
```

Create a class with two static methods to be injected by the plugin

```kotlin
package org.example

object MethodHook {
    @JvmStatic
    fun start(runtimeClazz: String, clazz: String, method: String) {
        println("MethodHook::start::$runtimeClazz::$clazz::$method")
    }

    @JvmStatic
    fun end(runtimeClazz: String, clazz: String, method: String) {
        println("MethodHook::end::$runtimeClazz::$clazz::$method")
    }
}
```

Build project's `debug` buildType.
Now, `onCreate(savedInstanceState: Bundle?)` method of all activities defined in `org.example`
package should be
instructed with `MethodHook::start` and `MethodHook::end` calls.

### Example

For a practical example of how to configure and use this plugin, refer to
the [examples](./examples).

### Deep dive

#### Config file

A config file specifies which methods in your classes the plugin will modify to inject method calls.
The files are defined in [Typesafe config](https://github.com/typesafehub/config) format.  
You can create as many files as you need, for example, we can create a config file for each Android
app component

```bash
./methodhook_activity.conf
./methodhook_service.conf
./methodhook_broadcast_receiver.conf
./methodhook_content_provider.conf
```

You can define multiple configs inside one file. Start a config with a name

```config
activity {
    superClass = "android.app.Activity"
    methods = [
        "onCreate"        
    ]
    packageId = "org.example"
    beginMethodWith = "org.example.MethodHook.start"
    endMethodWith = "org.example.MethodHook.end"
}
frgament {
    exactClass = "org.example.SomeFragment"
    methods = [
        "onResume"        
    ]
    beginMethodWith = "org.example.MethodHook.start"
    endMethodWith = "org.example.MethodHook.end"
}
```

Each config supports following options:

* `superClass`: Specifies the parent class (__canonical name__) for which the plugin should target
  methods in its
  subclasses, e.g. `android.app.Activity`. Must not be defined along with `exactClass`.
* `exactClass`: Specifies the exact class (__canonical name__) for which the plugin should target
  methods,
  `e.g. org.example.SomeClass`. Must not be defined along with `superClass`.
* `methods`: Specifies an array of methods (identified by their names) where the plugin will
  inject additional code, e.g. `onCreate`.
* `beginMethodWith`: Specifies a reference to a method to be injected at the beginning of instructed
  methods,
  e.g. `org.example.MethodHook.start`.
* `endMethodWith`: Specifies a reference to a method to be injected at the end of instructed
  methods,
  e.g. `org.example.MethodHook.end`.
* `packageId`: Specifies the package name that identifies the group of classes where the plugin will
  target methods for
  injection, e.g. `org.example`.  
  Not required, if not defined, the config will be applied to all files in your project, including
  third-party libraries

> [!IMPORTANT]
> * Each config must have either `superClass` or `exactClass` option, but not both.
> * Do not duplicate configs with same `superClass` or `exactClass` option.
> * Each config must have `methods` option.
> * Each config must have at least one of the options (or both): `beginMethodWith`, `endMethodWith`.
> * Bear in mind that, a class, targeted by the plugin, must be associated with only one config.
    Otherwise, you will get an error.

#### Plugin configuration

In order to apply plugin to application, add `androidMethodHook` configuration to the
application's `build.gradle[.kts]`
file

```gradle
// build.gradle.kts
plugins {
    id("com.android.application") version <version>
    id("dev.aleksrychkov.methodhook") version <version>
}

android {
    buildTypes {
        debug { … } 
        release { … }        
    }
}

androidMethodHook {
    forceLogging = true       
    configs {
        create("debug") {
            addConfig("./methodhook_activity_debug.conf")
        }
        create("release") {
            addConfig("./methodhook_activity_release.conf")
        }
    }
}
```

The `androidMethodHook` configuration supports next options:

* `forceLogging`: Enables info logs of the plugin. Same as if executing gradle command with `--info`
  flag,
  e.g. `./gradlew assembleDebug --info`. Default value is `false`.
* `configs`: Creates plugin's config for specific build variant. The name of config must be the same
  as name of build
  variant, e.g. buildType: `debug`, or if you have productFlavor named `demo`: `demoDebug`. You can
  have separate sets
  of config for different build variants.
    * `addConfig`: Adds relative path to a config file.

#### Inject method calls

Plugin allows to inject a single method call to the beginning or the end of a target method.
Injected method must be `public static void` and have three `String` arguments.  
Create a separate method to be injected at the beginning and end of the target method

```kotlin
@file:Suppress("UNUSED_PARAMETER")

package org.example

object MethodHook {
    @JvmStatic
    fun start(runtimeClazz: String, clazz: String, method: String) {
        // some logic to be executed at the beginning of a [method]
    }

    @JvmStatic
    fun end(runtimeClazz: String, clazz: String, method: String) {
        // some logic to be executed at the end of a [method]
    }
}
```

```java
package org.example;

public class MethodHook {
    public static void start(String runtimeClazz, String clazz, String method) {
        // some logic to be executed at the beginning of a [method]
    }

    public static void end(String runtimeClazz, String clazz, String method) {
        // some logic to be executed at the end of a [method]
    }
}
```

* first argument is a runtime class, `this.getClass().getName()`, e.g. `org.example.MainActivity`.
* second argument is an actual class where method was called, e.g. `org.example.AbstractActivity`.
* third argument is a method name, arguments, return type, e.g. `onCreate(Bundle)->void`.

Specify created methods in config

```conf
activity {
    …
    beginMethodWith = "org.example.MethodHook.start"
    endMethodWith = "org.example.MethodHook.end"
}
```

## License

Copyright Aleksandr Rychkov

Licensed under the Apache License, Version 2.0
