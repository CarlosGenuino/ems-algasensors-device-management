package com.algaworks.algasensors.device.management.api.controller;

import com.algaworks.algasensors.device.management.api.client.SensorMonitoringClient;
import com.algaworks.algasensors.device.management.api.model.SensorDetailOutput;
import com.algaworks.algasensors.device.management.api.model.SensorInput;
import com.algaworks.algasensors.device.management.api.model.SensorMonitoringOutput;
import com.algaworks.algasensors.device.management.api.model.SensorOutput;
import com.algaworks.algasensors.device.management.common.IdGenerator;
import com.algaworks.algasensors.device.management.domain.model.Sensor;
import com.algaworks.algasensors.device.management.domain.model.SensorId;
import com.algaworks.algasensors.device.management.domain.repository.SensorRepository;
import io.hypersistence.tsid.TSID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/sensors")
@RequiredArgsConstructor
public class SensorController {

    private final SensorRepository sensorRepository;
    private final SensorMonitoringClient sensorMonitoringClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SensorOutput create(@RequestBody SensorInput input){
        Sensor sensor = Sensor.builder()
                .id(new SensorId(IdGenerator.generateTSID()))
                .location(input.getLocation())
                .protocol(input.getProtocol())
                .model(input.getModel())
                .name(input.getName())
                .ip(input.getIp())
                .enabled(Boolean.FALSE)
                .build();
        sensorRepository.saveAndFlush(sensor);

        return new SensorOutput(sensor);
    }

    @GetMapping
    public ResponseEntity<Page<SensorOutput>> listSensor(@PageableDefault Pageable pageable){
        Page<SensorOutput> sensors = sensorRepository.findAll(pageable).map(SensorOutput::new);
        return ResponseEntity.ok(sensors);
    }

    @GetMapping("{sensorId}")
    public ResponseEntity<SensorOutput> getOne(@PathVariable TSID sensorId){
        Sensor sensor = sensorRepository.findById(new SensorId(sensorId)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(new SensorOutput(sensor));
    }

    @GetMapping("{sensorId}/detail")
    public ResponseEntity<SensorDetailOutput> getOneWithDetail(@PathVariable TSID sensorId){
        Sensor sensor = sensorRepository.findById(new SensorId(sensorId)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        SensorMonitoringOutput monitoring = sensorMonitoringClient.getDetail(sensorId);
        return ResponseEntity.ok(
                SensorDetailOutput.builder()
                        .sensor(new SensorOutput(sensor))
                        .monitoring(monitoring)
                        .build()
        );
    }

    @PutMapping("{sensorId}")
    public ResponseEntity<SensorOutput> updateSensor(@PathVariable TSID sensorId, @RequestBody SensorInput input){
        Sensor sensor = sensorRepository.findById(new SensorId(sensorId)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        sensor.setIp(input.getIp());
        sensor.setName(input.getName());
        sensor.setModel(input.getModel());
        sensor.setLocation(input.getLocation());
        sensor.setProtocol(input.getProtocol());
        sensorRepository.saveAndFlush(sensor);
        return ResponseEntity.ok(new SensorOutput(sensor));
    }

    @PutMapping("{sensorId}/enable")
    public ResponseEntity<SensorOutput> enableSensor(@PathVariable TSID sensorId){
        Sensor sensor = sensorRepository.findById(new SensorId(sensorId)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        sensor.setEnabled(Boolean.TRUE);
        sensorRepository.saveAndFlush(sensor);
        sensorMonitoringClient.enableMonitoring(sensorId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{sensorId}/enable")
    public ResponseEntity<SensorOutput> disableSensor(@PathVariable TSID sensorId){
        Sensor sensor = sensorRepository.findById(new SensorId(sensorId)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        sensor.setEnabled(Boolean.FALSE);
        sensorRepository.saveAndFlush(sensor);
        sensorMonitoringClient.disableMonitoring(sensorId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{sensorId}")
    public ResponseEntity<SensorOutput> deleteSensors(@PathVariable TSID sensorId){
        Sensor sensor = sensorRepository.findById(new SensorId(sensorId)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        sensorMonitoringClient.disableMonitoring(sensorId);
        sensorRepository.delete(sensor);
        return ResponseEntity.noContent().build();
    }
}
