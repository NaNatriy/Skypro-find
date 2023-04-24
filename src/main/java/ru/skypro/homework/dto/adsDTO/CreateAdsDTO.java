package ru.skypro.homework.dto.adsDTO;

import lombok.Data;

@Data
public class CreateAdsDTO {
    private String description;
    private Integer price;
    private String title;
}
