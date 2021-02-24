package com.ixyf.mappers;

import com.ixyf.domain.User;
import com.ixyf.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 用来做对象关系映射转化 shabiwanyier
 */
@Mapper()
public interface UserDtoMapper {

    // 获取实例
    UserDtoMapper INSTANCE = Mappers.getMapper(UserDtoMapper.class);

    /**
     * 将entity转化为dto
     * @param source
     * @return
     */
    UserDto convert2Dto(User source);

    /**
     * 将dto转化为entity
     * @param source
     * @return
     */
    User convert2Entity(UserDto source);

    /**
     * 将entity转化为dto list
     * @param source
     * @return
     */
    List<UserDto> convert2Dto(List<User> source);

    /**
     * 将dto转化为entity list
     * @param source
     * @return
     */
    List<User> convert2Entity(List<UserDto> source);
}
