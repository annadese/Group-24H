package com.group24h.enlistment;

import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class StudentTest {

    @Test
    void enlist_two_section_no_conflict() {
        // Given a student & two sections
        Student student = new Student(1);
        Section sec1 = new Section("A", new Schedule(Days.MTH, Period.H0830), new Room("G303", 30));
        Section sec2 = new Section("B", new Schedule(Days.TF, Period.H1000), new Room("G303", 30));

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
        Student student = new Student(1);
        Section sec1 = new Section("A", new Schedule(Days.MTH, Period.H0830), new Room("G303", 30));
        Section sec2 = new Section("B", new Schedule(Days.MTH, Period.H0830), new Room("G303", 30));

        // When the student enlists in both sections
        student.enlist(sec1);

        // Then an exception should be thrown on the second enlistment
        assertThrows(ScheduleConflictException.class, () -> student.enlist(sec2));
    }

    @Test
    void enlist_open_section() {
        // Given a student and an open section
        Student student = new Student(1);
        Section sec = new Section("A", new Schedule(Days.MTH, Period.H0830), new Room("G303", 30));

        // When the student enlists in the section
        student.enlist(sec);

        // Then the section should be found in the student & no other sections
        Collection<Section> sections = student.getSections();
        assertAll(
                () -> assertTrue(sections.contains(sec)),
                () -> assertEquals(1, sections.size())
        );
    }

    @Test
    void enlist_full_section() {
        // Given two students and a section w/ room capacity of 1
        Student student1 = new Student(1);
        Student student2 = new Student(2);
        Section sec = new Section("A", new Schedule(Days.MTH, Period.H0830), new Room("G303", 1));

        // When the two students enlist in the same section
        student1.enlist(sec);

        // Then an exception should be thrown on the second student's enlistment
        assertThrows(RuntimeException.class, () -> student2.enlist(sec));
    }

    @Test
    void cancel_enlisted_section() {
        // Given a student enlisted in a section
        Student student = new Student(1);
        Section sec = new Section("A", new Schedule(Days.MTH, Period.H0830), new Room("G303", 30));
        student.enlist(sec);

        // When the student cancels an enlisted section
        student.cancelEnlistment(sec);

        // Then the section should not be found in the student
        Collection<Section> sections = student.getSections();
        assertAll(
                () -> assertFalse(sections.contains(sec)),
                () -> assertEquals(0, sections.size())
        );
    }

    @Test
    void cancel_not_enlisted_section() {
        // Given a student and a section
        Student student = new Student(1);
        Section sec = new Section("A", new Schedule(Days.MTH, Period.H0830), new Room("G303", 30));

        // When the student cancels their enlistment in a section they are not enlisted in
        // Then an exception should be thrown on the student's cancellation
        assertThrows(RuntimeException.class, () -> student.cancelEnlistment(sec));
    }
}
