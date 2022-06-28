package cs2030.simulator;

class Statistic {
    private final double totalWait;
    private final int totalServe;
    private final int totalLeave;

    Statistic() {
        this.totalWait = 0.0;
        this.totalServe = 0;
        this.totalLeave = 0;
    }

    private Statistic(double totalWait, int totalServe, int totalLeave) {
        this.totalWait = totalWait;
        this.totalServe = totalServe;
        this.totalLeave = totalLeave;
    }

    Statistic addServe() {
        return new Statistic(this.totalWait, this.totalServe + 1, this.totalLeave);
    }

    Statistic addLeave() {
        return new Statistic(this.totalWait, this.totalServe, this.totalLeave + 1);
    }

    Statistic addWait(Double waitTime) {
        return new Statistic(this.totalWait + waitTime, this.totalServe, this.totalLeave);
    }

    int getTotalServed() {
        return this.totalServe;
    }

    int getTotalLeave() {
        return this.totalLeave;
    }

    double getTotalWait() {
        return this.totalWait;
    }

    @Override
    public String toString() {
        double averageWaitTime = this.totalWait / this.totalServe;
        return String.format("[%.3f %d %d]", averageWaitTime, totalServe, totalLeave);
    }
}
