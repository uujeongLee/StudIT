package studit.domain;

import java.io.Serializable;
import java.util.Objects;

public class TimeSlot implements Serializable {
    private final String day;
    private final String timeRange;

    public TimeSlot(String day, String timeRange) {
        this.day = day;
        this.timeRange = timeRange;
    }

    public String getDay() {
        return day;
    }

    public String getTimeRange() {
        return timeRange;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof TimeSlot other)) return false;
        return Objects.equals(day, other.day) &&
                Objects.equals(timeRange, other.timeRange);
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, timeRange);
    }

    @Override
    public String toString() {
        return day + " " + timeRange;
    }
}


