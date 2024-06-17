package rw.rca.year3.ne.v1.dtos;

import lombok.Getter;
import lombok.Setter;
import rw.rca.year3.ne.v1.models.LocationAddress;

@Getter
@Setter
public class GetAllLocationsDTO {

    private LocationAddress province;

    private LocationAddress district;

    private LocationAddress sector;

    private LocationAddress cell;

    private LocationAddress village;
}
