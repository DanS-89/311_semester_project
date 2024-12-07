package dao;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;

/**
 * StorageUploader class that handles file uploads to a Blob storage container
 */
public class StorageUploader {
    private BlobContainerClient containerClient;

    /**
     * Creates a StorageUploaer instance and initializes the BlobContainerClient
     */
    public StorageUploader( ) {
        this.containerClient = new BlobContainerClientBuilder()
                .connectionString("DefaultEndpointsProtocol=https;AccountName=stevenscsc311storage;AccountKey=Lwl9ushY49aH22s8y4r9CED6oIm4xXg2vTySpYEgM4YsEp7NvUEs485gAATpWVgTKmUdKRkkYlU7+AStvn7IZg==;EndpointSuffix=core.windows.net")
                .containerName("media-files")
                .buildClient();
    }

    /**
     * Uploads a file to the Blob storage container
     * @param filePath local filepath to the file to be uploaded
     * @param blobName Name of the file to be created in the storage container
     */
    public String uploadFile(String filePath, String blobName) {
        BlobClient blobClient = containerClient.getBlobClient(blobName);
        blobClient.uploadFromFile(filePath, true);
        return blobClient.getBlobUrl();
    }

    /**
     * Retrieves instance interacting with the storage container
     * @return the BlobContainerClient instance associated with the current storage container
     */
    public BlobContainerClient getContainerClient(){
        return containerClient;
    }
}
