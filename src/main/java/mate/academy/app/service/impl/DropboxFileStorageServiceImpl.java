package mate.academy.app.service.impl;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import mate.academy.app.exception.FileOperationException;
import mate.academy.app.service.FileStorageService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DropboxFileStorageServiceImpl implements FileStorageService {
    private final DbxClientV2 dropboxClient;

    @Override
    public String upload(String path, InputStream content) {
        try {
            FileMetadata metadata = dropboxClient.files()
                    .uploadBuilder(path)
                    .uploadAndFinish(content);
            return metadata.getId();
        } catch (DbxException e) {
            throw new FileOperationException("Failed to upload file to Dropbox API", e);
        } catch (IOException e) {
            throw new FileOperationException("Cannot read the provided file", e);
        }
    }

    @Override
    public InputStream download(String fileId) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            dropboxClient.files().downloadBuilder(fileId).download(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (DbxException | IOException e) {
            throw new FileOperationException("Failed to download file from Dropbox", e);
        }
    }

    @Override
    public void delete(String fileId) {
        try {
            dropboxClient.files().deleteV2(fileId);
        } catch (DbxException e) {
            throw new FileOperationException("Failed to delete file from Dropbox", e);
        }
    }
}