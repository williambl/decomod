package com.williambl.decomod;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ChainLinkDoorBlock extends DoorBlock {
    protected static final VoxelShape SOUTH_CLOSED_AABB = Block.box(0.0, 0.0, 7.0, 16.0, 16.0, 9.0);
    protected static final VoxelShape NORTH_CLOSED_AABB = Block.box(0.0, 0.0, 7.0, 16.0, 16.0, 9.0);
    protected static final VoxelShape WEST_CLOSED_AABB = Block.box(7.0, 0.0, 0.0, 9.0, 16.0, 16.0);
    protected static final VoxelShape EAST_CLOSED_AABB = Block.box(7.0, 0.0, 0.0, 9.0, 16.0, 16.0);

    protected static final VoxelShape SOUTH_OPEN_AABB_LEFT = Block.box(8.0, 0.0, 0.0, 24.0, 16.0, 2.0);
    protected static final VoxelShape NORTH_OPEN_AABB_LEFT = Block.box(-8.0, 0.0, 14.0, 8.0, 16.0, 16.0);
    protected static final VoxelShape WEST_OPEN_AABB_LEFT = Block.box(14.0, 0.0, 8.0, 16.0, 16.0, 24.0);
    protected static final VoxelShape EAST_OPEN_AABB_LEFT = Block.box(0.0, 0.0, -8.0, 2.0, 16.0, 8.0);
    protected static final VoxelShape SOUTH_OPEN_AABB_RIGHT = Block.box(-8.0, 0.0, 0.0, 8.0, 16.0, 2.0);
    protected static final VoxelShape NORTH_OPEN_AABB_RIGHT = Block.box(8.0, 0.0, 14.0, 24.0, 16.0, 16.0);
    protected static final VoxelShape WEST_OPEN_AABB_RIGHT = Block.box(14.0, 0.0, -8.0, 16.0, 16.0, 8.0);
    protected static final VoxelShape EAST_OPEN_AABB_RIGHT = Block.box(0.0, 0.0, 8.0, 2.0, 16.0, 24.0);

    protected ChainLinkDoorBlock(Properties properties, BlockSetType blockSetType) {
        super(properties, blockSetType);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        boolean closed = !state.getValue(OPEN);
        boolean isRight = state.getValue(HINGE) == DoorHingeSide.RIGHT;
        return switch (facing) {
            default -> closed ? EAST_CLOSED_AABB : (isRight ? NORTH_OPEN_AABB_RIGHT : SOUTH_OPEN_AABB_LEFT);
            case SOUTH -> closed ? SOUTH_CLOSED_AABB : (isRight ? EAST_OPEN_AABB_RIGHT : WEST_OPEN_AABB_LEFT);
            case WEST -> closed ? WEST_CLOSED_AABB : (isRight ? SOUTH_OPEN_AABB_RIGHT : NORTH_OPEN_AABB_LEFT);
            case NORTH -> closed ? NORTH_CLOSED_AABB : (isRight ? WEST_OPEN_AABB_RIGHT : EAST_OPEN_AABB_LEFT);
        };
    }
}
