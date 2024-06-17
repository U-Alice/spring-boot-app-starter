package rw.rca.year3.ne.v1.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rw.rca.year3.ne.v1.dtos.GetAllLocationsDTO;
import rw.rca.year3.ne.v1.enums.ELocationType;
import rw.rca.year3.ne.v1.models.LocationAddress;

import java.util.List;
import java.util.Optional;


public interface ILocationAddressService {
    LocationAddress findById(Long id);

    boolean existsById(Long id);

    List<LocationAddress> findAllByParentIdAndLocationType(LocationAddress province, ELocationType district);

    LocationAddress findVillageById(Long id);

    GetAllLocationsDTO findAllLocationByVillageId(Long id);

    LocationAddress findParentById(Long id);

    LocationAddress findCellById(Long id);

    LocationAddress findSectorById(Long id);

    LocationAddress findDistrictById(Long id);

    LocationAddress findProvinceById(Long id);

    LocationAddress findDistrictOfVillageId(Long id);

    LocationAddress findDistrictOfSectorId(Long id);

    LocationAddress findProvinceOfDistrictId(Long id);

    Page<LocationAddress> findAllDistricts(Pageable pageable);

    List<LocationAddress> findAllDistricts();

    Optional<LocationAddress> findVillageFromNames(String village, String cell, String sector, String district);

}
