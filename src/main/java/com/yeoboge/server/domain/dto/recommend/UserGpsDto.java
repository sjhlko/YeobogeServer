package com.yeoboge.server.domain.dto.recommend;

import java.util.Objects;

/**
 * 사용자의 현재 GPS 정보를 담는 DTO
 *
 * @param latitude 사용자의 현재 위도
 * @param longitude 사용자의 현재 경도
 */
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
