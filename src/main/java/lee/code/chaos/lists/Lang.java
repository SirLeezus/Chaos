package lee.code.chaos.lists;

import lee.code.core.util.bukkit.BukkitUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.Component;

@AllArgsConstructor
public enum Lang {
    SIDEBAR_TITLE("&6&lChaos Score"),
    MESSAGE_SENT("&6[{0}You &6-> &e{1}{2}&6] "),
    MESSAGE_RECEIVED("&6[&e{0}{1} &6-> {2}You&6] "),
    MOTD_SPLITTER("&2▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"),
    MOTD_TITLE("      &a-----===[ &#FFAA00&l&nJourney Chaos&r &a]===-----"),
    STORE("&a&lStore&7: &ehttps://journey.buycraft.net"),
    DIRECT_IP("&6&lDirect IP&7: &ejourney-chaos.mc.gg"),
    STORE_LINK("https://journey.buycraft.net"),
    DISCORD("&9&lDiscord&7: &ehttps://discord.gg/UP65uzBP7m"),
    DISCORD_LINK("https://discord.gg/UP65uzBP7m"),
    PREFIX("&6[&e!&6] &r"),
    USAGE("&6&lUsage: &e{0}"),
    DEATH_PREFIX("&cDeath &6➔ &r"),
    ANNOUNCEMENT("&e&lAnnouncement &6➔ &r"),
    BLUE_COLOR("&9"),
    SPECTATOR_COLOR("&e"),
    SERVER_UUID("ffffffff-ffff-ffff-ffff-ffffffffffff"),
    KILL_STREAK_ACTIVATED_PLAYER("&5Your kill streak &d&l{0} &5has been activated!"),
    KILL_STREAK_ACTIVATED_SERVER("&5The player {0}{1} &5activated the kill streak &d&l{2}&5!"),
    KILL_STREAK_ACTIVATED_HOVER("{0}"),
    KILL_STREAK_NUKE_WARNING("&e&lNuke Inbound &6☢ &7({0}&7)"),
    KILL_STREAK_HORSE_NAME("{0}{1}'s Horse"),
    PLAYER_LEFT_TEAM("&7The player {0}{1} &7left the {2} Team&7."),
    RED_COLOR("&c"),
    BLUE_TEAM("&9&lBlue"),
    RED_TEAM("&c&lRed"),
    SPECTATOR_TEAM("&e&lSpec"),
    TEAM_MESSAGE("&6[{0} Chat&6] {1}{2}&7: {3}{4}"),
    RESTART_MESSAGE("&aThe server is restarting, Journey Chaos will be back online soon!"),
    KILL_STREAK_MESSAGE("&5The player {0}{1} &5is on a &d&l{2} &5kill streak!"),
    MENU_KIT_SELECTED("&aYou successfully selected the kit &5&l{0}&a!"),
    MENU_KILL_STREAK_SELECTED("&aYou successfully selected the kill streak &d&l{0}&a!"),
    MENU_KILL_STREAK_UNSELECTED("&aYou successfully unselected the kill streak &d&l{0}&a!"),
    MENU_KIT_NAME("&5&l{0}"),
    MENU_KILL_STREAK_NAME("&d&l{0}"),
    MENU_KILL_STREAK_LORE_LOCKED("{0}\n \n&eRequired Kill Streak: &a{1}\n&c&lCost&7: &#f5a802{2} ⛃\n \n&e&l> &2&lBuy &aleft-click!"),
    MENU_KILL_STREAK_LORE_UNLOCKED("{0}\n \n&eRequired Kill Streak: &a{1}\n \n&e&l> &2&lSelect &aleft-click!"),
    MENU_KILL_STREAK_LORE_SELECTED("{0}\n \n&eRequired Kill Streak: &a{1}\n \n&e&l> &6&lSelected!\n&e&l> &2&lUnselect &aright-click!"),
    MENU_KIT_LORE_LOCKED(" \n&c&lCost&7: &#f5a802{0} ⛃\n \n&e&l> &2&lBuy &aleft-click!\n&e&l> &a&2&lPreview &aright-click!"),
    MENU_KIT_LORE_UNLOCKED(" \n&e&l> &2&lSelect &aleft-click!\n&e&l> &2&lPreview &aright-click!"),
    MENU_KIT_LORE_SELECTED(" \n&e&l> &6&lSelected!\n&e&l> &2&lPreview &aright-click!"),
    MENU_KIT_TITLE("&5&lKits &8Page {0}"),
    MENU_KILL_STREAK_TITLE("&d&lKill Streaks &8Page {0}"),
    MENU_KIT_PREVIEW_TITLE("&5&lKit &8(&5&l{0}&8)"),
    MENU_KILL_STREAK_PREVIEW_TITLE("&d&lKill Streak"),
    MENU_BUY_PREVIEW_LORE("&c&lCost&7: &#f5a802{0} ⛃"),
    MENU_BUY_KIT("&aYou successfully bought the kit &5&l{0} &aand now it is your selected kit."),
    MENU_KILL_STREAK("&aYou successfully bought the kill streak &d&l{0} &aand now is a selected kill streak."),
    MENU_JOINED_TEAM("&aThe player {0}{1} &ajoined the {2} Team&a!"),
    MENU_TEAMS_ITEM_LORE("&aPlayers&7: &e(&2{0}&7/&2{1}&e)"),
    MENU_JOIN_FILLED("&7The {0} Team &7is currently full."),
    MENU_ALREADY_ON_TEAM("&7You are already on a team."),
    MENU_TEAM_TITLE("&e&lSelect Team"),
    WAITING_WARNING("&eWaiting for more players to join a team!"),
    STARTING_TIMER_TITLE("&6{0}"),
    STARTING_TIMER_TITLE_FLIGHT("&cFight!!!"),
    STARTING_TIMER_TITLE_PREPARE("&cPrepare For Battle!"),
    GAME_WINNER_SLITTER("&6▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"),
    GAME_WINNER("              {0} Team Won"),
    GAME_WINNER_TOP_WOOL_BROKEN("&e&lMost Wool Broken&7: {0}{1} &e| &a{3}"),
    GAME_WINNER_TOP_KILLS("&e&lMost Kills&7: {0}{1} &e| &a{3}"),
    GAME_END_DRAW("               &e&lDraw"),
    CYCLING_MAP("&eCycling map in &6{0}s&e!"),
    CYCLING_MAP_STARTED("&eCycling map process has started!"),
    BROKE_WOOL("&aThe player {0}{1} &abroke a {2} Wool &ablock! {3}({4}/{5})"),
    TABLIST_HEADER("&#228B22▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬\n&#FFAA00&lJourney Chaos\n&#228B22▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"),
    TABLIST_FOOTER("\n" + RED_TEAM.getString(null) + "&7: &c{0} " + BLUE_TEAM.getString(null) + "&7: &9{1} " + SPECTATOR_TEAM.getString(null) + "&7: &e{2}\n"),
    PLAYER_KILLED_TARGET("&eThe player {0}{1} &ekilled {2}{3}&e."),
    PLAYER_DIED("&eThe player {0}{1} &edied."),
    ERROR_BUY_KIT_BALANCE("&cYou only have &#f5a802{0} ⛃ &cand you need &#f5a802{1} ⛃ &cto buy the kit &5&l{2}&c."),
    ERROR_BUY_KILL_STREAK_BALANCE("&cYou only have &#f5a802{0} ⛃ &cand you need &#f5a802{1} ⛃ &cto buy the kill streak &d&l{2}&c."),
    ERROR_COMMAND_MESSAGE_TO_SELF("&cYou can't message yourself."),
    ERROR_PLAYER_NOT_ONLINE("&cThe player &6{0} &cis not online."),
    ERROR_NOT_CONSOLE_COMMAND("&cThis command does not work in console."),
    ERROR_COMMAND_REPLY_NO_PLAYER("&cNo player was found for a reply."),
    ERROR_PREVIOUS_PAGE("&7You are already on the first page."),
    ERROR_NEXT_PAGE("&7You are on the last page."),
    ERROR_PLAYER_NOT_FOUND("&cThe player &6{0} &ccould not be found."),
    ERROR_COMMAND_BOOSTER("&cThere are currently no active boosters."),
    LEVEL_UP("&e&lYOU LEVELED UP TO LEVEL &a&l{0}&e&l!"),
    COIN_REWARD("&#f5a802+{0} ⛃"),
    COIN_REWARD_BOOSTER(COIN_REWARD.getString(null) + " &3(&b&lBooster Active&7: &bx{1}&3)"),
    BROADCAST_BOOSTER_ENDED("&aThe &#f5a802&lCoins x{0} Booster &ahas ended. You can thank the player &6{1} &afor activating it!"),
    BROADCAST_BOOSTER_STARTED("&aA &#f5a802&lCoins x{0} Booster &ahas started thanks to the player &6{1}&a!"),
    COMMAND_STATS_TITLE("&a---------------- &e[ &2&lStats &e] &a----------------"),
    COMMAND_STATS_SPLITTER("&a-----------------------------------------"),
    COMMAND_STATS_PLAYER_LINE("&e&lPlayer&7: &6{0}"),
    COMMAND_STATS_LEVEL_LINE("&e&lLevel&7: &a{0}"),
    COMMAND_STATS_COINS_LINE("&e&lCoins&7: &#f5a802{0} ⛃"),
    COMMAND_STATS_KILLS_LINE("&e&lKills&7: &2{0}"),
    COMMAND_STATS_LONGEST_KILL_STREAK_LINE("&e&lLongest Kill Streak&7: &2{0}"),
    COMMAND_STATS_DEATHS_LINE("&e&lDeaths&7: &2{0}"),
    COMMAND_STATS_WOOL_BROKEN_LINE("&e&lWool Broken&7: &2{0}"),
    COMMAND_STATS_GAMES_LINE("&e&lGames Played&7: &2{0}"),
    COMMAND_BOOSTER_ADD_SUCCESSFUL("&a{0}'s Drops x{1} Booster has been queued."),
    COMMAND_BOOSTER_ID_HOVER("&9&lID&7: &e{0}"),
    COMMAND_RANKSET_SUCCESSFUL("&aThe rank &6{0} &awas successfully set for the player &6{1}&a!"),
    COMMAND_REMOVERANK_SUCCESSFUL("&aYou successfully removed &6{0}'s rank!"),
    COMMAND_BOOSTER_TITLE("&a------------------ &e[ &2&lBoosters &e] &a------------------"),
    COMMAND_BOOSTER_ACTIVE("&b&lActive&7: &#f5a802Coins x{0} Booster &b| &6{1} &b| {2}"),
    COMMAND_BOOSTER_QUEUE("&3{0}&b. &#f5a802Coins x{1} Booster &b| &6{2}"),
    COMMAND_BOOSTER_SPLITTER("&a-------------------------------------------------"),
    COMMAND_BOOSTER_REMOVE_SUCCESSFUL("&aThe booster id &9{0} &awas successfully removed."),
    COMMAND_COINS_SUCCESSFUL("&aThe player &6{0} &ahas &#f5a802{1} ⛃&a!"),
    SCOREBOARD_LINE_1("&r&2---------------"),
    SCOREBOARD_LINE_2("&9Blue Wool&8: &9({0}/{1})"),
    SCOREBOARD_LINE_3("&cRed Wool&8: &c({0}/{1})"),
    SCOREBOARD_LINE_4("&2---------------"),
    SCOREBOARD_LINE_5("&eDeaths&8: &a{0}"),
    SCOREBOARD_LINE_6("&eKill Streak&8: &a{0}"),
    SCOREBOARD_LINE_7("&eKills&8: &a{0}"),
    SCOREBOARD_LINE_8("&2---------------&r"),
    ;

    @Getter private final String string;

    public String getString(String[] variables) {
        String value = string;
        if (variables == null || variables.length == 0) return BukkitUtils.parseColorString(value);
        for (int i = 0; i < variables.length; i++) value = value.replace("{" + i + "}", variables[i]);
        return BukkitUtils.parseColorString(value);
    }

    public Component getComponent(String[] variables) {
        String value = string;
        if (variables == null || variables.length == 0) return BukkitUtils.parseColorComponent(value);
        for (int i = 0; i < variables.length; i++) value = value.replace("{" + i + "}", variables[i]);
        return BukkitUtils.parseColorComponent(value);
    }
}
