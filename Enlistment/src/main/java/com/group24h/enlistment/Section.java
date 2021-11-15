package com.group24h.enlistment;

import java.util.Collection;
import java.util.Objects;

import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.Validate.*;

class Section {
    private final String sectionId;
    private final Schedule schedule;
    private final Room room;
    private int enlistedStudents;
    private final Subject subject;

    Section(String sectionId, Schedule schedule, Room room, Subject subject) {
        notBlank(sectionId, "sectionId cannot be null, empty or whitespace");
        isTrue(isAlphanumeric(sectionId),
                "sectionId must be alphanumeric, was: " + sectionId);

        this.sectionId = sectionId;
        this.schedule = schedule;
        this.room = room;
        this.enlistedStudents = 0;
        this.subject = subject;
    }

    void checkForConflict(Section other) {
        if (this.schedule.equals(other.schedule)) {
            throw new ScheduleConflictException("schedule conflict between current section " +
                    this + " and new section " + other + " " +
                    "at schedule " + this.schedule);
        }
        /*if (this.subject.equals(other.subject)) {
            throw new SubjectConflictException("duplicate subjects between current section " +
                    this.subject + " and new section " + other + " " +
                    "at subject " + other.subject);
        }*/
    }

    void checkCapacity() {
        isTrue(enlistedStudents < room.getCapacity(),"capacity limit reached for sectionId " + this.sectionId);
    }

    void addEnlistedStudent() {
        this.enlistedStudents += 1;
    }

    void removeEnlistedStudent() {
        this.enlistedStudents -= 1;
    }

    public Subject getSubject() {
        return subject;
    }

    @Override
    public String toString() {
        return sectionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(sectionId, section.sectionId) && Objects.equals(schedule, section.schedule);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sectionId, schedule);
    }
}

