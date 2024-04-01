package mainpackage.carsharingapp.repository;

import lombok.RequiredArgsConstructor;
import mainpackage.carsharingapp.dto.RentalSearchParameters;
import mainpackage.carsharingapp.model.Rental;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RentalSpecificationBuilder implements SpecificationBuilder<Rental> {
    private static final String IS_ACTIVE_KEY = "isActive";
    private static final String USER_ID_KEY = "userId";
    private final SpecificationProviderManager<Rental> rentalSpecificationProviderManager;

    @Override
    public Specification<Rental> build(RentalSearchParameters rentalSearchParameters) {
        Specification<Rental> spec = Specification.where(null);
        if (rentalSearchParameters.isActives() != null
                && rentalSearchParameters.isActives().length > 0) {
            spec = spec.and(rentalSpecificationProviderManager
                    .getSpecificationProvider(IS_ACTIVE_KEY).getSpecification(
                            rentalSearchParameters.isActives()));
        }
        if (rentalSearchParameters.usersId() != null
                && rentalSearchParameters.usersId().length > 0) {
            spec = spec.and(rentalSpecificationProviderManager
                    .getSpecificationProvider(USER_ID_KEY).getSpecification(
                            rentalSearchParameters.usersId()));
        }
        return spec;
    }
}
