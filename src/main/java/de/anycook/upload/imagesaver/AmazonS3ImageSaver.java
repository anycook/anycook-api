package de.anycook.upload.imagesaver;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import de.anycook.conf.Configuration;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Jan Graßegger<jan@anycook.de>
 */
public class AmazonS3ImageSaver extends ImageSaver {

    private final AmazonS3Client s3Client;
    private final String bucketName;
    private final Logger logger;
    private final String subPath;

    public AmazonS3ImageSaver(String subPath){
        logger = Logger.getLogger(getClass());

        AWSCredentials awsCredentials = new BasicAWSCredentials(Configuration.getPropertyImageS3AccessKey(),
                Configuration.getPropertyImageS3AccessSecret());
        s3Client = new AmazonS3Client(awsCredentials);
        bucketName = Configuration.getPropertyImageS3Bucket();
        this.subPath = subPath;
    }

    @Override
    public void save(String path, String fileName, BufferedImage newImage) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(newImage, "png", os);
        byte[] bytes = os.toByteArray();
        save(path, fileName, bytes);
    }

    @Override
    public void save(String path, String fileName, byte[] bytes){
        byte[] md5 = DigestUtils.md5(bytes);
        InputStream is = new ByteArrayInputStream(bytes);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(bytes.length);

        //setting max-age to 15 days
        metadata.setCacheControl("max-age=1296000");
        metadata.setContentMD5(new String(Base64.encodeBase64(md5)));
        PutObjectRequest request = new PutObjectRequest(bucketName, subPath + path + fileName, is, metadata);
        request.setCannedAcl(CannedAccessControlList.PublicRead);
        PutObjectResult result = s3Client.putObject(request);
        logger.debug("Etag:" + result.getETag() + "-->" + result);
    }
}
