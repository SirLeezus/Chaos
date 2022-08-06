package lee.code.chaos.lists;

import lee.code.core.util.bukkit.BukkitUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;

@AllArgsConstructor
public enum Lang {
    SIDEBAR_TITLE("&#FFA33A&lChaos Score"),
    MESSAGE_SENT("&9[&eYou &9-> &e{0}&9] "),
    MESSAGE_RECEIVED("&9[&e{0} &9-> &eYou&9] "),
    PREFIX("&6[&e!&6] &r"),
    USAGE("&6&lUsage: &e{0}"),
    DEATH_PREFIX("&cDeath &6➔ &r"),
    BLUE_COLOR("&9"),
    SPECTATOR_COLOR("&e"),
    RED_COLOR("&c"),
    BLUE_TEAM("&9&lBlue"),
    RED_TEAM("&c&lRed"),
    MENU_KIT_SELECTED("&aYou successfully selected the kit &5&l{0}&a!"),
    MENU_KIT_NAME("&5&l{0}"),
    MENU_KIT_LORE_LOCKED(" \n&c&lCost&7: &#FFF300{0} ⛃\n \n&e&l> &2&lBuy &aleft-click!\n&e&l> &a&2&lPreview &aright-click!"),
    MENU_KIT_LORE_UNLOCKED(" \n&e&l> &2&lSelect &aleft-click!\n&e&l> &2&lPreview &aright-click!"),
    MENU_KIT_TITLE("&5&lKits &8Page {0}"),
    MENU_TEAM_PREVIEW_TITLE("&5&lKit Preview"),
    MENU_BUY_PREVIEW_LORE("&c&lCost&7: &#FFF300{0} ⛃"),
    MENU_BUY_KIT("&aYou successfully bought the kit &5&l{0} &aand now it is your selected kit."),
    MENU_JOINED_TEAM("&aYou successfully joined the {0} Team&a!"),
    MENU_JOIN_FILLED("&7The {0} Team &7is currently full."),
    MENU_ALREADY_ON_TEAM("&7You are already on a team."),
    MENU_TEAM_TITLE("&e&lSelect Team"),
    WAITING_WARNING("&eWaiting for more players to join a team!"),
    STARTING_TIMER_TITLE("&6{0}"),
    STARTING_TIMER_TITLE_FLIGHT("&cFight!!!"),
    STARTING_TIMER_TITLE_PREPARE("&cPrepare For Battle!"),
    GAME_WINNER("&aThe {0} Team &ahas won the match!!!!"),
    GAME_END_DRAW("&aThe match has ended in a draw!"),
    CYCLING_MAP("&eCycling map in &6{0}s&e!"),
    CYCLING_MAP_STARTED("&eCycling map process has started!"),
    BROKE_WOOL("&aThe player {0}{1} &abroke a {2} Wool &ablock! {3}({4}/{5})"),
    TABLIST_HEADER("&#228B22▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬\n&#FFA33A&lJourney Chaos\n&#228B22▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"),
    TABLIST_FOOTER("\n&#228B22&lOnline&7: &#4dc462{0}"),
    PLAYER_KILLED_TARGET("&eThe player {0}{1} &ekilled {2}{3}&e."),
    PLAYER_DIED("&eThe player {0}{1} &edied."),
    ERROR_BUY_KIT_BALANCE("&cYou only have &#FFF300{0} ⛃ &cand you need &#FFF300{1} ⛃ &cto buy the kit &5&l{2}&c."),
    ERROR_COMMAND_MESSAGE_TO_SELF("&cYou can't message yourself."),
    ERROR_PLAYER_NOT_ONLINE("&cThe player &6{0} &cis not online."),
    ERROR_NOT_CONSOLE_COMMAND("&cThis command does not work in console."),
    ERROR_COMMAND_REPLY_NO_PLAYER("&cNo player was found for a reply."),
    ERROR_PREVIOUS_PAGE("&7You are already on the first page."),
    ERROR_NEXT_PAGE("&7You are on the last page."),
    ERROR_PLAYER_NOT_FOUND("&cThe player &6{0} &ccould not be found."),
    LEVEL_UP("&e&lYOU LEVELED UP TO LEVEL &a&l{0}&e&l!"),
    COIN_REWARD("&#FFF300+{0} ⛃"),
    COMMAND_STATS_TITLE("&a---------------- &e[ &2&lStats &e] &a----------------"),
    COMMAND_STATS_SPLITTER("&a-----------------------------------------"),
    COMMAND_STATS_PLAYER_LINE("&e&lPlayer&7: &6{0}"),
    COMMAND_STATS_LEVEL_LINE("&e&lLevel&7: &a{0}"),
    COMMAND_STATS_COINS_LINE("&e&lCoins&7: &#FFF300{0} ⛃"),
    COMMAND_STATS_KILLS_LINE("&e&lKills&7: &2{0}"),
    COMMAND_STATS_LONGEST_KILL_STREAK_LINE("&e&lLongest Kill Streak&7: &2{0}"),
    COMMAND_STATS_DEATHS_LINE("&e&lDeaths&7: &2{0}"),
    COMMAND_STATS_WOOL_BROKEN_LINE("&e&lWool Broken&7: &2{0}"),
    COMMAND_STATS_GAMES_LINE("&e&lGames Played&7: &2{0}"),
    ;

    @Getter private final String string;

    public String getString(String[] variables) {
        String value = ChatColor.translateAlternateColorCodes('&', string);
        if (variables == null) return value;
        else if (variables.length == 0) return value;
        for (int i = 0; i < variables.length; i++) value = value.replace("{" + i + "}", variables[i]);
        return ChatColor.translateAlternateColorCodes('&', value);
    }

    public Component getComponent(String[] variables) {
        String value = string;
        if (variables == null || variables.length == 0) return BukkitUtils.parseColorComponent(value);
        for (int i = 0; i < variables.length; i++) value = value.replace("{" + i + "}", variables[i]);
        return BukkitUtils.parseColorComponent(value);
    }
}
