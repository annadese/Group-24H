package com.group24h.enlistment;

import java.time.LocalTime;
import java.util.Objects;

import static org.apache.commons.lang3.Validate.notNull;

public class Period {
    LocalTime start;
    LocalTime end;

    Period(LocalTime start, LocalTime end){
        notNull(start);
        notNull(end);
        checkIfValidTime(start,end);
        this.start = start;
        this.end= end;
    }

    void checkIfValidTime(LocalTime start, LocalTime end){
        //Check if 30 min increments
        if (!(start.getMinute() == 30 || start.getMinute() == 0)
                || !(end.getMinute() == 30 || end.getMinute() == 0) ) {
            throw new InvalidPeriodException("Time not increment of 30 in start time: " +
                    start.toString() + " and  end time: " + end.toString() );
        }

        LocalTime validStart = LocalTime.of(8,29);
        LocalTime validEnd = LocalTime.of(17,31);

        //Check if within 8:30 am - 5:30 pm
        if(!start.isAfter(validStart) || !start.isBefore(validEnd)
                || !end.isBefore(validEnd) || !end.isAfter(validStart)){
            throw new InvalidPeriodException("Time not within valid start: " +
                    start + " and  end:" + end );
        }


        //Check if end is on start
        if(end.compareTo(start) == 0){
            throw new InvalidPeriodException("End time is before start time at start: " +
                    start + " and  end:" + end );
        }

        //Check if end is after start
        if(end.isBefore(start)){
            throw new InvalidPeriodException("End time is the same as start time at start: " +
                    start + " and  end:" + end );
        }




    }

    @Override
    public String toString() {
        return "Start: " + start.toString() + " End: " + end.toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Period period = (Period) o;
        return Objects.equals(start, period.start) && Objects.equals(end, period.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }
}
