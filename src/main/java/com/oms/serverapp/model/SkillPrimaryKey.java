package com.oms.serverapp.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SkillPrimaryKey implements Serializable {

    @Column(name = "device_id")
    Long deviceId;

    @Column(name = "failure_id")
    Long failureId;

    public SkillPrimaryKey() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SkillPrimaryKey that = (SkillPrimaryKey) o;
        return Objects.equals(deviceId, that.deviceId) &&
                Objects.equals(failureId, that.failureId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deviceId, failureId);
    }
}
