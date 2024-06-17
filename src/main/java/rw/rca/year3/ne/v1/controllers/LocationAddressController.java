package rw.rca.year3.ne.v1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rw.rca.year3.ne.v1.dtos.GetAllLocationsDTO;
import rw.rca.year3.ne.v1.enums.ELocationType;
import rw.rca.year3.ne.v1.exceptions.ResourceNotFoundException;
import rw.rca.year3.ne.v1.models.LocationAddress;
import rw.rca.year3.ne.v1.payload.ApiResponse;
import rw.rca.year3.ne.v1.repositories.ILocationAddressRepository;
import rw.rca.year3.ne.v1.services.ILocationAddressService;
import rw.rca.year3.ne.v1.utils.Constants;

@RestController
@RequestMapping("/api/v1/location-address")
@CrossOrigin
public class LocationAddressController {

    private final ILocationAddressRepository locationAddressRepository;
    private final ILocationAddressService locationAddressService;

    @Autowired
    public LocationAddressController(ILocationAddressRepository locationAddressRepository, ILocationAddressService locationAddressService) {
        this.locationAddressRepository = locationAddressRepository;
        this.locationAddressService = locationAddressService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable(value = "id") Long id) {
        LocationAddress locationAddress = this.locationAddressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LocationAddress", "id", id.toString()));

        return ResponseEntity.ok(ApiResponse.success(locationAddress));
    }

    @GetMapping("/all-locations/village/{id}")
    public ResponseEntity<ApiResponse> getAllLocationByVillageId(@PathVariable(value = "id") Long id) {
        GetAllLocationsDTO dto = locationAddressService.findAllLocationByVillageId(id);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }


    @GetMapping
    public Page<LocationAddress> getAll(
            @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int size) {

        Pageable pageable = (Pageable) PageRequest.of(page, size, Sort.Direction.DESC, "name");
        return this.locationAddressRepository.findAll(pageable);
    }

    @GetMapping("/parent-id/{id}")
    public Page<LocationAddress> getAllByParent(@PathVariable(value = "id") Long id,
                                                @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
                                                @RequestParam(value = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int size)
    {

        LocationAddress parentId = this.locationAddressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LocationAddress", " id " , id.toString()));

        Pageable pageable = (Pageable) PageRequest.of(page, size, Sort.Direction.DESC, "name");
        return this.locationAddressRepository.findAllByParentId(parentId,pageable);
    }

    @GetMapping("/provinces")
    public Page<LocationAddress> getAllProvinces(
            @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int size) {

        Pageable pageable = (Pageable) PageRequest.of(page, size, Sort.Direction.DESC, "name");
        return this.locationAddressRepository.findAllByLocationType(ELocationType.PROVINCE,pageable);
    }

    @GetMapping("/districts")
    public Page<LocationAddress> getAllDistricts(
            @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int size) {

        Pageable pageable = (Pageable) PageRequest.of(page, size, Sort.Direction.DESC, "name");
        return this.locationAddressRepository.findAllByLocationType(ELocationType.DISTRICT,pageable);
    }

    @GetMapping("/districts/province/{id}")
    public Page<LocationAddress> getAllDisctrictsByProvince(@PathVariable(value = "id") Long id,
                                                            @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
                                                            @RequestParam(value = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int size) {

        LocationAddress parentId = this.locationAddressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LocationAddress",  "id ", id.toString()));

        Pageable pageable = (Pageable) PageRequest.of(page, size, Sort.Direction.DESC, "name");
        return this.locationAddressRepository.findAllByParentIdAndLocationType(parentId, ELocationType.DISTRICT,pageable);
    }

    @GetMapping("/district/village/{id}")
    public LocationAddress getDistrictOfVillageId(@PathVariable(value = "id") Long id) {
        return this.locationAddressService.findDistrictOfVillageId(id);
    }

    @GetMapping("/district/sector/{id}")
    public LocationAddress getDistrictOfSectorId(@PathVariable(value = "id") Long id) {
        return this.locationAddressService.findDistrictOfSectorId(id);
    }

    @GetMapping("/province/district/{id}")
    public LocationAddress getProvinceOfDistrictId(@PathVariable(value = "id") Long id) {
        return this.locationAddressService.findProvinceOfDistrictId(id);
    }

    @GetMapping("/sectors")
    public Page<LocationAddress> getAllSectors(
            @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int size) {

        Pageable pageable = (Pageable) PageRequest.of(page, size, Sort.Direction.DESC, "name");
        return this.locationAddressRepository.findAllByLocationType(ELocationType.SECTOR,pageable);
    }

    @GetMapping("/sectors/district/{id}")
    public Page<LocationAddress> getAllSectorsByDistrict(@PathVariable(value = "id") Long id,
                                                         @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
                                                         @RequestParam(value = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int size) {

        LocationAddress parentId = this.locationAddressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LocationAddress" , "id", id.toString()));

        Pageable pageable = (Pageable) PageRequest.of(page, size, Sort.Direction.DESC, "name");
        return this.locationAddressRepository.findAllByParentIdAndLocationType( parentId, ELocationType.SECTOR,pageable);
    }

    @GetMapping("/cells")
    public Page<LocationAddress> getAllCells(
            @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int size) {

        Pageable pageable = (Pageable) PageRequest.of(page, size, Sort.Direction.DESC, "name");
        return this.locationAddressRepository.findAllByLocationType( ELocationType.CELL,pageable);
    }

    @GetMapping("/cells/sector/{id}")
    public Page<LocationAddress> getAllCellsBySector(@PathVariable(value = "id") Long id,
                                                     @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
                                                     @RequestParam(value = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int size) {

        LocationAddress parentId = this.locationAddressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LocationAddress",  "id", id.toString()));

        Pageable pageable = (Pageable) PageRequest.of(page, size, Sort.Direction.DESC, "name");
        return this.locationAddressRepository.findAllByParentIdAndLocationType(parentId, ELocationType.CELL,pageable);
    }

    @GetMapping("/villages")
    public Page<LocationAddress> getAllVillages(
            @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int size) {

        Pageable pageable = (Pageable) PageRequest.of(page, size, Sort.Direction.DESC, "name");
        return this.locationAddressRepository.findAllByLocationType( ELocationType.VILLAGE,pageable);
    }

    @GetMapping("/villages/cell/{id}")
    public Page<LocationAddress> getAllVillagesByCell(@PathVariable(value = "id") Long id,
                                                      @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
                                                      @RequestParam(value = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int size) {

        LocationAddress parentId = locationAddressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LocationAddress", "id", id.toString()));

        Pageable pageable = (Pageable) PageRequest.of(page, size, Sort.Direction.DESC, "name");
        return this.locationAddressRepository.findAllByParentIdAndLocationType( parentId, ELocationType.VILLAGE,pageable);
    }

    @GetMapping("/amasibo/by-village/{id}")
    public Page<LocationAddress> getAllAmasiboByVillage(@PathVariable(value = "id") Long id,
                                                        @RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
                                                        @RequestParam(value = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int size) {

        LocationAddress parentId = locationAddressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LocationAddress",  "id", id.toString()));

        Pageable pageable = (Pageable) PageRequest.of(page, size, Sort.Direction.DESC, "name");
        return this.locationAddressRepository.findAllByParentIdAndLocationType(parentId, ELocationType.ISIBO,pageable);
    }



}
