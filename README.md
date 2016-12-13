Papercut
========

Remember to remove the stuff you don't need from your codebase.

  * Tag parts of your code that should not make it to production
  * Automatically fail your build
  * Keep your codebase simple

```java
@RemoveThis
class TemporaryHack {
    // Some horrible workaround here
}
```

```java
class ThingDoer {
    @RemoveThis(date = "2017-01-01")
    private void doSomethingUseful() {
		// Something useful
    }
}
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
