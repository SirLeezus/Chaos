package lee.code.chaos.database.tables;

import lee.code.core.ormlite.field.DatabaseField;
import lee.code.core.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@DatabaseTable(tableName = "player")
public class PlayerTable {

    @DatabaseField(id = true, canBeNull = false)
    private UUID player;

    @DatabaseField(columnName = "coins", canBeNull = false)
    private long coins;

    @DatabaseField(columnName = "level", canBeNull = false)
    private double level;

    @DatabaseField(columnName = "kills", canBeNull = false)
    private int kills;

    @DatabaseField(columnName = "deaths", canBeNull = false)
    private int deaths;

    @DatabaseField(columnName = "longest_kill_streak", canBeNull = false)
    private int longestKillStreak;

    @DatabaseField(columnName = "wool_broken", canBeNull = false)
    private int woolBroken;

    @DatabaseField(columnName = "games", canBeNull = false)
    private int games;

    @DatabaseField(columnName = "perms", canBeNull = false)
    private String perms;

    @DatabaseField(columnName = "rank", canBeNull = false)
    private String rank;

    @DatabaseField(columnName = "kit", canBeNull = false)
    private String kit;

    @DatabaseField(columnName = "selected_kill_streaks", canBeNull = false)
    private String selectedKillStreaks;

    public PlayerTable(UUID player) {
        this.player = player;
        this.coins = 0;
        this.level = 1000;
        this.kills = 0;
        this.deaths = 0;
        this.longestKillStreak = 0;
        this.woolBroken = 0;
        this.games = 0;
        this.perms = "0";
        this.rank = "0";
        this.kit = "default";
        this.selectedKillStreaks = "0";
    }
}
