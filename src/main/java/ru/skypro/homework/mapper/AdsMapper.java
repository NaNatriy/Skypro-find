package ru.skypro.homework.mapper;

import ru.skypro.homework.dto.adsDTO.AdsDTO;
import ru.skypro.homework.dto.adsDTO.CreateAdsDTO;
import ru.skypro.homework.dto.adsDTO.FullAds;
import ru.skypro.homework.model.Ads;

public class AdsMapper {

    public static AdsDTO adToAdDto(Ads ads) {
        AdsDTO adsDTO = new AdsDTO();
        adsDTO.setPk(ads.getPk());
        adsDTO.setAuthor(ads.getAuthor().getId());
        adsDTO.setTitle(ads.getTitle());
        adsDTO.setPrice(ads.getPrice());
        adsDTO.setImage("/ads/image/" + ads.getPk());
        return adsDTO;
    }

    public static FullAds adToFullDTO(Ads ads) {
        FullAds fullAds = new FullAds();
        fullAds.setPk(ads.getPk());
        fullAds.setTitle(ads.getTitle());
        fullAds.setDescription(ads.getDescription());
        fullAds.setPrice(ads.getPrice());
        fullAds.setImage("/ads/image/" + ads.getPk());
        fullAds.setEmail(ads.getAuthor().getUsername());
        fullAds.setPhone(ads.getAuthor().getPhone());
        return fullAds;
    }

    public static Ads adToCreateAdDto(CreateAdsDTO createAdsDTO) {
        Ads ads = new Ads();
        ads.setTitle(createAdsDTO.getTitle());
        ads.setDescription(createAdsDTO.getDescription());
        ads.setPrice(createAdsDTO.getPrice());
        return ads;
    }
}
