package com.oms.serverapp.payload;

import com.oms.serverapp.model.Skill;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.stream.Collectors;

public class SkillPayload {

    private Long id;

    @NotNull(message = "Device cannot be null.")
    private Long device;

    @NotNull(message = "Failure cannot be null.")
    private Long failure;

    @NotNull(message = "Profit cannot be null.")
    private Integer profit;

    @NotNull()
    @Min(value = 15, message = "Minimum repair time should be between 15 and 480." )
    @Max(value = 480, message = "Minimum repair time should be between 15 and 480." )
    private Integer minRepairTime;

    @NotNull()
    @Min(value = 15, message = "Maximum repair time should be between 15 and 480.")
    @Max(value = 480, message = "Maximum repair time should be between 15 and 480.")
    private Integer maxRepairTime;

    private Set<Long> serviceMen;

    public SkillPayload() {
    }

    public SkillPayload(Skill skill) {
        this.id = skill.getId();
        this.device = skill.getDevice().getId();
        this.failure = skill.getFailure().getId();
        this.profit = skill.getProfit();
        this.minRepairTime = skill.getMinRepairTime();
        this.maxRepairTime = skill.getMaxRepairTime();
        this.serviceMen = skill.getServiceMen().stream().map(serviceMan -> serviceMan.getId()).collect(Collectors.toSet());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDevice() {
        return device;
    }

    public void setDevice(Long device) {
        this.device = device;
    }

    public Long getFailure() {
        return failure;
    }

    public void setFailure(Long failure) {
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

    public Set<Long> getServiceMen() {
        return serviceMen;
    }

    public void setServiceMen(Set<Long> serviceMen) {
        this.serviceMen = serviceMen;
    }
}
