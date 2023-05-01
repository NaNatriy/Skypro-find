package ru.skypro.homework.dto.avatarDTO;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
public class AvatarDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String filePath;
    private Long fileSize;
    private String mediaType;
    private byte[] data;
}
