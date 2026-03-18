package hr.java.clientcommunication.entity;

import java.time.LocalDateTime;

public class TimedGroupedCount<T> extends Entity {

    private T groupBy;
    private long count;
    private LocalDateTime timestamp;

    public TimedGroupedCount(Long id, T groupBy, long count, LocalDateTime timestamp) {
        super(id);
        this.groupBy = groupBy;
        this.count = count;
        this.timestamp = timestamp;
    }

    public TimedGroupedCount(T groupBy, long count, LocalDateTime timestamp) {
        this.groupBy = groupBy;
        this.count = count;
        this.timestamp = timestamp;
    }

    public T getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(T groupBy) {
        this.groupBy = groupBy;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "TimedGroupedCount{" +
                "groupBy=" + groupBy +
                ", count=" + count +
                ", timestamp=" + timestamp +
                '}';
    }
}
