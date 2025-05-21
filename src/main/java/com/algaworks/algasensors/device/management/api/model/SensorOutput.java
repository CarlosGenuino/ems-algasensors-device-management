package com.algaworks.algasensors.device.management.api.model;

import com.algaworks.algasensors.device.management.domain.model.Sensor;
import com.algaworks.algasensors.device.management.domain.model.SensorId;
import io.hypersistence.tsid.TSID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SensorOutput {
    private TSID id;
    private String name;
    private String ip;
    private String location;
    private String protocol;
    private String model;
    private Boolean enabled;

    public SensorOutput(Sensor sensor){
        this.id = sensor.getId().getValue();
        this.name = sensor.getName();
        this.ip = sensor.getIp();
        this.location = sensor.getLocation();
        this.protocol = sensor.getProtocol();
        this.model = sensor.getModel();
        this.enabled = sensor.getEnabled();
    }
}
