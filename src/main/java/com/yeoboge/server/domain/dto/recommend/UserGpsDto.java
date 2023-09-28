package com.yeoboge.server.domain.dto.recommend;

import java.util.Objects;

public record UserGpsDto(double latitude, double longitude) {
    @Override
    public boolean equals(Object o) {
        return latitude == ((UserGpsDto) o).latitude
                && longitude == ((UserGpsDto) o).longitude;
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }
}
