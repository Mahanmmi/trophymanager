package cy.jdkdigital.trophymanager.common.datamap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record DropRateMap(float dropRate) {
    public static final Codec<DropRateMap> DROP_RATE_CODEC = Codec.FLOAT
            .xmap(DropRateMap::new, DropRateMap::dropRate);

    public static final Codec<DropRateMap> CODEC = Codec.withAlternative(
            RecordCodecBuilder.create(in -> in.group(
                    Codec.FLOAT.fieldOf("dropRate").forGetter(DropRateMap::dropRate)).apply(in, DropRateMap::new)),
            DROP_RATE_CODEC);
}
