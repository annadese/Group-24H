package com.group24h.enlistment;

import java.util.*;

import static org.apache.commons.lang3.Validate.*;

class Student {

    private final int studentNumber;
    private Collection<Section> sections = new HashSet<>();
    private Collection<Subject> completedSubjects = new HashSet<>();

    Student(int studentNumber, Collection<Section> sections, Collection<Subject> completedSubjects) {
        isTrue (studentNumber >= 0,
                "studentNumber cannot be negative, was: " + studentNumber);

        notNull(sections, "sections cannot be null");
        this.studentNumber = studentNumber;
        this.sections.addAll(sections);
        this.sections.removeIf(Objects::isNull);
        this.completedSubjects.addAll(completedSubjects);
        this.completedSubjects.removeIf(Objects::isNull);
    }

    Student(int studentNumber) {
        this(studentNumber, Collections.emptyList(), Collections.emptyList());
    }

    void enlist(Section newSection) {
        notNull(newSection, "section cannot be null");
        sections.forEach(currSection -> currSection.checkForConflict(newSection));
        newSection.getSubject().checkPrerequisites(completedSubjects);
        newSection.lock();
        try {
            newSection.addEnlistedStudent();
            sections.add(newSection);
        } finally {
            newSection.unlock();
        }
    }

    void cancelEnlistment(Section section) {
        notNull(section, "section cannot be null");
        isTrue(sections.contains(section), "student is not enlisted in section");
        section.removeEnlistedStudent();
        sections.remove(section);
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
