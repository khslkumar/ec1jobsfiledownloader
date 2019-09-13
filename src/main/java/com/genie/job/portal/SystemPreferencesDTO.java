package com.genie.job.portal;


import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the SystemPreferences entity.
 */
public class SystemPreferencesDTO implements Serializable {

    private Long id;

    private String name;

    private String value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SystemPreferencesDTO systemPreferencesDTO = (SystemPreferencesDTO) o;
        if(systemPreferencesDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), systemPreferencesDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "SystemPreferencesDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", value='" + getValue() + "'" +
            "}";
    }
}
