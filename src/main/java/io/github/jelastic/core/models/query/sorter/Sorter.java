package io.github.jelastic.core.models.query.sorter;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.collect.ComparisonChain;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,  property = "sorterType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = FieldSorter.class, name = SorterType.FIELD),
        @JsonSubTypes.Type(value = GeoDistanceSorter.class, name = SorterType.GEO_DISTANCE),
        @JsonSubTypes.Type(value = ScriptSorter.class, name = SorterType.SCRIPT),
        @JsonSubTypes.Type(value = ScoreSorter.class, name = SorterType.SCORE)

})
@Data
@NoArgsConstructor
public abstract class Sorter implements Comparable<Sorter>{

    @Min(1)
    public int priority;

    private SortOrder sortOrder;

    private String sorterType;

    @Override
    public int compareTo(Sorter o) {
        return ComparisonChain.start()
                .compare(priority, o.getPriority())
                .result();
    }

    public Sorter(String sorterType) {
      this.sorterType = sorterType;
    }

    public Sorter(int priority, SortOrder sortOrder, String sorterType){
        this.priority = priority;
        this.sortOrder = sortOrder;
        this.sorterType = sorterType;
    }

    public abstract <V> V accept(SorterVisitor<V> visitor);
}
