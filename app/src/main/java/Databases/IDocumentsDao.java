package Databases;
import Databases.util.DbTask;
import Models.Cards.Customer;
import Models.Documents.Document;
import Models.Documents.FinanceRecord;
import Models.Documents.Item;

import java.util.List;

public interface IDocumentsDao {
    DbTask<List<FinanceRecord>> getFinanceRecords(Customer customer);
    DbTask<List<Document>> getInvoices(Customer customer);
    DbTask<List<Document>> getOrders(Customer customer);
    DbTask<List<Document>> getOffers(Customer customer);
    DbTask<List<Document>> getRevokedInvoices(Customer customer);
    DbTask<List<Item>> getDocumentItems(Document document);
}
