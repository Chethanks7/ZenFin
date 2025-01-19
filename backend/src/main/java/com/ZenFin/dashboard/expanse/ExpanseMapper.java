package com.ZenFin.dashboard.expanse;

import com.ZenFin.dashboard.pdfService.PdfExpanseData;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class ExpanseMapper {


  public Expense toExpanse(ExpenseDTO expanse){
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MM-yyyy");

    // Parse the date and validate that it's not in the future
    var expenseDate = LocalDate.parse(expanse.getDate(), formatter);
    System.err.println(expenseDate);
    System.err.println(LocalDate.now());
    if (expenseDate.isAfter(LocalDate.now())) {
      throw new IllegalArgumentException("Expense date cannot be in the future");
    }
    return Expense.builder()
      .amount(expanse.getAmount())
      .description(expanse.getDescription())
      .category(expanse.getCategory())
      .date(expenseDate)
      .recurring(expanse.isRecurring())
      .recurrenceFrequency(expanse.getRecurrenceFrequency())
      .build();
  }

  public ExpenseResponse toExpenseResponse(Expense expense){
    return ExpenseResponse.builder()
      .amount(expense.getAmount())
      .category(expense.getCategory())
      .date(String.valueOf(expense.getDate()))
      .userId(expense.getUser().getUserId())
      .build();
  }

  public PdfExpanseData toPdfExpenseData(Expense expense){
    return PdfExpanseData.builder()
      .date(expense.getDate().toString())
      .amount(expense.getAmount())
      .category(expense.getCategory())
      .description(expense.getDescription())
      .build();
  }


}
