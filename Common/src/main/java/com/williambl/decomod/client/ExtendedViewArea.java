package com.williambl.decomod.client;

import net.minecraft.world.level.ChunkPos;

public interface ExtendedViewArea {
    Iterable<ChunkPos> visibleChunkPositions();
}
