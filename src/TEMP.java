//package com.rivigo.raas.metadata;
//
//import com.rivigo.raas.metadata.dtos.BulkUploadResponseDto;
//import com.rivigo.raas.metadata.dtos.ResponseDto;
//import com.rivigo.raas.metadata.dtos.UserDto;
//import com.rivigo.raas.metadata.entity.mysql.user.Address;
//import com.rivigo.raas.metadata.enums.OwnerType;
//import com.rivigo.raas.metadata.repository.user.OwnerAddressRepository;
//import com.rivigo.raas.metadata.repository.user.OwnerRepository;
//import com.rivigo.raas.metadata.repository.user.UserRepository;
//import com.rivigo.raas.metadata.service.MetadataUtilService;
//import com.rivigo.raas.metadata.service.OwnerService;
//import com.rivigo.raas.metadata.service.impl.MetadataUtilServiceImpl;
//import com.rivigo.raas.metadata.service.impl.OwnerServiceImpl;
//import com.rivigo.raas.metadata.utils.Constants;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.Test;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import static org.mockito.ArgumentMatchers.any;
//import org.mockito.MockitoAnnotations;
//import org.mockito.junit.MockitoJUnitRunner;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.mockito.Mockito.when;
//
//@Slf4j
//@RunWith(MockitoJUnitRunner.Silent.class)
//public class OwnerServiceTest {
//
//    @InjectMocks OwnerServiceImpl ownerServiceImpl;
//    @Mock private MetadataUtilService metadataUtilService;
//    @Mock private UserRepository userRepository;
//    @Mock private OwnerRepository ownerRepository;
//    @Mock private OwnerAddressRepository ownerAddressRepository;
//
//    @BeforeEach
//    public void setup() {
//        MockitoAnnotations.initMocks(this);
//    }
//
//    @Test
//    public void createOwnerDtoTest() throws Exception {
////        doReturn(getUserDto()).when(metadataUtilService).uploa
//
//        when(metadataUtilService.uploadUserDtoFromExcel(any())).thenReturn(getUserDto());
//        when(userRepository.existsByPhoneNumberAndIsActiveTrue(any())).thenReturn(false);
//        when(ownerAddressRepository.existsByLine1AndLine2AndPincodeAndIsActiveTrue(userDto.getLine1(),
//                userDto.getLine2(), userDto.getPincode()).thenReturn(false);
//        when(userRepository.findByPhoneNumberAndIsActiveTrue(userDto.getPhoneNumber()).thenReturn(userDto);
//        when()
//        BulkUploadResponseDto responseDto = ownerServiceImpl.createOwnerDto(any());
//        log.info(String.valueOf(responseDto));
//
//    }
//    @Test
//    public void getResponseDtoTest(){
////        doReturn("")
//        ResponseDto responseDto = ownerServiceImpl.getResponseDto(Constants.OWNER);
//
//    }
//
//
//    private List<UserDto> getUserDto(){
//        List<UserDto> userDtoList = new ArrayList<>();
//        UserDto userdto1 = new UserDto();
//        userdto1.setName("name1");
//        userdto1.setPhoneNumber("phone1");
//        userdto1.setLine1("line1");
//        userdto1.setPincode(000000l);
//        userdto1.setCompanyName("RIVIGO");
//        userdto1.setOwnerType(OwnerType.MSME);
//        userdto1.setGstin("false");
//        userdto1.setCity("Delhi");
//
//
//        UserDto userdto2 = new UserDto();
//        userdto2.setName("name2");
//        userdto2.setPhoneNumber("phone2");
//        userdto2.setLine1("line2");
//        userdto2.setPincode(000001l);
//        userdto2.setCompanyName("FLIPKART");
//        userdto2.setOwnerType(OwnerType.MSME);
//        userdto2.setGstin("false");
//        userdto2.setCity("Mumbai");
//
//        userDtoList.add(userdto1);
//        userDtoList.add(userdto2);
//        return userDtoList;
//
//    }
//
//}
