package lee.code.chaos.managers.board;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import lee.code.chaos.managers.CollisionRule;
import org.bukkit.ChatColor;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class BoardManager extends AbstractBoard {

    private static final PacketType SCOREBOARD_TEAM = PacketType.Play.Server.SCOREBOARD_TEAM;

    public BoardManager(UUID uuid) {
        super(new PacketContainer(SCOREBOARD_TEAM), SCOREBOARD_TEAM, uuid);
        handle.getModifier().writeDefaults();
    }

    public void setTeamName(String value) {
        handle.getStrings().write(0, value);
    }

    public void setDisplayName(WrappedChatComponent value) {
        structure.getChatComponents().write(0, value);
    }

    public void setPrefix(WrappedChatComponent value) {
        structure.getChatComponents().write(1, value);
    }

    public void setSuffix(WrappedChatComponent value) {
        structure.getChatComponents().write(2, value);
    }

    public void setNameTagVisibility(String value) {
        handle.getStrings().write(1, value);
    }

    public void setColor(ChatColor value) {
        structure.getEnumModifier(ChatColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).write(0, value);
    }

    public String getCollisionRule() {
        return handle.getStrings().read(2);
    }

    public void setCollisionRule(CollisionRule rule) {
        structure.getStrings().write(1, rule.getSettings());
    }

    public void setPlayers(List<String> value) {
        handle.getSpecificModifier(Collection.class).write(0, value);
    }

}