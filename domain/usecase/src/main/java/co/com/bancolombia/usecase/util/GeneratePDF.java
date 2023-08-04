package co.com.bancolombia.usecase.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

public class GeneratePDF {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeneratePDF.class);

    public Document convertImagesPdf(List<String> imagesBase64) {

        var document = new Document();
        var out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, out);
        } catch (DocumentException e) {
            LOGGER.error("Error, fail post", e);
        }
        document.open();

        for (String trfImage : imagesBase64) {
            byte[] bytesImageTrf = Base64.decodeBase64((trfImage).getBytes());
            try {
                var img = Image.getInstance(bytesImageTrf);
                document.setPageSize(img);
                document.newPage();
                img.setAbsolutePosition(0, 0);
                document.add(img);
            } catch (DocumentException | IOException e) {
                LOGGER.error("Error, fail convert", e);
            }
        }
        document.close();
        out.toByteArray();

        return document;
    }
}
