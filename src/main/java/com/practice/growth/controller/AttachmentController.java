package com.practice.growth.controller;

import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.google.gson.Gson;
import com.practice.growth.domain.dto.AttachmentDto;
import com.practice.growth.exception.NotFoundException;
import com.practice.growth.repository.AttachmentRepository;
import com.practice.growth.service.AttachmentService;
import com.practice.growth.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
@Controller
@RequestMapping("/attachment/")
public class AttachmentController {

    protected final HttpServletRequest request;
    private final AttachmentService service;

    @PostMapping("upload")
    @ResponseBody
    public void upload(HttpServletResponse response,
                       @RequestParam(value = "file", required = false) MultipartFile multipartFile,
                       @RequestParam(value = "files[]", required = false) MultipartFile[] multipartFileList) throws Exception {

        Map<String, Object> result = new HashMap<>();
        PrintWriter writer = null;

        try {
            writer = response.getWriter();
        } catch (IOException exception) {
            log.info(exception.getMessage());
        }

        if (multipartFileList != null && multipartFileList.length > 0) {

            List<AttachmentDto> list = new ArrayList<>();

            for (MultipartFile multipleFile : multipartFileList) {

                String originalFileName = multipartFile.getOriginalFilename();
                boolean isPermit = FileUtils.permitExtensionCheck(originalFileName);

                if (!isPermit) {
                    result.put(FileUtils.RS_MESSAGE, "해당 파일은 업로드하실 수 없습니다.<br />확인 후 다시 업로드해주시기 바랍니다.");
                    break;
                }

                if (isPermit) list.add(service.upload(multipleFile));
            }

            result.put(FileUtils.RS_DATA, list);

            log.info("===== S3 multipartFileList Successfully Uploaded. =====");
        } else {

            String originalFileName = multipartFile.getOriginalFilename();
            boolean isPermit = FileUtils.permitExtensionCheck(originalFileName);

            result.put(FileUtils.RS_MESSAGE, isPermit ? "" : "해당 파일은 업로드하실 수 없습니다.<br />확인 후 다시 업로드해주시기 바랍니다.");

            if (isPermit) {
                result.put(FileUtils.RS_DATA, service.upload(multipartFile));

                log.info("===== S3 File Successfully Uploaded. =====");
            }
        }

        result.put(FileUtils.RS_STATUS, FileUtils.RS_SUCC);

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        writer.print(new Gson().toJson(result));
        writer.flush();
        writer.close();
    }

    @GetMapping("download")
    public void download(@RequestParam(value = "s3FileName") String s3FileName,
                         @RequestParam(value = "originFileName") String originFileName,
                         HttpServletResponse response) throws NotFoundException {
        S3ObjectInputStream fis = service.download(s3FileName, null);
        String fileName = originFileName;

        response.setHeader("Content-Type", "applAication/octet-stream");
        response.setHeader("Content-Transfer-Encoding", "binary");

        if (request.isSecure()) {
            response.setHeader("Cache-Control", "public");
            response.setHeader("Pragma", "public");
        }

        response.setHeader("Expires", "-1");
        response.setHeader("Connection", "close");

        String userAgent = request.getHeader("User-Agent");

        if (userAgent.indexOf("Trident") > -1) {
            try {
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                log.error(e.getMessage());
            }

            fileName = fileName.replaceAll("\\+", "%20");

        } else if (userAgent.indexOf("MSIE") > -1) {
            try {
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                log.error(e.getMessage());
            }

            fileName = fileName.replaceAll("\\+", " ");

        } else if (userAgent.indexOf("Mozilla") > -1) {
            try {
                fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
            } catch (UnsupportedEncodingException e) {
                log.error(e.getMessage());
            }
        } else {
            try {
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                log.error(e.getMessage());
            }
        }

        response.setHeader("Content-Disposition", "attachment;fileName=\"" + fileName + "\";");

        try {
            ServletOutputStream sos = response.getOutputStream();
            /**
             * Copy File
             */
            FileCopyUtils.copy(fis, sos);
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    private void fileDownload(String serverFileName, String originFileName, HttpServletResponse response, Long id) throws NotFoundException {
        S3ObjectInputStream fis = service.download(serverFileName, id);
        String fileName = originFileName;

        response.setHeader("Content-Type", "application/octet-stream");
        response.setHeader("Content-Transfer-Encoding", "binary");

        if (request.isSecure()) {
            response.setHeader("Cache-Control", "public");
            response.setHeader("Pragma", "public");
        }

        response.setHeader("Expires", "-1");
        response.setHeader("Connection", "close");

        String userAgent = request.getHeader("User-Agent");

        if (userAgent.indexOf("Trident") > -1) {
            try {
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                log.error(e.getMessage());
            }

            fileName = fileName.replaceAll("\\+", "%20");

        } else if (userAgent.indexOf("MSIE") > -1) {
            try {
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                log.error(e.getMessage());
            }

            fileName = fileName.replaceAll("\\+", " ");

        } else if (userAgent.indexOf("Mozilla") > -1) {
            try {
                fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
            } catch (UnsupportedEncodingException e) {
                log.error(e.getMessage());
            }
        } else {
            try {
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                log.error(e.getMessage());
            }
        }

        response.setHeader("Content-Disposition", "attachment;fileName=\"" + fileName + "\";");

        try {
            ServletOutputStream sos = response.getOutputStream();
            /**
             * Copy File
             */
            FileCopyUtils.copy(fis, sos);
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }
}
