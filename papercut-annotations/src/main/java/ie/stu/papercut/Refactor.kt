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
package ie.stu.papercut

/**
 * Mark some code that you want to refactor in your codebase.
 *
 * The default behavior for a plain annotation is to print a warning during your build. You can provide descriptive
 * text, set a code expiration date, or version number, switch from warnings to failures, or use [Milestone]s for
 * managing the refactor.
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
)
annotation class Refactor(
        /**
         * Specify a string that can be used to describe when to refactor some code.
         *
         * @return Descriptive text to identify when code should be refactored
         */
        val value: String = "",

        /**
         * Specify a date at which the item should be refactored.
         *
         * Currently only YYYY-MM-DD format is supported.
         *
         * @return The date by which the refactor should be completed
         */
        val date: String = "",

        /**
         * Specify whether a failure to meet the refactor condition should break the build. Defaults to false.
         *
         * @return Whether to fail your build or not
         */
        val stopShip: Boolean = false,

        /**
         * Specify a milestone by which the code should have been refactored. Must exactly match a [Milestone]
         * annotation specified somewhere in your codebase.
         *
         * @return The [Milestone] to refactor code by
         */
        val milestone: String = "",

        /**
         * Specify a versionCode by which the code should have been refactored. Must be able to be parsed as an integer
         * as performed by Integer.parseInt().
         *
         * @return The versionCode to refactor code by
         */
        val versionCode: String = "",

        /**
         * Specify a versionName by which the code should have been refactored. Must meet the semantic versioning
         * specification to be supported, e.g. MAJOR.MINOR.PATCH
         *
         * @see [http://semver.org](http://semver.org)
         *
         *
         * @return The versionName to refactor code by
         */
        val versionName: String = ""
)