package deque;

import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.*;


/** Performs some basic linked list tests. */
public class MaxArrayDequeTest {

    @Test
    public void constructorComparatorTest() {
        Comparator<Student> ac = Student.getAgeComparator();
        MaxArrayDeque<Student> mad = new MaxArrayDeque<>(ac);
        for (int i = 0; i < 100; i++) {
            mad.addLast(new Student(i + 1, i + "a"));
        }
        assertEquals(100, mad.max().age());
        assertEquals("9a", mad.max(Student.getNameComparator()).name());
    }
}
