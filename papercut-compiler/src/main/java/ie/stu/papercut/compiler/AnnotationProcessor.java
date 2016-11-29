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

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
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
            final String objectType = element.getSimpleName().toString();
            final RemoveThis annotation = element.getAnnotation(RemoveThis.class);

            final boolean stopShip = annotation.stopShip();

            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;

            try {
                date = simpleDateFormat.parse(annotation.date());
            } catch (ParseException e) {
                throw new RuntimeException("STOP SHIP: Incorrect date format in @RemoveThis annotation. Please " +
                        "follow YYYY-MM-DD format.");
            }

            if (date.before(new Date()) || date.equals(new Date())) {
                if (stopShip) {
                    throw new RuntimeException("STOP SHIP: @RemoveThis found.");
                } else {
                    //TODO Figure out the fricking logger
                    System.out.println("@RemoveThis found.");
                }
            }
        }

        return true;
    }
}
