package com.luanpereira.semcitecsystem.controllers;

import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.svg.element.SvgImage;
import com.luanpereira.semcitecsystem.models.Inscription;
import com.luanpereira.semcitecsystem.models.OfficialDocModel;
import com.luanpereira.semcitecsystem.repositories.OfficialDocRepository;
import com.luanpereira.semcitecsystem.services.AccentRemover;
import com.luanpereira.semcitecsystem.services.InscriptionService;
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
import java.util.Locale;
import java.util.UUID;

@Controller
@RequestMapping("/pdf")
public class PdfGenerator {

    @Autowired
    private OfficialDocRepository officialDocRepository;
    @Autowired
    private InscriptionService inscriptionService;

    private static String BRASAO_RESOURCE = "classpath:static/img/brasao.png";

    private static String OFFICIAL_DOC_DIRECTORY;

    public PdfGenerator(@Value("${officialDocDirectory}") String officialDocDirectory) {
        OFFICIAL_DOC_DIRECTORY = officialDocDirectory;
    }

    @GetMapping("/officialDocGenerator/{uuid}")
    public ResponseEntity<Resource> createFormattedPdf(@PathVariable final UUID uuid) throws IOException {
        OfficialDocModel officialDocData = this.officialDocRepository.findFirstByUuid(uuid);

        String outputPath = OFFICIAL_DOC_DIRECTORY + officialDocData.getNumber().replace('/', '-') + "_" + AccentRemover.accentRemover(officialDocData.getSubject().trim()) + ".pdf";

        PdfWriter pdfWriter = new PdfWriter(outputPath);
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        Document document = new Document(pdfDocument, PageSize.A4);
        document.setMargins(30f, 60f, 30f, 60f); // Margens em pontos (1 polegada = 72 pontos)

        try {

            addHeaderAndTitle(document);
            addContent(document, officialDocData);
            addFooter(document);

            if (officialDocData.getFilePath() != null && !officialDocData.getFilePath().isEmpty()) {
                // Importar o conteúdo do PDF de entrada
                PdfDocument annex = new PdfDocument(new PdfReader(officialDocData.getFilePath()));
                int numberOfPages = annex.getNumberOfPages();

                for (int pageNumber = 1; pageNumber <= numberOfPages; pageNumber++) {
                    annex.copyPagesTo(pageNumber, pageNumber, pdfDocument);
                }

            }

            document.close(); // Fecha o documento

        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileReturn(outputPath);
    }

    @GetMapping("/inscriptionPdf/{uuid}")
    public ResponseEntity<Resource> inscriptionPdfGenerator(@PathVariable final UUID uuid) throws IOException {
        Inscription inscriptionData = this.inscriptionService.findById(uuid);

        String outputPath = OFFICIAL_DOC_DIRECTORY + AccentRemover.accentRemover(inscriptionData.getStudent().getName()) + inscriptionData.getInscriptionCode() + ".pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outputPath));
        Document document = new Document(pdfDocument, PageSize.A4);
        document.setMargins(30f, 60f, 30f, 60f); // Margens em pontos (1 polegada = 72 pontos)

        try {

            addHeaderAndTitle(document);
            addInscriptionContent(document, inscriptionData);
            addInscriptionFooter(document);

            document.close(); // Fecha o documento

        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileReturn(outputPath);
    }

    private static void addHeaderAndTitle(Document document) throws IOException {
        // Criar uma instância da classe Image. Adiciona o brasão ao título
        Image image = new Image(ImageDataFactory.create(BRASAO_RESOURCE));
        image.scale(0.2f, 0.2f);
        image.setHorizontalAlignment(HorizontalAlignment.CENTER);
        document.add(image); // Adicionar a imagem ao documento

        // Adiciona o título formatado
        PdfFont times_bold = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);
        Paragraph title = new Paragraph()
            .add("""
                    ESTADO 
                    PREFEITURA MUNICIPAL 
                    CNPJ: 00.000.000/0001-000
                    """)
            .setFont(times_bold)
            .setFontSize(10)
            .setTextAlignment(TextAlignment.CENTER);
        document.add(title);

        // Adiciona sub título formatado
        Paragraph subTitle = new Paragraph()
            .add("""
                    Nome da Rua, 000 – Bairro – Cidade – MA
                    E-mail: emaildasecretaria@gmail.com
                    """)
            .setFontSize(10)
            .setTextAlignment(TextAlignment.CENTER);
        subTitle.setMarginBottom(60f);
        document.add(subTitle);
    }

    private static void addContent(Document document, OfficialDocModel officialDocData) throws IOException {
        // Adiciona uma frase formatada
        Table numberDate = new Table(2).useAllAvailableWidth();

        PdfFont f = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);
        Cell cellLeft = new Cell(1, 1)
                .add(new Paragraph("Ofício nº " + officialDocData.getNumber()))
                .setFont(f)
                .setFontSize(12)
                .setTextAlignment(TextAlignment.LEFT)
                .setBorder(Border.NO_BORDER);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", Locale.forLanguageTag("pt-BR"));
        String currentDate = officialDocData.getCreationAt().format(dateFormatter);
        Cell cellRight = new Cell(1, 1)
                .add(new Paragraph("Coroatá, " + currentDate))
                .setFont(f)
                .setFontSize(12)
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorder(Border.NO_BORDER);

        numberDate.addCell(cellLeft);
        numberDate.addCell(cellRight);
        numberDate.setMarginBottom(30f);

        document.add(numberDate);

        String receiverTitle = officialDocData.getReceiverTitle();
        String receiverContent = String.format("%s\n%s",
                officialDocData.getReceiver(),
                officialDocData.getReceiverPosition()
        );

        Paragraph receiver = new Paragraph(receiverTitle + "\n" + receiverContent);
        receiver.setMarginBottom(40f);
        document.add(receiver);

        // Assunto do ofício
        Paragraph subject = new Paragraph()
                .add(officialDocData.getSubject());
        document.add(subject);

        // Descrição do ofício
        // Adicionar uma tabela
        Table table = new Table(1).useAllAvailableWidth();
        Cell cell = new Cell()
                .setHeight(280f)
                .setMaxHeight(280f)
                .setBorder(Border.NO_BORDER);

        HtmlConverter.convertToElements(officialDocData.getDescription()).forEach(iElement -> {
            if (iElement instanceof Div) cell.add((Div) iElement);
            if (iElement instanceof List) cell.add((List) iElement);
            if (iElement instanceof Image) cell.add((Image) iElement);
            if (iElement instanceof SvgImage) cell.add((SvgImage) iElement);
            if (iElement instanceof Paragraph) cell.add((Paragraph) iElement);
            if (iElement instanceof Table) cell.add((Table) iElement);
        });

        table.addCell(cell);

        // Adicionar a tabela ao documento
        document.add(table);
    }

    private static void addFooter(Document document) {
        // Adiciona rodapé para assinatura
        Paragraph footer = new Paragraph()
                .add("""
                        Atenciosamente:
                        Nome do Secretário ou Secretária
                        Secretário Municipal do Orgão
                        Decreto nº 000/2024
                        """)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(footer);
    }

    private static void addInscriptionContent(Document document, Inscription inscriptionData) throws IOException {
        // Adiciona o título formatado
        PdfFont timesBold = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);
        Paragraph title = new Paragraph()
                .add("Comprovante de Matrícula")
                .setTextAlignment(TextAlignment.CENTER)
                .setFont(timesBold)
                .setFontSize(14)
                .setMarginBottom(40f);
        document.add(title);

        //=========== Table ==============
        // Adiciona uma frase formatada
        Table contentTable = new Table(new float[]{100, 100, 100, 100}).useAllAvailableWidth();

        Cell cellName = new Cell(1, 2)
                .add(new Paragraph("Nome: ").setFontSize(10))
                .add(new Paragraph(inscriptionData.getStudent().getName()))
                .setPaddingTop(10)
                .setBorder(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(new DeviceGray(), 1));
        contentTable.addCell(cellName);

        Cell cellCode = new Cell(1, 2)
                .add(new Paragraph("Código: ").setFontSize(10))
                .add(new Paragraph(inscriptionData.getInscriptionCode()))
                .setPaddingTop(10)
                .setBorder(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(new DeviceGray(), 1));
        contentTable.addCell(cellCode);

        Cell cellCpf = new Cell()
                .add(new Paragraph("CPF: ").setFontSize(10))
                .add(new Paragraph(inscriptionData.getStudent().getCpf()))
                .setPaddingTop(10)
                .setBorder(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(new DeviceGray(), 1));
        contentTable.addCell(cellCpf);

        Cell cellPhone = new Cell()
                .add(new Paragraph("Fone: ").setFontSize(10))
                .add(new Paragraph(inscriptionData.getStudent().getPhone1()))
                .setPaddingTop(10)
                .setBorder(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(new DeviceGray(), 1));
        contentTable.addCell(cellPhone);

        Cell cellEmail = new Cell(1, 2)
                .add(new Paragraph("E-mail: ").setFontSize(10))
                .add(new Paragraph(inscriptionData.getStudent().getEmail()))
                .setPaddingTop(10)
                .setBorder(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(new DeviceGray(), 1));
        contentTable.addCell(cellEmail);

        Cell cellAddress = new Cell(1, 4)
                .add(new Paragraph("Endereço: ").setFontSize(10))
                .add(new Paragraph(inscriptionData.getStudent().getStreet() + " - nº " +
                inscriptionData.getStudent().getHouseNumber() + " - " +
                inscriptionData.getStudent().getNeighborhood() + " - " +
                inscriptionData.getStudent().getCity() + " / " +
                inscriptionData.getStudent().getState()))
                .setPaddingTop(10)
                .setBorder(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(new DeviceGray(), 1));
        contentTable.addCell(cellAddress);

        Cell cellCourse = new Cell(1, 2)
                .add(new Paragraph("Curso: ").setFontSize(10))
                .add(new Paragraph(inscriptionData.getCourse().getName()))
                .setPaddingTop(10)
                .setBorder(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(new DeviceGray(), 1));
        contentTable.addCell(cellCourse);

        Cell cellWorkload = new Cell()
                .add(new Paragraph("C. Horária: ").setFontSize(10))
                .add(new Paragraph(inscriptionData.getCourse().getWorkload() + " Horas"))
                .setPaddingTop(10)
                .setBorder(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(new DeviceGray(), 1));
        contentTable.addCell(cellWorkload);

        Cell cellEntity = new Cell()
                .add(new Paragraph("Entidade: ").setFontSize(10))
                .add(new Paragraph(inscriptionData.getCourse().getEntity()))
                .setPaddingTop(10)
                .setBorder(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(new DeviceGray(), 1));
        contentTable.addCell(cellEntity);

        Cell cellClassroom = new Cell()
                .add(new Paragraph("Turma: ").setFontSize(10))
                .add(new Paragraph(inscriptionData.getClassroom().getName()))
                .setPaddingTop(10)
                .setBorder(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(new DeviceGray(), 1));
        contentTable.addCell(cellClassroom);

        Cell cellDays = new Cell()
                .add(new Paragraph("Dias: ").setFontSize(10))
                .add(new Paragraph(inscriptionData.getClassroom().getDays()))
                .setPaddingTop(10)
                .setBorder(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(new DeviceGray(), 1));
        contentTable.addCell(cellDays);

        Cell cellHours = new Cell()
                .add(new Paragraph("Horário: ").setFontSize(10))
                .add(new Paragraph(inscriptionData.getClassroom().getHours() + " h"))
                .setPaddingTop(10)
                .setBorder(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(new DeviceGray(), 1));
        contentTable.addCell(cellHours);

        Cell cellLocal = new Cell()
                .add(new Paragraph("Local: ").setFontSize(10))
                .add(new Paragraph("Sala: " + inscriptionData.getClassroom().getRoom()))
                .setPaddingTop(10)
                .setBorder(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(new DeviceGray(), 1));
        contentTable.addCell(cellLocal);

        Cell cellAuthor = new Cell(1, 2)
                .add(new Paragraph("Resp. pela matrícula: ").setFontSize(10))
                .add(new Paragraph(inscriptionData.getAuthor().getName()))
                .setPaddingTop(10)
                .setBorder(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(new DeviceGray(), 1));
        contentTable.addCell(cellAuthor);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", Locale.forLanguageTag("pt-BR"));
        String inscriptionDate = inscriptionData.getInscriptionDate().format(dateFormatter);
        Cell cellDate = new Cell(1, 2)
                .add(new Paragraph("Data da matrícula: ").setFontSize(10))
                .add(new Paragraph(inscriptionDate))
                .setPaddingTop(10)
                .setBorder(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(new DeviceGray(), 1));
        contentTable.addCell(cellDate);

        contentTable.setMarginBottom(140f);

        document.add(contentTable);

    }

    private static void addInscriptionFooter(Document document) throws IOException {
        // Adiciona uma frase formatada
        Table signTable = new Table(new float[]{5,200,10,200,5}).useAllAvailableWidth();

        Cell cell = new Cell();
        cell.setBorder(Border.NO_BORDER);

        Cell cellLeft = new Cell()
                .add(new Paragraph("Ass. do(a) Aluno(a)"))
                .setTextAlignment(TextAlignment.CENTER)
                .setBorder(Border.NO_BORDER)
                .setBorderTop(new SolidBorder(new DeviceGray(), 1));

        Cell cellRight = new Cell()
                .add(new Paragraph("Ass. responsável pela inscrição"))
                .setTextAlignment(TextAlignment.CENTER)
                .setBorder(Border.NO_BORDER)
                .setBorderTop(new SolidBorder(new DeviceGray(), 1));

        signTable.addCell(cell);
        signTable.addCell(cellLeft);
        signTable.addCell(cell);
        signTable.addCell(cellRight);
        signTable.addCell(cell);

//        signTable.setMarginBottom(30f);

        document.add(signTable);
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