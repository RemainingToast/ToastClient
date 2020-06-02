package toast.client.commands.cmds;

import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.container.ShulkerBoxContainer;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.ItemStack;
import toast.client.commands.Command;
import toast.client.utils.Logger;
import toast.client.utils.ShulkerBoxUtils;

import java.util.ArrayList;
import java.util.List;

public class Peek extends Command {
    public Peek() {
        super("peek", "View contents of the shulker you are holding", false, "peek");
    }

    public static List<Runnable> queue = new ArrayList<>();

    public static void nextQueue() {
        if (queue.isEmpty()) return;
        queue.get(0).run();
        queue.remove(0);
    }

    @Override
    public void run(String[] args) {
        if(mc.player == null || mc.player.inventory.getMainHandStack().isEmpty()){Logger.message("You need to be holding Shulker", Logger.ERR); return;}
        ItemStack item = mc.player.inventory.getMainHandStack();
        List<ItemStack> items = ShulkerBoxUtils.getItemsInShulker(item);
        BasicInventory inv = new BasicInventory(items.toArray(new ItemStack[27]));
        if(ShulkerBoxUtils.isShulkerBox(item)){
            System.out.println("Hand is shulker");
            queue.add(() -> {
                mc.openScreen(new ShulkerBoxScreen(
                        new ShulkerBoxContainer(420, mc.player.inventory, inv),
                        mc.player.inventory,
                        item.getName()));
            });
            /*try{
                mc.openScreen(new ShulkerBoxScreen(new ShulkerBoxContainer(420, mc.player.inventory, inv), mc.player.inventory, item.getName()));
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        }else {
            Logger.message("You need to be holding Shulker", Logger.ERR);
        }
    }
}
