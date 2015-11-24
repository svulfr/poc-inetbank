package ru.ulfr.poc.pages;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.ui.Model;
import ru.ulfr.poc.modules.account.model.Account;
import ru.ulfr.poc.modules.bank.model.Currency;
import ru.ulfr.poc.modules.processor.model.Transaction;
import ru.ulfr.poc.modules.users.model.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Locale;
import java.util.Map;


/**
 * Utility class to render PDF based on statement model
 * <p/>
 * Uses Apache PDFBox to create PDFs.
 * This code is essentially non thread-safe, so each thread should use own instance.
 */
public class StatementPdfRenderer {

    /**
     * Model containing all information required for rendering
     */
    private Model model;

    /**
     * Font we use
     */
    private PDFont font = PDType1Font.COURIER;
    /**
     * PDF Content stream
     */
    private PDPageContentStream contentStream;

    /**
     * Offset of current line in PDF metrics, from bottom of the page
     */
    private float lineY = 650;

    /**
     * Constructs renderer for specified model
     *
     * @param model model with statement data
     */
    public StatementPdfRenderer(Model model) {
        this.model = model;
    }

    /**
     * Renders header
     *
     * @param text header text
     * @throws IOException
     */
    protected void renderHeader(String text) throws IOException {
        contentStream.beginText();
        contentStream.setFont(font, 16);
        contentStream.moveTextPositionByAmount(100, 700);
        contentStream.drawString(text);
        contentStream.endText();
    }

    /**
     * Renders line (as pair of key-value) and shifts {@link #lineY} value to next line
     *
     * @param text  key text
     * @param value value text
     * @throws IOException
     */
    protected void renderLine(String text, String value) throws IOException {
        contentStream.beginText();
        contentStream.setFont(font, 12);
        contentStream.moveTextPositionByAmount(100, lineY);
        contentStream.drawString(text);
        contentStream.moveTextPositionByAmount(100, 0);
        contentStream.drawString(value);
        contentStream.endText();
        lineY -= 20;
    }

    /**
     * Renders statement as PDF and returns byte array with PDF data
     *
     * @return byte array with PDF data
     * @throws IOException
     * @throws COSVisitorException
     */
    @SuppressWarnings("unchecked")
    public byte[] getPDF() throws IOException, COSVisitorException {

        try (ByteArrayOutputStream documentStream = new ByteArrayOutputStream()) {

            // Create a document and add a page to it
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);

            // Start a new content stream which will "hold" the to be created content
            contentStream = new PDPageContentStream(document, page);

            Transaction tx = (Transaction) model.asMap().get("transaction");
            Account account = (Account) model.asMap().get("account");

            // core here follows logic of handling customer-statement.ftl, using same terms
            Map<String, Currency> currencies = (Map<String, Currency>) model.asMap().get("currencies");
            renderHeader("Statement for bank operation " + tx.getId());
            DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.US);
            renderLine("Date: ", dateFormat.format(tx.getCreatedOn()));
            String txType = "", opSign = "", lcAmount;
            switch (tx.getTxType()) {
                case Transaction.TYPE_DEPOSIT:
                    txType = "Deposit";
                    opSign = "+";
                    break;
                case Transaction.TYPE_WITHDRAW:
                    txType = "Withdrawal";
                    opSign = "-";
                    break;
                case Transaction.TYPE_TRANSFER:
                    txType = account.getId() == tx.getOriginId() ? "Payment" : "Income";
                    opSign = account.getId() == tx.getOriginId() ? "-" : "+";
            }
            if (tx.getOriginId() != null && tx.getOriginId() == account.getId()) {
                lcAmount = String.format("%s %s", tx.getOriginAmount().toPlainString(), currencies.get("" + tx.getOriginCurrency()).getUiCode());
            } else {
                lcAmount = String.format("%s %s", tx.getRecipientAmount().toPlainString(), currencies.get("" + tx.getRecipientCurrency()).getUiCode());
            }
            renderLine("Type: ", txType);
            renderLine("Amount:", String.format("%s %s", tx.getTxAmount().toPlainString(), currencies.get("" + tx.getTxCurrency()).getUiCode()));
            renderLine("Acc. curr.:", account.getCurrency().getName());
            renderLine("Acc. change:", opSign + lcAmount);
            if (tx.getParty() != null) {
                User party = tx.getParty();
                renderLine(tx.getOriginId() == account.getId() ? "Paid to:" : "Received from:",
                        String.format("%s (%s)", party.getName(), party.getEmail()));
            }


            // Make sure that the content stream is closed:
            contentStream.close();

            // Save the results and ensure that the document is properly closed:
            document.save(documentStream);
            document.close();
            return documentStream.toByteArray();
        }
    }
}
