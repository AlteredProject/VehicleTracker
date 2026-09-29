package app.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class MotorVehicleDTO {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("registration_number")
    private String registrationNumber;
    @JsonProperty("status")
    private String status;
    @JsonProperty("vin")
    private String vin;
    @JsonProperty("type")
    private String type;
    @JsonProperty("make")
    private String make;
    @JsonProperty("model")
    private String model;
    @JsonProperty("variant")
    private String variant;
    @JsonProperty("chassis_type")
    private String chassisType;
    @JsonProperty("model_year")
    private Integer modelYear;
    @JsonProperty("color")
    private String color;
    @JsonProperty("fuel_type")
    private String fuelType;
    @JsonProperty("engine:volume")
    private Double engineVolume;
    @JsonProperty("engine_power")
    private Double enginePower;
}
