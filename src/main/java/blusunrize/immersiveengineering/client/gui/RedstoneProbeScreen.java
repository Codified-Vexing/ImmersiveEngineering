/*
 * BluSunrize
 * Copyright (c) 2020
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package blusunrize.immersiveengineering.client.gui;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import blusunrize.immersiveengineering.api.Lib;
import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.client.gui.elements.GuiButtonBoolean;
import blusunrize.immersiveengineering.common.blocks.metal.ConnectorProbeTileEntity;
import blusunrize.immersiveengineering.common.network.MessageTileSync;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;

import static blusunrize.immersiveengineering.client.ClientUtils.mc;

public class RedstoneProbeScreen extends ClientTileScreen<ConnectorProbeTileEntity>
{
	public RedstoneProbeScreen(ConnectorProbeTileEntity tileEntity, ITextComponent title)
	{
		super(tileEntity, title);
		this.xSize = 216;
		this.ySize = 80;
	}

	private GuiButtonBoolean[] colorButtonsSend;
	private GuiButtonBoolean[] colorButtonsReceive;

	@Override
	public void init()
	{
		super.init();
		mc().keyboardListener.enableRepeatEvents(true);

		this.buttons.clear();

		colorButtonsSend = new GuiButtonBoolean[16];
		colorButtonsReceive = new GuiButtonBoolean[16];
		for(int i = 0; i < colorButtonsSend.length; i++)
		{
			final DyeColor color = DyeColor.byId(i);
			colorButtonsSend[i] = RedstoneConnectorScreen.buildColorButton(colorButtonsSend, guiLeft+20+(i%4*14), guiTop+10+(i/4*14),
					tileEntity.redstoneChannelSending.ordinal()==i, color, btn -> {
						tileEntity.redstoneChannelSending = color;
					});
			this.addButton(colorButtonsSend[i]);

			colorButtonsReceive[i] = RedstoneConnectorScreen.buildColorButton(colorButtonsReceive, guiLeft+136+(i%4*14), guiTop+10+(i/4*14),
					tileEntity.redstoneChannel.ordinal()==i, color, btn -> {
						tileEntity.redstoneChannel = color;
					});
			this.addButton(colorButtonsReceive[i]);
		}
	}

	@Override
	public void onClose()
	{
		super.onClose();

		CompoundNBT message = new CompoundNBT();
		message.putInt("redstoneChannel", tileEntity.redstoneChannel.getId());
		message.putInt("redstoneChannelSending", tileEntity.redstoneChannelSending.ordinal());
		ImmersiveEngineering.packetHandler.sendToServer(new MessageTileSync(tileEntity, message));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(int mouseX, int mouseY, float partialTick)
	{

	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY, float partialTick)
	{
		this.font.drawString(new TranslationTextComponent(Lib.GUI_CONFIG+"redstone_color_sending").getFormattedText(), guiLeft, guiTop, DyeColor.WHITE.getTextColor());
		this.font.drawString(new TranslationTextComponent(Lib.GUI_CONFIG+"redstone_color_receiving").getFormattedText(), guiLeft+116, guiTop, DyeColor.WHITE.getTextColor());

		ArrayList<ITextComponent> tooltip = new ArrayList<>();
		for(int i = 0; i < colorButtonsSend.length; i++)
			if(colorButtonsSend[i].isHovered()||colorButtonsReceive[i].isHovered())
			{
				tooltip.add(new TranslationTextComponent(Lib.GUI_CONFIG+"redstone_color"));
				tooltip.add(new TranslationTextComponent("color.minecraft."+DyeColor.byId(i).getTranslationKey())
						.setStyle(new Style().setColor(TextFormatting.GRAY)));
			}

		if(!tooltip.isEmpty())
		{
			ClientUtils.drawHoveringText(tooltip, mouseX, mouseY, font, guiLeft+xSize, -1);
			RenderHelper.enableGUIStandardItemLighting();
		}
	}
}