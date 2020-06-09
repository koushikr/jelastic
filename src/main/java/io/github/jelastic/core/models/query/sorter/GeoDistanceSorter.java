package io.github.jelastic.core.models.query.sorter;


import lombok.*;
import org.elasticsearch.common.geo.GeoPoint;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class GeoDistanceSorter extends JElasticSorter {

    @NotNull
    @NotEmpty
    private String fieldName;

    @NotNull
    @NotEmpty
    @Singular
    private Set<GeoPoint> geoPoints;

    public GeoDistanceSorter(){super(SorterType.GEO_DISTANCE);}

    @Builder
    public GeoDistanceSorter(int priority, SortOrder sortOrder, String fieldName, @Singular Set<GeoPoint> geoPoints){
        super(priority, sortOrder, SorterType.GEO_DISTANCE);
        this.geoPoints = geoPoints;
        this.fieldName = fieldName;

    }

    @Override
    public <V> V accept(SorterVisitor<V> visitor) {
        return visitor.visit(this);
    }

}
