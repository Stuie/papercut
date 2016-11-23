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
package ie.stu.papercut.compiler;

import ie.stu.papercut.RemoveThis;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedAnnotationTypes("ie.stu.papercut.RemoveThis")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class AnnotationProcessor extends AbstractProcessor{

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        System.out.println("PROCESSING PAPERCUT ANNOTATIONS");
        for (final Element element : roundEnv.getElementsAnnotatedWith(RemoveThis.class)) {
            System.out.println("FOUND A REMOVETHIS ANNOTATION");
            final String objectType = element.getSimpleName().toString();

            if (element.getAnnotation(RemoveThis.class).stopShip()) {
                System.out.println("ANNOTATION SAYS TO STOP SHIP!");
                throw new IllegalStateException("STOP SHIP: @RemoveThis specified");

            }
        }

        return true;
    }
}
