package io.github.jelastic.core.helpers;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashMap;

/**
 * @author koushik
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class MapElement extends HashMap<String, Object> {

    public void addElement(String name, Object value) {
        this.put(name, value);
    }

    public void merge(MapElement mapElement) {
        this.putAll(mapElement);
    }
}
