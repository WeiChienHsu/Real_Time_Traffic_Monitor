package com.kevin.spring.uber.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.Embeddable;

/* Unit for each different Car - ex. vin number */
@Data
@RequiredArgsConstructor /* For initialize UnitInfo */
@AllArgsConstructor
@Embeddable /* @Embeddable注释,表示此类可以被插入某个entity中 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UnitInfo {
    /* Vin number could not be changed after built */
    /* Immutable field */
    private final String unitVin;
    private String engineMake;
    private String customerName;
    private String unitNumber;

    /* Private Constructor: Avoid to be called from outside of Object */
    private UnitInfo() {
        this.unitVin = "";
    }
}
