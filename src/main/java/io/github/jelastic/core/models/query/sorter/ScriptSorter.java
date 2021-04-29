package io.github.jelastic.core.models.query.sorter;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.elasticsearch.script.Script;

import javax.validation.constraints.NotNull;

import static org.elasticsearch.search.sort.ScriptSortBuilder.ScriptSortType;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ScriptSorter extends Sorter {

    @NotNull
    private Script script;

    @NotNull
    private ScriptSortType scriptSortType;

    public ScriptSorter(){super(SorterType.SCRIPT);}

    @Builder
    public ScriptSorter(int priority, SortOrder sortOrder, Script script, ScriptSortType scriptSortType) {
        super(priority, sortOrder, SorterType.SCRIPT);
        this.script = script;
        this.scriptSortType = scriptSortType;
    }

    @Override
    public <V> V accept(SorterVisitor<V> visitor) {
    return visitor.visit(this);
  }
}
