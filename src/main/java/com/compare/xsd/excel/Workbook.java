package com.compare.xsd.excel;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;

/**
 * Workbook implementation of an Excel Workbook file.
 */
@Log4j2
@Getter
public class Workbook {
    private final File file;

    private XSSFWorkbook workbook;

    /**
     * Initialize a new instance of {@link Workbook}.
     *
     * @param file Set the file of the workbook.
     * @throws ExcelLoadingException Is thrown when the given file couldn't be loaded.
     */
    public Workbook(File file) throws ExcelLoadingException {
        Assert.notNull(file, "file cannot be null");
        this.file = file;

        init();
    }

    /**
     * Create a new worksheet within the workbook.
     *
     * @param name        Set the name of the workbook.
     * @param shortenName Set if the name must be shortened when too long.
     * @return Returns the existing or created worksheet.
     * @throws NameTooLongException Is thrown when the given name is too long and #shortenName is false.
     */
    public Worksheet getOrCreateWorksheet(String name, boolean shortenName) throws NameTooLongException {
        Assert.hasText(name, "name cannot be empty");

        name = Worksheet.getWorksheetName(name, shortenName);
        XSSFSheet sheet = workbook.getSheet(name);

        if (sheet != null) {
            return new Worksheet(this, sheet);
        } else {
            return Worksheet.newWorksheet(name, this, shortenName);
        }
    }

    /**
     * Delete the given worksheet if it already exists and create a new one.
     *
     * @param name        Set the name of the workbook.
     * @param shortenName Set if the name must be shortened when too long.
     * @return Returns the created worksheet.
     * @throws NameTooLongException Is thrown when the given name is too long and #shortenName is false.
     */
    public Worksheet deleteAndCreateWorksheet(String name, boolean shortenName) throws NameTooLongException {
        Assert.hasText(name, "name cannot be empty");

        name = Worksheet.getWorksheetName(name, shortenName);
        XSSFSheet sheet = workbook.getSheet(name);

        if (sheet != null) {
            workbook.removeSheetAt(workbook.getSheetIndex(sheet));
        }

        return Worksheet.newWorksheet(name, this, shortenName);
    }

    /**
     * Save the workbook.
     *
     * @throws IOException Is thrown when the workbook couldn't be saved.
     */
    public void save() throws IOException {
        log.debug("Saving excel file...");
        workbook.write(FileUtils.openOutputStream(this.file));
        log.debug("Excel file saved");
    }

    private void init() throws ExcelLoadingException {
        try {
            if (this.file.exists()) {
                log.debug("Reading excel file " + this.file);
                this.workbook = new XSSFWorkbook(this.file);
                log.debug("Excel file read");
            } else {
                log.debug("Creating excel file " + this.file);
                this.workbook = new XSSFWorkbook();
                log.debug("Excel file created");
            }

            this.workbook.setMissingCellPolicy(Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        } catch (IOException | InvalidFormatException ex) {
            throw new ExcelLoadingException(this.file, ex);
        }
    }
}
