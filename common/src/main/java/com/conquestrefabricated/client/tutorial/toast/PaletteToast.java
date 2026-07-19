package com.conquestrefabricated.client.tutorial.toast;

//import com.conquestrefabricated.client.ModBinds;
//import com.conquestrefabricated.client.tutorial.Tutorials;
//import net.minecraft.client.gui.components.toasts.ToastComponent;
//import net.minecraft.client.gui.screens.inventory.InventoryScreen;
//
//public class PaletteToast extends AbstractToast {
//
//    private static final String line1 = "Press '%s' whilst hovering over";
//    private static final String line2 = "a block to see its variants!";
//
//    public PaletteToast() {
//        super(SUBTITLE, SUBTITLE);
//    }
//
//    @Override
//    public boolean shouldRender(ToastComponent gui) {
//        return gui.getMinecraft().screen instanceof InventoryScreen && !Tutorials.openPalette;
//    }
//
//    @Override
//    public Visibility getVisibility() {
//        if (Tutorials.openPalette) {
////            if (!section.getOrElse("block_palette", false)) {
////                section.set("block_palette", true);
////                section.save();
////            }
//            return Visibility.HIDE;
//        }
//        return Visibility.SHOW;
//    }
//
//    @Override
//    public String getLine1() {
//        return String.format(line1, ModBinds.getPaletteBind().getTranslatedKeyMessage().getString());
//    }
//
//    @Override
//    public String getLine2() {
//        return line2;
//    }
//}
