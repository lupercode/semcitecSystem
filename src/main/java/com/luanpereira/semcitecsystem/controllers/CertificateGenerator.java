package com.luanpereira.semcitecsystem.controllers;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.luanpereira.semcitecsystem.models.Inscription;
import com.luanpereira.semcitecsystem.services.InscriptionService;
import com.luanpereira.semcitecsystem.services.AccentRemover;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Controller
@RequestMapping("/certificateGenerator")
public class CertificateGenerator {

    @Autowired
    private InscriptionService inscriptionService;

    private static String OFFICIAL_DOC_DIRECTORY;

    private static String certificateTemplate = "static/img/certificado.pdf";

    public CertificateGenerator(@Value("${officialDocDirectory}") String officialDocDirectory) {
        OFFICIAL_DOC_DIRECTORY = officialDocDirectory;
        //certificateTemplate = OFFICIAL_DOC_DIRECTORY + "certificado.pdf";
    }

    @GetMapping("/classroom/{uuid}")
    public ResponseEntity<Resource> classroomCertificate(@PathVariable UUID uuid) throws IOException {
        List<Inscription> inscriptionList = inscriptionService.findByClassroomUuid(uuid);

        String outputPath = OFFICIAL_DOC_DIRECTORY + AccentRemover.accentRemover(inscriptionList.get(0).getCourse().getName() + "-" + inscriptionList.get(0).getClassroom().getName()) + "-certificado.pdf";

        PdfWriter pdfWriter = new PdfWriter(outputPath);
        PdfReader pdfReader = new PdfReader(certificateTemplate);
        PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter);
        Document document = new Document(pdfDocument, PageSize.A4.rotate());

        try {

            for (Inscription inscription : inscriptionList) {

                PdfCanvas canvas = new PdfCanvas(pdfDocument.addNewPage());
                canvas.addImageFittedIntoRectangle(ImageDataFactory.create("classpath:static/img/certificado.png"), PageSize.A4.rotate(), false);

                certificate(inscription, document);

                document.add(new AreaBreak());

            }
            pdfDocument.removePage(pdfDocument.getLastPage());
            document.close(); // Fecha o documento


        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileReturn(outputPath);
    }

    @GetMapping("/inscription/{uuid}")
    public ResponseEntity<Resource> studentCertificate(@PathVariable UUID uuid) throws IOException {
        Inscription inscription = this.inscriptionService.findById(uuid);

        String outputPath = OFFICIAL_DOC_DIRECTORY  + AccentRemover.accentRemover(inscription.getStudent().getName()) + "-certificado.pdf";

        PdfWriter pdfWriter = new PdfWriter(outputPath);
        PdfReader pdfReader = new PdfReader(certificateTemplate);
        PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter);
        Document document = new Document(pdfDocument, PageSize.A4.rotate());

        certificate(inscription, document);

        document.close(); // Fecha o documento

        return fileReturn(outputPath);
    }

    public void certificate(Inscription inscription, Document document) {
        try {
            Paragraph classCode = new Paragraph(inscription.getInscriptionCode())
                .setBold()
                .setFontSize(16)
                .setFontColor(ColorConstants.WHITE)
                .setRelativePosition(175f, 107f, 0, 0);
            document.add(classCode);

            String fontPath = "static/fonts/Tangerine-Bold.ttf";
            PdfFont tangerineBold = PdfFontFactory.createFont(fontPath);
            Paragraph studentName = new Paragraph(inscription.getStudent().getName())
                .setWidth(550f)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(44)
                .setFont(tangerineBold)
                .setMarginTop(180)
                .setMarginLeft(200);
            document.add(studentName);

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", Locale.forLanguageTag("pt-BR"));
            String closingDate = inscription.getClassroom().getClosingDate().format(dateFormatter);

            Paragraph p = new Paragraph(String.format("""
                        Certificamos que o(a) aluno(a) supracitado, portador do CPF %s, concluiu com sucesso o curso %s promovido pela Secretaria Municipal de Ciência e Tecnologia de Coroatá em %s totalizando uma carga horária total de %s horas.
                        """, 
                        inscription.getStudent().getCpf(), 
                        inscription.getCourse().getName(), 
                        closingDate, 
                        inscription.getCourse().getWorkload()))
                .setWidth(550f)
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setFontSize(18)
                .setMarginTop(25)
                .setMarginLeft(200);
            document.add(p);

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private static ResponseEntity<Resource> fileReturn(String fileName) throws IOException {
        Path filePath = Paths.get(OFFICIAL_DOC_DIRECTORY).resolve(fileName);
        Resource resource = new UrlResource(filePath.toUri());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + resource.getFilename());
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

}