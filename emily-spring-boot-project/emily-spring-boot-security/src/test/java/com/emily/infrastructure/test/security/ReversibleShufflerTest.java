package com.emily.infrastructure.test.security;

import com.emily.infrastructure.security.utils.ReversibleShufflerUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author :  Emily
 * @since :  2025/4/5 下午9:56
 */
public class ReversibleShufflerTest {
    @Test
    public void tesetShuffler() {
        String originStr = "SDDS1 834567「8{92}1  566";
        long seed = System.currentTimeMillis();
        String shuffle = ReversibleShufflerUtils.shuffle(originStr, seed);
        String reversibleShuffle = ReversibleShufflerUtils.reverseShuffle(shuffle, seed);
        Assertions.assertEquals(originStr, reversibleShuffle);
    }
}
