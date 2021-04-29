package com.codesquad.sidedish.web.sidedish;

import com.codesquad.sidedish.utils.SampleDataFactory;
import com.codesquad.sidedish.web.sidedish.DTO.SidedishDTO;
import com.codesquad.sidedish.web.sidedish.domain.Image;
import com.codesquad.sidedish.web.sidedish.domain.Price;
import com.codesquad.sidedish.web.sidedish.domain.Sidedish;
import com.codesquad.sidedish.web.sidedish.domain.SidedishCategory;
import com.codesquad.sidedish.web.sidedish.repository.ImageRepository;
import com.codesquad.sidedish.web.sidedish.repository.SidedishCategoryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class DTOMappingTest {

    @Autowired
    SidedishCategoryRepository sidedishCategoryRepository;

    @Autowired
    ImageRepository imageRepository;

    ObjectMapper objectMapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

    @Test
    void createBestSidedishes() throws JsonProcessingException {
        List<SidedishDTO> sidedishDTOs = SampleDataFactory.createBestSidedishes();

        List<SidedishCategory> categories = sidedishDTOs.stream().map(SidedishDTO::sidedishBestCategory).collect(Collectors.toList());

        System.out.println(objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(categories));

    }

    @Test
    void saveImage() throws JsonProcessingException {
        SidedishCategory sidedishCategory = sidedishCategoryRepository.save(new SidedishCategory("메인반찬", false));

        Image image = imageRepository.save(new Image("test", "test"));
        Image thumbnailImage1 = imageRepository.save(new Image("test thumbnail", "test thumbnail"));
        Image thumbnailImage2 = imageRepository.save(new Image("test thumbnail2", "test thumbnail2"));

        Sidedish sidedish = Sidedish.builder()
                .name("반찬1")
                .description("설명")
                .normalPrice(new Price(100L))
                .salePrice(new Price(70L))
                .stock(5)
                .sidedishImage(image.sidedishImage("test"))
                .build();

        sidedish.addSidedishThumbImages(Arrays.asList(thumbnailImage1, thumbnailImage2));

        SidedishCategory result = sidedishCategoryRepository.save(sidedishCategory.addSidedishes(Arrays.asList(sidedish)));

        SidedishDTO mappingResult = SidedishDTO.of(result, new HashMap<Long, Image>() {{
            put(image.getId(), image);
        }});

        System.out.println(objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(mappingResult));
    }

    @Test
    void saveImageNotFounded() throws JsonProcessingException {
        SidedishCategory sidedishCategory = sidedishCategoryRepository.save(new SidedishCategory("메인반찬", false));

        Image image = imageRepository.save(new Image("test", "test"));

        Sidedish sidedish = Sidedish.builder()
                .name("반찬1")
                .description("설명")
                .normalPrice(new Price(100L))
                .salePrice(new Price(70L))
                .stock(5)
                .build();

        SidedishCategory result = sidedishCategoryRepository.save(sidedishCategory.addSidedishes(Arrays.asList(sidedish)));

        SidedishDTO mappingResult = SidedishDTO.of(result, new HashMap<Long, Image>() {{
            put(image.getId(), image);
        }});

        System.out.println(objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(mappingResult));
    }
}