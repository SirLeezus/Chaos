package lee.code.chaos.maps;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class ScoreData {

    public ScoreData(UUID uuid) {
        this.player = uuid;
    }

    private UUID player;
    private int kills;
    private int killStreak;
    private int deaths;
    private boolean killSteakOneUsed;
    private boolean killSteakTwoUsed;
    private boolean killSteakThreeUsed;

}
