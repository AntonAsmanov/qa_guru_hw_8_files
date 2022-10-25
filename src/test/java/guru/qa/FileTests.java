package guru.qa;


import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import guru.qa.model.User;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import static org.assertj.core.api.Assertions.assertThat;

public class FileTests {
    ClassLoader cl = FileTests.class.getClassLoader();

    @Test
    void zipTest() throws Exception {
        ZipFile zf = new ZipFile(new File("src/test/resources/zip-file.zip"));
        try(ZipInputStream zis = new ZipInputStream(Objects.requireNonNull(cl.getResourceAsStream("zip-file.zip")))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().contains("xlsx.xlsx")) {
                    try (InputStream is = zf.getInputStream(entry)) {
                        XLS xls = new XLS(is);
                        assertThat(xls.excel
                                .getSheetAt(0)
                                .getRow(1)
                                .getCell(0)
                                .getStringCellValue()
                        ).isEqualTo("Ivanov");
                    }
                }
                else if (entry.getName().contains("csv.csv")) {
                    try (InputStream is = zf.getInputStream(entry)) {
                        CSVReader reader = new CSVReader(new InputStreamReader(is));
                        List<String[]> content = reader.readAll();
                        String[] row = content.get(2);
                        assertThat(row[0]).isEqualTo("Petrov");
                        assertThat(row[1]).isEqualTo("Petr");
                    }
                }
                else if (entry.getName().contains("pdf.pdf")) {
                    try (InputStream is = zf.getInputStream(entry)) {
                        PDF pdf = new PDF(is);
                        assertThat(pdf.text).contains("PDF Example");
                    }
                }
            }
        }
    }

    @Test
    void checkJSON() throws Exception {
        File file = new File("src/test/resources/example.json");
        ObjectMapper objectMapper = new ObjectMapper();
        User user = objectMapper.readValue(file, User.class);
        assertThat(user.name).isEqualTo("Ivanov Ivan Ivanovich");
        assertThat(user.id).isEqualTo(12345);
        assertThat(user.fileList.get(0).id).isEqualTo(1);
        assertThat(user.fileList.get(0).name).isEqualTo("File 1");
    }

}
