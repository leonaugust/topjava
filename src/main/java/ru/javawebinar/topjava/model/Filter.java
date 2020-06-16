package ru.javawebinar.topjava.model;


import java.time.LocalDate;
import java.time.LocalTime;

public class Filter {
    private final LocalDate startDate;
    private final LocalTime startTime;
    private final LocalDate endDate;
    private final LocalTime endTime;

    public Filter(LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime) {
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }
}
