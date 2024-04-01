package mainpackage.carsharingapp.repository.rental;

import java.util.Arrays;
import mainpackage.carsharingapp.model.Rental;
import mainpackage.carsharingapp.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class IsActiveSpecificationProvider implements SpecificationProvider<Rental> {
    public Specification<Rental> getSpecification(Object[] params) {
        return (root, query, criteriaBuilder) -> root.get("isActive")
                .in(Arrays.stream(params).toArray());
    }

    @Override
    public String getKey() {
        return "isActive";
    }
}
