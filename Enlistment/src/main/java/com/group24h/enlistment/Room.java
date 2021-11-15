package com.group24h.enlistment;

import java.util.Objects;

import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.Validate.*;

class Room {

    private final String roomName;
    private final int capacity;

    Room(String roomName, int capacity) {
        notBlank(roomName, "roomName cannot be blank, empty or whitespace");
        notNull(capacity);
        isTrue(isAlphanumeric(roomName),
                "roomName must be alphanumeric, was: " + roomName);
        isTrue(capacity > 0, "capacity must be greater than zero, was: " + capacity);
        this.roomName = roomName;
        this.capacity = capacity;
    }

    void checkIfAtOrOverCapacity(int occupancy) {
        if (occupancy >= capacity) {
            throw new CapacityException("at or over capacity of " + capacity + "  at occupancy of " + occupancy);
        }
    }

    @Override
    public String toString() {
        return roomName;
    }

//    int getCapacity() {
//        return capacity;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return capacity == room.capacity && Objects.equals(roomName, room.roomName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomName, capacity);
    }
}
