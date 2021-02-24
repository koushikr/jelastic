package io.github.jelastic.core.models.query.sorter;

public interface SorterVisitor<T> {

    T visit(FieldSorter fieldSorter);

    T visit(GeoDistanceSorter geoDistanceSorter);

    T visit(ScriptSorter scriptSorter);

    T visit(ScoreSorter scoreSorter);

}
