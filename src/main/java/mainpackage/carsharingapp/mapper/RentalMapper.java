package mainpackage.carsharingapp.mapper;

import mainpackage.carsharingapp.config.MapperConfig;
import mainpackage.carsharingapp.dto.RentalRequestDto;
import mainpackage.carsharingapp.dto.RentalResponseDto;
import mainpackage.carsharingapp.dto.ReturnRentalResponseDto;
import mainpackage.carsharingapp.model.Rental;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface RentalMapper {
    Rental toEntity(RentalRequestDto rentalRequestDto);

    RentalResponseDto toDto(Rental rental);

    ReturnRentalResponseDto toReturnRentalResponseRentalDto(Rental rental);
}
