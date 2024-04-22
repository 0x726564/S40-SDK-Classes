package javax.microedition.pim;

public interface ContactList extends PIMList {
   Contact createContact();

   Contact importContact(Contact var1);

   void removeContact(Contact var1) throws PIMException;
}
