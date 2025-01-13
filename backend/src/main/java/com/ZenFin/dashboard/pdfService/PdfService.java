package com.ZenFin.dashboard.pdfService;

import com.ZenFin.dashboard.expanse.Expense;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

@Service
public class PdfService {

  public void exportExpense(HttpServletResponse response,
                            List<Expense> expenses) throws Exception {

    Document document = new Document(PageSize.A4);
    PdfWriter.getInstance(document, response.getOutputStream());

    document.open();

    Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
    Paragraph title = new Paragraph("Expense Report", font);
    title.setAlignment(Paragraph.ALIGN_CENTER);
    document.add(title);

    document.add(new Paragraph("\n"));

    PdfPTable table = new PdfPTable(4);
    table.setWidthPercentage(100);
    table.setSpacingBefore(10);

    addTableHeader(table);
    addRows(table,expenses);
    document.add(table);

    BigDecimal total = expenses.stream()
      .map(Expense::getAmount)
      .reduce(BigDecimal.ZERO, BigDecimal::add);

    Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
    Paragraph footer = new Paragraph("Total Expenses: " + total, footerFont);
    footer.setAlignment(Paragraph.ALIGN_RIGHT);
    document.add(footer);

    document.close();
  }

  private void addTableHeader(PdfPTable table) {
    Stream.of("Date", "Category", "Description", "Amount").forEach(columnTitle -> {
      PdfPCell header = new PdfPCell();
      header.setPhrase(new Phrase(columnTitle));
      header.setHorizontalAlignment(Element.ALIGN_CENTER);
      table.addCell(header);
    });
  }

  private void addRows(PdfPTable table, List<Expense> expenses) {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    for (Expense expense : expenses) {
      String date = expense.getDate().format(formatter);
      table.addCell(date);
      table.addCell(expense.getCategory());
      table.addCell(expense.getDescription());
      table.addCell(expense.getAmount().toString());
    }
  }

}
