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
package ie.stu.papercut.compiler

import com.github.zafarkhaja.semver.Version
import javax.lang.model.SourceVersion
import com.google.auto.service.AutoService
import javax.lang.model.element.TypeElement
import ie.stu.papercut.Milestone
import ie.stu.papercut.Refactor
import java.lang.IllegalArgumentException
import java.text.ParseException
import javax.tools.Diagnostic
import java.text.SimpleDateFormat
import java.util.Date
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedOptions
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.element.Element

@SupportedAnnotationTypes(
        "ie.stu.papercut.Refactor",
        "ie.stu.papercut.Milestone"
)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(OPTION_VERSION_CODE, OPTION_VERSION_NAME)
@AutoService(Processor::class)
class AnnotationProcessor : AbstractProcessor() {
    private val milestones = mutableSetOf<String>()
    private lateinit var messager: Messager
    private var versionCode: String? = null
    private var versionName: String? = null

    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        versionCode = processingEnv.options[OPTION_VERSION_CODE]
        versionName = processingEnv.options[OPTION_VERSION_NAME]
        messager = processingEnv.messager
    }

    override fun process(annotations: Set<TypeElement?>, roundEnv: RoundEnvironment): Boolean {
        buildMilestoneList(roundEnv.getElementsAnnotatedWith(Milestone::class.java))
        parseTechDebtElements(roundEnv.getElementsAnnotatedWith(Refactor::class.java))
        return true
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latest()
    }

    private fun buildMilestoneList(elements: Set<Element>) {
        for (element in elements) {
            val milestone = element.getAnnotation(Milestone::class.java)
            val milestoneName: String = milestone.value
            milestones.add(milestoneName)
        }
    }

    private fun parseTechDebtElements(elements: Set<Element>) {
        elements.forEach { element ->
            element.getAnnotation(Refactor::class.java)?.let { refactorAnnotation ->
                val description: String = refactorAnnotation.value
                val givenDate = refactorAnnotation.date
                val stopShip = refactorAnnotation.stopShip
                val milestone: String = refactorAnnotation.milestone
                val versionCode: String = refactorAnnotation.versionCode
                val versionName: String = refactorAnnotation.versionName
                val annotationType: String = Refactor::class.java.simpleName

                val messageKind = if (stopShip) {
                    Diagnostic.Kind.ERROR
                } else {
                    Diagnostic.Kind.WARNING
                }

                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
                val date: Date? = try {
                    if (givenDate.isNotEmpty()) {
                        simpleDateFormat.parse(givenDate)
                    } else {
                        null
                    }
                } catch (e: ParseException) {
                    messager.printMessage(Diagnostic.Kind.ERROR, "Incorrect date format in Papercut annotation." +
                            "Please use YYYY-MM-DD format.")
                    null
                }

                var breakConditionMet = noConditionsSet(date, milestone, versionCode, versionName)
                breakConditionMet = breakConditionMet || dateConditionMet(date)
                breakConditionMet = breakConditionMet || milestoneConditionMet(milestone)
                breakConditionMet = breakConditionMet || versionCodeConditionMet(versionCode)
                breakConditionMet = breakConditionMet || versionNameConditionMet(versionName, element)

                if (breakConditionMet) {
                    if (description.isNotEmpty()) {
                        messager.printMessage(messageKind, String.format("@%1\$s found with description %2\$s at: ",
                                annotationType, description), element)
                    } else {
                        messager.printMessage(messageKind, String.format("@%1\$s found at: ", annotationType), element)
                    }
                }
            }
        }
    }

    private fun noConditionsSet(date: Date?, milestone: String, versionCode: String,
                                versionName: String): Boolean {
        return date == null && milestone.isEmpty() && versionCode.isEmpty() && versionName.isEmpty()
    }

    private fun dateConditionMet(date: Date?): Boolean {
        return date != null && (date.before(Date()) || date == Date())
    }

    private fun milestoneConditionMet(milestone: String): Boolean {
        return milestone.isNotEmpty() && !milestones.contains(milestone)
    }

    private fun versionCodeConditionMet(versionCode: String): Boolean {
        return versionCode.isNotEmpty() && versionCode.toInt() <= this.versionCode!!.toInt()
    }

    private fun versionNameConditionMet(versionName: String, element: Element): Boolean {
        // Drop out quickly if there's no versionName set otherwise the try/catch below is doomed to fail.
        if (versionName.isEmpty()) return false
        val comparison = try {
            val conditionVersion = Version.valueOf(versionName)
            val currentVersion = Version.valueOf(this.versionName)
            Version.BUILD_AWARE_ORDER.compare(conditionVersion, currentVersion)
        } catch (e: IllegalArgumentException) {
            messager.printMessage(Diagnostic.Kind.ERROR, String.format("Failed to parse versionName: %1\$s. " +
                    "Please use a versionName that matches the specification on http://semver.org/", versionName),
                    element)

            // Assume the break condition is met if the versionName is invalid.
            return true
        } catch (e: com.github.zafarkhaja.semver.ParseException) {
            messager.printMessage(Diagnostic.Kind.ERROR, String.format("Failed to parse versionName: %1\$s. " +
                    "Please use a versionName that matches the specification on http://semver.org/", versionName),
                    element)
            return true
        }
        return versionName.isNotEmpty() && comparison <= 0
    }
}
