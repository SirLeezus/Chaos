package lee.code.chaos.lists;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Rank {
    MOD("&d[&5&lMod&d]"),
    ADMIN("&2[&a&lAdmin&2]"),
    OWNER("&6[&#F40000&lOwner&6]"),
    ;

    @Getter private final String prefix;
}
