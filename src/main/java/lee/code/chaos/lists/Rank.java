package lee.code.chaos.lists;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Rank {
    MOD("&6[&5&lMod&6]", 0),
    ADMIN("&6[&a&lAdmin&6]", 0),
    OWNER("&6[&#F40000&lOwner&6]", 0),

    VIP("&6[&#FCFF2D&lVIP&6]", 1),
    MVP("&6[&#00D8F6&lMVP&6]", 2),
    ELITE("&6[&#33cc33&lElite&6]", 3),
    ;

    @Getter private final String prefix;
    @Getter private final int priority;
}
