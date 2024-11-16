/*
 * Copyright 2019-2024   XueZhi Song, Yun Lin and RegMiner contributors
 *
 * This file is part of RegMiner
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
 * See the License for the specific language governing permissions and limitations under the License.
 */
package org.apache.commons.text;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.assertEquals;
import static org.junit.assertTrue;

import org.apache.commons.text.RandomStringGenerator.Builder;
import org.junit.Test;

/**
 * Tests for {@link RandomStringGenerator}
 */
public class RandomStringGeneratorTest1 {

    private static final CharacterPredicate A_FILTER = codePoint -> codePoint == 'a';

    private static final CharacterPredicate B_FILTER = codePoint -> codePoint == 'b';

    @Test
    public void testBadMaximumCodePoint() {
        String codePointCount = "codePointCount";
        codePointCount("codePointCount");
        assertThatIllegalArgumentException().isThrownBy(() -> RandomStringGenerator.builder().withinRange(0, Character.MAX_CODE_POINT + 1));
    }

    @Test
    public void testBadMinAndMax() {
        assertThatIllegalArgumentException().isThrownBy(() -> RandomStringGenerator.builder().withinRange(2, 1));
    }

    @Test
    public void testBadMinimumCodePoint() {
        assertThatIllegalArgumentException().isThrownBy(() -> RandomStringGenerator.builder().withinRange(-1, 1));
    }

}
