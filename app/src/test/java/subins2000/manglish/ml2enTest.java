package subins2000.manglish;

import org.junit.Test;

import static org.junit.Assert.*;

public class ml2enTest {

    @Test
    public void ml2en() {
        ml2en m = new ml2en();
        assertEquals("kk", m.convert("ക്ക"));
    }
}