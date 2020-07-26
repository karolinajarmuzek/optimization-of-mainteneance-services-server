package com.oms.serverapp.model;

import com.oms.serverapp.payload.SkillPayload;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Table(name = "skills")
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Device cannot be null.")
    @ManyToOne(fetch = FetchType.LAZY)
    private Device device;

    @NotNull(message = "Failure cannot be null.")
    @ManyToOne(fetch = FetchType.LAZY)
    private Failure failure;

    @NotNull(message = "Profit cannot be null.")
    private Integer profit;

    @NotNull(message = "Minimum repair time should be between 15 and 480.")
    @Min(value = 15, message = "Minimum repair time should be between 15 and 480.")
    @Max(value = 480, message = "Minimum repair time should be between 15 and 480.")
    private Integer minRepairTime; //in minutes

    @NotNull(message = "Maximum repair time should be between 15 and 480.")
    @Min(value = 15, message = "Maximum repair time should be between 15 and 480.")
    @Max(value = 480, message = "Maximum repair time should be between 15 and 480.")
    private Integer maxRepairTime; //in minutes

    @ManyToMany(mappedBy = "ownedSkills", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    Set<ServiceMan> serviceMen;

    public Skill() {
    }

    public Skill(SkillPayload skillPayload, Device device, Failure failure, Set<ServiceMan> serviceMen) {
        this.device = device;
        this.failure = failure;
        this.profit = skillPayload.getProfit();
        this.minRepairTime = skillPayload.getMinRepairTime();
        this.maxRepairTime = skillPayload.getMaxRepairTime();
        this.serviceMen = serviceMen;
    }

    public Skill(Skill skill, SkillPayload skillPayload, Device device, Failure failure, Set<ServiceMan> serviceMen) {
        this.id = skill.getId();
        this.device = device;
        this.failure = failure;
        this.profit = skillPayload.getProfit() != null ? skillPayload.getProfit() : skill.getProfit();
        this.minRepairTime = skillPayload.getMinRepairTime() != null ? skill.getMinRepairTime() : skill.getMinRepairTime();
        this.maxRepairTime = skillPayload.getMaxRepairTime() != null ? skill.getMaxRepairTime() : skill.getMaxRepairTime();
        this.serviceMen = serviceMen;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
