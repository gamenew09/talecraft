package tiffit.talecraft.client.gui.npc;

import java.io.IOException;
import java.util.List;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tiffit.talecraft.entity.NPC.ContainerNPCMerchant;
import tiffit.talecraft.entity.NPC.NPCShop;
import tiffit.talecraft.entity.NPC.NPCShop.NPCTrade;

@SideOnly(Side.CLIENT)
/**I find copying the class was the easiest way to do this*/
public class GuiNPCMerchant extends GuiContainer
{
    /** The GUI texture for the villager merchant GUI. */
    private static final ResourceLocation MERCHANT_GUI_TEXTURE = new ResourceLocation("textures/gui/container/villager.png");
    /** The current IMerchant instance in use for this specific merchant. */
    private final NPCShop merchant;
    /** The button which proceeds to the next available merchant recipe. */
    private GuiNPCMerchant.MerchantButton nextButton;
    /** Returns to the previous Merchant recipe if one is applicable. */
    private GuiNPCMerchant.MerchantButton previousButton;
    /** The integer value corresponding to the currently selected merchant recipe. */
    private int selectedMerchantRecipe;
    /** The chat component utilized by this GuiMerchant instance. */
    private final ITextComponent chatComponent;

    public GuiNPCMerchant(InventoryPlayer player, IMerchant merchant, World world){
        super(new ContainerNPCMerchant(player, merchant, world));
        this.merchant = (NPCShop) merchant;
        this.chatComponent = merchant.getDisplayName();
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    @Override
		public void initGui(){
        super.initGui();
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.nextButton = (GuiNPCMerchant.MerchantButton)this.func_189646_b(new GuiNPCMerchant.MerchantButton(1, i + 120 + 27, j + 24 - 1, true));
        this.previousButton = (GuiNPCMerchant.MerchantButton)this.func_189646_b(new GuiNPCMerchant.MerchantButton(2, i + 36 - 19, j + 24 - 1, false));
        this.nextButton.enabled = false;
        this.previousButton.enabled = false;
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    @Override
		protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
        String s = this.chatComponent.getUnformattedText();
        this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
        this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
    }

    /**
     * Called from the main game loop to update the screen.
     */
    @Override
		public void updateScreen(){
        super.updateScreen();
        List<NPCTrade> trades = merchant.getTrades();

        if (trades != null){
            this.nextButton.enabled = this.selectedMerchantRecipe < trades.size() - 1;
            this.previousButton.enabled = this.selectedMerchantRecipe > 0;
        }
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    @Override
		protected void actionPerformed(GuiButton button) throws IOException{
        boolean flag = false;

        if (button == this.nextButton){
            ++this.selectedMerchantRecipe;
            List<NPCTrade> trades = merchant.getTrades();

            if (trades != null && this.selectedMerchantRecipe >= trades.size()){
                this.selectedMerchantRecipe = trades.size() - 1;
            }

            flag = true;
        }
        else if (button == this.previousButton){
            selectedMerchantRecipe--;

            if (selectedMerchantRecipe < 0){
                selectedMerchantRecipe = 0;
            }

            flag = true;
        }

        if (flag){
            ((ContainerNPCMerchant)this.inventorySlots).setCurrentRecipeIndex(this.selectedMerchantRecipe);
            PacketBuffer packetbuffer = new PacketBuffer(Unpooled.buffer());
            packetbuffer.writeInt(this.selectedMerchantRecipe);
            this.mc.getConnection().sendPacket(new CPacketCustomPayload("MC|TrSel", packetbuffer));
        }
    }

    /**
     * Draws the background layer of this container (behind the items).
     */
    @Override
		protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(MERCHANT_GUI_TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
        List<NPCTrade> trades = merchant.getTrades();

        if (trades != null && !trades.isEmpty()){
            int k = this.selectedMerchantRecipe;

            if (k < 0 || k >= trades.size()){
                return;
            }

            NPCTrade trade = (NPCTrade)trades.get(k);

            if (!trade.inStock()){
                this.mc.getTextureManager().bindTexture(MERCHANT_GUI_TEXTURE);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.disableLighting();
                this.drawTexturedModalRect(this.guiLeft + 83, this.guiTop + 21, 212, 0, 28, 21);
                this.drawTexturedModalRect(this.guiLeft + 83, this.guiTop + 51, 212, 0, 28, 21);
            }
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
		public void drawScreen(int mouseX, int mouseY, float partialTicks){
        super.drawScreen(mouseX, mouseY, partialTicks);
        List<NPCTrade> trades = merchant.getTrades();

        if (trades != null && !trades.isEmpty()){
            int i = (this.width - this.xSize) / 2;
            int j = (this.height - this.ySize) / 2;
            int k = this.selectedMerchantRecipe;
            NPCTrade trade = (NPCTrade)trades.get(k);
            ItemStack buy = trade.getBuying();
            ItemStack sell = trade.getSelling();
            GlStateManager.pushMatrix();
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableColorMaterial();
            GlStateManager.enableLighting();
            this.itemRender.zLevel = 100.0F;
            this.itemRender.renderItemAndEffectIntoGUI(buy, i + 36, j + 24);
            this.itemRender.renderItemOverlays(this.fontRendererObj, buy, i + 36, j + 24);

            this.itemRender.renderItemAndEffectIntoGUI(sell, i + 120, j + 24);
            this.itemRender.renderItemOverlays(this.fontRendererObj, sell, i + 120, j + 24);
            this.itemRender.zLevel = 0.0F;
            GlStateManager.disableLighting();

            if (this.isPointInRegion(36, 24, 16, 16, mouseX, mouseY) && buy != null){
                this.renderToolTip(buy, mouseX, mouseY);
            }
            else if (sell != null && this.isPointInRegion(120, 24, 16, 16, mouseX, mouseY) && sell != null){
                this.renderToolTip(sell, mouseX, mouseY);
            }
            else if (this.isPointInRegion(83, 21, 28, 21, mouseX, mouseY) || this.isPointInRegion(83, 51, 28, 21, mouseX, mouseY)){
            	if(trade.inStock()){
            		String stock = trade.getStock() == -1 ? "Infinite" : (trade.getStock() + "");
            		TextFormatting color = trade.getStock() == -1 ? TextFormatting.GOLD : TextFormatting.WHITE;
            		this.drawCreativeTabHoveringText("Stock: " + color + stock, mouseX, mouseY);
            	}else this.drawCreativeTabHoveringText(TextFormatting.DARK_RED + "Out Of Stock", mouseX, mouseY);
            }

            GlStateManager.popMatrix();
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
        }
    }

    public IMerchant getMerchant(){
        return this.merchant;
    }

    @SideOnly(Side.CLIENT)
    static class MerchantButton extends GuiButton{
            private final boolean forward;

            public MerchantButton(int buttonID, int x, int y, boolean p_i1095_4_){
                super(buttonID, x, y, 12, 19, "");
                this.forward = p_i1095_4_;
            }

            /**
             * Draws this button to the screen.
             */
            @Override
						public void drawButton(Minecraft mc, int mouseX, int mouseY){
                if (this.visible){
                    mc.getTextureManager().bindTexture(GuiNPCMerchant.MERCHANT_GUI_TEXTURE);
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    boolean flag = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
                    int i = 0;
                    int j = 176;

                    if (!this.enabled){
                        j += this.width * 2;
                    }
                    else if (flag){
                        j += this.width;
                    }

                    if (!this.forward){
                        i += this.height;
                    }

                    this.drawTexturedModalRect(this.xPosition, this.yPosition, j, i, this.width, this.height);
                }
            }
        }
}
