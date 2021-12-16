//
//UserDetailDto supplier = userClient.getUserDetailsByPhoneNumberAndSystem(new UserRequestDto(supplyRequestDto.getSupplierId(),
//        UserSystem.SUPPLY));
//        int ownerPilotFlag = 0;
//        int ownerFlag = 0;
//        if(supplier.getUserRoleTypeEnumList().contains(UserRoleTypeEnum.OWNER) &&
//        supplier.getUserRoleTypeEnumList().contains(UserRoleTypeEnum.PRIMARY_PILOT))
//        ownerPilotFlag = 1;
//        if(supplier.getUserRoleTypeEnumList().contains(UserRoleTypeEnum.OWNER) &&
//        !supplier.getUserRoleTypeEnumList().contains(UserRoleTypeEnum.PRIMARY_PILOT))
//        ownerFlag = 1;
//
//        List<SupplyFeedDto> supplyFeedDtosList = new ArrayList<>();
//        //List<Trip> tripList = tripRepository.findBySupplierIdAndIsActiveIsTrue(supplyRequestDto.getSupplierId().toString());
//
//        List<VehicleMappingRequestDTO> vehicleMappingRequestDTOList = supplyClientService.getFeedbackListForSupplier(
//        supplyRequestDto.getSupplierId());
//        List<String> orderBookingIds = new ArrayList<>();
//        vehicleMappingRequestDTOList.stream().map(v -> orderBookingIds.add(v.getOrderId()));
//        List<OrderDto> orderDtoList = orderClient.getOrders(new OrderBookingIdRequest(orderBookingIds));
////        List<Trip> tripList = tripRepository.findBySupplierIdAndIsActiveIsTrue(supplyRequestDto.getSupplierId().toString());
//        for (OrderDto orderDto : orderDtoList) {
//        SupplyFeedDto supplyFeedDto = new SupplyFeedDto();
//        VehicleFeedbackStatusDto vehicleFeedbackStatusDto =
//        supplyClientService.getVehicleDetailsFromOrder(orderDto.getOrderBookingId());
//        if (supplyRequestDto.getSupplyTripStatus().equals(SupplyTripStatus.PENDING) &&
//        vehicleFeedbackStatusDto.getFeedback().equals(VehicleFeedbackEnum.PENDING)) {
//        supplyFeedDto = addSupplyFeedDetails(orderDto, vehicleFeedbackStatusDto);
//        // setting routedetails from origin and destination code provided in orderdto
//        GenericResponseDto<ODInfoDto> responseDto = vehicleClient.getRouteDetails(orderDto.getSourceConsignorLaneCode(),
//        orderDto.getDestinationConsignorLaneCode());
//        supplyFeedDto.setRouteSections(responseDto.getData().getRoute());
//        supplyFeedDto.setRelaySections(responseDto.getData().getRelayPS());
//        supplyFeedDto.setExpectedLoadingTime(orderDto.getLoadingTime());
//        supplyFeedDtosList.add(supplyFeedDto);
//        }
//        // Case when a single order/trip will be returned as actions have to be performed on it
//        else if ((supplyRequestDto.getSupplyTripStatus().equals(SupplyTripStatus.ONGOING) && ownerFlag == 0)
//        && (vehicleFeedbackStatusDto.getFeedback().equals(VehicleFeedbackEnum.ACCEPTED))) {
//        Trip trip = tripRepository.findByOrderIdAndIsActiveIsTrue(orderDto.getOrderBookingId()).orElseThrow(
//        () -> new RaasTripManagementException("Trip not found related to orderId")
//        );
//        if (trip.getStatus() == TripStatus.COMPLETE || trip.getStatus() == TripStatus.CANCELLED) continue;
//        supplyFeedDto = addSupplyFeedDetails(orderDto, vehicleFeedbackStatusDto);
//        supplyFeedDto.setTripId(trip.getCode());
//        supplyFeedDto.setRouteSections(trip.getTripSectionList());
//        supplyFeedDto.setRelaySections(trip.getRelaySectionList());
//        supplyFeedDto.setStatus(trip.getStatus());
//        // as this case involves Primary Pilot OR Owner Pilot
//        supplyFeedDto.setPrimaryPilotName(supplier.getName());
//        supplyFeedDto.setPrimaryPilotPhone(supplier.getPhoneNumber());
//        supplyFeedDtosList.add(supplyFeedDto);
//        }
//        // Case when a multiple orders/trips will be returned as its only for viewing (ongoing + onwer and otherongoing + ownerpilot)
//        else if ((supplyRequestDto.getSupplyTripStatus().equals(SupplyTripStatus.ONGOING) && ownerFlag == 1)
//        && (supplyRequestDto.getSupplyTripStatus().equals(SupplyTripStatus.OTHER_ONGOING) && ownerPilotFlag == 1)
//        && (vehicleFeedbackStatusDto.getFeedback().equals(VehicleFeedbackEnum.ACCEPTED))) {
//        Trip trip = tripRepository.findByOrderIdAndIsActiveIsTrue(orderDto.getOrderBookingId()).orElseThrow(
//        () -> new RaasTripManagementException("Trip not found related to orderId")
//        );
//        if (trip.getStatus() == TripStatus.COMPLETE || trip.getStatus() == TripStatus.CANCELLED) continue;
//        supplyFeedDto = addSupplyFeedDetails(orderDto, vehicleFeedbackStatusDto);
//        supplyFeedDto.setTripId(trip.getCode());
//        supplyFeedDto.setRouteSections(trip.getTripSectionList());
//        supplyFeedDto.setRelaySections(trip.getRelaySectionList());
//        supplyFeedDto.setStatus(trip.getStatus());
//        UserDetailDto userdto = userClient.getUserDetailsByPhoneNumberAndSystem(new UserRequestDto(trip.getPrimaryPilotId(),
//        UserSystem.SUPPLY));
//        supplyFeedDto.setPrimaryPilotName(userdto.getName());
//        supplyFeedDto.setPrimaryPilotPhone(userdto.getPhoneNumber());
//        supplyFeedDtosList.add(supplyFeedDto);
//        }
//        else if (supplyRequestDto.getSupplyTripStatus().equals(SupplyTripStatus.CLOSED) &&
//        vehicleFeedbackStatusDto.getFeedback().equals(VehicleFeedbackEnum.ACCEPTED)) {
//        Trip trip = tripRepository.findByOrderIdAndIsActiveIsTrue(orderDto.getOrderBookingId()).orElseThrow(
//        () -> new RaasTripManagementException("Trip not found related to orderId"));
//        if (trip.getStatus() != TripStatus.COMPLETE) continue;
//        supplyFeedDto = addSupplyFeedDetails(orderDto, vehicleFeedbackStatusDto);
//        supplyFeedDto.setTripId(trip.getCode());
//        supplyFeedDto.setRouteSections(trip.getTripSectionList());
//        supplyFeedDto.setRelaySections(trip.getRelaySectionList());
//        supplyFeedDto.setLoadingTime(getLoadingTime(trip));
//        supplyFeedDto.setUnLoadingTime(getUnloadingTime(trip));
//        supplyFeedDto.setActualDistance(trip.getActualDistance());
//        supplyFeedDtosList.add(supplyFeedDto);
//        }
//        else {
//        throw new RaasTripManagementException("Adequate Trip Supply Status not provided");
//        }
//        }