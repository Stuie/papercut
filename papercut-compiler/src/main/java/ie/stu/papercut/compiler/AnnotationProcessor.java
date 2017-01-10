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

import com.github.zafarkhaja.semver.Version;
import com.google.auto.service.AutoService;
import ie.stu.papercut.Milestone;
import ie.stu.papercut.Refactor;
import ie.stu.papercut.RemoveThis;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
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
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions({
        "versionCode",
        "versionName"
})
@AutoService(Processor.class)
public class AnnotationProcessor extends AbstractProcessor {
    private static final String OPTION_VERSION_CODE = "versionCode";
    private static final String OPTION_VERSION_NAME = "versionName";
    private static final Set<String> milestones = new HashSet<>();

    private Messager messager;
    private String versionCode;
    private String versionName;

    @Override
    public synchronized void init(final ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        versionCode = processingEnv.getOptions().get(OPTION_VERSION_CODE);
        versionName = processingEnv.getOptions().get(OPTION_VERSION_NAME);

        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        buildMilestoneList(roundEnv.getElementsAnnotatedWith(Milestone.class));

        parseTechDebtElements(roundEnv.getElementsAnnotatedWith(RemoveThis.class));
        parseTechDebtElements(roundEnv.getElementsAnnotatedWith(Refactor.class));

        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
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

            final String description;
            final String givenDate;
            final boolean stopShip;
            final String milestone;
            final String versionCode;
            final String versionName;
            final String annotationType;

            if (removeThisAnnotation != null) {
                description = removeThisAnnotation.value();
                givenDate = removeThisAnnotation.date();
                stopShip = removeThisAnnotation.stopShip();
                milestone = removeThisAnnotation.milestone();
                versionCode = removeThisAnnotation.versionCode();
                versionName = removeThisAnnotation.versionName();
                annotationType = RemoveThis.class.getSimpleName();
            } else {
                description = refactorAnnotation.value();
                givenDate = refactorAnnotation.date();
                stopShip = refactorAnnotation.stopShip();
                milestone = refactorAnnotation.milestone();
                versionCode = refactorAnnotation.versionCode();
                versionName = refactorAnnotation.versionName();
                annotationType = Refactor.class.getSimpleName();
            }

            final Diagnostic.Kind messageKind = stopShip ? Diagnostic.Kind.ERROR : Diagnostic.Kind.WARNING;
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            Date date = null;

            try {
                if (!givenDate.isEmpty()) {
                    date = simpleDateFormat.parse(givenDate);
                }
            } catch (final ParseException e) {
                messager.printMessage(Diagnostic.Kind.ERROR, "Incorrect date format in Papercut annotation." +
                        "Please use YYYY-MM-DD format.");
            }

            boolean breakConditionMet = noConditionsSet(date, milestone, versionCode, versionName);

            breakConditionMet = breakConditionMet || dateConditionMet(date);
            breakConditionMet = breakConditionMet || milestoneConditionMet(milestone);
            breakConditionMet = breakConditionMet || versionCodeConditionMet(versionCode);
            breakConditionMet = breakConditionMet || versionNameConditionMet(versionName, element);

            if (breakConditionMet) {
                if (!description.isEmpty()) {
                    messager.printMessage(messageKind, String.format("@%1$s found with description %2$s at: ",
                            annotationType, description), element);
                } else {
                    messager.printMessage(messageKind, String.format("@%1$s found at: ", annotationType), element);
                }
            }
        }
    }

    private boolean noConditionsSet(final Date date, final String milestone, final String versionCode,
                                    final String versionName) {
        return date == null && milestone.isEmpty() && versionCode.isEmpty() && versionName.isEmpty();
    }

    private boolean dateConditionMet(final Date date) {
        return date != null && (date.before(new Date()) || date.equals(new Date()));
    }

    private boolean milestoneConditionMet(final String milestone) {
        return !milestone.isEmpty() && !milestones.contains(milestone);
    }

    private boolean versionCodeConditionMet(final String versionCode) {
        return !versionCode.isEmpty() && Integer.parseInt(versionCode) <= Integer.parseInt(this.versionCode);
    }

    private boolean versionNameConditionMet(final String versionName, final Element element) {
        // Drop out quickly if there's no versionName set otherwise the try/catch below is doomed to fail.
        if (versionName.isEmpty()) return false;

        int comparison;

        try {
            final Version conditionVersion = Version.valueOf(versionName);
            final Version currentVersion = Version.valueOf(this.versionName);

            comparison = Version.BUILD_AWARE_ORDER.compare(conditionVersion, currentVersion);
        } catch (final IllegalArgumentException | com.github.zafarkhaja.semver.ParseException e) {
            messager.printMessage(Diagnostic.Kind.ERROR, String.format("Failed to parse versionName: %1$s. " +
                    "Please use a versionName that matches the specification on http://semver.org/", versionName),
                    element);

            // Assume the break condition is met if the versionName is invalid.
            return true;
        }

        return !versionName.isEmpty() && comparison <= 0;
    }
}
