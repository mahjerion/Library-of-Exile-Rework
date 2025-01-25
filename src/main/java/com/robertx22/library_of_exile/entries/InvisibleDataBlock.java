package com.robertx22.library_of_exile.entries;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class InvisibleDataBlock extends BaseEntityBlock {

    public InvisibleDataBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.AIR));
    }


    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new InvisibleDataBlockEntity(pPos, pState);
    }

    //copied from airblock
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return Shapes.empty();
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return new BlockEntityTicker<T>() {
            @Override
            public void tick(Level world, BlockPos pos, BlockState pState, T be) {
                if (!world.isClientSide) {
                    if (be instanceof InvisibleDataBlockEntity sbe) {
                        sbe.ticks++;
                        sbe.get().ifPresent(x -> {
                            x.tick(sbe, world, pos);
                        });
                    }
                }
            }
        };
    }

}
