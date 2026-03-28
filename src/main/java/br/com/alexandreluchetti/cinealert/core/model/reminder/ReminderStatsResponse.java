package br.com.alexandreluchetti.cinealert.core.model.reminder;

public class ReminderStatsResponse {

    private long total;
    private long pending;
    private long sent;
    private long cancelled;

    public ReminderStatsResponse(long total, long pending, long sent, long cancelled) {
        this.total = total;
        this.pending = pending;
        this.sent = sent;
        this.cancelled = cancelled;
    }

    public long getTotal() {
        return total;
    }

    public long getPending() {
        return pending;
    }

    public long getSent() {
        return sent;
    }

    public long getCancelled() {
        return cancelled;
    }
}
