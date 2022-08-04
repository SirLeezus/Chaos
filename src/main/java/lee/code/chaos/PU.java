package lee.code.chaos;

import lee.code.chaos.lists.Lang;
import lee.code.core.util.bukkit.BukkitUtils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PU {

    public double calcXpForLevel(int level) {
        double baseXP = 500;
        double exponent = 1.04f;
        return baseXP + (baseXP * Math.pow(level, exponent));
    }

    public double calculateFullTargetXp(int level) {
        double requiredXP = 0;
        for (int i = 0; i <= level; i++) {
            requiredXP += calcXpForLevel(i);
        }
        return requiredXP;
    }

    public int calculateLevel(double xp) {
        int level = 0;
        double maxXp = calcXpForLevel(0);
        do {
            maxXp += calcXpForLevel(++level);
        } while (maxXp < xp);
        return level;
    }

    public void sendLevelUp(UUID uuid, int level) {
        Player target = Bukkit.getPlayer(uuid);
        if (target != null && target.isOnline()) {
            Chaos.getPlugin().getGameManager().updateDisplayName(target);
            playLevelUpSound(target);
            target.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.LEVEL_UP.getComponent(new String[] { BukkitUtils.parseValue(level) })));
        }
    }

    public void sendCoinReward(UUID uuid, long amount) {
        Player target = Bukkit.getPlayer(uuid);
        if (target != null && target.isOnline()) {
            playRewardSound(target);
            target.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COIN_REWARD.getComponent(new String[] { BukkitUtils.parseValue(amount) })));
        }
    }

    private void playRewardSound(Player player) {
        Sound sound = Sound.sound(Key.key("entity.experience_orb.pickup"), Sound.Source.PLAYER, 1f,2);
        player.playSound(sound, Sound.Emitter.self());
    }

    private void playLevelUpSound(Player player) {
        Sound sound = Sound.sound(Key.key("entity.evoker.prepare_summon"), Sound.Source.PLAYER, 1f,1);
        player.playSound(sound, Sound.Emitter.self());
    }
}
