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
import ie.stu.papercut.Milestone;
import ie.stu.papercut.Refactor;
import ie.stu.papercut.RemoveThis;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@SupportedAnnotationTypes({
        "ie.stu.papercut.RemoveThis",
        "ie.stu.papercut.Refactor",
        "ie.stu.papercut.Milestone"
})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@AutoService(Processor.class)
public class AnnotationProcessor extends AbstractProcessor {
    private static final Set<String> milestones = new HashSet<>();

    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        buildMilestoneList(roundEnv.getElementsAnnotatedWith(Milestone.class));

        parseTechDebtElements(roundEnv.getElementsAnnotatedWith(RemoveThis.class));
        parseTechDebtElements(roundEnv.getElementsAnnotatedWith(Refactor.class));

        return true;
    }

    private void buildMilestoneList(Set<? extends Element> elements) {
        for (final Element element : elements) {
            final Milestone milestone = element.getAnnotation(Milestone.class);
            final String milestoneName = milestone.value();

            milestones.add(milestoneName);
        }
    }

    private void parseTechDebtElements(Set<? extends Element> elements) {
        for (final Element element : elements) {
            final RemoveThis removeThisAnnotation = element.getAnnotation(RemoveThis.class);
            final Refactor refactorAnnotation = element.getAnnotation(Refactor.class);

            // Handling the case where both annotations are present would be overly complicated. Since they're
            // essentially interchangeable I doubt anyone will encounter this. If you're reading this because you
            // encountered the issue then please pick between the annotations. The docs should help you choose.
            if (removeThisAnnotation != null && refactorAnnotation != null) {
                messager.printMessage(Diagnostic.Kind.ERROR, "Specifying @RemoveThis and @Refactor on the same" +
                        "code is not currently supported.", element);
            }

            String description;
            String givenDate;
            boolean stopShip;
            String milestone;

            if (removeThisAnnotation != null) {
                description = removeThisAnnotation.value();
                givenDate = removeThisAnnotation.date();
                stopShip = removeThisAnnotation.stopShip();
                milestone = removeThisAnnotation.milestone();
            } else {
                description = refactorAnnotation.value();
                givenDate = refactorAnnotation.date();
                stopShip = refactorAnnotation.stopShip();
                milestone = refactorAnnotation.milestone();
            }

            final Diagnostic.Kind messageKind = stopShip ? Diagnostic.Kind.ERROR : Diagnostic.Kind.WARNING;
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            Date date = null;

            try {
                if (!givenDate.isEmpty()) {
                    date = simpleDateFormat.parse(givenDate);
                }
            } catch (final ParseException e) {
                messager.printMessage(Diagnostic.Kind.ERROR,"Incorrect date format in @RemoveThis annotation." +
                        "Please use YYYY-MM-DD format.");
            }

            if (dateConditionMet(date) || milestoneConditionMet(milestone)) {
                if (!description.isEmpty()) {
                    messager.printMessage(messageKind, "@RemoveThis found with description '" + description
                                    + "' at: ", element);
                } else {
                    messager.printMessage(messageKind, "@RemoveThis found at: ", element);
                }
            }
        }
    }

    private boolean dateConditionMet(final Date date) {
        return date != null && (date.after(new Date()) || date.equals(new Date()));
    }

    private boolean milestoneConditionMet(final String milestone) {
        return !milestones.contains(milestone);
    }
}
