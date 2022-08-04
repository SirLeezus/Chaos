package lee.code.chaos.listeners;

import lee.code.core.util.bukkit.BukkitUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import java.util.List;

public class SignListener implements Listener {

    @EventHandler (priority = EventPriority.LOWEST)
    public void onSignChange(SignChangeEvent e) {
        List<Component> lines = e.lines();
        int number = 0;
        for (Component line : lines) {
            Component newLine = BukkitUtils.parseColorComponent(BukkitUtils.serializeComponent(line));
            e.line(number, newLine);
            number++;
        }
    }
}
