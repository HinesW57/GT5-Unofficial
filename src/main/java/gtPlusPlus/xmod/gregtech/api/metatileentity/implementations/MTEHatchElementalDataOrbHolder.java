package gtPlusPlus.xmod.gregtech.api.metatileentity.implementations;

import static gregtech.common.modularui2.util.CommonGuiComponents.gridTemplate4by4;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.widget.SlotGroup;

import gregtech.api.enums.ItemList;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.IConfigurationCircuitSupport;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.modularui2.GTGuis;
import gregtech.api.render.TextureFactory;
import gregtech.common.items.ItemIntegratedCircuit;
import gtPlusPlus.api.objects.Logger;
import gtPlusPlus.core.lib.GTPPCore;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;

public class MTEHatchElementalDataOrbHolder extends MTEHatch implements IConfigurationCircuitSupport {

    public MTEHatchElementalDataOrbHolder(int aID, String aName, String aNameRegional, int aTier) {
        super(
            aID,
            aName,
            aNameRegional,
            aTier,
            17,
            new String[] { "Holds Data Orbs for the Elemental Duplicator", "Can insert/extract the circuit slot",
                "A circuit must be used to select a slot (1-16)", GTPPCore.GT_Tooltip.get() });
    }

    public MTEHatchElementalDataOrbHolder(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, 17, aDescription, aTextures);
    }

    @Override
    public ITexture[] getTexturesActive(ITexture aBaseTexture) {
        return new ITexture[] { aBaseTexture, TextureFactory.of(TexturesGtBlock.Overlay_Hatch_Data_Orb) };
    }

    @Override
    public ITexture[] getTexturesInactive(ITexture aBaseTexture) {
        return new ITexture[] { aBaseTexture, TextureFactory.of(TexturesGtBlock.Overlay_Hatch_Data_Orb) };
    }

    @Override
    public boolean isFacingValid(ForgeDirection facing) {
        return true;
    }

    @Override
    public boolean isAccessAllowed(EntityPlayer aPlayer) {
        return true;
    }

    @Override
    public boolean isValidSlot(int aIndex) {
        return true;
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new MTEHatchElementalDataOrbHolder(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        openGui(aPlayer);
        return true;
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTimer) {
        if (aBaseMetaTileEntity.isServerSide() && aBaseMetaTileEntity.hasInventoryBeenModified()) {
            fillStacksIntoFirstSlots();
        }
    }

    public void updateSlots() {
        for (int i = 0; i < mInventory.length - 1; i++)
            if (mInventory[i] != null && mInventory[i].stackSize <= 0) mInventory[i] = null;
        fillStacksIntoFirstSlots();
    }

    protected void fillStacksIntoFirstSlots() {
        for (int i = 0; i < mInventory.length - 1; i++) {
            if (mInventory[i] != null && mInventory[i].stackSize <= 0) {
                mInventory[i] = null;
            }
        }
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
    }

    @Override
    public void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {}

    @Override
    public boolean allowPullStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, ForgeDirection side,
        ItemStack aStack) {
        Logger.INFO("Checking if we can pull " + aStack.getDisplayName() + " from slot " + aIndex);
        return aIndex == mInventory.length - 1 && aStack != null
            && aStack.getItem() instanceof ItemIntegratedCircuit
            && side == getBaseMetaTileEntity().getFrontFacing();
    }

    @Override
    public boolean allowPutStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, ForgeDirection side,
        ItemStack aStack) {
        Logger.INFO("Checking if we can put " + aStack.getDisplayName() + " into slot " + aIndex);
        return aIndex == mInventory.length - 1 && aStack != null
            && aStack.getItem() instanceof ItemIntegratedCircuit
            && side == getBaseMetaTileEntity().getFrontFacing();
    }

    public ArrayList<ItemStack> getInventory() {
        ArrayList<ItemStack> aContents = new ArrayList<>();
        for (int i = getBaseMetaTileEntity().getSizeInventory() - 2; i >= 0; i--) {
            if (getBaseMetaTileEntity().getStackInSlot(i) != null)
                aContents.add(getBaseMetaTileEntity().getStackInSlot(i));
        }
        return aContents;
    }

    public ItemStack getOrbByCircuit() {
        ItemStack aCirc = getBaseMetaTileEntity().getStackInSlot(getCircuitSlot());
        if (aCirc != null && aCirc.getItem() instanceof ItemIntegratedCircuit) {
            int slot = aCirc.getItemDamage() - 1; // slots are 0 indexed but there's no 0 circuit
            if (slot < getBaseMetaTileEntity().getSizeInventory() - 1) {
                return getBaseMetaTileEntity().getStackInSlot(slot);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public boolean canInsertItem(int aIndex, ItemStack aStack, int ordinalSide) {
        if (aIndex == mInventory.length - 1 && aStack != null
            && aStack.getItem() instanceof ItemIntegratedCircuit
            && ordinalSide == getBaseMetaTileEntity().getFrontFacing()
                .ordinal()) {
            Logger.INFO("Putting " + aStack.getDisplayName() + " into slot " + aIndex);
            return true;
        }
        return false;
    }

    @Override
    public boolean canExtractItem(int aIndex, ItemStack aStack, int ordinalSide) {
        if (aIndex == mInventory.length - 1 && aStack != null && aStack.getItem() instanceof ItemIntegratedCircuit) {
            Logger.INFO("Pulling " + aStack.getDisplayName() + " from slot " + aIndex);
            return true;
        }
        return false;
    }

    @Override
    public boolean allowSelectCircuit() {
        return true;
    }

    @Override
    public int getCircuitSlot() {
        return getSlots(mTier);
    }

    @Override
    public int getCircuitSlotX() {
        return 153;
    }

    @Override
    public int getCircuitSlotY() {
        return 63;
    }

    @Override
    protected boolean useMui2() {
        return true;
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings uiSettings) {
        syncManager.registerSlotGroup("item_inv", 4);
        return GTGuis.mteTemplatePanelBuilder(this, data, syncManager, uiSettings)
            .build()
            .child(
                gridTemplate4by4(
                    index -> new ItemSlot().slot(
                        new ModularSlot(inventoryHandler, index).slotGroup("item_inv")
                            .filter(stack -> ItemList.Tool_DataOrb.isStackEqual(stack, false, true)))
                        .background(GTGuiTextures.SLOT_ITEM_STANDARD, GTGuiTextures.OVERLAY_SLOT_DATA_ORB)));
    }

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        builder.widget(
            SlotGroup.ofItemHandler(inventoryHandler, 4)
                .startFromSlot(0)
                .endAtSlot(15)
                .background(getGUITextureSet().getItemSlot(), GTUITextures.OVERLAY_SLOT_DATA_ORB)
                .applyForWidget(
                    widget -> widget.setFilter(stack -> ItemList.Tool_DataOrb.isStackEqual(stack, false, true)))
                .build()
                .setPos(52, 7));
    }
}
