package cy.jdkdigital.trophymanager.common.datamap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record NbtMap(List<String> nbtKeys) {
    public static final Codec<NbtMap> NBT_CODEC = Codec.list(Codec.STRING)
            .xmap(NbtMap::new, NbtMap::nbtKeys);

    public static final Codec<NbtMap> CODEC = Codec.withAlternative(
            RecordCodecBuilder.create(in -> in.group(
                    Codec.list(Codec.STRING).fieldOf("nbt").forGetter(NbtMap::nbtKeys)).apply(in, NbtMap::new)),
            NBT_CODEC);
}
