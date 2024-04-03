package mainpackage.carsharingapp.mapper;

import mainpackage.carsharingapp.config.MapperConfig;
import mainpackage.carsharingapp.dto.PaymentResponseDto;
import mainpackage.carsharingapp.model.Payment;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    PaymentResponseDto toDto(Payment payment);
}
