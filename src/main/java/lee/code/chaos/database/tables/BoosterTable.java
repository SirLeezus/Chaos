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
@DatabaseTable(tableName = "boosters")
public class BoosterTable {

    @DatabaseField(id = true, canBeNull = false)
    private int id;

    @DatabaseField(columnName = "player", canBeNull = false)
    private UUID player;

    @DatabaseField(columnName = "multiplier", canBeNull = false)
    private int multiplier;

    @DatabaseField(columnName = "time", canBeNull = false)
    private long time;

    @DatabaseField(columnName = "active", canBeNull = false)
    private boolean active;

    @DatabaseField(columnName = "duration", canBeNull = false)
    private long duration;

    public BoosterTable(int id, UUID player, int multiplier, long time, boolean active, long duration) {
        this.id = id;
        this.player = player;
        this.multiplier = multiplier;
        this.time = time;
        this.active = active;
        this.duration = duration;
    }
}