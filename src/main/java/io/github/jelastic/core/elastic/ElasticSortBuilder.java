package io.github.jelastic.core.elastic;

import io.github.jelastic.core.models.query.sorter.FieldSorter;
import io.github.jelastic.core.models.query.sorter.GeoDistanceSorter;
import io.github.jelastic.core.models.query.sorter.ScriptSorter;
import io.github.jelastic.core.models.query.sorter.SorterVisitor;
import io.github.jelastic.core.utils.ElasticUtils;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;

public class ElasticSortBuilder implements SorterVisitor<SortBuilder> {

    @Override
    public SortBuilder visit(FieldSorter fieldSorter) {
        return SortBuilders.fieldSort(fieldSorter.getFieldName())
                .order(ElasticUtils.getSortOrder(fieldSorter.getSortOrder()));
    }

    @Override
    public SortBuilder visit(GeoDistanceSorter geoDistanceSorter) {
        return SortBuilders.geoDistanceSort(geoDistanceSorter.getFieldName(),
                geoDistanceSorter.getGeoPoints().toArray(new GeoPoint[geoDistanceSorter.getGeoPoints().size()]))
                .order(ElasticUtils.getSortOrder(geoDistanceSorter.getSortOrder()));
    }

    @Override
    public SortBuilder visit(ScriptSorter scriptSorter) {
        return SortBuilders.scriptSort(scriptSorter.getScript(), scriptSorter.getScriptSortType())
                .order(ElasticUtils.getSortOrder(scriptSorter.getSortOrder()));
    }

}
