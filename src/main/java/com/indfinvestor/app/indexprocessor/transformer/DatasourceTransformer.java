package com.indfinvestor.app.indexprocessor.transformer;

import com.indfinvestor.app.indexprocessor.model.IndexData;

import java.io.File;
import java.util.List;

public interface DatasourceTransformer {


    List<IndexData> transform(File csvFile,String pattern);
}
