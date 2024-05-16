package com.glomozda.cloudnativeapp1.controller;

import com.amazonaws.services.s3.Headers;
import com.glomozda.cloudnativeapp1.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Slf4j
@RestController
@RequestMapping(path="/image")
public class ImageController {
    protected final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("")
    public ResponseEntity<String> postImage(@RequestParam("file") MultipartFile imageFile) throws IOException {
        byte[] bytes = imageFile.getBytes();

        log.info("File Name: " + imageFile.getOriginalFilename());
        log.info("File Content Type: " + imageFile.getContentType());
        log.info("File Content Size: " + bytes.length);

        String id = UUID.randomUUID() + "-" + imageFile.getOriginalFilename();
        HttpStatusCode responseCode = imageService.uploadImage(id, bytes);
        if (responseCode.is2xxSuccessful()) {
            return (new ResponseEntity<>(id, null, responseCode));
        } else {
            return (new ResponseEntity<>("ERROR!", null, responseCode));
        }
    }

    @GetMapping("/{label}")
    public ResponseEntity<String> getImagesListByLabel(@PathVariable String label) {
        Map<String, List<String>> headers = new HashMap<>();
        headers.put(Headers.CONTENT_TYPE, Collections.singletonList(ContentType.APPLICATION_JSON.getMimeType()));
        return (new ResponseEntity<>(
                    imageService.getImagesDataByLabel(label),
                    new LinkedMultiValueMap<>(headers),
                    HttpStatus.OK));
    }
}
