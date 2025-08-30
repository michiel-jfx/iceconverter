package nl.dotjava.javafx.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/** class for deserialization to mobile storage. */
public class StorageProperty {
    public StorageProperty() {
        // default empty constructor
    }
    private String key;
    private String value;
    @JsonCreator
    public StorageProperty(
            @JsonProperty("key") String key,
            @JsonProperty("value") String value) {
        this.key = key;
        this.value = value;
    }
    @JsonProperty("key")
    public String getKey() {
        return this.key;
    }
    @JsonProperty("key")
    public void setKey(String key) {
        this.key = key;
    }
    @JsonProperty("value")
    public String getValue() {
        return this.value;
    }
    @JsonProperty("value")
    public void setValue(String value) {
        this.value = value;
    }
}
