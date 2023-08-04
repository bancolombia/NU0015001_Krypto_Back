package co.com.bancolombia.usecase.images;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.DocumentException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ImagesUseCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImagesUseCase.class);

    private String endPoint = "external/imfile/upimfile";

    public ResponseData uploadFile(TrfsImages data) throws DocumentException, FileNotFoundException {
        responseData = new ResponseData();

        if (data.getTrfCompleta() != null
                && (data.getFirmantesImagenes() != null || data.getSelloProtectorImagenes() != null)) {

            if ("Success".equals(data.getEstadoTrf())) {

                setImageTrf(data);
                setImageSignatories(data);
                setImageSealsGuards(data);

                buildInfoImages(data);
            }
        } else {
            messageError = ConstantsErrors.ERROR_INCORRECT_FIELDS;
            responseData = setErrorApplication();
        }

        if (responseData.getData() == null) {
            StatusDescription statusDescription = StatusDescription.builder()
                    .descripcionEstado(Constants.INCORRECT_TRANSACTION).build();
            responseData.setData(statusDescription);
        }
        return responseData;
    }

    private void setImageTrf(TrfsImages data) throws FileNotFoundException, DocumentException {
        if (!data.getTrfCompleta().isEmpty()) {

            final int[] countImagenesTRF = {1};

            data.getTrfComplet().forEach(trfImage -> {
                byte[] bytesImageTrf = org.apache.commons.codec.binary.Base64
                        .decodeBase64((trfImage.getImages()).getBytes());
                String fileName = generateFileName(data.getNumberSecuence(), data.getNumberPage() + "",
                        countImagenesTRF[0] + "");

                ImagesData imagesData = ImagesData.builder()
                        .claveImagen(Constants.FOLDER_IMAGES_TEMPORAL + data.getNumeroConsecutivo() + "/SuccessDocuments/"
                                + fileName)
                        .extensionImagen(trfImage.getExtensionImage()).imagen(bytesImageDocument).nameFile(fileName)
                        .metadataCacheControl(Constants.METADATA_CACHE_CONTROL)
                        .metadataContentType(Constants.METADATA_CONTENT_TYPE).build();
                Boolean isCorrectUpload = respositoryDataImages.uploadFile(imagesData);

                if (!isCorrectUpload) {
                    responseData = setErrorApplication();
                }
                countImagenesTRF[0]++;
            });

        }

    }
}
