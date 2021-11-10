package com.group24h.enlistment;

import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class StudentTest {

    @Test
    void enlist_two_section_no_conflict() {
        // Given a student and two sections
        Student student = new Student(1);
        Section sec1 = new Section("A", new Schedule(Days.MTH, Period.H0830));
        Section sec2 = new Section("B", new Schedule(Days.TF, Period.H1000));

        // When the student enlists in both sections
        student.enlist(sec1);
        student.enlist(sec2);

        // Then both sections should be found in the student & no other sections
        Collection<Section> sections = student.getSections();
        assertAll(
            () -> assertTrue(sections.containsAll(List.of(sec1, sec2))),
            () -> assertEquals(2, sections.size())
        );
    }

    @Test
    void enlist_two_sections_same_schedule() {
        // Given a student & two sections w/ same schedule
        Student student = new Student(1, Collections.emptyList());
        Section sec1 = new Section("A", new Schedule(Days.MTH, Period.H0830));
        Section sec2 = new Section("B", new Schedule(Days.MTH, Period.H0830));

        // When the student enlists in both sections
        student.enlist(sec1);

        // Then an exception should be thrown on the second enlistment
        assertThrows(ScheduleConflictException.class, () -> student.enlist(sec2));
    }
}
