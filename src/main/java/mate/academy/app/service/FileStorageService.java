package mate.academy.app.service;

import java.io.InputStream;

public interface FileStorageService {
    String upload(String path, InputStream content);

    InputStream download(String fileId);

    void delete(String fileId);
}
