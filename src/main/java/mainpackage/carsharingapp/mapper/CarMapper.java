package mainpackage.carsharingapp.mapper;

import mainpackage.carsharingapp.config.MapperConfig;
import mainpackage.carsharingapp.dto.CarRequestDto;
import mainpackage.carsharingapp.dto.CarResponseDto;
import mainpackage.carsharingapp.model.Car;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface CarMapper {
    Car toEntity(CarRequestDto carRequestDto);

    CarResponseDto toDto(Car car);
}
