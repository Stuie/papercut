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

import com.google.auto.service.AutoService;
import ie.stu.papercut.RemoveThis;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

@SupportedAnnotationTypes("ie.stu.papercut.RemoveThis")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@AutoService(Processor.class)
public class AnnotationProcessor extends AbstractProcessor {
    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        for (final Element element : roundEnv.getElementsAnnotatedWith(RemoveThis.class)) {
            final RemoveThis annotation = element.getAnnotation(RemoveThis.class);
            final Messager messager = processingEnv.getMessager();
            final PackageElement packageElement = processingEnv.getElementUtils().getPackageOf(element);

            final boolean stopShip = annotation.stopShip();

            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date;

            try {
                date = simpleDateFormat.parse(annotation.date());
            } catch (final ParseException e) {
                throw new RuntimeException("Incorrect date format in @RemoveThis annotation. Please " +
                        "follow YYYY-MM-DD format.");
            }

            // TODO Need to include the stacktrace when logging these messages so people can find the right file.
            if (date.before(new Date()) || date.equals(new Date())) {
                if (stopShip) {
                    if (!annotation.value().isEmpty()) {
                        messager.printMessage(Diagnostic.Kind.ERROR,
                                "@RemoveThis found with description '" + annotation.value()
                                        + "' at: ", element);
                    } else {
                        messager.printMessage(Diagnostic.Kind.ERROR, "STOP SHIP: @RemoveThis found at: ",
                                element);
                    }
                } else {
                    if (!annotation.value().isEmpty()) {
                        messager.printMessage(Diagnostic.Kind.WARNING,
                                "@RemoveThis found with description '" + annotation.value(), element);
                    } else {
                        messager.printMessage(Diagnostic.Kind.WARNING,
                                "@RemoveThis found without description at: " + packageElement.getSimpleName(),
                                element);
                    }
                }
            }
        }

        return true;
    }
}
