package okurun.radaroperator;

public class EnemyState {
    public final int enemyId;
    public final double x;
    public final double y;
    public final double heading;
    public final double velocity;
    public final double energy;
    public final int scandTurnNum;
    public final EnemyState previousState;

    public EnemyState(int enemyId) {
        this.enemyId = enemyId;
        this.x = 0;
        this.y = 0;
        this.heading = 0;
        this.velocity = 0;
        this.energy = 100;
        this.scandTurnNum = 0;
        this.previousState = null;
    }

    public EnemyState(int enemyId, double x, double y, double heading, double velocity, double energy, int scandTurnNum, EnemyState previousState) {
        this.enemyId = enemyId;
        this.x = x;
        this.y = y;
        this.heading = heading;
        this.velocity = velocity;
        this.energy = energy;
        this.scandTurnNum = scandTurnNum;
        this.previousState = previousState;
    }

    public double getTurnDegree() {
        if (previousState == null) return 0.0;
        final double diffHeading = heading - previousState.heading;
        final int diffTurnNum = scandTurnNum - previousState.scandTurnNum;
        if (diffHeading == 0 || diffTurnNum == 0) return 0.0;
        return diffHeading / diffTurnNum;
    }

    @Override
    public String toString() {
        return "EnemyState{" +
                "enemyId=" + enemyId +
                ", x=" + x +
                ", y=" + y +
                ", heading=" + heading +
                ", velocity=" + velocity +
                ", energy=" + energy +
                ", scandTurnNum=" + scandTurnNum +
                '}';
    }
}