package com.group24h.enlistment;

import java.util.Objects;

import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.Validate.*;

class Section {
    private final String sectionId;
    private final Schedule schedule;
    private final Room room;
    private int enlistedStudents;

    Section(String sectionId, Schedule schedule, Room room) {
        notBlank(sectionId, "sectionId cannot be null, empty or whitespace");
        isTrue(isAlphanumeric(sectionId),
                "sectionId must be alphanumeric, was: " + sectionId);

        this.sectionId = sectionId;
        this.schedule = schedule;
        this.room = room;
        this.enlistedStudents = 0;
    }

    void checkForConflict(Section other) {
        if (this.schedule.equals(other.schedule)) {
            throw new ScheduleConflictException("schedule conflict between current section " +
                    this + " and new section " + other + " " +
                    "at schedule " + this.schedule);
        }
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

