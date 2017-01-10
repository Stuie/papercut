/*
 * Copyright (C) 2016 Stuart Gilbert
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
package ie.stu.papercut;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark some code that you want to remove from your codebase in the future.
 *
 * The default behavior for a plain annotation is to fail your build. You can provide descriptive text, set a code
 * expiration date, or version number, switch from failures to warnings, or use {@link Milestone}s for managing the
 * removal.
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE, ElementType.CONSTRUCTOR})
public @interface RemoveThis {
    /**
     * Specify a string that can be used to describe when to remove some code if there's no known date.
     *
     * @return Descriptive text to identify when code should be removed
     */
    String value() default "";

    /**
     * Specify a date at which the item should be removed.
     *
     * Currently only YYYY-MM-DD format is supported.
     *
     * @return The date by which the refactor should be completed
     */
    String date() default "";

    /**
     * Specify whether a failure to meet the removal condition should break the build. Defaults to true.
     *
     * @return Whether to fail your build or not
     */
    boolean stopShip() default true;

    /**
     * Specify a milestone by which the code should have been removed. Must exactly match a {@link Milestone} annotation
     * specified somewhere in your codebase.
     *
     * @return The {@link Milestone} to remove code by
     */
    String milestone() default "";

    /**
     * Specify a versionCode by which the code should have been removed. Must be able to be parsed as an integer as
     * performed by Integer.parseInt().
     *
     * @return The versionCode to remove code by
     */
    String versionCode() default "";

    /**
     * Specify a versionName by which the code should have been removed. Must meet the semantic versioning specification
     * to be supported, e.g. MAJOR.MINOR.PATCH
     *
     * @see <a href="http://semver.org">http://semver.org</a>
     *
     * @return The versionName to remove code by
     */
    String versionName() default "";
}
