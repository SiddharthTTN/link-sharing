package com.ttn.linksharing.service;

import com.ttn.linksharing.entity.DocumentResource;
import com.ttn.linksharing.entity.User;
import com.ttn.linksharing.repositories.DocumentResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;

@Service
public class DocumentResourceService {

    @Autowired
    DocumentResourceRepository documentResourceRepository;

    private static String UPLOAD_DOCUMENT_FOLDER = "/home/ttn/linksharing-attatchments/linkdocument/";

    public void shareDocument(DocumentResource documentResource) {
        try {
            // Get the file and save it somewhere
            MultipartFile multipartFile = documentResource.getDocumentResource();
            String fileName = new Date().getTime() + documentResource.getDocumentResource().getOriginalFilename();
            String filePath = UPLOAD_DOCUMENT_FOLDER + fileName.replaceAll(" ", "-");
//            String filePath = fileName.replaceAll(" ", "-");
            multipartFile.transferTo(new File(filePath));
            documentResource.setPath(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        documentResourceRepository.save(documentResource);
    }
}
