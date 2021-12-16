//package com.rivigo.raas.trip_management.service.impl;
//
//import com.rivigo.raas.trip_management.dto.DetailedTripDTO;
//import com.rivigo.raas.trip_management.dto.TripAuditDTO;
//import com.rivigo.raas.trip_management.dto.TripDTO;
//import com.rivigo.raas.trip_management.dto.TripLiteCreationDTO;
//import com.rivigo.raas.trip_management.dto.TripOrderRequestDTO;
//import com.rivigo.raas.trip_management.entity.mysql.trip.Trip;
//import com.rivigo.raas.trip_management.entity.mysql.trip.TripAudit;
//import com.rivigo.raas.trip_management.enums.TripStatus;
//import com.rivigo.raas.trip_management.exception.RaasTripManagementException;
//import com.rivigo.raas.trip_management.repository.mysql.TripRepository;
//import com.rivigo.raas.trip_management.service.SequenceService;
//import com.rivigo.raas.trip_management.service.TripAuditService;
//import com.rivigo.raas.trip_management.service.TripService;
//import com.rivigo.raas.trip_management.service.converter.TripConverterService;
//import com.rivigo.raas.trip_management.utils.Constants;
//import com.rivigo.raas.trip_management.utils.SequenceUtils;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor(onConstructor = @__(@Autowired))
//public class TripServiceImpl implements TripService {
//    private final TripRepository tripRepository;
//    private final TripAuditService tripAuditService;
//    private final TripConverterService tripConverterService;
//    private final SequenceService sequenceService;
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
//        //TODO Adapt trip for tripDTO
//        return createTrip(tripDTO);
//    }
//
//    @Override
//    @Transactional
//    public TripDTO updatePrimaryPilot(Long tripId, String primaryPilot) {
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
//    public DetailedTripDTO viewTripByOrderId(String orderId) {
//        TripDTO tripDTO = tripConverterService.convertFrom(
//                tripRepository.findByOrderIdAndIsActiveIsTrue(orderId).orElseThrow(() ->
//                        new RaasTripManagementException("No trip against given order id")));
//        DetailedTripDTO detailedTripDTO = new DetailedTripDTO();
//        BeanUtils.copyProperties(tripDTO, detailedTripDTO);
//        //TODO Set user details
//        List<TripAudit> tripAudits = tripAuditService.getAuditTrail(tripDTO.getCode());
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
//    @Override
//    public List<TripDTO> viewByOrderIdAndOptionalStatus(TripOrderRequestDTO tripOrderRequestDTO) {
//        List<Trip> trips;
//        if (tripOrderRequestDTO.getTripStatus() != null) {
//            trips = tripRepository.findAllByOrderIdInAndStatusAndIsActiveIsTrue(tripOrderRequestDTO.getOrderId(), tripOrderRequestDTO.getTripStatus());
//        } else {
//            trips = tripRepository.findAllByOrderIdInAndIsActiveIsTrue(tripOrderRequestDTO.getOrderId());
//        }
//        //TODO fetch and populate pilot/supplier details
//        return trips.stream().map(tripConverterService::convertFrom)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    @Transactional
//    public void updateTripStatus(Long tripId, TripStatus newStatus) {
//        Trip trip = tripRepository.findById(tripId)
//                .orElseThrow(() -> new RaasTripManagementException("Invalid trip Id"));
//        validateTripStatusChange(trip.getStatus(), newStatus);
//        updateTripStatus(trip, newStatus);
//    }
//
//    @Override
//    public void cancelTrip(Long tripId) {
//        Trip trip = tripRepository.findById(tripId)
//                .orElseThrow(() -> new RaasTripManagementException("Invalid trip Id"));
//        updateTripStatus(trip, TripStatus.CANCELLED);
//
//    }
//
//    @Override
//    public void cancelOrderTrip(String orderId) {
//        Trip trip = tripRepository.findByOrderIdAndIsActiveIsTrue(orderId)
//                .orElseThrow(() -> new RaasTripManagementException("Invalid order Id"));
//        updateTripStatus(trip, TripStatus.CANCELLED);
//
//    }
//
//    // Validates that the last valid status is current status and order is maintained
//    private void validateTripStatusChange(TripStatus original, TripStatus newStatus) {
//        if (original.getOrder() >= newStatus.getOrder()) {
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
//    private void updateTripStatus(Trip trip, TripStatus newStatus) {
//        createTripAudit(trip.getCode(), trip.getStatus(), newStatus);
//        trip.setStatus(newStatus);
//        tripRepository.save(trip);
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
//    private Long startTimeInTrail(TripStatus tripStatus, List<TripAudit> tripAudits) {
//        return tripAudits.stream()
//                .filter(tripAudit ->
//                        tripAudit.getToStatus().equals(tripStatus))
//                .map(TripAudit::getCreatedTimestamp)
//                .findFirst().orElse(null);
//    }
//
//    private Long endTimeInTrail(TripStatus tripStatus, List<TripAudit> tripAudits) {
//        return tripAudits.stream()
//                .filter(tripAudit ->
//                        tripAudit.getFromStatus().equals(tripStatus))
//                .map(TripAudit::getCreatedTimestamp)
//                .findFirst().orElse(null);
//    }
//
//}
