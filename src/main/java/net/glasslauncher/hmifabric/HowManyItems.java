package net.glasslauncher.hmifabric;

import net.fabricmc.api.ModInitializer;
import net.glasslauncher.hmifabric.tabs.Tab;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ScreenBase;
import net.minecraft.client.gui.screen.container.ContainerBase;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.ScreenScaler;
import net.minecraft.item.ItemInstance;
import net.modificationstation.stationloader.api.client.event.option.KeyBindingRegister;
import net.modificationstation.stationloader.api.common.mod.StationMod;
import org.lwjgl.input.Mouse;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class HowManyItems implements ModInitializer, StationMod, KeyBindingRegister {

    public static Logger logger = Logger.getLogger(HowManyItems.class.getName());

    private static Method fill;

    @Override
    public void preInit() {
    }

    @Override
    public void registerKeyBindings(List<KeyBinding> list) {
        list.add(Config.toggleOverlay);
    }

    /**
     * @deprecated Use addTab()
     */
    public static void addModTab(Tab tab) {
        addTab(tab);
    }

    //Use this if you are a making a mod that adds a tab
    public static void addTab(Tab tab) {
        if(tab != null) {
            modTabs.add(tab);
        }
    }

    public static void addGuiToBlock(Class<? extends ContainerBase> gui, ItemInstance item) {
        TabUtils.putItemGui(gui, item);
    }

    public static void addWorkBenchGui(Class<? extends ContainerBase> gui) {
        TabUtils.addWorkBenchGui(gui);
    }

    public static void addEquivalentWorkbench(ItemInstance item) {
        TabUtils.addEquivalentWorkbench(item);
    }

    public static void addEquivalentFurnace(ItemInstance item) {
        TabUtils.addEquivalentFurnace(item);
    }

    public GuiOverlay overlay;
    public static HowManyItems thisMod;

    public static void onSettingChanged() {
        if(thisMod.overlay != null) thisMod.overlay.init();
        Config.writeConfig();
    }

    public boolean OnTickInGUI(Minecraft mc, ScreenBase guiscreen) {
        if(guiscreen instanceof ContainerBase) {
            ContainerBase screen = (ContainerBase)guiscreen;
            if(Config.overlayEnabled) {
                if(GuiOverlay.screen != screen || overlay == null || screen.width != overlay.width || screen.height != overlay.height
                        || screen.width != overlay.xSize || screen.height != overlay.ySize) {
                    overlay = new GuiOverlay(screen);
                }
                overlay.onTick();
            }
            Utils.drawStoredToolTip();
            if(Utils.isKeyDown(Config.pushRecipe) || Utils.isKeyDown(Config.pushUses)) {
                if(!keyHeldLastTick) {
                    boolean getUses = Utils.isKeyDown(Config.pushUses);
                    if (guiscreen instanceof ContainerBase) {
                        ItemInstance newFilter = null;

                        ScreenScaler scaledresolution = new ScreenScaler(mc.options, mc.actualWidth, mc.actualHeight);
                        int i = scaledresolution.getScaledWidth();
                        int j = scaledresolution.getScaledHeight();
                        int posX = (Mouse.getEventX() * i) / mc.actualWidth;
                        int posY = j - (Mouse.getEventY() * j) / mc.actualHeight - 1;
                        newFilter = Utils.hoveredItem((ContainerBase)guiscreen, posX, posY);
                        if (newFilter == null) {
                            newFilter = GuiOverlay.hoverItem;
                        }
                        if(newFilter == null) {
                            if(guiscreen instanceof GuiRecipeViewer)
                                newFilter = ((GuiRecipeViewer)guiscreen).getHoverItem();
                        }
                        if(newFilter != null) {
                            pushRecipe(guiscreen, newFilter, getUses);
                        }
                        else {
                            if(Config.overlayEnabled && guiscreen == GuiOverlay.screen && !GuiOverlay.searchBoxFocused() && Config.fastSearch) {
                                GuiOverlay.focusSearchBox();
                            }
                        }
                    }
                }
            }
            else if(Utils.isKeyDown(Config.prevRecipe)) {
                if(!keyHeldLastTick) {
                    if ((guiscreen instanceof GuiRecipeViewer || guiscreen instanceof GuiOverlay) && !GuiOverlay.searchBoxFocused()) {
                        if(guiscreen instanceof GuiOverlay && GuiOverlay.screen instanceof GuiRecipeViewer) guiscreen = GuiOverlay.screen;
                        if(guiscreen instanceof GuiRecipeViewer) ((GuiRecipeViewer) guiscreen).pop();
                    }
                    else {
                        if(Config.overlayEnabled && guiscreen == GuiOverlay.screen && !GuiOverlay.searchBoxFocused() && Config.fastSearch)
                            if(!GuiOverlay.emptySearchBox()) GuiOverlay.focusSearchBox();
                    }
                }
            }
            else if(Config.clearSearchBox.key == Config.focusSearchBox.key
                    && Utils.isKeyDown(Config.clearSearchBox)) {

                if (guiscreen instanceof ContainerBase
                        || guiscreen instanceof GuiOverlay) {
                    if(System.currentTimeMillis() > focusCooldown) {
                        focusCooldown = System.currentTimeMillis() + 800L;
                        if(!GuiOverlay.searchBoxFocused())
                            GuiOverlay.clearSearchBox();
                        GuiOverlay.focusSearchBox();
                    }
                }
            }
            else if(Utils.isKeyDown(Config.clearSearchBox)) {
                if (guiscreen instanceof ContainerBase
                        || guiscreen instanceof GuiOverlay) {
                    GuiOverlay.clearSearchBox();
                }
            }
            else if(Utils.isKeyDown(Config.focusSearchBox)) {
                if (guiscreen instanceof ContainerBase
                        || guiscreen instanceof GuiOverlay) {
                    if(System.currentTimeMillis() > focusCooldown) {
                        focusCooldown = System.currentTimeMillis() + 800L;
                        GuiOverlay.focusSearchBox();
                    }
                }
            }
            else if(Utils.isKeyDown(Config.allRecipes)) {
                if (guiscreen instanceof GuiOverlay) {
                    guiscreen = GuiOverlay.screen;
                }
                pushRecipe(guiscreen, null, false);
            }
            else {
                keyHeldLastTick = false;
            }
            if(Utils.isKeyDown(Config.pushRecipe) || Utils.isKeyDown(Config.pushUses) || Utils.isKeyDown(Config.prevRecipe)) {
                keyHeldLastTick = true;
            }

        }
        return true;
    }

    public boolean OnTickInGame(Minecraft minecraft)
    {
        if(minecraft.currentScreen == null && Utils.isKeyDown(Config.allRecipes) && !keyHeldLastTick) {
            keyHeldLastTick = true;
            pushRecipe(null, null, false);
        }
        return true;
    }

    public static boolean keyHeldLastTick = false;
    private static long focusCooldown = 0L;

    public static void pushRecipe(ScreenBase gui, ItemInstance item, boolean getUses) {
        if(Utils.getMC().player.inventory.getCursorItem() == null) {
            if (gui instanceof GuiRecipeViewer) {
                ((GuiRecipeViewer) gui).push(item, getUses);
            }
            else if (!GuiOverlay.searchBoxFocused() && getTabs().size() > 0){
                GuiRecipeViewer newgui = new GuiRecipeViewer(item, getUses, gui);
                Utils.getMC().currentScreen = newgui;
                ScreenScaler scaledresolution = new ScreenScaler(Utils.getMC().options, Utils.getMC().actualWidth, Utils.getMC().actualHeight);
                int i = scaledresolution.getScaledWidth();
                int j = scaledresolution.getScaledHeight();
                newgui.init(Utils.getMC(), i, j);
                Utils.getMC().skipGameRender = false;
            }
        }
    }

    public static void pushTabBlock(ScreenBase gui, ItemInstance item) {
        if (gui instanceof GuiRecipeViewer) {
            ((GuiRecipeViewer) gui).pushTabBlock(item);
        }
        else if (!GuiOverlay.searchBoxFocused() && getTabs().size() > 0){
            Utils.getMC().lockCursor();
            GuiRecipeViewer newgui = new GuiRecipeViewer(item, gui);
            Utils.getMC().currentScreen = newgui;
            ScreenScaler scaledresolution = new ScreenScaler(Utils.getMC().options, Utils.getMC().actualWidth, Utils.getMC().actualHeight);
            int i = scaledresolution.getScaledWidth();
            int j = scaledresolution.getScaledHeight();
            newgui.init(Utils.getMC(), i, j);
            Utils.getMC().skipGameRender = false;
        }
    }

    public static void drawRect(int i, int j, int k, int l, int i1) {
        // This is not that slow. Its only getting the method with reflection that is slow.
        try {
            fill.invoke(Utils.gui, i, j, k, l, i1);
        } catch (Exception e) {
            logger.severe("Something went very wrong rendering a GUI!");
            e.printStackTrace();
        }
    }

    public static ArrayList<Tab> getTabs() {
        if(tabs == null) {
            allTabs = new ArrayList<>();

            TabUtils.loadTabs(allTabs, thisMod);

            for(Tab tab : modTabs) {
                allTabs.add(tab);
            }
            Config.readConfig();
            tabs = Config.orderTabs();
        }
        return tabs;
    }

    public static void tabOrderChanged(boolean[] tabEnabled, Tab[] tabOrder) {
        Config.tabOrderChanged(tabEnabled, tabOrder);
        tabs = Config.orderTabs();
    }

    private static ArrayList<Tab> tabs;
    public static ArrayList<Tab> allTabs;
    private static ArrayList<Tab> modTabs = new ArrayList<>();

    @Override
    public void onInitialize() {
        KeyBindingRegister.EVENT.register(this);
        try {
            fill = Utils.getMethod(DrawableHelper.class, new String[] {"fill", "method_1932"}, new Class<?>[] {int.class, int.class, int.class, int.class, int.class});
        } catch (Exception e) {
            e.printStackTrace();
        }
        fill.setAccessible(true);
        thisMod = this;
        Config.init();
    }
}
