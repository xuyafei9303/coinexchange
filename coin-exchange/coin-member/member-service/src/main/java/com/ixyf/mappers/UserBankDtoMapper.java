package com.ixyf.mappers;

import com.ixyf.domain.UserBank;
import com.ixyf.dto.UserBankDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserBankDtoMapper {

    UserBankDtoMapper INSTANCE = Mappers.getMapper(UserBankDtoMapper.class);

    UserBankDto toConvertDto(UserBank source);

    UserBank toConvertEntity(UserBankDto source);

    List<UserBankDto> toConvertDto(List<UserBank> source);

    List<UserBank> toConvertEntity(List<UserBankDto> source);
}
