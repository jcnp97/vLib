package asia.virtualmc.vLib.utilities.enums;

public class EnumsLib {
    public enum DaylightType {
        DAY, DUSK, MORNING, NIGHT;
    }

    public enum FramePosition {
        POS1, POS2;
    }

    public enum UpdateType {
        ADD, SUBTRACT, SET;
    }

    public enum MessageType {
        RED, GREEN, YELLOW;
    }

    public enum BossBarColor {
        BLUE, GREEN, PINK, PURPLE, RED, WHITE, YELLOW;
    }

    public enum TimeUnits {
        HOURLY(3600),
        DAILY(86400),
        WEEKLY(604800),
        MONTHLY(2592000);
        private final long seconds;
        TimeUnits(long seconds) {
            this.seconds = seconds;
        }
        public long getSeconds() {
            return seconds;
        }
    }

    public enum RayTraceType {
        ENTITY,
        BLOCK
    }

    public enum Seasons {
        SPRING,
        SUMMER,
        FALL,
        WINTER,
        DISABLED
    }

    public enum Skills {
        FARMING,
        FISHING,
        MINING,
        ARCHAEOLOGY,
        COOKING,
        INVENTION
    }
}
