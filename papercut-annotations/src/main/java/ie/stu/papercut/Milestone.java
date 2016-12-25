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
 * Define a milestone for your project that has not yet been met.
 *
 * Any milestones defined in your codebase can be referenced in your {@link RemoveThis} or {@link Refactor} annotations.
 * Removing a milestone will cause any matching {@link RemoveThis} or {@link Refactor} to fail.
 *
 * You may want to keep these milestone definitions together in one class so they are easy to manage. Milestones can be
 * attached to any field, but attaching them to unrelated fields with real use cases may lead to accidental removal.
 *
 * <pre><code>
 *    public class Milestones {
 *       {@literal @}Milestone("LOGIN_REDESIGN") public static final String LOGIN_REDESIGN = "LOGIN_REDESIGN";
 *    }
 *
 *    public class ThingDoer {
 *       {@literal @}Refactor(milestone = Milestones.LOGIN_REDESIGN)
 *        public void doSomethingHacky() {
 *
 *        }
 *    }
 * </code></pre>
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface Milestone {
    /**
     * Specify the name of a milestone to be referenced in your {@link RemoveThis} or {@link Refactor} annotations'
     * milestone parameter.
     *
     * @return Descriptive text for your development milestone
     */
    String value() default "";
}
