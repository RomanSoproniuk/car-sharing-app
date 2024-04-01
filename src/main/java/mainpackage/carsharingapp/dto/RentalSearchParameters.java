package mainpackage.carsharingapp.dto;

public record RentalSearchParameters(Boolean[] isActives,
                                     Long[] usersId) {
}
