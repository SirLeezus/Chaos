package lee.code.chaos.lists;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Setting {
    WIN_SCORE(10),
    ;

    @Getter private final int value;
}
