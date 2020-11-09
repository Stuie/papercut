package ie.stu.papercut

/*
 * Copyright (C) 2020 Stuart Gilbert
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
@Target(
        AnnotationTarget.FUNCTION,
        AnnotationTarget.PROPERTY_GETTER,
        AnnotationTarget.PROPERTY_SETTER,
        AnnotationTarget.FIELD,
        AnnotationTarget.ANNOTATION_CLASS,
        AnnotationTarget.CLASS,
        AnnotationTarget.CONSTRUCTOR,
        // TODO Confirm this list
)
annotation class Debt(
        /**
         * A brief identifier of the Debt this annotation is highlighting. This is intended to be a brief summary,
         * it may be displayed on a single line during build output, or as the title of a section in a report.
         */
        val value: String = "",

        /**
         * The date the Debt was added to the codebase. This can be used to help prioritize what debt to work on.
         *
         * Format must be YYYY-MM-DD.
         */
        val addedDate: String = "",

        /**
         * A description of the Debt being added. This can be displayed when longer output is expected.
         */
        val description: String = "",

        /**
         * A URL or other identifier for an issue, story, epic, task, card, etc. that represents this Debt.
         */
        val id: String = "",

        /**
         * A description of why this debt was added instead of resolved during development, e.g. time, external team
         * support, lack of designs, etc.
         */
        val reason: String = "",

        /**
         * An estimated cost of addressing the highlighted Debt.
         */
        val cost: String = "",

        /**
         * If this value is set to true, and any set conditions fail, Papercut will break your build. If it is set to
         * false, Papercut will print a warning in your build output.
         *
         * Possible conditions: <pre>removalDate</pre>, <pre>milestone</pre>, <pre>versionCode</pre>, and
         * <pre>versionName</pre>
         *
         * Default value is <pre>false</pre>.
         */
        val stopShip: Boolean = false,

        /**
         * The date by which the debt should be removed.
         *
         * Format must be YYYY-MM-DD.
         */
        val removalDate: String = "",

        /**
         * The [Milestone] by which the debt should be removed.
         */
        val milestone: String = "",

        /**
         * The version code by which the debt should be removed. This is an integer that typically increases for every
         * build or release of your software.
         *
         * Default value is Int.MAX_VALUE
         */
        val versionCode: Int = Int.MAX_VALUE,

        /**
         * The version name by which the debt should be removed. Must meet the semantic versioning specification to
         * be supported.
         *
         * @see <a href="http://semver.org">http://semver.org</a>
         */
        val versionName: String = "",
)
