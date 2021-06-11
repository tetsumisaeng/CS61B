package flik;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestFlik {

    @Test
    public void TestFlik1() {
        assertTrue(Flik.isSameNumber(0, 0));
        assertTrue(Flik.isSameNumber(1, 1));
        assertTrue(Flik.isSameNumber(-1, -1));
        assertTrue(Flik.isSameNumber(1024, 1024));
    }
}
