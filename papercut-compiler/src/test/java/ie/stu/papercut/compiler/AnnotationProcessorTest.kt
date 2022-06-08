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

import ie.stu.papercut.Debt
import ie.stu.papercut.Milestone
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.tools.Diagnostic

class AnnotationProcessorTest {
    private val arbitraryDeadline0 = "Arbitrary Deadline #0"
    private val arbitraryDeadline1 = "Arbitrary Deadline #1"
    private val sampleReason = "Time constraints"
    private val sampleDebtValue = "Debt value"
    private val sampleDebtDescription = "Debt description"
    private val specialEditionReleaseVersionName = "Special Edition Release"
    private val invalidDate = "01-01-2000"
    private val validDate = "2020-11-01"
    private val sampleAddedDate = "1999-12-31"

    private lateinit var subject: AnnotationProcessor

    @RelaxedMockK
    lateinit var processingEnvironment: ProcessingEnvironment

    @RelaxedMockK
    lateinit var messager: Messager

    @RelaxedMockK
    lateinit var roundEnvironment: RoundEnvironment

    @MockK
    lateinit var debtElement: Element

    @MockK
    lateinit var debtAnnotationWithoutCondition: Debt

    @MockK
    lateinit var milestoneElement: Element

    @MockK
    lateinit var milestoneAnnotation: Milestone

    init {
        MockKAnnotations.init(this)
    }

    @BeforeEach
    fun setup() {
        subject = AnnotationProcessor()

        val versionCodeOptions = mapOf(
            "versionCode" to "10",
            "versionName" to "10.20.30"
        )

        every { processingEnvironment.options } returns versionCodeOptions
        every { processingEnvironment.messager } returns messager
    }

    @Test
    fun `init sets up messager from environment`() {
        subject.init(processingEnvironment)

        verify(exactly = 1) { processingEnvironment.messager }
    }

    @Test
    fun `process prints warning when no conditions set`() {
        setupDebtAnnotationWithoutCondition()

        val (messageKind, _, element) = executeCaptureAndReturn()

        assertEquals(Diagnostic.Kind.WARNING, messageKind)
        assertEquals(debtElement, element)
    }

    @Test
    fun `process prints added date when present`() {
        setupDebtAnnotationWithoutCondition()
        every { debtAnnotationWithoutCondition.addedDate } returns sampleAddedDate

        val (_, message, _) = executeCaptureAndReturn()

        assertTrue(message.contains(sampleAddedDate))
    }

    @Test
    fun `process prints a message without value when empty`() {
        setupDebtAnnotationWithoutCondition()
        every { debtAnnotationWithoutCondition.value } returns ""

        val (_, message, _) = executeCaptureAndReturn()

        assertFalse(message.contains(VALUE))
    }

    @Test
    fun `process prints error when no conditions set and stopShip is true`() {
        setupDebtAnnotationWithoutCondition()
        every { debtAnnotationWithoutCondition.stopShip } returns true

        val (messageKind, _, element) = executeCaptureAndReturn()

        assertEquals(Diagnostic.Kind.ERROR, messageKind)
        assertEquals(debtElement, element)
    }

    @Test
    fun `process prints message containing milestone when milestone is unrecognized`() {
        setupDebtAnnotationWithoutCondition()
        setupMilestoneAnnotation()
        every { debtAnnotationWithoutCondition.milestone } returns arbitraryDeadline0

        val (_, message, _) = executeCaptureAndReturn()

        assertTrue(message.contains(MILESTONE))
        assertTrue(message.contains(arbitraryDeadline0))
    }

    @Test
    fun `process prints message without milestone when no milestone is set`() {
        setupDebtAnnotationWithoutCondition()
        setupMilestoneAnnotation()

        val (_, message, _) = executeCaptureAndReturn()

        assertFalse(message.contains(MILESTONE))
    }

    @Test
    fun `process prints message containing description when description set`() {
        setupDebtAnnotationWithoutCondition()

        val (_, message, _) = executeCaptureAndReturn()

        assertTrue(message.contains(sampleDebtDescription))
    }

    @Test
    fun `process prints message without description when description empty`() {
        setupDebtAnnotationWithoutCondition()
        every { debtAnnotationWithoutCondition.description } returns ""

        val (_, message, _) = executeCaptureAndReturn()

        assertFalse(message.contains(WITH_DESCRIPTION))
    }

    @Test
    fun `process prints message containing value when value set`() {
        setupDebtAnnotationWithoutCondition()

        val (_, message, _) = executeCaptureAndReturn()

        assertTrue(message.contains(sampleDebtValue))
    }

    @Test
    fun `process prints message containing reason when reason is set`() {
        setupDebtAnnotationWithoutCondition()

        val (_, message, _) = executeCaptureAndReturn()

        assertTrue(message.contains(sampleReason))
    }

    @Test
    fun `process prints message without reason when reason is not set`() {
        setupDebtAnnotationWithoutCondition()
        every { debtAnnotationWithoutCondition.reason } returns ""

        val (_, message, _) = executeCaptureAndReturn()

        assertFalse(message.contains(DEBT_REASON))
    }

    @Test
    fun `process prints a message when version code condition met`() {
        setupDebtAnnotationWithoutCondition()
        every { debtAnnotationWithoutCondition.versionCode } returns 1

        val (_, message, _) = executeCaptureAndReturn()

        assertTrue(message.contains(sampleDebtDescription))
    }

    @Test
    fun `process does not print a message when version code condition is not met`() {
        setupDebtAnnotationWithoutCondition()
        every { debtAnnotationWithoutCondition.versionCode } returns 11

        subject.init(processingEnvironment)
        subject.process(emptySet(), roundEnvironment)

        verify(exactly = 0) { messager.printMessage(any(), any(), any()) }
    }

    @Test
    fun `process prints a message when version name condition met`() {
        setupDebtAnnotationWithoutCondition()
        every { debtAnnotationWithoutCondition.versionName } returns "9.0.0"

        val (_, message, _) = executeCaptureAndReturn()

        assertTrue(message.contains(sampleDebtDescription))
    }

    @Test
    fun `process does not print a message when version name condition is not met`() {
        setupDebtAnnotationWithoutCondition()
        every { debtAnnotationWithoutCondition.versionName } returns "11.0.0"

        subject.init(processingEnvironment)
        subject.process(emptySet(), roundEnvironment)

        verify(exactly = 0) { messager.printMessage(any(), any(), any()) }
    }

    @Test
    fun `process prints error for invalid version name in options`() {
        setupDebtAnnotationWithoutCondition()
        val versionCodeOptions = mapOf(
            "versionCode" to "1337",
            "versionName" to specialEditionReleaseVersionName
        )

        every { processingEnvironment.options } returns versionCodeOptions
        every { debtAnnotationWithoutCondition.versionName } returns "11.0.0"

        subject.init(processingEnvironment)
        subject.process(emptySet(), roundEnvironment)

        val messageSlot = slot<String>()
        val elementSlot = slot<Element>()

        verifyOrder {
            messager.printMessage(Diagnostic.Kind.ERROR, capture(messageSlot), capture(elementSlot))
            messager.printMessage(Diagnostic.Kind.WARNING, any(), any())
        }

        assertTrue(messageSlot.captured.contains(specialEditionReleaseVersionName))
    }

    @Test
    fun `process prints error for invalid version name in annotation`() {
        setupDebtAnnotationWithoutCondition()
        val versionCodeOptions = mapOf(
            "versionCode" to "1337",
            "versionName" to "11.0.0"
        )

        every { processingEnvironment.options } returns versionCodeOptions
        every { debtAnnotationWithoutCondition.versionName } returns specialEditionReleaseVersionName

        subject.init(processingEnvironment)
        subject.process(emptySet(), roundEnvironment)

        val messageSlot = slot<String>()
        val elementSlot = slot<Element>()

        verifyOrder {
            messager.printMessage(Diagnostic.Kind.ERROR, capture(messageSlot), capture(elementSlot))
            messager.printMessage(Diagnostic.Kind.WARNING, any(), any())
        }

        assertTrue(messageSlot.captured.contains(specialEditionReleaseVersionName))
    }

    @Test
    fun `process prints error for invalid removal date format`() {
        setupDebtAnnotationWithoutCondition()
        every { debtAnnotationWithoutCondition.removalDate } returns invalidDate

        subject.init(processingEnvironment)
        subject.process(emptySet(), roundEnvironment)

        val messageSlot = slot<String>()
        val elementSlot = slot<Element>()

        verifyOrder {
            messager.printMessage(Diagnostic.Kind.ERROR, capture(messageSlot), capture(elementSlot))
            messager.printMessage(Diagnostic.Kind.WARNING, any(), any())
        }

        assertTrue(messageSlot.captured.contains(invalidDate))
    }

    @Test
    fun `process does not print error for valid removal date format`() {
        setupDebtAnnotationWithoutCondition()
        every { debtAnnotationWithoutCondition.removalDate } returns validDate

        subject.init(processingEnvironment)
        subject.process(emptySet(), roundEnvironment)

        verify(exactly = 0) { messager.printMessage(Diagnostic.Kind.ERROR, any(), any()) }
    }

    private fun executeCaptureAndReturn(): Result {
        subject.init(processingEnvironment)
        subject.process(emptySet(), roundEnvironment)

        val messageKindSlot = slot<Diagnostic.Kind>()
        val messageSlot = slot<String>()
        val elementSlot = slot<Element>()

        verify { messager.printMessage(capture(messageKindSlot), capture(messageSlot), capture(elementSlot)) }

        return Result(messageKindSlot.captured, messageSlot.captured, elementSlot.captured)
    }

    private fun setupDebtAnnotationWithoutCondition() {
        every { debtAnnotationWithoutCondition.value } returns sampleDebtValue
        every { debtAnnotationWithoutCondition.description } returns sampleDebtDescription
        every { debtAnnotationWithoutCondition.reason } returns sampleReason
        every { debtAnnotationWithoutCondition.addedDate } returns ""
        every { debtAnnotationWithoutCondition.stopShip } returns false
        every { debtAnnotationWithoutCondition.milestone } returns ""
        every { debtAnnotationWithoutCondition.removalDate } returns ""
        every { debtAnnotationWithoutCondition.versionCode } returns Int.MAX_VALUE
        every { debtAnnotationWithoutCondition.versionName } returns ""

        every {
            roundEnvironment.getElementsAnnotatedWith(Debt::class.java)
        } returns setOf(debtElement)

        every {
            debtElement.getAnnotation(Debt::class.java)
        } returns debtAnnotationWithoutCondition
    }

    private fun setupMilestoneAnnotation() {
        every { milestoneAnnotation.value } returns arbitraryDeadline1

        every {
            roundEnvironment.getElementsAnnotatedWith(Milestone::class.java)
        } returns setOf(milestoneElement)

        every {
            milestoneElement.getAnnotation(Milestone::class.java)
        } returns milestoneAnnotation
    }

    data class Result(
        val messageKind: Diagnostic.Kind,
        val message: String,
        val element: Element,
    )
}
