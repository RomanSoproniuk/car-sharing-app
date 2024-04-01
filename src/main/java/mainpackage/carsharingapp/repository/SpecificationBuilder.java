package mainpackage.carsharingapp.repository;

import mainpackage.carsharingapp.dto.RentalSearchParameters;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T> {
    Specification<T> build(RentalSearchParameters rentalSearchParameters);
}
