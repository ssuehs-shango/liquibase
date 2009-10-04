package liquibase.statementexecute;

import liquibase.database.*;
import liquibase.database.core.*;
import liquibase.test.DatabaseTestContext;
import liquibase.statement.*;
import liquibase.statement.core.AddColumnStatement;
import liquibase.statement.core.CreateTableStatement;
import liquibase.statement.core.RenameColumnStatement;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class RenameColumnExecuteTest extends AbstractExecuteTest {

    protected static final String TABLE_NAME = "table_name";
    protected static final String COLUMN_NAME = "column_name";


    @Override
    protected List<? extends SqlStatement> setupStatements(Database database) {
        ArrayList<CreateTableStatement> statements = new ArrayList<CreateTableStatement>();
        CreateTableStatement table = new CreateTableStatement(null, TABLE_NAME);
        if (database instanceof MySQLDatabase) {
            table.addPrimaryKeyColumn("id", "int", null, "pk_");
        } else {
            table.addColumn("id", "int", null, new NotNullConstraint());
        }
        table.addColumn(COLUMN_NAME, "int");
        statements.add(table);

        if (database.supportsSchemas()) {
            table = new CreateTableStatement(DatabaseTestContext.ALT_SCHEMA, TABLE_NAME);
            table
                    .addColumn("id", "int", null, new NotNullConstraint());
            statements.add(table);
        }
        return statements;
    }

    @SuppressWarnings("unchecked")
    @Test
    public void noSchema() throws Exception {
        this.statementUnderTest = new RenameColumnStatement(null, TABLE_NAME, COLUMN_NAME, "new_name", "int");

        assertCorrect("rename column table_name.column_name to new_name", DerbyDatabase.class, InformixDatabase.class, MaxDBDatabase.class);
        assertCorrect("alter table table_name alter column column_name rename to new_name", H2Database.class, HsqlDatabase.class);
        assertCorrect("alter table table_name alter column column_name to new_name", FirebirdDatabase.class);
        assertCorrect("alter table `table_name` change `column_name` `new_name` int", MySQLDatabase.class);
        assertCorrect("exec sp_rename '[dbo].[table_name].[column_name]', 'new_name'", MSSQLDatabase.class);
        assertCorrectOnRest("alter table [table_name] rename column [column_name] to [new_name]");
    }
}