package com.robertx22.library_of_exile.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class MapChunkGenerator extends ChunkGenerator {

    public static final Codec<MapChunkGenerator> CODEC = RecordCodecBuilder.create((b) ->
    {
        var group = b.group(
                FlatLevelGeneratorSettings.CODEC.fieldOf("settings").forGetter(x -> x.settings),
                ExtraCodecs.NON_EMPTY_STRING.fieldOf("map_id").forGetter(x -> x.mapId)
        );

        return group.apply(b, b.stable((x, y) -> new MapChunkGenerator(x, y)));
    });

    public final FlatLevelGeneratorSettings settings;

    public String mapId;

    public MapChunkGenerator(FlatLevelGeneratorSettings set, String mapid) {
        super(new FixedBiomeSource(set.getBiome()));
        this.settings = set;
        this.mapId = mapid;
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public void applyCarvers(WorldGenRegion pLevel, long pSeed, RandomState pRandom, BiomeManager pBiomeManager, StructureManager pStructureManager, ChunkAccess pChunk, GenerationStep.Carving pStep) {

    }

    @Override
    public void buildSurface(WorldGenRegion pLevel, StructureManager pStructureManager, RandomState pRandom, ChunkAccess pChunk) {
        var event = new MapChunkGenEvent(pRandom, pLevel.getServer().getStructureManager(), pChunk, pLevel, mapId);
        MapGenEvents.MAP_CHUNK_GEN.callEvents(event);
    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion pLevel) {

    }


    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor pExecutor, Blender pBlender, RandomState pRandom, StructureManager pStructureManager, ChunkAccess pChunk) {
        //makeBase(pChunk);

        return CompletableFuture.completedFuture(pChunk);
    }

    public int getMinY() {
        return 0;
    }

    public int getGenDepth() {
        return 384;
    }

    public int getSeaLevel() {
        return -63;
    }

    @Override
    public int getBaseHeight(int pX, int pZ, Heightmap.Types pType, LevelHeightAccessor pLevel, RandomState pRandom) {
        List<BlockState> list = this.settings.getLayers();

        for (int i = Math.min(list.size(), pLevel.getMaxBuildHeight()) - 1; i >= 0; --i) {
            BlockState blockstate = list.get(i);
            if (blockstate != null && pType.isOpaque().test(blockstate)) {
                return pLevel.getMinBuildHeight() + i + 1;
            }
        }

        return pLevel.getMinBuildHeight();
    }

    @Override
    public NoiseColumn getBaseColumn(int pX, int pZ, LevelHeightAccessor pHeight, RandomState pRandom) {
        return new NoiseColumn(pHeight.getMinBuildHeight(), this.settings.getLayers().stream().limit((long) pHeight.getHeight()).map((p_204549_) -> {
            return p_204549_ == null ? Blocks.AIR.defaultBlockState() : p_204549_;
        }).toArray((p_204543_) -> {
            return new BlockState[p_204543_];
        }));
    }

    @Override
    public void addDebugScreenInfo(List<String> pInfo, RandomState pRandom, BlockPos pPos) {

    }

    // overriding this should stop all structure spawns..?
    @Override
    public void createStructures(RegistryAccess pRegistryAccess, ChunkGeneratorStructureState pStructureState, StructureManager pStructureManager, ChunkAccess pChunk, StructureTemplateManager pStructureTemplateManager) {

    }

}
