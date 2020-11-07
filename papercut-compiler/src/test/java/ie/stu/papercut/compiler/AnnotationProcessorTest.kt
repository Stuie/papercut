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

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment

class AnnotationProcessorTest {
    lateinit var annotationProcessor: AnnotationProcessor

    @RelaxedMockK
    lateinit var processingEnvironment: ProcessingEnvironment

    @RelaxedMockK
    lateinit var messager: Messager

    init {
        MockKAnnotations.init(this)
    }

    @BeforeEach
    fun setup() {
        annotationProcessor = AnnotationProcessor()

        val versionCodeOptions = mapOf("versionCode" to "1")

        every { processingEnvironment.options } returns versionCodeOptions
        every { processingEnvironment.messager } returns messager
    }

    @Test
    fun init_setsUpMessager() {
        annotationProcessor.init(processingEnvironment)

        verify { processingEnvironment.messager }
    }

    @Test
    fun init_retrievesOptions() {
        annotationProcessor.init(processingEnvironment)

        verify(exactly = 2) { processingEnvironment.options }
    }
}
