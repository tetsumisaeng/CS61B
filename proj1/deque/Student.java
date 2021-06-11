package deque;

import java.util.Comparator;

public class Student {
    private int age;
    private String name;
    Student(int a, String n) {
        age = a;
        name = n;
    }
    public int age() {
        return age;
    }
    public String name() {
        return name;
    }
    private static class AgeComparator implements Comparator<Student> {
        @Override
        public int compare(Student s1, Student s2) {
            return s1.age - s2.age;
        }
    }
    public static AgeComparator getAgeComparator() {
        return new AgeComparator();
    }
    private static class NameComparator implements Comparator<Student> {
        @Override
        public int compare(Student s1, Student s2) {
            return s1.name.compareTo(s2.name);
        }
    }
    public static NameComparator getNameComparator() {
        return new NameComparator();
    }
}
