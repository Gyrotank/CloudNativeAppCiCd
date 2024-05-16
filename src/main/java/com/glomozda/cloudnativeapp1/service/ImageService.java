package com.glomozda.cloudnativeapp1.service;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

@Service
public interface ImageService {
    HttpStatusCode uploadImage(String id, byte[] imageFileContents);

    String getImagesDataByLabel(String label);
}
