//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
//package com.rivigo.raas.trip_management.service.impl;
//
//        import com.raas.common.dto.OrderCumulatedChargeDto;
//        import com.raas.demand.client.service.InternalClient;
//        import com.raas.enums.TripStatusEnum;
//        import com.rivigo.raas.billing.enums.ChargeType;
//        import com.rivigo.raas.metadata.client.service.CompanyClient;
//        import com.rivigo.raas.metadata.client.service.UserClient;
//        import com.rivigo.raas.metadata.client.service.VehicleClient;
//        import com.rivigo.raas.metadata.dtos.NodeDto;
//        import com.rivigo.raas.metadata.dtos.ODInfoDto;
//        import com.rivigo.raas.metadata.dtos.VehicleDto;
//        import com.rivigo.raas.metadata.dtos.company.response.CompanyCreateOrUpdateResponseDto;
//        import com.rivigo.raas.metadata.dtos.company.response.ConsignorAddressResponseDetailDto;
//        import com.rivigo.raas.metadata.dtos.user.UserDetailDto;
//        import com.rivigo.raas.metadata.dtos.user.UserRequestDto;
//        import com.rivigo.raas.metadata.enums.UserRoleTypeEnum;
//        import com.rivigo.raas.metadata.enums.UserSystem;
//        import com.rivigo.raas.order.metadata.OrderClient;
//        import com.rivigo.raas.order.metadata.dto.OrderBookingIdRequest;
//        import com.rivigo.raas.order.metadata.dto.OrderDto;
//        import com.rivigo.raas.raas_auth.context.RaasUserContext;
//        import com.rivigo.raas.trip_management.dto.DetailedTripDTO;
//        import com.rivigo.raas.trip_management.dto.DetailedTripDtoByVehicle;
//        import com.rivigo.raas.trip_management.dto.FeedAddressDto;
//        import com.rivigo.raas.trip_management.dto.LocationDto;
//        import com.rivigo.raas.trip_management.dto.RelaySectionDto;
//        import com.rivigo.raas.trip_management.dto.SupplyFeedDto;
//        import com.rivigo.raas.trip_management.dto.SupplyFeedRequestDto;
//        import com.rivigo.raas.trip_management.dto.SupplyFeedResponseDto;
//        import com.rivigo.raas.trip_management.dto.SupplyRequestDto;
//        import com.rivigo.raas.trip_management.dto.SupplyTripStatus;
//        import com.rivigo.raas.trip_management.dto.TripAuditDTO;
//        import com.rivigo.raas.trip_management.dto.TripDTO;
//        import com.rivigo.raas.trip_management.dto.TripLiteCreationDTO;
//        import com.rivigo.raas.trip_management.dto.TripOrderRequestDTO;
//        import com.rivigo.raas.trip_management.entity.mysql.trip.Trip;
//        import com.rivigo.raas.trip_management.entity.mysql.trip.TripAudit;
//        import com.rivigo.raas.trip_management.enums.TripStatus;
//        import com.rivigo.raas.trip_management.exception.RaasTripManagementException;
//        import com.rivigo.raas.trip_management.repository.mysql.TripRepository;
//        import com.rivigo.raas.trip_management.service.ChronosService;
//        import com.rivigo.raas.trip_management.service.SequenceService;
//        import com.rivigo.raas.trip_management.service.TripAuditService;
//        import com.rivigo.raas.trip_management.service.TripService;
//        import com.rivigo.raas.trip_management.service.converter.TripConverterService;
//        import com.rivigo.raas.trip_management.utils.Constants;
//        import com.rivigo.raas.trip_management.utils.SequenceUtils;
//        import com.vyom.athena.base.dto.GenericResponseDto;
//        import com.vyom.athena.transport.exception.TransportException;
//        import lombok.RequiredArgsConstructor;
//        import lombok.extern.slf4j.Slf4j;
//        import org.springframework.beans.BeanUtils;
//        import org.springframework.beans.factory.annotation.Autowired;
//        import org.springframework.data.domain.Page;
//        import org.springframework.data.domain.PageRequest;
//        import org.springframework.data.domain.Pageable;
//        import org.springframework.stereotype.Service;
//        import org.springframework.transaction.annotation.Transactional;
//        import org.springframework.util.CollectionUtils;
//
//        import java.math.BigDecimal;
//        import java.util.ArrayList;
//        import java.util.Arrays;
//        import java.util.HashMap;
//        import java.util.List;
//        import java.util.Map;
//        import java.util.Optional;
//        import java.util.function.Function;
//        import java.util.stream.Collectors;
//
//        import static com.rivigo.raas.trip_management.utils.Constants.PHONE_NUMBER_REGEX;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor(onConstructor = @__(@Autowired))
//public class TripServiceImpl implements TripService {
//    private final TripRepository tripRepository;
//    private final TripAuditService tripAuditService;
//    private final TripConverterService tripConverterService;
//    private final SequenceService sequenceService;
//    private final OrderClient orderClient;
//    private final InternalClient internalClient;
//    private final CompanyClient companyClient;
//    private final UserClient userClient;
//    private final VehicleClient vehicleClient;
//    private final ChronosService chronosService;
//    private Integer SUCCESS_CODE = 0;
//
//    @Override
//    @Transactional
//    public TripDTO createTrip(TripDTO tripDTO) {
//        validateDTO(tripDTO);
//        Trip trip = tripConverterService.convertTo(tripDTO);
//        int sequence = sequenceService.getAndUpdateNextSequence("TRIP");
//        trip.setCode(String.format(Constants.TRIP_CODE_FORMAT, SequenceUtils.bitShuffle(sequence)));
//        createTripAudit(trip.getCode(), trip.getStatus(), TripStatus.VEHICLE_ASSIGNED);
//        trip.setStatus(TripStatus.VEHICLE_ASSIGNED);
//        return tripConverterService.convertFrom(tripRepository.save(trip));
//    }
//
//    @Override
//    public TripDTO createTripWithLiteDTO(TripLiteCreationDTO tripLiteCreationDTO) {
//        TripDTO tripDTO = new TripDTO();
//        tripDTO.setVehicleNumber(tripLiteCreationDTO.getVehicleNumber());
//        tripDTO.setOrderId(tripLiteCreationDTO.getOrderId());
//        try {
//            VehicleDto vehicleDto = vehicleClient.getVehicleAndSupplierInfoByVehicleNumber(
//                    tripLiteCreationDTO.getVehicleNumber());
//            tripDTO.setPrimaryPilotId(vehicleDto.getPilotPhoneNumber());
//            tripDTO.setSupplierId(vehicleDto.getOwnerPhoneNumber());
//            OrderDto orderDto = orderClient.getOrderByOrderBookingId(tripLiteCreationDTO.getOrderId());
//            tripDTO.setClientCode(orderDto.getCompanyId().toString());
//            tripDTO.setOriginCode(orderDto.getSourceConsignorLaneCode());
//            tripDTO.setDestinationCode(orderDto.getDestinationConsignorLaneCode());
//            tripDTO.setOriginWarehouseId(orderDto.getSourceConsignorAddressId().toString());
//            tripDTO.setDestinationWarehouseId(orderDto.getDestinationConsignorAddressId().toString());
//            ODInfoDto odInfoDto = vehicleClient.getRouteDetails(tripDTO.getOriginCode(), tripDTO.getDestinationCode());
//            tripDTO.setSystemDistance(BigDecimal.valueOf(odInfoDto.getDistance()));
//            tripDTO.setSystemTAT(Optional.ofNullable(odInfoDto.getTat()).orElse(0L).intValue());
//            tripDTO.setRelaySections(odInfoDto.getRelayPS());
//            tripDTO.setRouteSections(odInfoDto.getRoute());
//            return createTrip(tripDTO);
//        } catch (TransportException e) {
//            log.error("Transport exception in creating trip ", e);
//            throw new RaasTripManagementException("Could not create trip");
//        }
//    }
//
//    @Override
//    @Transactional
//    public TripDTO updatePrimaryPilot(Long tripId, String primaryPilot) {
//        if(!primaryPilot.matches(PHONE_NUMBER_REGEX)){
//            throw new RaasTripManagementException("Invalid Phone Number");
//        }
//        Trip trip = getTripById(tripId).orElseThrow(() ->
//                new RaasTripManagementException("Invalid trip id"));
//        trip.setPrimaryPilotId(primaryPilot);
//        //TODO Propagate data to metadata service
//        return tripConverterService.convertFrom(tripRepository.save(trip));
//    }
//
//    @Override
//    public Optional<Trip> getTripById(Long tripId) {
//        return tripRepository.findById(tripId);
//    }
//
//    @Override
//    public TripDTO viewTrip(Long tripId) {
//        return tripConverterService.convertFrom(
//                tripRepository.findById(tripId).orElseThrow(() ->
//                        new RaasTripManagementException("Invalid trip Id")));
//    }
//
//    @Override
//    public List<DetailedTripDtoByVehicle> getLatestTripDtoByVehicleNumberIn(List<String> vehicleNumberList) {
//
//        List<DetailedTripDtoByVehicle> detailedTripDtoByVehicleList = new ArrayList<>();
//
//
//        vehicleNumberList.forEach(vehicleNumber->{
//            List<Trip> tripList = tripRepository.findAllByVehicleNumberAndStatusNotInAndIsActiveIsTrue(vehicleNumber, Arrays.asList(TripStatus.CANCELLED, TripStatus.COMPLETE));
//
//            List<DetailedTripDTO> detailedTripDTOList = new ArrayList<>();
//
//            // a vehcile would be having max 2 trips which are active and not complete(current, future(ASSIGNED))
//            tripList.forEach(trip -> {
//                TripDTO tripDTO = tripConverterService.convertFrom(trip);
//                DetailedTripDTO detailedTripDTO = populateDetailedDTO(tripDTO, tripAuditService.getAuditTrail(tripDTO.getCode()));
//                detailedTripDTOList.add(detailedTripDTO);
//            });
//
//            DetailedTripDtoByVehicle detailedTripDtoByVehicle = new DetailedTripDtoByVehicle();
//            detailedTripDtoByVehicle.setVehicleNumber(vehicleNumber);
//            detailedTripDtoByVehicle.setDetailedTripDTOList(detailedTripDTOList);
//            detailedTripDtoByVehicleList.add(detailedTripDtoByVehicle);
//
//        });
//        return detailedTripDtoByVehicleList;
//    }
//
//    @Override
//    public DetailedTripDTO viewTripByOrderId(String orderId) {
//        TripDTO tripDTO = tripConverterService.convertFrom(
//                tripRepository.findByOrderIdAndIsActiveIsTrue(orderId).orElseThrow(() ->
//                        new RaasTripManagementException("No trip against given order id")));
//        return populateDetailedDTO(tripDTO, tripAuditService.getAuditTrail(tripDTO.getCode()));
//    }
//
//    @Override
//    public List<DetailedTripDTO> viewByOrderIdAndOptionalStatus(TripOrderRequestDTO tripOrderRequestDTO) {
//        List<Trip> trips;
//        if (tripOrderRequestDTO.getTripStatus() != null) {
//            trips = tripRepository.findAllByOrderIdInAndStatusAndIsActiveIsTrue(tripOrderRequestDTO.getOrderId(), tripOrderRequestDTO.getTripStatus());
//        } else {
//            trips = tripRepository.findAllByOrderIdInAndIsActiveIsTrue(tripOrderRequestDTO.getOrderId());
//        }
//        List<TripAudit> tripAudits = tripAuditService.getAuditTrail(trips.stream().map(Trip::getCode).collect(Collectors.toList()));
//        Map<String, List<TripAudit>> tripAuditMap = tripAudits.stream().collect(Collectors.groupingBy(TripAudit::getCode));
//
//        return trips.stream().map(tripConverterService::convertFrom)
//                .map(tripDTO -> populateDetailedDTO(tripDTO, tripAuditMap.get(tripDTO.getCode())))
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    @Transactional
//    public void updateTripStatus(Long tripId, TripStatus newStatus) {
//        Trip trip = tripRepository.findById(tripId)
//                .orElseThrow(() -> new RaasTripManagementException("Invalid trip Id"));
//        validateTripStatusChange(trip.getStatus(), newStatus);
//        validateLoadingUnloadingEnd(trip.getOrderId(), newStatus);
//        updateTripStatus(trip, newStatus);
//    }
//
//    @Override
//    public TripDTO updateTripStatusBasedOnCode(String tripCode, TripStatus newStatus) {
//        Trip trip = tripRepository.findByCodeAndIsActiveIsTrue(tripCode)
//                .orElseThrow(() -> new RaasTripManagementException("Invalid trip code"));
//        validateTripStatusChange(trip.getStatus(), newStatus);
//        validateLoadingUnloadingEnd(trip.getOrderId(), newStatus);
//        return tripConverterService.convertFrom(updateTripStatus(trip, newStatus));
//    }
//
//    @Override
//    public void cancelTrip(Long tripId) {
//        Trip trip = tripRepository.findById(tripId)
//                .orElseThrow(() -> new RaasTripManagementException("Invalid trip Id"));
//        validateTripCancelAllowed(trip);
//        updateTripStatus(trip, TripStatus.CANCELLED);
//
//    }
//
//    @Override
//    public void cancelOrderTrip(String orderId) {
//        Trip trip = tripRepository.findByOrderIdAndIsActiveIsTrue(orderId)
//                .orElseThrow(() -> new RaasTripManagementException("Invalid order Id"));
//        validateTripCancelAllowed(trip);
//        updateTripStatus(trip, TripStatus.CANCELLED);
//
//    }
//
//    // Validates that the last valid status is current status and order is maintained
//    private void validateTripStatusChange(TripStatus original, TripStatus newStatus) {
//        if (original.getOrder() >= newStatus.getOrder() || TripStatus.CANCELLED.equals(newStatus)) {
//            throw new RaasTripManagementException("Invalid State Transition");
//        }
//        int lastValidStatusOrder = Arrays.stream(TripStatus.values()).filter(
//                tripStatus -> tripStatus.getOrder() < newStatus.getOrder()
//        ).mapToInt(TripStatus::getOrder).max().orElseThrow(() -> new RaasTripManagementException("No previous state found"));
//        if (lastValidStatusOrder != original.getOrder()) {
//            throw new RaasTripManagementException("Invalid State Transition");
//        }
//    }
//
//    private void validateLoadingUnloadingEnd(String orderId, TripStatus tripStatus) {
//        TripStatusEnum tripStatusEnum;
//        if (TripStatus.LOADING_COMPLETE.equals(tripStatus)) {
//            tripStatusEnum = TripStatusEnum.LOADING_END;
//        } else if (TripStatus.UNLOADING_COMPLETE.equals(tripStatus)) {
//            tripStatusEnum = TripStatusEnum.UNLOADING_END;
//        } else {
//            return;
//        }
//        try {
//            if (!handleResponse(internalClient.validateTripStatusForOrder(orderId, tripStatusEnum)).isAllowed()) {
//                throw new RaasTripManagementException("Order docs/charges not closed yet");
//            }
//        } catch (TransportException e) {
//            log.error("Error occurred ", e);
//            throw new RaasTripManagementException("Could not confirm with demand. Try again later");
//        }
//    }
//
//    private void validateTripCancelAllowed(Trip trip) {
//        if (trip.getStatus().getOrder() >= TripStatus.LOADING_COMPLETE.getOrder()) {
//            throw new RaasTripManagementException("Can't cancel loaded order");
//        }
//    }
//
//    private Trip updateTripStatus(Trip trip, TripStatus newStatus) {
//        createTripAudit(trip.getCode(), trip.getStatus(), newStatus);
//        trip.setStatus(newStatus);
//        return tripRepository.save(trip);
//    }
//
//    private void createTripAudit(String tripCode, TripStatus fromStatus, TripStatus toStatus) {
//        tripAuditService.createTripAudit(TripAuditDTO.builder()
//                .fromStatus(fromStatus)
//                .toStatus(toStatus)
//                .code(tripCode)
//                .build());
//    }
//
//    private void validateDTO(TripDTO tripDTO) {
//        if (tripRepository.findByOrderIdAndIsActiveIsTrue(tripDTO.getOrderId()).isPresent()) {
//            throw new RaasTripManagementException("Trip already present against order id");
//        }
//    }
//
//    @Override
//    public SupplyFeedResponseDto getSupplyFeedTrip(SupplyFeedRequestDto supplyFeedRequestDto) throws TransportException {
//
//        if(RaasUserContext.current().getContactNumber() == null) throw new RaasTripManagementException("User Context Not Obtained");
//
//        UserDetailDto supplier = getUserDetails(RaasUserContext.current().getContactNumber());
//
//        int ownerFlag = 0;
//        if (supplier.getUserRoleTypeEnumList().contains(UserRoleTypeEnum.OWNER))
//            ownerFlag = 1;
//
//        List<SupplyFeedDto> supplyFeedDtosList = new ArrayList<>();
//
//        // If ONGOING / CLOSED / OTHER ONGOING
//        List<TripStatus> tripOngoingStatusList = new ArrayList<>();
//        List<TripStatus> tripClosedStatusList = new ArrayList<>();
//        Arrays.asList(TripStatus.values()).forEach(tripStatus -> {
//            if (!tripStatus.name().equals(TripStatus.COMPLETE.name()) && !tripStatus.name().equals(TripStatus.CANCELLED.name()))
//                tripOngoingStatusList.add(tripStatus);
//            else tripClosedStatusList.add(tripStatus);
//        });
//
//        List<TripStatus> actualTripStatus;
//        if (supplyFeedRequestDto.getSupplyTripStatus().equals(SupplyTripStatus.CLOSED))
//            actualTripStatus = tripClosedStatusList;
//        else if (supplyFeedRequestDto.getSupplyTripStatus().equals(SupplyTripStatus.PILOT_TRIP) ||
//                supplyFeedRequestDto.getSupplyTripStatus().equals(SupplyTripStatus.OWNER_PILOT_TRIPS))
//            actualTripStatus = tripOngoingStatusList;
//        else throw new RaasTripManagementException("Correct Supply Trip Status Not Provided");
//
//
//        Page<Trip> tripList = null;
//
//        Pageable pageRequest = PageRequest.of(supplyFeedRequestDto.getPageNumber() - 1, supplyFeedRequestDto.getPageSize());
//        if(supplyFeedRequestDto.getSupplyTripStatus().equals(SupplyTripStatus.PILOT_TRIP) ||
//                (supplyFeedRequestDto.getSupplyTripStatus().equals(SupplyTripStatus.CLOSED) && (ownerFlag != 1)))
//        {
//            tripList = tripRepository.findByPrimaryPilotIdAndStatusInAndIsActiveIsTrue(RaasUserContext.current().getContactNumber(),
//                    actualTripStatus, pageRequest);
//            for(Trip trip : tripList) {
//                SupplyFeedDto supplyFeedDto = new SupplyFeedDto();
//                OrderDto orderDto = orderClient.getOrderByOrderBookingId(trip.getOrderId());
////                addSupplyFeedDetails(supplyFeedDto, trip, orderDto);
//                supplyFeedDto.setStatus(trip.getStatus());
//                // as this case involves Primary Pilot OR Owner Pilot
//                supplyFeedDto.setPrimaryPilotName(supplier.getName());
//                supplyFeedDto.setPrimaryPilotPhone(supplier.getPhoneNumber());
//                if(supplyFeedRequestDto.getSupplyTripStatus().equals(SupplyTripStatus.CLOSED)){
//                    setLoadindUnloadingTime(trip.getOrderId(), supplyFeedDto);
//                }
//                supplyFeedDtosList.add(supplyFeedDto);
//            }
//        }
//
//        else if(supplyFeedRequestDto.getSupplyTripStatus().equals(SupplyTripStatus.OWNER_PILOT_TRIPS) ||
//                (supplyFeedRequestDto.getSupplyTripStatus().equals(SupplyTripStatus.CLOSED) && (ownerFlag == 1))){
//            tripList = tripRepository.findBySupplierIdAndStatusInAndIsActiveIsTrue(RaasUserContext.current().getContactNumber(),
//                    actualTripStatus, pageRequest);
//            for(Trip trip : tripList) {
//                SupplyFeedDto supplyFeedDto = new SupplyFeedDto();
//                OrderDto orderDto = orderClient.getOrderByOrderBookingId(trip.getOrderId());
////                addSupplyFeedDetails(supplyFeedDto, trip, orderDto);
//                supplyFeedDto.setStatus(trip.getStatus());
//                // as this case involves Primary Pilot OR Owner Pilot
//                UserDetailDto userdto = getUserDetails(trip.getPrimaryPilotId());
//                supplyFeedDto.setPrimaryPilotName(userdto.getName());
//                supplyFeedDto.setPrimaryPilotPhone(userdto.getPhoneNumber());
//                if(supplyFeedRequestDto.getSupplyTripStatus().equals(SupplyTripStatus.CLOSED)){
//                    setLoadindUnloadingTime(trip.getOrderId(), supplyFeedDto);
//                }
//                supplyFeedDtosList.add(supplyFeedDto);
//            }
//        }
//
//        SupplyFeedResponseDto supplyFeedResponseDto = new SupplyFeedResponseDto();
//        supplyFeedResponseDto.setSupplyFeedDtoList(supplyFeedDtosList);
////        if (CollectionUtils.isEmpty(tripList)) {
////
////            throw new RaasTripManagementException("")
////        }
//
//        // TODO Change this
//        assert tripList != null;
//        supplyFeedResponseDto.setTotalElements(tripList.getTotalElements());
//        return supplyFeedResponseDto;
//    }
//
//    private void setLoadindUnloadingTime(String orderId, SupplyFeedDto supplyFeedDto) {
//        DetailedTripDTO detailedTripDTO = viewTripByOrderId(orderId);
//        supplyFeedDto.setUnLoadingTime(detailedTripDTO.getLoadingEndTime()-detailedTripDTO.getLoadingStartTime());
//        supplyFeedDto.setLoadingTime(detailedTripDTO.getUnloadingEndTime()-detailedTripDTO.getUnloadingStartTime());
//    }
//
//    @Override
//    public SupplyFeedResponseDto getSupplyFeedTripTest(SupplyRequestDto supplyRequestDto) throws TransportException {
//
//        UserDetailDto supplier = getUserDetails(supplyRequestDto.getSupplierId());
//
//        int ownerFlag = 0;
//        if (supplier.getUserRoleTypeEnumList().contains(UserRoleTypeEnum.OWNER))
//            ownerFlag = 1;
//
//        List<SupplyFeedDto> supplyFeedDtosList = new ArrayList<>();
//
//        // If ONGOING / CLOSED / OTHER ONGOING
//        List<TripStatus> tripOngoingStatusList = new ArrayList<>();
//        List<TripStatus> tripClosedStatusList = new ArrayList<>();
//        Arrays.asList(TripStatus.values()).forEach(tripStatus -> {
//            if (!tripStatus.name().equals(TripStatus.COMPLETE.name()) && !tripStatus.name().equals(TripStatus.CANCELLED.name()))
//                tripOngoingStatusList.add(tripStatus);
//            else tripClosedStatusList.add(tripStatus);
//        });
//
//        List<TripStatus> actualTripStatus;
//        if (supplyRequestDto.getSupplyTripStatus().equals(SupplyTripStatus.CLOSED))
//            actualTripStatus = tripClosedStatusList;
//        else if (supplyRequestDto.getSupplyTripStatus().equals(SupplyTripStatus.PILOT_TRIP) ||
//                supplyRequestDto.getSupplyTripStatus().equals(SupplyTripStatus.OWNER_PILOT_TRIPS))
//            actualTripStatus = tripOngoingStatusList;
//        else throw new RaasTripManagementException("Correct Supply Trip Status Not Provided");
//
//
//        Page<Trip> tripList = null;
//
//        Pageable pageRequest = PageRequest.of(supplyRequestDto.getPageNumber()-1, supplyRequestDto.getPageSize());
//        if(supplyRequestDto.getSupplyTripStatus().equals(SupplyTripStatus.PILOT_TRIP) ||
//                (supplyRequestDto.getSupplyTripStatus().equals(SupplyTripStatus.CLOSED) && (ownerFlag != 1))) {
//            tripList = tripRepository.findByPrimaryPilotIdAndStatusInAndIsActiveIsTrue(supplyRequestDto.getSupplierId(),
//                    actualTripStatus, pageRequest);
//        }
////
////            Map<String, OrderDto> tripOrderMap = createTripOrderMap(tripList);
////            Map<Integer, ConsignorAddressResponseDetailDto> consignorAddressMap = createConsignorAddressMap(tripOrderMap);
////            Map<String, UserDetailDto> userMap = createUserMap(tripList);
////            Map<String, LocationDto> vehicleLocationMap = createLocationMap(tripList);
//////            Map<Integer, CompanyCreateOrUpdateResponseDto> companyMap = createCompanyMap(tripOrderMap);
////
////            Map<String, ConsignorAddressResponseDetailDto> cosignorAddressmap;
////            for(Trip trip : tripList) {
////                SupplyFeedDto supplyFeedDto = new SupplyFeedDto();
////                OrderDto orderDto = tripOrderMap.get(trip.getOrderId());
////                addSupplyFeedDetails(consignorAddressMap, vehicleLocationMap, supplyFeedDto, trip, orderDto);
////                supplyFeedDto.setStatus(trip.getStatus());
////                // as this case involves Primary Pilot OR Owner Pilot
////                if(supplyRequestDto.getSupplyTripStatus().equals(SupplyTripStatus.CLOSED)){
////                    setLoadindUnloadingTime(trip.getOrderId(), supplyFeedDto);
////                }
////                addPilotDetails(supplyFeedDto, supplier);
////                supplyFeedDtosList.add(supplyFeedDto);
////            }
////        }
//
//        else if(supplyRequestDto.getSupplyTripStatus().equals(SupplyTripStatus.OWNER_PILOT_TRIPS) ||
//                (supplyRequestDto.getSupplyTripStatus().equals(SupplyTripStatus.CLOSED) && (ownerFlag == 1))){
//            tripList = tripRepository.findBySupplierIdAndStatusInAndIsActiveIsTrue(supplyRequestDto.getSupplierId(),
//                    actualTripStatus, pageRequest);}
//
//
//        Map<String, OrderDto> tripOrderMap = createTripOrderMap(tripList);
//        Map<Integer, ConsignorAddressResponseDetailDto> consignorAddressMap = createConsignorAddressMap(tripOrderMap);
//        Map<String, UserDetailDto> userMap = createUserMap(tripList);
//        Map<String, LocationDto> vehicleLocationMap = createLocationMap(tripList);
//        Map<Integer, CompanyCreateOrUpdateResponseDto> companyMap = createCompanyMap(tripOrderMap);
//
//        for(Trip trip : tripList) {
//            SupplyFeedDto supplyFeedDto = new SupplyFeedDto();
//
//            OrderDto orderDto = tripOrderMap.get(trip.getOrderId());
//            addSupplyFeedDetails(consignorAddressMap, vehicleLocationMap, supplyFeedDto, trip, orderDto);
//            supplyFeedDto.setStatus(trip.getStatus());
//            // as this case involves Primary Pilot OR Owner Pilot
//
//            UserDetailDto userDto = userMap.get(trip.getPrimaryPilotId());
//            if(supplyRequestDto.getSupplyTripStatus().equals(SupplyTripStatus.CLOSED)){
//                setLoadindUnloadingTime(trip.getOrderId(), supplyFeedDto);
//            }
//            addPilotDetails(supplyFeedDto, userDto);
//            supplyFeedDtosList.add(supplyFeedDto);
//        }
//
//
//        SupplyFeedResponseDto supplyFeedResponseDto = new SupplyFeedResponseDto();
//        supplyFeedResponseDto.setSupplyFeedDtoList(supplyFeedDtosList);
//        supplyFeedResponseDto.setTotalElements(tripList.getTotalElements());
//        return supplyFeedResponseDto;
//    }
//
//    private void addPilotDetails(SupplyFeedDto supplyFeedDto, UserDetailDto user) {
//
//        // TODO update driver details
//        supplyFeedDto.setDriverName(user.getName());
//        supplyFeedDto.setDriverPhone(user.getPhoneNumber());
//
//        supplyFeedDto.setPrimaryPilotName(user.getName());
//        supplyFeedDto.setPrimaryPilotPhone(user.getPhoneNumber());
//    }
//
//    private SupplyFeedDto addSupplyFeedDetails(Map<Integer, ConsignorAddressResponseDetailDto> consignorAddressMap,
//                                               //Map<Integer, CompanyCreateOrUpdateResponseDto> companyMap,
//                                               Map<String, LocationDto> vehicleMap,
//                                               SupplyFeedDto supplyFeedDto, Trip trip, OrderDto orderDto) throws TransportException {
//
//        supplyFeedDto.setOriginAddressDto(setFeedAddressDto(consignorAddressMap.get(orderDto.getSourceConsignorAddressId())));
//        supplyFeedDto.setDestinationAddressDto(setFeedAddressDto(consignorAddressMap.get(orderDto.getDestinationConsignorAddressId())));
//
//        // TODO update and fix company map issue
//        supplyFeedDto.setCompanyName(handleResponse(companyClient.getCompany(orderDto.getCompanyId())).getCompanyName());
//        supplyFeedDto.setVehicleNumber(trip.getVehicleNumber());
//
//        // TODO change this when order_detail table is modified so that vehicle type is present - eliminate extra call
//        supplyFeedDto.setVehicleType(vehicleClient.getVehicleAndSupplierInfoByVehicleNumber(
//                trip.getVehicleNumber()).getVehicleType());
//        supplyFeedDto.setTripId(trip.getCode());
//        supplyFeedDto.setRouteSections(getRouteList(trip.getTripSectionList()));
//        supplyFeedDto.setRelaySections(getRelaySections(trip.getRelaySectionList()));
//
//        // TODO - ask if better method to do this ?
//        LocationDto locationDto = vehicleMap.get(trip.getVehicleNumber())!=null ? vehicleMap.get(trip.getVehicleNumber()) : new LocationDto();
//        supplyFeedDto.setLatitude(locationDto.getLatitude());
//        supplyFeedDto.setLongitude(locationDto.getLongitude());
//
//        addCharges(supplyFeedDto, trip.getOrderId());
//
//
//        return supplyFeedDto;
//    }
//
//    private void addCharges(SupplyFeedDto supplyFeedDto, String orderId) throws TransportException {
//        List<OrderCumulatedChargeDto> orderCumulatedChargeDtoList = handleResponse(internalClient.getCumulatedCharge(orderId));
//        BigDecimal loadingCharges = BigDecimal.ZERO, unloadingCharges = BigDecimal.ZERO;
//        for(OrderCumulatedChargeDto orderCumulatedChargeDto : orderCumulatedChargeDtoList){
//            orderCumulatedChargeDto.getOrderChargeDto().stream().filter(c ->
//                    c.getChargeType().equals(ChargeType.LOADING)).collect(Collectors.toList()).forEach(
//                    a -> loadingCharges.add(a.getCharge()));
//            orderCumulatedChargeDto.getOrderChargeDto().stream().filter(c ->
//                    c.getChargeType().equals(ChargeType.UNLOADING)).collect(Collectors.toList()).forEach(
//                    a -> unloadingCharges.add(a.getCharge()));
//        }
//        supplyFeedDto.setLoadingCharges(loadingCharges);
//        supplyFeedDto.setUnloadingCharges(unloadingCharges);
//    }
//
//    private void setCharges(SupplyFeedDto supplyFeedDto) {
//
//    }
//
//    private FeedAddressDto setFeedAddressDto(ConsignorAddressResponseDetailDto consignorAddress) {
//        FeedAddressDto feedAddressDto = new FeedAddressDto();
//        feedAddressDto.setName(consignorAddress.getName());
//        feedAddressDto.setSpocs(consignorAddress.getSpocs());
//        BeanUtils.copyProperties(consignorAddress.getAddress(), feedAddressDto);
//        return feedAddressDto;
//    }
//
//    private Map<String, LocationDto> createLocationMap(Page<Trip> tripPage) {
//        List<String> vehicleNumberList = tripPage.stream().map(Trip::getVehicleNumber).collect(Collectors.toList());
//        List<LocationDto> locationDtoList = chronosService.getLatLong(vehicleNumberList);
//        return locationDtoList.stream().collect(Collectors.toMap(LocationDto::getVehicleNumber, Function.identity()));
//    }
//
//    private Map<Integer, CompanyCreateOrUpdateResponseDto> createCompanyMap(Map<String, OrderDto> tripOrderMap) throws TransportException {
//        List<Integer> companyIdList = new ArrayList<>();
//        tripOrderMap.entrySet().forEach(t -> {
//            companyIdList.add(t.getValue().getCompanyId());
//        });
//        List<CompanyCreateOrUpdateResponseDto> companyList = handleResponse(companyClient.getAllCompanies(companyIdList));
//        return companyList.stream().collect(Collectors.toMap(CompanyCreateOrUpdateResponseDto::getCompanyId, Function.identity()));
//    }
//
//    private Map<String, UserDetailDto> createUserMap(Page<Trip> tripPage) throws TransportException {
//        List<String> userId  = tripPage.stream().map(t -> t.getPrimaryPilotId()).collect(Collectors.toList());
//        List<UserDetailDto> userDetailDtoList = handleResponse(userClient.getUserDetailsByPhoneNumber(userId));
//        return userDetailDtoList.stream().collect(Collectors.toMap(UserDetailDto::getPhoneNumber, Function.identity()));
//    }
//
//    private Map<Integer, ConsignorAddressResponseDetailDto> createConsignorAddressMap(Map<String, OrderDto> tripOrderMap)
//            throws TransportException {
//        List<String> consignorAddressIds = new ArrayList<>();
//        tripOrderMap.entrySet().stream().forEach(a -> {
//            consignorAddressIds.add(a.getValue().getDestinationConsignorAddressId().toString());
//            consignorAddressIds.add(a.getValue().getSourceConsignorAddressId().toString());
//        });
//        Integer placeHolder = 1;
//        List<ConsignorAddressResponseDetailDto> consignorAddressResponseDetailDtoList =
//                handleResponse(companyClient.getConsignorAddressList(consignorAddressIds, placeHolder));
//        return consignorAddressResponseDetailDtoList.stream().collect(Collectors.toMap(ConsignorAddressResponseDetailDto::getId,
//                Function.identity()));
//    }
//
//    private Map<String, OrderDto> createTripOrderMap(Page<Trip> tripPage) throws TransportException {
//        List<String> orderBookingIdList = tripPage.stream().map(Trip::getOrderId).collect(Collectors.toList());
//        List<OrderDto> orderDtoList = orderClient.getOrders(new OrderBookingIdRequest(orderBookingIdList));
//        return orderDtoList.stream().collect(Collectors.toMap(OrderDto::getOrderBookingId, Function.identity()));
//    }
//
//    private List<RelaySectionDto> getRelaySections(String relaySectionList) throws TransportException {
//        List<String> relaySections = Arrays.asList(relaySectionList.split(Constants.RELAY_DELIMITER));
//        relaySections = relaySections.stream().map(String::trim).collect(Collectors.toList());
//        List<RelaySectionDto> relayList = new ArrayList<>();
//        for(String section : relaySections)
//        {
//            List<NodeDto> relayNodeList = getRouteList(section);
//            relayList.add(new RelaySectionDto(relayNodeList.get(0), relayNodeList.get(1)));
//        }
//        return relayList;
//    }
//
//    private List<NodeDto> getRouteList(String tripSectionList) throws TransportException {
//        List<String> routeList = Arrays.asList(tripSectionList.split(Constants.ROUTE_DELIMITER));
//        routeList = routeList.stream().map(String::trim).collect(Collectors.toList());
//
//        List<NodeDto> vehicleNodeDto =  vehicleClient.getNodes(routeList).getData();
//        Map<String, NodeDto> map = vehicleNodeDto.stream()
//                .collect(Collectors.toMap(NodeDto::getNodeCode, Function.identity()));
//        return routeList.stream().map(map::get).collect(Collectors.toList());
//    }
//
//    private Long startTimeInTrail(TripStatus tripStatus, List<TripAudit> tripAudits) {
//        return tripAudits.stream()
//                .filter(tripAudit ->
//                        tripStatus.equals(tripAudit.getToStatus()))
//                .map(TripAudit::getCreatedTimestamp)
//                .findFirst().orElse(null);
//    }
//
//    private Long endTimeInTrail(TripStatus tripStatus, List<TripAudit> tripAudits) {
//        return tripAudits.stream()
//                .filter(tripAudit ->
//                        tripStatus.equals(tripAudit.getFromStatus()))
//                .map(TripAudit::getCreatedTimestamp)
//                .findFirst().orElse(null);
//    }
//
//    private DetailedTripDTO populateDetailedDTO(TripDTO tripDTO, List<TripAudit> tripAudits) {
//        DetailedTripDTO detailedTripDTO = new DetailedTripDTO();
//        BeanUtils.copyProperties(tripDTO, detailedTripDTO);
//        detailedTripDTO.setSupplierDetails(getUserDetails(tripDTO.getSupplierId()));
//        detailedTripDTO.setPrimaryPilotDetails(getUserDetails(tripDTO.getPrimaryPilotId()));
//        if (tripDTO.getStatus().getOrder() > TripStatus.LOADING_START.getOrder()) {
//            detailedTripDTO.setLoadingStartTime(startTimeInTrail(TripStatus.LOADING_START, tripAudits));
//        }
//        if (tripDTO.getStatus().getOrder() > TripStatus.LOADING_COMPLETE.getOrder()) {
//            detailedTripDTO.setLoadingEndTime(endTimeInTrail(TripStatus.LOADING_COMPLETE, tripAudits));
//        }
//        if (tripDTO.getStatus().getOrder() > TripStatus.UNLOADING_START.getOrder()) {
//            detailedTripDTO.setUnloadingStartTime(startTimeInTrail(TripStatus.UNLOADING_START, tripAudits));
//        }
//        if (tripDTO.getStatus().getOrder() > TripStatus.UNLOADING_COMPLETE.getOrder()) {
//            detailedTripDTO.setUnloadingEndTime(endTimeInTrail(TripStatus.UNLOADING_COMPLETE, tripAudits));
//        }
//        return detailedTripDTO;
//    }
//
//
//    private UserDetailDto getUserDetails(String phone) {
//        GenericResponseDto<UserDetailDto> responseDto;
//        try {
//            responseDto = userClient.getUserDetailsByPhoneNumberAndSystem(new UserRequestDto(phone,
//                    UserSystem.SUPPLY));
//        } catch (TransportException e) {
//            log.error("Error occurred ", e);
//            throw new RaasTripManagementException("Could not fetch user details");
//        }
//        return handleResponse(responseDto);
//    }
//
//    private <T> T handleResponse(GenericResponseDto<T> genericResponseDto) {
//        if (SUCCESS_CODE.equals(genericResponseDto.getCode())) {
//            return genericResponseDto.getData();
//        } else {
//            throw new RaasTripManagementException(genericResponseDto.getMessage());
//        }
//    }
//
//}{
//}
