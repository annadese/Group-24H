package com.group24h.enlistment;

import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

class StudentTest {

    @Test
    void enlist_two_section_no_conflict() {
        // Given a student & two sections
        Student student = new Student(1);
        Section sec1 = new Section("A", new Schedule(Days.MTH, Period.H0830), new Room("G303", 30), new Subject("STSWENG", Collections.EMPTY_SET));
        Section sec2 = new Section("B", new Schedule(Days.TF, Period.H1000), new Room("G303", 30), new Subject("STSWENG", Collections.EMPTY_SET));

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
        Section sec1 = new Section("A", new Schedule(Days.MTH, Period.H0830), new Room("G303", 30), new Subject("STSWENG", Collections.EMPTY_SET));
        Section sec2 = new Section("B", new Schedule(Days.MTH, Period.H0830), new Room("G303", 30), new Subject("STSWENG", Collections.EMPTY_SET));

        // When the student enlists in both sections
        student.enlist(sec1);

        // Then an exception should be thrown on the second enlistment
        assertThrows(ScheduleConflictException.class, () -> student.enlist(sec2));
    }


    @Test
    void enlist_within_room_capacity() {
        // Given two students and one section with room capacity 5
        Student student1 = new Student(1);
        Student student2 = new Student(2);
        final int CAPACITY = 5;
        Room room =  new Room("X", CAPACITY);
        Section section = new Section("A", new Schedule(Days.MTH, Period.H0830), room, new Subject("STSWENG", Collections.EMPTY_SET));
        // When the two students enlist in the section
        student1.enlist(section);
        student2.enlist(section);
        // Then the number for students in the section should be 2
        assertEquals(2, section.getNumberOfStudents());
    }

    @Test
    void enlist_exceeding_room_capacity() {
        // Given two students and one section with room capacity 1
        Student student1 = new Student(1);
        Student student2 = new Student(2);
        final int CAPACITY = 1;
        Room room =  new Room("X", CAPACITY);
        Section section = new Section("A", new Schedule(Days.MTH, Period.H0830), room, new Subject("STSWENG", Collections.EMPTY_SET));
        // When the two students enlist in the section
        student1.enlist(section);
        // Then an exception should be thrown at 2nd enlistment
        assertThrows(CapacityException.class, () -> student2.enlist(section));
    }

    @Test
    void enlist_students_at_capacity_in_two_sections_sharing_the_same_room() {
        // Given 2 sections that share same room w/ capacity 1, and 2 students
        final int CAPACITY = 1;
        Room room = new Room("X", CAPACITY);
        Section sec1 = new Section("A", new Schedule(Days.MTH, Period.H0830), room, new Subject("STSWENG", Collections.EMPTY_SET));
        Section sec2 = new Section("B", new Schedule(Days.TF, Period.H0830), room, new Subject("STSWENG", Collections.EMPTY_SET));
        Student student1 = new Student(1);
        Student student2 = new Student(2);
        // When each student enlists in a different section
        student1.enlist(sec1);
        student2.enlist(sec2);
        // No exception should be thrown
    }

    @Test
    void enlist_concurrent_almost_full_section() throws Exception {
        for (int i = 0; i < 20; i++) { // repeat test 20 times
            // Given multiple students wanting to enlist in a section w/ capacity of 1
            Student student1 = new Student(1);
            Student student2 = new Student(2);
            Student student3 = new Student(3);
            Student student4 = new Student(4);
            Student student5 = new Student(5);
            Section section = new Section("X", new Schedule(Days.MTH, Period.H0830), new Room("Y", 1), new Subject("STSWENG", Collections.EMPTY_SET));
            // When they enlist concurrently
            CountDownLatch latch = new CountDownLatch(1);
            new EnlistmentThread(student1, section, latch).start();
            new EnlistmentThread(student2, section, latch).start();
            new EnlistmentThread(student3, section, latch).start();
            new EnlistmentThread(student4, section, latch).start();
            new EnlistmentThread(student5, section, latch).start();
            latch.countDown();
            Thread.sleep(100);
            // Only one should be able to enlist
            assertEquals(1, section.getNumberOfStudents());
        }
    }

    private static class EnlistmentThread extends Thread {
        private final Student student;
        private final Section section;
        private final CountDownLatch latch;

        public EnlistmentThread(Student student, Section section, CountDownLatch latch) {
            this.student = student;
            this.section = section;
            this.latch = latch;
        }

        @Override
        public void run() {
            try {
                latch.await(); // The thread keeps waiting till it is informed
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            try {
                student.enlist(section);
            } catch (CapacityException e) {
                // DO NOTHING... avoid printing messy stack trace
            }
        }
    }

    @Test
    void cancel_enlisted_section() {
        // Given a student enlisted in a section
        Student student = new Student(1);
        Section sec = new Section("A", new Schedule(Days.MTH, Period.H0830), new Room("G303", 30), new Subject("STSWENG", Collections.EMPTY_SET));
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
        Section sec = new Section("A",
                new Schedule(Days.MTH, Period.H0830),
                new Room("G303", 30),
                new Subject("STSWENG", Collections.EMPTY_SET));

        // When the student cancels their enlistment in a section they are not enlisted in
        // Then an exception should be thrown on the student's cancellation
        assertThrows(RuntimeException.class, () -> student.cancelEnlistment(sec));
    }

    @Test
    void enlist_section_new_subject() {
        // Given a student that has a section
        final int INITIAL_NUMBER_OF_STUDENTS = 5;
        Section enlistedSection = new Section("A",
                new Schedule(Days.MTH, Period.H0830),
                new Room("Z", 10),
                new Subject("Y", Collections.EMPTY_SET),
                INITIAL_NUMBER_OF_STUDENTS);
        Section sectionToBeEnlisted = new Section("B",
                new Schedule(Days.WS, Period.H1130),
                new Room("X", 10),
                new Subject("W", Collections.EMPTY_SET),
                INITIAL_NUMBER_OF_STUDENTS);
        Student student = new Student(1, List.of(enlistedSection), Collections.EMPTY_SET);

        // When the student enlists in a new section
        student.enlist(sectionToBeEnlisted);

        // Then both sections should be found in the student
        Collection<Section> sections = student.getSections();
        assertAll(
                () -> assertTrue(sections.containsAll(List.of(enlistedSection, sectionToBeEnlisted))),
                () -> assertEquals(2, sections.size())
        );

    }

    @Test
    void enlist_section_enlisted_subject() {
        // Given a student and a section
        final int INITIAL_NUMBER_OF_STUDENTS = 5;
        Subject subject = new Subject("W", Collections.EMPTY_SET);
        Section enlistedSection = new Section("A",
                new Schedule(Days.MTH, Period.H0830),
                new Room("Z", 10),
                subject,
                INITIAL_NUMBER_OF_STUDENTS);
        Section sectionToBeEnlisted = new Section("B",
                new Schedule(Days.WS, Period.H1130),
                new Room("Y", 10),
                subject,
                INITIAL_NUMBER_OF_STUDENTS);
        Student student = new Student(1, Collections.EMPTY_SET, Collections.EMPTY_SET);

        // When the student enlists in the section with a subject they had already enlisted in
        student.enlist(enlistedSection);

        // Then an exception should be thrown on the student's enlistment
        assertThrows(SubjectConflictException.class, () -> student.enlist(sectionToBeEnlisted));
    }

    @Test
    void enlist_complete_prerequisites() {
        // Given a student with completed prerequisites to a subject
        final int INITIAL_NUMBER_OF_STUDENTS = 5;
        Subject prerequisite = new Subject("W", Collections.EMPTY_SET);
        Subject subject = new Subject("X", List.of(prerequisite));
        Section sectionToBeEnlisted = new Section("B",
                new Schedule(Days.WS, Period.H1130),
                new Room("Y", 10),
                subject,
                INITIAL_NUMBER_OF_STUDENTS);
        Student student = new Student(1, Collections.EMPTY_SET, List.of(prerequisite));

        // When the student enlists in the section with prerequisites they had already completed
        student.enlist(sectionToBeEnlisted);

        // Then there should be no exception
    }

    @Test
    void enlist_incomplete_prerequisites() {
        // Given a student with an incomplete prerequisite
        final int INITIAL_NUMBER_OF_STUDENTS = 5;
        Subject prerequisite = new Subject("W", Collections.EMPTY_SET);
        Subject subject1 = new Subject("X", List.of(prerequisite));
        Subject subject2 = new Subject("Z", Collections.EMPTY_SET);
        Section sectionToBeEnlisted = new Section("B",
                new Schedule(Days.WS, Period.H1130),
                new Room("Y", 10),
                subject1,
                INITIAL_NUMBER_OF_STUDENTS);
        Student student = new Student(1, Collections.EMPTY_SET, List.of(subject2));

        // When the student with an incomplete prerequisite enlists in the section
        // Then an exception should be thrown on the student's enlistment
        assertThrows(IllegalArgumentException.class, () -> student.enlist(sectionToBeEnlisted));
    }
}
