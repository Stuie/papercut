Papercut
========

Keep your codebase simple.

  * Keep track of technical debt in your code
  * Set conditions for removal and catch them during your build

@Debt
-----------

The `@Debt` annotation can be used to track technical debt in your codebase, along with requirement conditions for its removal.

```kotlin
class TemporaryHack {
    @Debt
    val debug = true
}
```

```kotlin
class ThingDoer {
    @Debt(
        addedDate = "2020-01-01",
        value = "After SOME_FEATURE has launched"
    )
    private fun someHackyMethod() {
		// Here there be monsters
    }
}
```

The `stopShip` parameter can be used to print an error message during your build. By default you just get a warning.

```kotlin
@Debt(
    value = "After SOME_FEATURE has launched",
    removalDate = "2021-01-01",
    stopShip = true
)
private fun someHackyMethod() {
    //TODO FIXME
}
```

With a small modification to your build.gradle file you can use version codes or version names as well as dates in your annotations.

In the case of an Android app, you can pass your version code and name to Papercut using the configuration below.

```groovy
android {
    defaultConfig {
        versionCode 4
        versionName "0.4.0"
        javaCompileOptions {
            annotationProcessorOptions {
                arguments = ['versionCode': String.valueOf(defaultConfig.versionCode),
                             'versionName': defaultConfig.versionName]
            }
        }
    }
}
```

The arguments must both be passed as strings. The `versionCode` will be parsed as an integer and the `versionName` must match the [semantic versioning schema][2].

You can then use the `versionCode` or `versionName` parameters in your annotations as below.

```kotlin
@Debt(versionName = "0.4.0")
private void fetchSomethingRemote() {
    // Temporary hack, please remove
}
```

@Milestone
----------

You can define milestones by which you would like to refactor or remove code. When you reach a milestone just delete
the `@Milestone` annotated field. If you use constants as in the first example below then deleting the field
should cause your IDE to highlight the instances that referenced it. If you use plain strings in your `@Debt`
annotations then they must match your defined `@Milestone`s exactly, as in the second example.

```kotlin
class Milestones {
    @Milestone("LOGIN_REDESIGN") val loginRedesign = "LOGIN_REDESIGN"
    @Milestone("SOME_FEATURE") val someFeature = "SOME_FEATURE"
}

class ImportantThingDoer {
    @Debt(milestone = Milestones.loginRedesign)
    private fun onlyUsedByLoginScreen() {

    }

    @Debt(milestone = "SOME_FEATURE")
    fun callOldAPI() {

    }
}
```

Download
--------
For Kotlin projects with Kotlin DSL gradle files:

```kotlin
plugins {
    kotlin("kapt") version "1.4.20"
}

dependencies {
    implementation("ie.stu:papercut-annotations:0.9.1")
    kapt("ie.stu:papercut-compiler:0.9.1")
}
```

For Java projects with groovy gradle files:
```groovy
dependencies {
    implementation: 'ie.stu:papercut-annotations:0.9.1'
    annotationProcessor: 'ie.stu:papercut-compiler:0.9.1'
}
```

License
-------

    Copyright 2016 Stuart Gilbert

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

[1]: http://stuie.github.com/papercut/
[2]: http://semver.org
