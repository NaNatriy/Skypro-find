package ru.skypro.homework.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.ResponseWrapper;
import ru.skypro.homework.dto.adsDTO.AdsDTO;
import ru.skypro.homework.dto.adsDTO.CreateAdsDTO;
import ru.skypro.homework.dto.adsDTO.FullAds;
import ru.skypro.homework.exception.NotFoundException;
import ru.skypro.homework.mapper.AdsMapper;
import ru.skypro.homework.model.Ads;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.repository.UserRepository;

import java.io.IOException;
import java.util.stream.Collectors;

@Service
public class AdsService {
    private final AdsRepository adsRepository;
    private final UserRepository userRepository;

    public AdsService(AdsRepository adsRepository, UserRepository userRepository) {
        this.adsRepository = adsRepository;
        this.userRepository = userRepository;
    }

    public ResponseWrapper<AdsDTO> getAll() {
        ResponseWrapper<AdsDTO> wrap = new ResponseWrapper<>();
        wrap.setResults(adsRepository.findAll().stream().map(AdsMapper::adToAdDto).collect(Collectors.toList()));
        wrap.setCount(wrap.getResults().size());
        return wrap;
    }

    public AdsDTO create(MultipartFile imageFile, CreateAdsDTO createAdsDTO, Authentication authentication) {
        Ads ads = AdsMapper.adToCreateAdDto(createAdsDTO);
        try {
            byte[] bytes = imageFile.getBytes();
            ads.setImage(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        User user = userRepository.findByUsername(authentication.getName()).orElseThrow(NotFoundException::new);
        ads.setAuthor(user);
        return AdsMapper.adToAdDto(adsRepository.saveAndFlush(ads));
    }


    public FullAds getById(Integer id) {
        return AdsMapper.adToFullDTO(adsRepository.findById(id).orElseThrow(NotFoundException::new));
    }

    @Transactional
    public void remove(Integer id) {
        adsRepository.deleteById(id);
    }

    @Transactional
    public AdsDTO update(Integer id, CreateAdsDTO createAdsDTO) {
        Ads ads = adsRepository.findById(id).orElseThrow(NotFoundException::new);
        ads.setTitle(createAdsDTO.getTitle());
        ads.setDescription(createAdsDTO.getDescription());
        ads.setPrice(createAdsDTO.getPrice());
        return AdsMapper.adToAdDto(adsRepository.save(ads));
    }

    public ResponseWrapper<AdsDTO> getMyAds(Authentication authentication) {
        ResponseWrapper<AdsDTO> wrap = new ResponseWrapper<>();
        User user = userRepository.findByUsername(authentication.getName()).orElseThrow(NotFoundException::new);
        wrap.setResults(adsRepository.findAllByAuthorId(user.getId()).stream().map(AdsMapper::adToAdDto).collect(Collectors.toList()));
        wrap.setCount(wrap.getResults().size());
        return wrap;
    }

    @Transactional
    public byte[] updateImage(Integer id, MultipartFile avatar) {
        Ads ads = adsRepository.findById(id).orElseThrow(NotFoundException::new);
        try {
            byte[] bytes = avatar.getBytes();
            ads.setImage(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return adsRepository.saveAndFlush(ads).getImage();
    }

    public byte[] getAdImage(Integer id) {
        return adsRepository.findById(id).orElseThrow(NotFoundException::new).getImage();
    }

    public ResponseWrapper<AdsDTO> search(String title) {
        ResponseWrapper<AdsDTO> wrapper = new ResponseWrapper<>();
        wrapper.setResults(adsRepository.findAllByTitleContainsIgnoreCase(title).stream().map(AdsMapper::adToAdDto).collect(Collectors.toList()));
        wrapper.setCount(wrapper.getResults().size());
        return wrapper;
    }
}
