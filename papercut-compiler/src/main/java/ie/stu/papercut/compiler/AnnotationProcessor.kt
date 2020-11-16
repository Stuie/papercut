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
import ie.stu.papercut.Debt
import javax.lang.model.element.TypeElement
import ie.stu.papercut.Milestone
import javax.tools.Diagnostic
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
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
        "ie.stu.papercut.Debt",
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
        parseTechDebtElements(roundEnv.getElementsAnnotatedWith(Debt::class.java))
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
            element.getAnnotation(Debt::class.java)?.let { debtAnnotation ->
                val value = debtAnnotation.value
                val description = debtAnnotation.description
                val reason = debtAnnotation.reason
                val addedDate = debtAnnotation.addedDate
                val stopShip = debtAnnotation.stopShip
                val milestone = debtAnnotation.milestone
                val removalDate = debtAnnotation.removalDate
                val versionCode: Int = debtAnnotation.versionCode
                val versionName = debtAnnotation.versionName

                val messageKind = if (stopShip) {
                    Diagnostic.Kind.ERROR
                } else {
                    Diagnostic.Kind.WARNING
                }

                val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val parsedRemovalDate: LocalDateTime? = try {
                    if (removalDate.isNotEmpty()) {
                        LocalDateTime.parse(removalDate, dateTimeFormatter)
                    } else {
                        null
                    }
                } catch (e: DateTimeParseException) {
                    messager.printMessage(
                        Diagnostic.Kind.ERROR,
                        String.format(
                            "Unrecognized date format in Papercut annotation '%1\$s'. Please use YYYY-MM-DD format " +
                                    "for removalDate: %2\$s",
                            removalDate,
                            e.localizedMessage
                        ),
                        element
                    )
                    null
                }

                val breakConditionMet = noConditionsSet(parsedRemovalDate, milestone, versionCode, versionName)
                        || dateConditionMet(parsedRemovalDate)
                        || milestoneConditionMet(milestone)
                        || versionCodeConditionMet(versionCode)
                        || versionNameConditionMet(versionName, element)

                if (breakConditionMet) {
                    printDebtMessage(messager, messageKind, value, description, addedDate, reason, milestone, element)
                }
            }
        }
    }

    private fun noConditionsSet(date: LocalDateTime?, milestone: String, versionCode: Int,
                                versionName: String): Boolean {
        return date == null && milestone.isEmpty() && versionCode == Int.MAX_VALUE && versionName.isEmpty()
    }

    private fun dateConditionMet(date: LocalDateTime?): Boolean {
        return date != null && (date.isBefore(LocalDateTime.now()) || date == LocalDateTime.now())
    }

    private fun milestoneConditionMet(milestone: String): Boolean {
        return milestone.isNotEmpty() && !milestones.contains(milestone)
    }

    private fun versionCodeConditionMet(versionCode: Int): Boolean {
        return this.versionCode?.let {
            it.isNotEmpty() && versionCode != Int.MAX_VALUE && versionCode <= it.toInt()
        } ?: false
    }

    private fun versionNameConditionMet(versionName: String, element: Element): Boolean {
        // Drop out quickly if there's no versionName set otherwise the try/catch below is doomed to fail.
        if (versionName.isEmpty()) return false

        val comparison = try {
            val conditionVersion = Version.valueOf(versionName)
            val currentVersion = Version.valueOf(this.versionName)
            Version.BUILD_AWARE_ORDER.compare(conditionVersion, currentVersion)
        } catch (e: Exception) {
            messager.printMessage(
                Diagnostic.Kind.ERROR,
                String.format(
                    "Failed to parse version name: %1\$s or %2\$s. Please use a versionName that matches the " +
                        "specification on http://semver.org/",
                    versionName,
                    this.versionName
                ),
                element
            )

            // Assume the break condition is met if the versionName is invalid.
            return true
        }

        return versionName.isNotEmpty() && comparison <= 0
    }

    private fun printDebtMessage(
        messager: Messager,
        messageKind: Diagnostic.Kind,
        value: String,
        description: String,
        addedDate: String,
        reason: String,
        milestone: String,
        element: Element,
    ) {
        val messageBuilder = StringBuilder(DEBT_FOUND)

        messageBuilder.append(if (value.isNotEmpty()) {
            " $VALUE '%1\$s',"
        } else {
            "%1\$s"
        })

        if (addedDate.isNotEmpty()) {
            messageBuilder.append(" $ADDED_DATE '%2\$s',")
        } else {
            messageBuilder.append("%2\$s")
        }

        if (reason.isNotEmpty()) {
            messageBuilder.append(" $DEBT_REASON '%3\$s',")
        } else {
            messageBuilder.append("%3\$s")
        }

        if (milestone.isNotEmpty()) {
            messageBuilder.append(" $MILESTONE '%4\$s',")
        } else {
            messageBuilder.append("%4\$s")
        }

        if (description.isNotEmpty()) {
            messageBuilder.append(" $WITH_DESCRIPTION '%5\$s',")
        } else {
            messageBuilder.append("%5\$s")
        }

        messageBuilder.append(" at: ")

        messager.printMessage(
            messageKind,
            String.format(
                messageBuilder.toString(),
                value,
                addedDate,
                reason,
                milestone,
                description
            ),
            element
        )
    }
}
