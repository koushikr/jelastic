package io.github.jelastic.core.models.query.sorter;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FieldSorter extends Sorter {

    @NotNull
    @NotEmpty
    private String fieldName;

    public FieldSorter(){super(SorterType.FIELD);}

    @Builder
    public FieldSorter(int priority, SortOrder sortOrder, String fieldName) {
        super(priority, sortOrder, SorterType.FIELD);
        this.fieldName = fieldName;
    }

    @Override
    public <V> V accept(SorterVisitor<V> visitor) {
    return visitor.visit(this);
  }
}