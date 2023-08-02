package com.williambl.decomod;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;

public class IronFenceBlock extends IronBarsBlock {
    protected IronFenceBlock(Properties $$0) {
        super($$0);
    }

    @Override
    protected int getAABBIndex(BlockState state) {
        return this.stateToIndex.computeIntIfAbsent(state, (statex) -> {
            int i = 0;
            if (statex.getValue(NORTH)) {
                i |= indexFor(Direction.NORTH);
                if (!statex.getValue(EAST) && !statex.getValue(WEST)) {
                    i |= indexFor(Direction.SOUTH);
                }
            }

            if (statex.getValue(EAST)) {
                i |= indexFor(Direction.EAST);
                if (!statex.getValue(NORTH) && !statex.getValue(SOUTH)) {
                    i |= indexFor(Direction.WEST);
                }
            }

            if (statex.getValue(SOUTH)) {
                i |= indexFor(Direction.SOUTH);
                if (!statex.getValue(EAST) && !statex.getValue(WEST)) {
                    i |= indexFor(Direction.NORTH);
                }
            }

            if (statex.getValue(WEST)) {
                i |= indexFor(Direction.WEST);
                if (!statex.getValue(NORTH) && !statex.getValue(SOUTH)) {
                    i |= indexFor(Direction.EAST);
                }
            }

            return i;
        });
    }
}
