package com.practice.growth.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.practice.growth.configurations.component.S3Properties;
import com.practice.growth.domain.dto.AttachmentDto;
import com.practice.growth.domain.types.FileType;
import com.practice.growth.exception.NotFoundException;
import com.practice.growth.utils.AmazonS3Utils;
import com.practice.growth.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;

@Log4j2
@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final S3Properties s3Properties;
    @Value("${spring.profiles.active}")
    String profiles;

    public AttachmentDto upload(MultipartFile multipartFile) throws Exception {

        if (multipartFile == null || multipartFile.getOriginalFilename() == null) return null;

        try {
            log.info("===== S3 File Upload start. =====");

            /**
             * AWS
             */
            String bucketName = s3Properties.getBucketName();
            String originalFileName = multipartFile.getOriginalFilename();

            int startIndex = originalFileName.replaceAll("\\\\", "/").lastIndexOf("/");

            originalFileName = originalFileName.substring(startIndex + 1);
            String fileExtension = FilenameUtils.getExtension(originalFileName);
            String s3FileName = RandomStringUtils.randomAlphanumeric(10) + "." + fileExtension;
            FileType fileType = FileUtils.defineMediaType(fileExtension);

            AmazonS3 s3Client = AmazonS3Utils.amazonS3(profiles, s3Properties);

            if (s3Client != null) {
                ObjectMetadata meta = new ObjectMetadata();
                meta.setContentType(multipartFile.getContentType());
                meta.setContentLength(multipartFile.getBytes().length);

                PutObjectRequest request = new PutObjectRequest(bucketName, s3FileName, multipartFile.getInputStream(), meta);
                if("grove".equals(profiles) || "local".equals(profiles)) request.withCannedAcl(CannedAccessControlList.PublicRead);
                s3Client.putObject(request);

                URL url = s3Client.getUrl(bucketName, s3FileName);

                // TEST
                String fileFullPath = url.toString();

                // AttachmentDto SET.
                AttachmentDto dto = new AttachmentDto();
                dto.setOrgFilename(originalFileName);
                dto.setSavedFilename(s3FileName);
                dto.setFullPath(fileFullPath);
                dto.setFileSize((int) multipartFile.getSize());
                dto.setThumbnailPath(fileFullPath);
                dto.setFileType(fileType);

                return dto;
            }
        } catch (AmazonServiceException e) {
            /**
             * The call was transmitted successfully, but Amazon S3 couldn't process
             * it, so it returned an error response.
             */
            e.printStackTrace();
            log.info("===== S3 File Upload exception !!! =====");
        }

        return null;
    }

    public S3ObjectInputStream download(String s3FileName, Long id) throws NotFoundException {

        if (StringUtils.isBlank(s3FileName)) return null;

        S3ObjectInputStream s3ObjectInputStream = null;

        try {

            AmazonS3 s3Client = AmazonS3Utils.amazonS3(profiles, s3Properties);

            if (s3Client != null) {

                S3Object s3Object = s3Client.getObject(new GetObjectRequest(s3Properties.getBucketName(), s3FileName));
                s3ObjectInputStream = s3Object.getObjectContent();

                //log.info("Content-Type: ", s3Object.getObjectMetadata().getContentType());
                log.info("===== S3 Successfully Download. =====");

            }
        } catch (AmazonServiceException e) {
            e.printStackTrace();
            log.info("===== S3 Download exception !!! =====");
        }

        return s3ObjectInputStream;
    }
}
