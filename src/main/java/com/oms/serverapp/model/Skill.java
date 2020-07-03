package com.oms.serverapp.model;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Table(name = "skills")
public class Skill {

    @EmbeddedId
    SkillPrimaryKey id;

    @NotNull(message = "Device data cannot be null.")
    @ManyToOne
    @MapsId("device_id")
    private Device device;

    @NotNull(message = "Failure data cannot be null.")
    @ManyToOne
    @MapsId("failure_id")
    private Failure failure;

    @NotNull(message = "Profit cannot be null")
    private Integer profit;

    @Min(15)
    @Max(480)
    private Integer minRepairTime; //in minutes

    @Min(15)
    @Max(480)
    private Integer maxRepairTime; //in minutes

    @ManyToMany(mappedBy = "ownedSkills", cascade = CascadeType.ALL)
    Set<ServiceMan> serviceMen;

    public Skill() {
    }

    public Skill(Device device, Failure failure, Integer minRepairTime, Integer maxRepairTime) {
        this.device = device;
        this.failure = failure;
        this.minRepairTime = minRepairTime;
        this.maxRepairTime = maxRepairTime;
    }

    public SkillPrimaryKey getId() {
        return id;
    }

    public void setId(SkillPrimaryKey id) {
        this.id = id;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Failure getFailure() {
        return failure;
    }

    public void setFailure(Failure failure) {
        this.failure = failure;
    }

    public Integer getProfit() {
        return profit;
    }

    public void setProfit(Integer profit) {
        this.profit = profit;
    }

    public Integer getMinRepairTime() {
        return minRepairTime;
    }

    public void setMinRepairTime(Integer minRepairTime) {
        this.minRepairTime = minRepairTime;
    }

    public Integer getMaxRepairTime() {
        return maxRepairTime;
    }

    public void setMaxRepairTime(Integer maxRepairTime) {
        this.maxRepairTime = maxRepairTime;
    }

    public Set<ServiceMan> getServiceMen() {
        return serviceMen;
    }

    public void setServiceMen(Set<ServiceMan> serviceMen) {
        this.serviceMen = serviceMen;
    }
}
