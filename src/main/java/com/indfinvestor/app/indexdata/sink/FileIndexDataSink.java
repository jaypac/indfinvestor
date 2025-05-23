package com.indfinvestor.app.indexdata.sink;

import com.indfinvestor.app.indexdata.IndexDataFetchParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Service
public class FileIndexDataSink implements IndexDataSink {

    @Value("${app.index.sink.file.folderPath}")
    private String folderPath;

    @Override
    public SinkType getType() {
        return SinkType.FILE;
    }

    @Override
    public void write(IndexDataFetchParams params, InputStream is) {

        String dirPath = folderPath + File.separator + params.uuid();
        String filePath = dirPath + File.separator + params.indexName() + ".txt";

        try {
            Path path = Paths.get(filePath);
            Files.createDirectories(path.getParent());
            Files.write(path, is.readAllBytes(), StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
