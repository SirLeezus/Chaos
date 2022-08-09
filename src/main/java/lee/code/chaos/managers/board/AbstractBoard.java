package lee.code.chaos.managers.board;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.AbstractStructure;
import com.comphenix.protocol.events.InternalStructure;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.common.base.Objects;
import lee.code.chaos.Chaos;
import lee.code.chaos.Data;
import lee.code.chaos.lists.Lang;
import lee.code.chaos.lists.Setting;
import lee.code.chaos.maps.MapData;
import lee.code.chaos.maps.ScoreData;
import lee.code.core.util.bukkit.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.UUID;

public class AbstractBoard {
    protected PacketContainer handle;
    protected AbstractStructure structure;
    protected UUID uuid;
    private String lastKillScore;
    private String lastDeathScore;
    private String lastKillSteakScore;
    private String lastRedScore;
    private String lastBlueScore;

    protected AbstractBoard(PacketContainer handle, PacketType type, UUID uuid) {
        if (handle == null) throw new IllegalArgumentException("Packet handle cannot be NULL.");
        if (!Objects.equal(handle.getType(), type)) throw new IllegalArgumentException(handle.getHandle() + " is not a packet of type " + type);

        this.handle = handle;
        this.structure = handle.getOptionalStructures().readSafely(0).get();
        this.uuid = uuid;
    }

    public void broadcastSidebarPacket() {
        Data data = Chaos.getPlugin().getData();
        for (Player player : Bukkit.getOnlinePlayers()) {
            data.getBoardPacket(player.getUniqueId()).sendSidebarPacket();
        }
    }

    public void sendSidebarPacket() {
        try {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                handle.getOptionalStructures().write(0, Optional.of((InternalStructure) structure));
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, getSideBarObjective());
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, getSidebarDisplaySlot());
                newSideBarPacket(player);
            }
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Cannot send packet.", e);
        }
    }

    public void sendPacket(Player receiver) {
        try {
            handle.getOptionalStructures().write(0, Optional.of((InternalStructure) structure));
            ProtocolLibrary.getProtocolManager().sendServerPacket(receiver, handle);
            updateHeath();
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Cannot send packet.", e);
        }
    }

    public void broadcastPacket() {
        handle.getOptionalStructures().write(0, Optional.of((InternalStructure) structure));
        ProtocolLibrary.getProtocolManager().broadcastServerPacket(handle);
        updateHeath();
    }

    public void updateHeath() {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null && player.isOnline()) {
            double health = player.getAbsorptionAmount() + player.getHealth();
            ProtocolLibrary.getProtocolManager().broadcastServerPacket(getHealthObjective());
            ProtocolLibrary.getProtocolManager().broadcastServerPacket(getHeathDisplaySlotPacket());
            ProtocolLibrary.getProtocolManager().broadcastServerPacket(newHeathPacket(player, (int) health));
        }
    }

    //sidebar
    private PacketContainer getSidebarDisplaySlot() {
        // http://wiki.vg/Protocol#Display_Scoreboard
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE);

        //sidebar
        packet.getIntegers().write(0, 1);

        // set objective name - The unique name for the scoreboard to be displayed.
        packet.getStrings().write(0, "score");

        return packet;
    }

    //sidebar
    private PacketContainer getSideBarObjective() {
        // http://wiki.vg/Protocol#Scoreboard_Objective
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_OBJECTIVE);
        // set name - limited to String(16)
        packet.getStrings().write(0, "score");

        // set mode - 0 to create the scoreboard. 1 to remove the scoreboard. 2 to update the display text.
        packet.getIntegers().write(0, 0);

        // set display name - Component
        packet.getChatComponents().write(0, WrappedChatComponent.fromJson(BukkitUtils.serializeColorComponentJson(Lang.SIDEBAR_TITLE.getString())));

        // set type - either "integer" or "hearts"
        packet.getEnumModifier(HealthDisplay.class, 2).write(0, HealthDisplay.INTEGER);

        return packet;
    }

    //sidebar
    private void newSideBarPacket(Player player) {
        Chaos plugin = Chaos.getPlugin();
        MapData mapData = plugin.getData().getActiveMap().getData();
        ScoreData scoreData = plugin.getData().getPlayerScoreData(player.getUniqueId());
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        try {
            String kills = "&eKills&8: &a" + scoreData.getKills();
            String killSteak = "&eKill Streak&8: &a" + scoreData.getKillStreak();
            String death = "&eDeaths&8: &a" + scoreData.getDeaths();
            String redScore = "&cRed Wool&8: &c(" + (Setting.WIN_SCORE.getValue() - mapData.getBlueScore()) + "/" + Setting.WIN_SCORE.getValue() + ")";
            String blueScore = "&9Blue Wool&8: &9(" + (Setting.WIN_SCORE.getValue() - mapData.getRedScore()) + "/" + Setting.WIN_SCORE.getValue() + ")";

            protocolManager.sendServerPacket(player, createSidebarPacket(8, "&2---------------", EnumWrappers.ScoreboardAction.CHANGE));
            protocolManager.sendServerPacket(player, createSidebarPacket(7, lastKillScore, EnumWrappers.ScoreboardAction.REMOVE));
            protocolManager.sendServerPacket(player, createSidebarPacket(7, kills, EnumWrappers.ScoreboardAction.CHANGE));
            protocolManager.sendServerPacket(player, createSidebarPacket(6, lastKillSteakScore, EnumWrappers.ScoreboardAction.REMOVE));
            protocolManager.sendServerPacket(player, createSidebarPacket(6, killSteak, EnumWrappers.ScoreboardAction.CHANGE));
            protocolManager.sendServerPacket(player, createSidebarPacket(5, lastDeathScore, EnumWrappers.ScoreboardAction.REMOVE));
            protocolManager.sendServerPacket(player, createSidebarPacket(5, death, EnumWrappers.ScoreboardAction.CHANGE));
            protocolManager.sendServerPacket(player, createSidebarPacket(4, "&r&2---------------", EnumWrappers.ScoreboardAction.CHANGE));
            protocolManager.sendServerPacket(player, createSidebarPacket(3, lastRedScore, EnumWrappers.ScoreboardAction.REMOVE));
            protocolManager.sendServerPacket(player, createSidebarPacket(3, redScore, EnumWrappers.ScoreboardAction.CHANGE));
            protocolManager.sendServerPacket(player, createSidebarPacket(2, lastBlueScore, EnumWrappers.ScoreboardAction.REMOVE));
            protocolManager.sendServerPacket(player, createSidebarPacket(2, blueScore, EnumWrappers.ScoreboardAction.CHANGE));
            protocolManager.sendServerPacket(player, createSidebarPacket(1, "&2---------------&r", EnumWrappers.ScoreboardAction.CHANGE));
            lastKillScore = kills;
            lastKillSteakScore = killSteak;
            lastDeathScore = death;
            lastRedScore = redScore;
            lastBlueScore = blueScore;
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Cannot send packet.", e);
        }
    }

    private PacketContainer createSidebarPacket(int score, String displayName, EnumWrappers.ScoreboardAction action) {
        // http://wiki.vg/Protocol#Update_Score
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_SCORE);

        // the name of the score
        packet.getStrings().write(0, BukkitUtils.parseColorString(displayName));

        // set the action - 0 to create/update an item. 1 to remove an item.
        packet.getScoreboardActions().write(0, action);

        // set objective name - The name of the objective the score belongs to
        packet.getStrings().write(1, "score");

        // set value of the score- The score to be displayed next to the entry. Only sent when Action does not equal 1.
        packet.getIntegers().write(0, score);

        return packet;
    }


    private PacketContainer getHeathDisplaySlotPacket() {
        // http://wiki.vg/Protocol#Display_Scoreboard
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE);

        //below name
        packet.getIntegers().write(0, 2);

        // set objective name - The unique name for the scoreboard to be displayed.
        packet.getStrings().write(0, "health");

        return packet;
    }

    private PacketContainer getHealthObjective() {
        // http://wiki.vg/Protocol#Scoreboard_Objective
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_OBJECTIVE);
        // set name - limited to String(16)
        packet.getStrings().write(0, "health");

        // set mode - 0 to create the scoreboard. 1 to remove the scoreboard. 2 to update the display text.
        packet.getIntegers().write(0, 0);

        // set display name - Component
        packet.getChatComponents().write(0, WrappedChatComponent.fromJson(BukkitUtils.serializeColorComponentJson("&4❤")));

        // set type - either "integer" or "hearts"
        packet.getEnumModifier(HealthDisplay.class, 2).write(0, BoardManager.HealthDisplay.HEARTS);

        return packet;
    }

    private PacketContainer newHeathPacket(Player player, int amount) {
        // http://wiki.vg/Protocol#Update_Score
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_SCORE);

        // the name of the score
        packet.getStrings().write(0, player.getName());

        // set the action - 0 to create/update an item. 1 to remove an item.
        packet.getScoreboardActions().write(0, EnumWrappers.ScoreboardAction.CHANGE);

        // set objective name - The name of the objective the score belongs to
        packet.getStrings().write(1, "health");

        // set value of the score- The score to be displayed next to the entry. Only sent when Action does not equal 1.
        packet.getIntegers().write(0, amount);

        return packet;
    }

    public enum HealthDisplay {
        INTEGER, HEARTS
    }
}