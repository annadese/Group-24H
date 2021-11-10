package com.group24h.enlistment;

public class ScheduleConflictException extends RuntimeException {
    ScheduleConflictException(String msg) {
        super(msg);
    }
}
