package com.group24h.enlistment;

import java.util.*;
import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.Validate.*;

class Student {

    private final int studentNumber;
    private Collection<Section> sections = new HashSet<>();

    Student(int studentNumber, Collection<Section> sections) {
        isTrue (studentNumber >= 0,
                "studentNumber cannot be negative, was: " + studentNumber);

        notNull(sections, "sections cannot be null");
        this.studentNumber = studentNumber;
        this.sections.addAll(sections);
        this.sections.removeIf(Objects::isNull);
    }

    Student(int studentNumber) {
        this(studentNumber, Collections.emptyList());
    }

    void enlist(Section newSection) {
        notNull(newSection, "section cannot be null");
        sections.forEach(currSection -> currSection.checkForConflict(newSection));
        sections.add(newSection);
    }

    Collection<Section> getSections() {
        return new ArrayList<>(sections);
    }

    @Override
    public String toString() {
        return "Student# " + studentNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return studentNumber == student.studentNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentNumber);
    }
}
