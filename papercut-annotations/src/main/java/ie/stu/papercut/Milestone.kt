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
package ie.stu.papercut

/**
 * Define a milestone for your project that has not yet been met.
 *
 * Any milestones defined in your codebase can be referenced in your [Debt] annotations.
 * Removing a milestone will cause any [Debt] annotations referencing it as a condition to fail.
 *
 * You may want to keep these milestone definitions together in one class so they are easy to manage. Milestones can be
 * attached to any field, but attaching them to unrelated fields with real use cases may lead to accidental removal.
 *
 * ```
 * @Milestone("LOGIN_REDESIGN") const val loginRedesign = "LOGIN_REDESIGN";
 *
 * [...]
 *
 * class ThingDoer {
 *
 *     @Debt(milestone = Milestones.LOGIN_REDESIGN)
 *     fun doSomethingHacky() {
 *
 *     }
 *
 * }
 * ```
 */
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
@Target(
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
    AnnotationTarget.LOCAL_VARIABLE,
)
annotation class Milestone(
    /**
     * Specify the name of a milestone to be referenced in your [Debt] annotations' <pre>milestone</pre>
     * parameter.
     */
    val value: String = ""
)
