package com.example.demo.Service;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class ImageService {

    private FileSystem getFileSystem() throws IOException {
        Configuration configuration = new Configuration();
        configuration.set("fs.defaultFS", "hdfs://localhost:9000");
        return FileSystem.get(configuration);
    }

    public void uploadImageToHDFS(String imageName, MultipartFile imgFile) throws IOException {
        try (InputStream inputStream = imgFile.getInputStream()) {
            Path hdfsPath = new Path("/images/" + imageName);
            FileSystem fs = getFileSystem();
            try (FSDataOutputStream outputStream = fs.create(hdfsPath)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                System.out.println("Image uploaded to HDFS: " + imageName);
            }
        }
    }


    public Resource loadImageFromHDFS(String imageName) throws IOException {
        FileSystem fs = getFileSystem();
        Path hdfsPath = new Path("/images/" + imageName);
        InputStream inputStream = fs.open(hdfsPath);
        return new InputStreamResource(inputStream);
    }
}
