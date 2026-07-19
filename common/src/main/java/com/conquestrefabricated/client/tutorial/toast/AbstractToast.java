package com.conquestrefabricated.client.tutorial.toast;

//import com.mojang.blaze3d.systems.RenderSystem;
//import net.minecraft.client.gui.GuiGraphics;
//import net.minecraft.client.gui.components.toasts.Toast;
//import net.minecraft.client.gui.components.toasts.ToastComponent;
//import net.minecraft.resources.Identifier;
//
//
//public abstract class AbstractToast implements Toast {
//
//    protected static final int TITLE = -11534256;
//    protected static final int SUBTITLE = -16777216;
//
//    private final int line1Color;
//    private final int line2Color;
//
//    public AbstractToast(int line1Color, int line2Color) {
//        this.line1Color = line1Color;
//        this.line2Color = line2Color;
//    }
//
//    @Override
//    public Visibility render(GuiGraphics drawContext, ToastComponent toastGui, long delta) {
//        if (shouldRender(toastGui)) {
//            /*TODO SHOULD J BE ZERO OR TEXTURE*/
//            RenderSystem.setShaderTexture(0, 0);
//            //used to be color3f?
//            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
//            drawContext.blit(Identifier.fromNamespaceAndPath("conquest", "toast"), 0, 0, 0, 96, 160, 32);
//
//            if (getLine2().isEmpty()) {
//                drawContext.drawString(toastGui.getMinecraft().font, getLine1(), (int) 5.0F, (int) 12.0F, line1Color, true);
//            } else {
//                drawContext.drawString(toastGui.getMinecraft().font, getLine1(), (int) 5.0F, (int) 7.0F, line1Color, true);
//                drawContext.drawString(toastGui.getMinecraft().font, getLine2(), (int) 5.0F, (int) 18.0F, line2Color, true);
//            }
//        }
//        return getVisibility();
//    }
//
//    public abstract String getLine1();
//
//    public abstract String getLine2();
//
//    public abstract boolean shouldRender(ToastComponent gui);
//
//    public abstract Visibility getVisibility();
//}
