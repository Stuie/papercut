Papercut
========

Keep your codebase simple.

  * Annotate parts of your code that shouldn't make it to production
  * Automatically fail your build
  * Remember to remove code you don't need

@RemoveThis
-----------

```java
class TemporaryHack {
    @RemoveThis
    private static final boolean DEBUG = true;
}
```

```java
class ThingDoer {
    @RemoveThis(date = "2017-01-01", value = "After SOME_FEATURE has launched")
    private void someHackyMethod() {
		// Here there be monsters
    }
}
```

Your build will fail at compile time by default.

```
:app:compileDebugJavaWithJavac
/Users/stu/workspaces/TestApp/app/src/main/java/testing/TemporaryHack.java:10: error: STOP SHIP: @RemoveThis found at:
    private static final boolean DEBUG = true;
                                 ^
/Users/stu/workspaces/TestApp/app/src/main/java/testing/ThingDoer.java:12: error: @RemoveThis found with description 'After SOME_FEATURE has launched' at:
    private void someHackyMethod() {
                 ^
2 errors
:app:compileDebugJavaWithJavac FAILED
```

If you set the `stopShip` parameter to false in the annotation then you will only receive a warning. This matches the behavior of `Refactor`.

```java
@RemoveThis(value = "After SOME_FEATURE has launched", stopShip = false)
private void someHackyMethod() {
    //TODO FIXME
}
```

```
:app:compileDebugJavaWithJavac
/Users/stu/workspaces/TestApp/app/src/main/java/testing/SomeApplication.java:54: warning: @RemoveThis found with description 'After SOME_FEATURE has launched
    private void someHackyMethod() {
                 ^
1 warning
```

@Refactor
---------

`@Refactor` exactly matches `@RemoveThis`, but uses warnings instead of failing your build by default. You can set the `stopShip` parameter to `true` to cause a build failure with a `@Refactor` annotation if you wish.

@Milestone
----------

You can define milestones by which you would like to refactor or remove code. When you reach a milestone just delete
the `@Milestone` annotated field. If you use constants as in the first example below then deleting the field
should cause your IDE to highlight the instances that referenced it. If you use plain strings in your `@RemoveThis` or
`@Refactor` annotations then they must match your defined `@Milestone`s exactly, as in the second example.

```java
public class Milestones {
    @Milestone("LOGIN_REDESIGN") public static final String LOGIN_REDESIGN = "LOGIN_REDESIGN";
    @Milestone("SOME_FEATURE") public static final String SOME_FEATURE = "SOME_FEATURE";
    @Milestone("VERSION_2") public static final String VERSION_2 = "VERSION_2";
}

public class ImportantThingDoer {
    @Refactor(milestone = Milestones.LOGIN_REDESIGN)
    private void onlyUsedByLoginScreen() {

    }

    @RemoveThis(milestone = "VERSION_2")
    public void callOldAPI() {

    }
}
```

For full documentation and additional information, see [the website][1].

Download
--------

```groovy
dependencies {
	compile: 'ie.stu:papercut-annotations:0.0.3'
	annotationProcessor: 'ie.stu:papercut-compiler:0.0.3'
}
```

To use Papercut only in release builds you can alter the annotationProcessor line to use `releaseAnnotationProcessor` or
`debugAnnotationProcessor` to suit your needs. Delaying the execution until you're trying to build your release version
may be lead to unexpected build failures.

```groovy
dependencies {
    compile: 'ie.stu:papercut-annotation:0.0.3'
    releaseAnnotationProcessor: 'ie.stu:papercut-compiler:0.0.3'
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

Snapshots of the development version are available in [Sonatype's `snapshots` repository][snap].

[1]: http://stuie.github.com/papercut/
[snap]: https://oss.sonatype.org/content/repositories/snapshots/
