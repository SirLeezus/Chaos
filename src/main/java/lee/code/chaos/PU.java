package lee.code.chaos;

import lee.code.chaos.database.CacheManager;
import lee.code.chaos.killstreaks.KillStreak;
import lee.code.chaos.kits.Kit;
import lee.code.chaos.lists.Lang;
import lee.code.chaos.maps.MapData;
import lee.code.core.util.bukkit.BukkitUtils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

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
        CacheManager cacheManager = Chaos.getPlugin().getCacheManager();
        Player target = Bukkit.getPlayer(uuid);
        if (target != null && target.isOnline()) {
            playRewardSound(target);
            if (cacheManager.isBoosterActive()) target.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COIN_REWARD_BOOSTER.getComponent(new String[] { BukkitUtils.parseValue(amount), String.valueOf(cacheManager.getBoosterMultiplier(cacheManager.getActiveBoosterID())) })));
            else target.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COIN_REWARD.getComponent(new String[] { BukkitUtils.parseValue(amount) })));
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

    public void setAttackDamage(ItemMeta meta, double amount) {
        AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", amount, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, modifier);
    }

    public Kit getPreviewItemKit(ItemStack item) {
        Chaos plugin = Chaos.getPlugin();
        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "key");
        String kit = dataContainer.get(key, PersistentDataType.STRING);
        if (kit != null) return plugin.getData().getKit(kit);
        else return null;
    }

    public KillStreak getPreviewItemKillStreak(ItemStack item) {
        Chaos plugin = Chaos.getPlugin();
        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "key");
        String kit = dataContainer.get(key, PersistentDataType.STRING);
        if (kit != null) return plugin.getData().getKillStreak(kit);
        else return null;
    }

    public void setPreviewItemKitMeta(ItemMeta itemMeta, String key) {
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        dataContainer.set(new NamespacedKey(Chaos.getPlugin(), "key"), PersistentDataType.STRING, key);
    }

    public boolean isSafeLocation(Location location) {
        MapData map = Chaos.getPlugin().getData().getActiveMap().getData();
        Location redSpawn = map.getRedSpawn();
        Location blueSpawn = map.getBlueSpawn();
        Location gameSpawn = map.getSpawn();
        int radius = 5;
        boolean red = location.distanceSquared(redSpawn) <= radius * radius;
        boolean blue = location.distanceSquared(blueSpawn) <= radius * radius;
        boolean spawn = location.distanceSquared(gameSpawn) <= radius * radius;
        return red || blue || spawn;
    }

    public void scheduleBoosterChecker() {
        Chaos plugin = Chaos.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (cacheManager.areBoosters()) {
                int id = cacheManager.getActiveBoosterID() != 0 ? cacheManager.getActiveBoosterID() : cacheManager.getNextBoosterQueueID();
                long time = cacheManager.getBoosterTime(id);
                int multiplier = cacheManager.getBoosterMultiplier(id);
                String name = cacheManager.getBoosterPlayerName(id);
                if (cacheManager.isBoosterActive()) {
                    if (time < 0) {
                        cacheManager.removeBooster(id);
                        Bukkit.getServer().sendMessage(Lang.ANNOUNCEMENT.getComponent(null).append(Lang.BROADCAST_BOOSTER_ENDED.getComponent(new String[] { String.valueOf(multiplier), name })));
                    }
                } else if (id != 0) {
                    cacheManager.setBoosterActive(id, true);
                    Bukkit.getServer().sendMessage(Lang.ANNOUNCEMENT.getComponent(null).append(Lang.BROADCAST_BOOSTER_STARTED.getComponent(new String[] { String.valueOf(multiplier), name })));
                }
            }
        }), 0L, 20L);
    }
}
