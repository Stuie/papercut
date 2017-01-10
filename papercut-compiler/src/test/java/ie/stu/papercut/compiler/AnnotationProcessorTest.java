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

import org.junit.Before;
import org.junit.Test;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AnnotationProcessorTest {
    private AnnotationProcessor annotationProcessor;
    private ProcessingEnvironment processingEnvironment;
    private Messager messager;

    @Before
    public void setup() {
        annotationProcessor = new AnnotationProcessor();

        processingEnvironment = mock(ProcessingEnvironment.class);
        messager = mock(Messager.class);

        final Map<String, String> versionCodeOptions = new HashMap<>();
        versionCodeOptions.put("versionCode", "1");

        when(processingEnvironment.getOptions()).thenReturn(versionCodeOptions);
        when(processingEnvironment.getMessager()).thenReturn(messager);
    }

    @Test
    public void init_setsUpMessager() throws Exception {
        annotationProcessor.init(processingEnvironment);

        verify(processingEnvironment).getMessager();
    }

    @Test
    public void init_retrievesOptions() throws Exception {
        annotationProcessor.init(processingEnvironment);

        verify(processingEnvironment, times(2)).getOptions();
    }
}