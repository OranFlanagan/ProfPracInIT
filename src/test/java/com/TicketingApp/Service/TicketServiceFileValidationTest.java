package com.TicketingApp.Service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.Test;

class TicketServiceFileValidationTest {

    private final TicketService service = new TicketService(null, null);

    @Test
    void acceptsPdfWhenExtensionMatches() throws Exception {
        assertEquals("application/pdf", service.validateAndDetectMimeType(createMinimalPdf(), "document.pdf"));
    }

    @Test
    void acceptsPngWhenExtensionMatches() throws Exception {
        assertEquals("image/png", service.validateAndDetectMimeType(createMinimalPng(), "evidence.PNG"));
    }

    @Test
    void acceptsDocxWhenPackageStructureMatches() throws Exception {
        assertEquals(
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                service.validateAndDetectMimeType(createMinimalDocx(), "letter.docx")
        );
    }

    @Test
    void acceptsXlsxWhenPackageStructureMatches() throws Exception {
        assertEquals(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                service.validateAndDetectMimeType(createMinimalXlsx(), "grades.xlsx")
        );
    }

    @Test
    void acceptsOdtWhenPackageStructureMatches() throws Exception {
        String detectedMimeType = service.validateAndDetectMimeType(createMinimalOdt(), "notes.odt");

        assertTrue(Set.of(
                "application/vnd.oasis.opendocument.text",
                "application/x-vnd.oasis.opendocument.text"
        ).contains(detectedMimeType));
    }

    @Test
    void acceptsOdsWhenPackageStructureMatches() throws Exception {
        String detectedMimeType = service.validateAndDetectMimeType(createMinimalOds(), "inventory.ods");

        assertTrue(Set.of(
                "application/vnd.oasis.opendocument.spreadsheet",
                "application/x-vnd.oasis.opendocument.spreadsheet"
        ).contains(detectedMimeType));
    }

    @Test
    void rejectsRenamedImageAsPdf() throws Exception {
        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> service.validateAndDetectMimeType(createMinimalPng(), "report.pdf")
        );

        assertTrue(error.getMessage().contains("does not match detected MIME type"));
    }

    @Test
    void rejectsUnsupportedExtensions() throws Exception {
        byte[] pdfBytes = createMinimalPdf();

        assertAll(
                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> service.validateAndDetectMimeType(pdfBytes, "notes.txt")
                ),
                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> service.validateAndDetectMimeType(pdfBytes, "grades.csv")
                ),
                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> service.validateAndDetectMimeType(pdfBytes, "macro.docm")
                )
        );
    }

    @Test
    void rejectsMalformedFilenames() throws Exception {
        byte[] pdfBytes = createMinimalPdf();

        assertAll(
                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> service.validateAndDetectMimeType(pdfBytes, null)
                ),
                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> service.validateAndDetectMimeType(pdfBytes, "filename")
                ),
                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> service.validateAndDetectMimeType(pdfBytes, "filename.")
                )
        );
    }

    private byte[] createMinimalPdf() {
        return "%PDF-1.4\n1 0 obj\n<<>>\nendobj\ntrailer\n<<>>\n%%EOF"
                .getBytes(StandardCharsets.US_ASCII);
    }

    private byte[] createMinimalPng() {
        return new byte[] {
                (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,
                0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52,
                0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01,
                0x08, 0x06, 0x00, 0x00, 0x00, 0x1F, (byte) 0x15, (byte) 0xC4,
                (byte) 0x89, 0x00, 0x00, 0x00, 0x0A, 0x49, 0x44, 0x41,
                0x54, 0x78, (byte) 0x9C, 0x63, 0x60, 0x00, 0x00,
                0x00, 0x02, 0x00, 0x01, (byte) 0xE5, 0x27, (byte) 0xD4,
                (byte) 0xA2, 0x00, 0x00, 0x00, 0x00, 0x49, 0x45,
                0x4E, 0x44, (byte) 0xAE, 0x42, 0x60, (byte) 0x82
        };
    }

    private byte[] createMinimalDocx() throws IOException {
        return createZipPackage(new String[][] {
                {
                        "[Content_Types].xml",
                        """
                        <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                        <Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
                          <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
                          <Default Extension="xml" ContentType="application/xml"/>
                          <Override PartName="/word/document.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/>
                        </Types>
                        """
                },
                {
                        "_rels/.rels",
                        """
                        <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                        <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
                          <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/>
                        </Relationships>
                        """
                },
                {
                        "word/document.xml",
                        """
                        <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                        <w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
                          <w:body><w:p><w:r><w:t>Test</w:t></w:r></w:p></w:body>
                        </w:document>
                        """
                }
        });
    }

    private byte[] createMinimalXlsx() throws IOException {
        return createZipPackage(new String[][] {
                {
                        "[Content_Types].xml",
                        """
                        <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                        <Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
                          <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
                          <Default Extension="xml" ContentType="application/xml"/>
                          <Override PartName="/xl/workbook.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/>
                          <Override PartName="/xl/worksheets/sheet1.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>
                        </Types>
                        """
                },
                {
                        "_rels/.rels",
                        """
                        <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                        <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
                          <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="xl/workbook.xml"/>
                        </Relationships>
                        """
                },
                {
                        "xl/workbook.xml",
                        """
                        <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                        <workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main"
                                  xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">
                          <sheets>
                            <sheet name="Sheet1" sheetId="1" r:id="rId1"/>
                          </sheets>
                        </workbook>
                        """
                },
                {
                        "xl/_rels/workbook.xml.rels",
                        """
                        <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                        <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
                          <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet1.xml"/>
                        </Relationships>
                        """
                },
                {
                        "xl/worksheets/sheet1.xml",
                        """
                        <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                        <worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">
                          <sheetData>
                            <row r="1"><c r="A1" t="inlineStr"><is><t>Test</t></is></c></row>
                          </sheetData>
                        </worksheet>
                        """
                }
        });
    }

    private byte[] createMinimalOdt() throws IOException {
        return createOpenDocumentPackage(
                "application/vnd.oasis.opendocument.text",
                """
                <?xml version="1.0" encoding="UTF-8"?>
                <office:document-content
                    xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0"
                    xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0"
                    office:version="1.2">
                  <office:body>
                    <office:text>
                      <text:p>Test</text:p>
                    </office:text>
                  </office:body>
                </office:document-content>
                """
        );
    }

    private byte[] createMinimalOds() throws IOException {
        return createOpenDocumentPackage(
                "application/vnd.oasis.opendocument.spreadsheet",
                """
                <?xml version="1.0" encoding="UTF-8"?>
                <office:document-content
                    xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0"
                    xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0"
                    office:version="1.2">
                  <office:body>
                    <office:spreadsheet>
                      <table:table table:name="Sheet1"/>
                    </office:spreadsheet>
                  </office:body>
                </office:document-content>
                """
        );
    }

    private byte[] createOpenDocumentPackage(String mimeType, String contentXml) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        try (ZipOutputStream zip = new ZipOutputStream(buffer)) {
            byte[] mimeTypeBytes = mimeType.getBytes(StandardCharsets.UTF_8);
            CRC32 crc = new CRC32();
            crc.update(mimeTypeBytes);

            ZipEntry mimeTypeEntry = new ZipEntry("mimetype");
            mimeTypeEntry.setMethod(ZipEntry.STORED);
            mimeTypeEntry.setSize(mimeTypeBytes.length);
            mimeTypeEntry.setCompressedSize(mimeTypeBytes.length);
            mimeTypeEntry.setCrc(crc.getValue());
            zip.putNextEntry(mimeTypeEntry);
            zip.write(mimeTypeBytes);
            zip.closeEntry();

            writeZipEntry(zip, "content.xml", contentXml);
            writeZipEntry(zip, "META-INF/manifest.xml", createOpenDocumentManifest(mimeType));
        }

        return buffer.toByteArray();
    }

    private String createOpenDocumentManifest(String mimeType) {
        return String.format(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>%n"
                        + "<manifest:manifest xmlns:manifest=\"urn:oasis:names:tc:opendocument:xmlns:manifest:1.0\" manifest:version=\"1.2\">%n"
                        + "  <manifest:file-entry manifest:full-path=\"/\" manifest:media-type=\"%s\"/>%n"
                        + "  <manifest:file-entry manifest:full-path=\"content.xml\" manifest:media-type=\"text/xml\"/>%n"
                        + "</manifest:manifest>%n",
                mimeType
        );
    }

    private byte[] createZipPackage(String[][] entries) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        try (ZipOutputStream zip = new ZipOutputStream(buffer)) {
            for (String[] entry : entries) {
                writeZipEntry(zip, entry[0], entry[1]);
            }
        }

        return buffer.toByteArray();
    }

    private void writeZipEntry(ZipOutputStream zip, String name, String content) throws IOException {
        ZipEntry entry = new ZipEntry(name);
        zip.putNextEntry(entry);
        zip.write(content.getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
    }
}