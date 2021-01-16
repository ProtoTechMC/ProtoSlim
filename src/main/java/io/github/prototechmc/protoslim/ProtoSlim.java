package io.github.prototechmc.protoslim;

import net.fabricmc.api.ModInitializer;
import net.minecraft.network.packet.s2c.play.WorldBorderS2CPacket;
import net.minecraft.world.border.WorldBorder;

public class ProtoSlim implements ModInitializer {
	public static final double BORDER_WIDTH = 16;
	public static final double BORDER_LENGTH = 2 * 29999984;

	@Override
	public void onInitialize() {
	}
}
