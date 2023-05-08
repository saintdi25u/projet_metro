package fr.ul.miage;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

public class PlanTest {

    @Test
    @DisplayName("Test station la plus proche")
    public void testNearestStation1() {
        Plan p = new Plan();
        float initialX = 6;
        float initialY = 16;
        Station stationNearest = p.getNearestStation(initialX, initialY);
        assertThat(stationNearest.getName()).isEqualTo("H");
    }
}
