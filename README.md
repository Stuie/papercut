Papercut
========

Remember to remove the stuff you don't need from your codebase.

  * Tag parts of your code that should not make it to production
  * Automatically fail your build
  * Keep your codebase simple

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

If you set the `stopShip` parameter to false in the annotation then you will only receive a warning.

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

For documentation and additional information, see [the website][1].

Download
--------

```groovy
dependencies {
	compile: 'ie.stu:papercut-annotations:0.0.2'
	annotationProcessor: 'ie.stu:papercut-compiler:0.0.2'
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
