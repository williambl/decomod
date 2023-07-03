package com.williambl.decomod.fabric.wallpaper;

import com.mojang.serialization.Codec;
import com.williambl.decomod.Constants;
import com.williambl.decomod.DMRegistry;
import com.williambl.decomod.DecoMod;
import com.williambl.decomod.wallpaper.WallpaperChunk;
import com.williambl.decomod.wallpaper.WallpaperType;
import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

import static com.williambl.decomod.DecoMod.id;

public class ChunkWallpaperComponent implements WallpaperChunk, Component, AutoSyncedComponent {
    public static final ComponentKey<ChunkWallpaperComponent> KEY = ComponentRegistry.getOrCreate(id("wallpaper"), ChunkWallpaperComponent.class);
    private static final Codec<Map<BlockPos, EnumMap<Direction, WallpaperType>>> WALLPAPERS_CODEC
            = Codec.unboundedMap(Codec.STRING.xmap(Long::parseLong, Object::toString).xmap(BlockPos::of, BlockPos::asLong), Codec.unboundedMap(Direction.CODEC, DMRegistry.WALLPAPER_REGISTRY.get().byNameCodec()).xmap(EnumMap::new, Function.identity()));
    private static final String WALLPAPERS_NBT_KEY = "wallpapers";

    private final LinkedHashMap<BlockPos, EnumMap<Direction, WallpaperType>> wallpapers = new LinkedHashMap<>();
    private final ChunkAccess chunk;

    public ChunkWallpaperComponent(ChunkAccess chunk) {
        this.chunk = chunk;
    }

    private void clearEmptyMaps() {
        this.wallpapers.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    @Override
    public void readFromNbt(@NotNull CompoundTag tag) {
        this.wallpapers.clear();
        if (tag.contains(WALLPAPERS_NBT_KEY)) {
            this.wallpapers.putAll(WALLPAPERS_CODEC.decode(NbtOps.INSTANCE, tag.get(WALLPAPERS_NBT_KEY)).getOrThrow(false, Constants.LOGGER::error).getFirst());
        }
    }

    @Override
    public void writeToNbt(@NotNull CompoundTag tag) {
        this.clearEmptyMaps();
        if (!this.wallpapers.isEmpty()) {
            tag.put(WALLPAPERS_NBT_KEY, WALLPAPERS_CODEC.encode(this.wallpapers, NbtOps.INSTANCE, NbtOps.INSTANCE.emptyMap()).getOrThrow(false, Constants.LOGGER::error));
        }
    }

    @Override
    public void writeSyncPacket(FriendlyByteBuf buf, ServerPlayer recipient) {
        this.clearEmptyMaps();
        buf.writeVarInt(this.wallpapers.size());
        for (var entry : this.wallpapers.entrySet()) {
            buf.writeBlockPos(entry.getKey());
            buf.writeByte(entry.getValue().size());
            for (var entry2 : entry.getValue().entrySet()) {
                buf.writeEnum(entry2.getKey());
                buf.writeVarInt(DMRegistry.WALLPAPER_REGISTRY.get().getId(entry2.getValue()));
            }
        }
    }

    @Override
    public void applySyncPacket(FriendlyByteBuf buf) {
        int numEntries = buf.readVarInt();
        var wallpaperPositions = new HashSet<>(this.wallpapers.keySet());
        wallpaperPositions.forEach(blockPos -> Minecraft.getInstance().levelRenderer.setBlocksDirty(blockPos.getX()-1, blockPos.getY()-1, blockPos.getZ()-1, blockPos.getX()+1, blockPos.getY()+1, blockPos.getZ()+1));

        this.wallpapers.clear(); //TODO: do this better
        for (int i = 0; i < numEntries; i++) {
            var blockPos = buf.readBlockPos();
            int numEntries2 = buf.readByte();
            var map = new EnumMap<Direction, WallpaperType>(Direction.class);
            for (int j = 0; j < numEntries2; j++) {
                map.put(buf.readEnum(Direction.class), DMRegistry.WALLPAPER_REGISTRY.get().byId(buf.readVarInt()));
            }
            this.wallpapers.put(blockPos, map);
            Minecraft.getInstance().levelRenderer.setBlocksDirty(blockPos.getX()-1, blockPos.getY()-1, blockPos.getZ()-1, blockPos.getX()+1, blockPos.getY()+1, blockPos.getZ()+1);
        }
    }

    @Override
    public WallpaperType addWallpaper(BlockPos pos, Direction dir, WallpaperType data) {
        var res = this.wallpapers.compute(pos, ($, m) -> {
            if (m == null) {
                m = new EnumMap<>(Direction.class);
            }
            m.put(dir, data);
            return m;
        }).get(dir);

        this.chunk.setUnsaved(true);
        KEY.sync(this.chunk);
        return res;
    }

    @Override
    public WallpaperType removeWallpaper(BlockPos pos, Direction dir) {
        var enumMap = this.wallpapers.get(pos);
        if (enumMap == null) {
            return null;
        }

        var res = enumMap.remove(dir);
        this.chunk.setUnsaved(true);
        KEY.sync(this.chunk);
        return res;
    }

    @Override
    public void removeWallpaper(BlockPos pos) {
        this.wallpapers.remove(pos);
    }

    @NotNull
    @Override
    public Iterator<Map.Entry<BlockPos, EnumMap<Direction, WallpaperType>>> iterator() {
        return this.wallpapers.entrySet().iterator();
    }
}