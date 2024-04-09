package mainpackage.carsharingapp.repository;

import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T, V> {
    Specification<T> build(V searchParameters);
}
